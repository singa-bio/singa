package bio.singa.simulation.model.simulation;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.formatter.TimeFormatter;
import bio.singa.features.model.Feature;
import bio.singa.features.parameters.Environment;
import bio.singa.features.units.UnitRegistry;
import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.geometry.model.Polygon;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.features.Ratio;
import bio.singa.simulation.model.agents.linelike.LineLikeAgentLayer;
import bio.singa.simulation.model.agents.pointlike.VesicleLayer;
import bio.singa.simulation.model.agents.surfacelike.MembraneLayer;
import bio.singa.simulation.model.agents.volumelike.VolumeLayer;
import bio.singa.simulation.model.agents.volumelike.VolumeLikeAgent;
import bio.singa.simulation.model.concentrations.InitialConcentration;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.graphs.NeighborhoodMappingManager;
import bio.singa.simulation.model.modules.UpdateModule;
import bio.singa.simulation.model.modules.concentration.ConcentrationDelta;
import bio.singa.simulation.model.simulation.error.NumericalError;
import bio.singa.simulation.model.modules.concentration.imlementations.transport.Diffusion;
import bio.singa.simulation.model.modules.displacement.DisplacementBasedModule;
import bio.singa.simulation.model.rules.AssignmentRule;
import bio.singa.simulation.model.rules.AssignmentRules;
import bio.singa.simulation.trajectories.errors.DebugRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Time;
import java.util.*;

import static tech.units.indriya.unit.MetricPrefix.MICRO;
import static tech.units.indriya.unit.Units.SECOND;

/**
 * @author cl
 */
public class Simulation {

    private static final Logger logger = LoggerFactory.getLogger(Simulation.class);

    /**
     * The graph structure.
     */
    private AutomatonGraph graph;

    /**
     * The layer for vesicles.
     */
    private VesicleLayer vesicleLayer;

    /**
     * The layer for membranes.
     */
    private MembraneLayer membraneLayer;

    private LineLikeAgentLayer lineLayer;

    private VolumeLayer volumeLayer;

    /**
     * The base area of the membrane
     */
    private Rectangle simulationRegion;

    /**
     * The chemical entities referenced in the graph.
     */
    private Map<String, ChemicalEntity> chemicalEntities;

    /**
     * The assignment rules.
     */
    private List<AssignmentRule> assignmentRules;

    /**
     * The current epoch.
     */
    private long epoch;

    /**
     * The currently elapsed time.
     */
    private ComparableQuantity<Time> elapsedTime;

    /**
     * The sections top be updated
     */
    private List<Updatable> updatables;

    /**
     * The nodes, that are observed during simulation.
     */
    private Set<Updatable> observedUpdatables;

    private UpdateScheduler scheduler;

    private List<UpdateModule> modules;

    private List<InitialConcentration> concentrations;

    private Quantity<Time> maximalTimeStep;

    private Map<Updatable, List<ConcentrationDelta>> observedDeltas;

    private boolean initializationDone;

    private boolean vesiclesWillMove;

    private boolean debug;
    private DebugRecorder debugRecorder;

    /**
     * Creates a new plain simulation.
     */
    public Simulation() {
        modules = new ArrayList<>();
        assignmentRules = new ArrayList<>();
        concentrations = new ArrayList<>();
        chemicalEntities = new HashMap<>();
        elapsedTime = Quantities.getQuantity(0.0, UnitRegistry.getTimeUnit());
        epoch = 0;
        initializationDone = false;
        vesiclesWillMove = false;
        debug = false;
        observedUpdatables = new HashSet<>();
        vesicleLayer = new VesicleLayer(this);
        scheduler = new UpdateScheduler(this);
        observedDeltas = new HashMap<>();
    }

    /**
     * Calculates the next epoch.
     */
    public void nextEpoch() {
        logger.debug("Starting epoch {} ({}).", epoch, elapsedTime);
        if (!initializationDone) {
            initializeModules();
            initializeVesicleLayer();
            initializeSubsectionAdjacency();
            scheduler.initializeThreadPool();
            initializationDone = true;
        }

        // clear observed nodes if necessary
        if (!observedUpdatables.isEmpty()) {
            for (Updatable observedUpdatable : observedUpdatables) {
                // remember all updatables until they are written
                if (!observedDeltas.containsKey(observedUpdatable)) {
                    observedDeltas.put(observedUpdatable, new ArrayList<>());
                }
                for (ConcentrationDelta delta : observedUpdatable.getConcentrationManager().getPotentialDeltas()) {
                    // adjust to time step
                    observedDeltas.get(observedUpdatable).add(delta.multiply(1.0 / UnitRegistry.getTime().to(MICRO(SECOND)).getValue().doubleValue()));
                }
                // clear them
                observedUpdatable.getConcentrationManager().clearPotentialDeltas();
            }
        }

        // collect newly created updatables
        collectUpdatables();

        // apply concentrations
        applyConcentrations();

        // apply all modules
        scheduler.nextEpoch();

        // apply generated deltas
        logger.debug("Applying deltas.");
        for (Updatable updatable : updatables) {
            if (updatable.getConcentrationManager().hasDeltas()) {
                logger.trace("Deltas in {}:", updatable.getStringIdentifier());
                updatable.getConcentrationManager().applyDeltas();
            }
        }

        if (vesicleLayer != null && vesiclesWillMove) {
            // move vesicles
            vesicleLayer.applyDeltas();
            // associate nodes
            vesicleLayer.associateVesicles();
        }

        // update epoch and elapsed time
        updateEpoch();
        // if time step did not change it can possibly be increased
        if (timeStepShouldIncrease()) {
            scheduler.increaseTimeStep("previous error was very small");
        }

    }

    private boolean timeStepShouldIncrease() {

        // if a maximal time step is set
        if (maximalTimeStep != null) {
            final double currentTimeStep = UnitRegistry.getTime().to(maximalTimeStep.getUnit()).getValue().doubleValue();
            final double maximalTimeStep = this.maximalTimeStep.getValue().doubleValue();
            // if the current time step is already the maximal time step
            if (currentTimeStep >= maximalTimeStep) {
                return false;
            }
        }

        // if the the error that was computed previously is very small
        final double globalTolerance = scheduler.getErrorManager().getGlobalNumericalTolerance();
        final NumericalError latestGlobalError = scheduler.getErrorManager().getGlobalNumericalError();
        if (globalTolerance - latestGlobalError.getValue() > 0.2 * globalTolerance) {
            // System.out.println("global error "+ latestGlobalError);
            final double latestLocalError = scheduler.getErrorManager().getLocalNumericalError().getValue();
            double localNumericalTolerance = scheduler.getErrorManager().getLocalNumericalTolerance();
            // System.out.println("local error "+ latestLocalError);
            if (localNumericalTolerance - latestLocalError > 0.2 * localNumericalTolerance) {
                // try larger time step
                return true;
            }
        }

        return false;
    }

    private void initializeModules() {
        logger.info("Initializing modules:");
        for (UpdateModule module : getModules()) {
            module.initialize();
            scheduler.addModule(module);
            logger.info("Module {}", module.getIdentifier());
            for (Feature<?> feature : module.getFeatures()) {
                logger.info("  Feature {} = {}", feature.getClass().getSimpleName(), feature.getContent());
            }
            // save some computation time in case vesicles will not move; this effects:
            // associations between nodes and vesicles are only calculated once
            // no collisions are checked
            if (module instanceof DisplacementBasedModule) {
                vesiclesWillMove = true;
            }
        }
    }

    public void addConcentration(InitialConcentration initialConcentration) {
        concentrations.add(initialConcentration);
    }

    private void applyConcentrations() {
        // skip if concentrations is empty
        if (concentrations == null || concentrations.isEmpty()) {
            return;
        }
        // apply concentrations
        ListIterator<InitialConcentration> iterator = concentrations.listIterator();
        while (iterator.hasNext()) {
            InitialConcentration concentration = iterator.next();
            if (concentration.getTime().isLessThanOrEqualTo(elapsedTime)) {
//                logger.info("Initialized concentration {}.", concentration);
                concentration.apply(this);
                if (!concentration.isFix()) {
                    iterator.remove();
                }
            }
        }
    }

    public List<InitialConcentration> getConcentrations() {
        return concentrations;
    }

    public void setConcentrations(List<InitialConcentration> concentrations) {
        this.concentrations = concentrations;
    }

    public void initializeSpatialRepresentations() {
        logger.info("Initializing spatial representations of automaton nodes.");
        // TODO initialize via voronoi diagrams
        // or rectangles
        for (AutomatonNode node : graph.getNodes()) {
            // create rectangles centered on the nodes with side length of node distance
            Vector2D position = node.getPosition();
            double offset = Environment.convertSystemToSimulationScale(UnitRegistry.getSpace()) * 0.5;
            Vector2D topLeft = new Vector2D(position.getX() - offset, position.getY() - offset);
            Vector2D bottomRight = new Vector2D(position.getX() + offset, position.getY() + offset);
            node.setSpatialRepresentation(new Rectangle(topLeft, bottomRight));
        }
    }

    private void initializeVesicleLayer() {
        logger.info("Initializing vesicle layer and individual vesicles.");
        // initialize simulation space
        if (simulationRegion == null) {
            simulationRegion = new Rectangle(Environment.getSimulationExtend(), Environment.getSimulationExtend());
        }
        vesicleLayer.setSimulation(this);
        vesicleLayer.associateVesicles();
    }

    private void initializeSubsectionAdjacency() {
        if (graph.getNodes().size() < 1) {
            return;
        }
        // if there is diffusion in the modules, adjacency needs to be defined
        Optional<Diffusion> optionalModule = getModules().stream()
                .filter(Diffusion.class::isInstance)
                .map(Diffusion.class::cast)
                .findAny();
        // for each node
        NeighborhoodMappingManager.initializeNeighborhoodForGraph(graph);
        for (AutomatonNode node : graph.getNodes()) {
            if (getVolumeLayer() != null) {
                // for each volume
                for (VolumeLikeAgent agent : getVolumeLayer().getAgents()) {
                    // initialize reduction
                    Polygon cortexArea = agent.getArea();
                    if (optionalModule.isPresent()) {
                        Ratio ratio = optionalModule.get().getFeature(Ratio.class);
                        NeighborhoodMappingManager.initializeDiffusiveReduction(node, cortexArea, ratio);
                    }
                }
            }

        }
    }


    public VesicleLayer getVesicleLayer() {
        return vesicleLayer;
    }

    public void setVesicleLayer(VesicleLayer vesicleLayer) {
        this.vesicleLayer = vesicleLayer;
    }

    public MembraneLayer getMembraneLayer() {
        return membraneLayer;
    }

    public void setMembraneLayer(MembraneLayer membraneLayer) {
        this.membraneLayer = membraneLayer;
    }

    public VolumeLayer getVolumeLayer() {
        return volumeLayer;
    }

    public void setVolumeLayer(VolumeLayer volumeLayer) {
        this.volumeLayer = volumeLayer;
    }

    public LineLikeAgentLayer getLineLayer() {
        return lineLayer;
    }

    public void setLineLayer(LineLikeAgentLayer lineLayer) {
        this.lineLayer = lineLayer;
    }

    public Rectangle getSimulationRegion() {
        return simulationRegion;
    }

    public void setSimulationRegion(Rectangle simulationRegion) {
        this.simulationRegion = simulationRegion;
    }

    public void collectUpdatables() {
        updatables = new ArrayList<>(graph.getNodes());
        updatables.addAll(vesicleLayer.getVesicles());
        updatables.addAll(vesicleLayer.getAspiringPits());
        updatables.addAll(vesicleLayer.getMaturingPits());
    }

    /**
     * Apply all referenced assignment rules.
     */
    public void applyAssignmentRules() {
        for (AssignmentRule rule : assignmentRules) {
            for (AutomatonNode bioNode : graph.getNodes()) {
                rule.applyRule(bioNode);
            }
        }
    }

    /**
     * Adds a list of assignment rules, sorting them by their dependencies.
     *
     * @param assignmentRules The assignment rules.
     * @see AssignmentRules#sortAssignmentRulesByPriority(List)
     */
    public void setAssignmentRules(List<AssignmentRule> assignmentRules) {
        this.assignmentRules = AssignmentRules.sortAssignmentRulesByPriority(assignmentRules);
    }

    public List<Updatable> getUpdatables() {
        return updatables;
    }

    public List<UpdateModule> getModules() {
        return modules;
    }

    public void addModule(UpdateModule module) {
        // logger.info("Adding module {}.", module.toString());
        module.setSimulation(this);
        module.checkFeatures();
        for (ChemicalEntity referencedEntity : module.getReferencedChemicalEntities()) {
            addReferencedEntity(referencedEntity);
        }
        modules.add(module);
    }

    public UpdateScheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(UpdateScheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void setMaximalTimeStep(Quantity<Time> maximalTimeStep) {
        this.maximalTimeStep = maximalTimeStep;
        logger.info("Maximal timestep set to {}.", TimeFormatter.formatTime(maximalTimeStep));
    }

    /**
     * Update the epoch counter and elapsed time.
     */
    private void updateEpoch() {
        epoch++;
        elapsedTime = elapsedTime.add(UnitRegistry.getTime());
    }

    public ComparableQuantity<Time> getElapsedTime() {
        return elapsedTime;
    }

    public AutomatonGraph getGraph() {
        return graph;
    }

    public void setGraph(AutomatonGraph graph) {
        // logger.info("Adding graph.");
        this.graph = graph;
        initializeSpatialRepresentations();
    }

    public long getEpoch() {
        return epoch;
    }

    /**
     * Returns the chemical entities.
     *
     * @return The chemical entities.
     */
    public Collection<ChemicalEntity> getChemicalEntities() {
        return chemicalEntities.values();
    }

    public ChemicalEntity getChemicalEntity(String primaryIdentifier) {
        return chemicalEntities.get(primaryIdentifier);
    }

    public void addReferencedEntity(ChemicalEntity chemicalEntity) {
        chemicalEntities.put(chemicalEntity.getIdentifier(), chemicalEntity);
    }

    public Set<Updatable> getObservedUpdatables() {
        return observedUpdatables;
    }

    public List<ConcentrationDelta> getPreviousObservedDeltas(Updatable updatable) {
        return observedDeltas.get(updatable);
    }

    public void clearPreviouslyObservedDeltas() {
        observedDeltas.clear();
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public DebugRecorder getDebugRecorder() {
        return debugRecorder;
    }

    public void setDebugRecorder(DebugRecorder debugRecorder) {
        this.debugRecorder = debugRecorder;
    }

}
