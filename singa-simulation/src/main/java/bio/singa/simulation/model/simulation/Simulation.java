package bio.singa.simulation.model.simulation;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.ComplexEntity;
import bio.singa.features.formatter.TimeFormatter;
import bio.singa.features.identifiers.SimpleStringIdentifier;
import bio.singa.features.parameters.Environment;
import bio.singa.features.units.UnitRegistry;
import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.agents.linelike.LineLikeAgentLayer;
import bio.singa.simulation.model.agents.pointlike.VesicleLayer;
import bio.singa.simulation.model.agents.surfacelike.MembraneLayer;
import bio.singa.simulation.model.agents.volumelike.VolumeLayer;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.UpdateModule;
import bio.singa.simulation.model.modules.concentration.ConcentrationDelta;
import bio.singa.simulation.model.rules.AssignmentRule;
import bio.singa.simulation.model.rules.AssignmentRules;
import bio.singa.simulation.model.sections.concentration.ConcentrationInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Time;
import java.util.*;

import static tec.uom.se.unit.MetricPrefix.MICRO;
import static tec.uom.se.unit.Units.SECOND;

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
    private Map<SimpleStringIdentifier, ChemicalEntity> chemicalEntities;

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
    private ArrayList<Updatable> updatables;

    /**
     * The nodes, that are observed during simulation.
     */
    private Set<Updatable> observedUpdatables;

    private UpdateScheduler scheduler;

    private List<UpdateModule> modules;

    private ConcentrationInitializer concentrationInitializer;

    private boolean initializationDone;

    private Quantity<Time> maximalTimeStep;

    private Map<Updatable, List<ConcentrationDelta>> observedDeltas;

    /**
     * Creates a new plain simulation.
     */
    public Simulation() {
        modules = new ArrayList<>();
        assignmentRules = new ArrayList<>();
        chemicalEntities = new HashMap<>();
        elapsedTime = Quantities.getQuantity(0.0, UnitRegistry.getTimeUnit());
        epoch = 0;
        initializationDone = false;
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
            initializeConcentrations();
            initializeVesicleLayer();
            initializationDone = true;
        }
        // clear observed nodes if necessary
        if (!observedUpdatables.isEmpty()) {
            for (Updatable observedUpdatable : observedUpdatables) {
                // remember all updatables until they are written
                if (!observedDeltas.containsKey(observedUpdatable)) {
                    observedDeltas.put(observedUpdatable, new ArrayList<>());
                }
                for (ConcentrationDelta delta : observedUpdatable.getPotentialConcentrationDeltas()) {
                    // adjust to time step
                    observedDeltas.get(observedUpdatable).add(delta.multiply(1.0 / UnitRegistry.getTime().to(MICRO(SECOND)).getValue().doubleValue()));
                }
                // clear them
                observedUpdatable.clearPotentialConcentrationDeltas();
            }
        }
        // apply all modules
        scheduler.nextEpoch();
        // apply generated deltas
        logger.debug("Applying deltas.");
        for (Updatable updatable : updatables) {
            if (updatable.hasDeltas()) {
                logger.trace("Deltas in {}:", updatable.getStringIdentifier());
                updatable.applyDeltas();
            }
        }
        // move vesicles
        if (vesicleLayer != null) {
            vesicleLayer.applyDeltas();
            vesicleLayer.associateVesicles();
        }
        // update epoch and elapsed time
        updateEpoch();
        // if time step did not change it can possibly be increased
        if (timestepShuldIncrease()) {
            scheduler.increaseTimeStep();
        }
    }


    private boolean timestepShuldIncrease() {
        // if time step was reduced in this epoch there is no need to test if it should increase
        if (scheduler.timeStepWasAlteredInThisEpoch()) {
            return false;
        }
        // if a maximal time step is set
        if (maximalTimeStep != null) {
            final double currentTimeStep = UnitRegistry.getTime().to(maximalTimeStep.getUnit()).getValue().doubleValue();
            final double maximalTimeStep = this.maximalTimeStep.getValue().doubleValue();
            // if the current time step is already the maximal time step
            if (currentTimeStep >= maximalTimeStep) {
                return false;
            }
        }
        // check if wou gould gain simulation speed by increasing the time step
        // this can be done, if the the error that was computed previously is very small
        final double recalculationCutoff = scheduler.getRecalculationCutoff();
        final double latestError = scheduler.getLargestError().getValue();
        if (recalculationCutoff - latestError > 0.2 * recalculationCutoff) {
            // try larger time step
            return true;
        }
        return false;
    }

    private void initializeModules() {
        for (UpdateModule module : getModules()) {
            module.initialize();
            scheduler.addModule(module);
        }
    }

    private void initializeConcentrations() {
        if (concentrationInitializer != null) {
            logger.info("Initializing starting concentrations.");
            concentrationInitializer.initialize(this);
        }
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

    public ConcentrationInitializer getConcentrationInitializer() {
        return concentrationInitializer;
    }

    public void collectUpdatables() {
        updatables = new ArrayList<>(graph.getNodes());
        updatables.addAll(vesicleLayer.getVesicles());
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

    public ArrayList<Updatable> getUpdatables() {
        return updatables;
    }

    public List<UpdateModule> getModules() {
        return modules;
    }

    public void addModule(UpdateModule module) {
        logger.info("Adding module {}.", module.toString());
        module.setSimulation(this);
        module.checkFeatures();
        for (ChemicalEntity referencedEntity : module.getReferencedEntities()) {
            addReferencedEntity(referencedEntity);
        }
        modules.add(module);
    }

    public void setGraph(AutomatonGraph graph) {
        logger.info("Adding graph.");
        this.graph = graph;
        initializeSpatialRepresentations();
    }

    public UpdateScheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(UpdateScheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void setMaximalTimeStep(Quantity<Time> maximalTimeStep) {
        this.maximalTimeStep = maximalTimeStep;
        logger.info("Maximal timestep set to" + TimeFormatter.formatTime(maximalTimeStep));
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

    public Set<ChemicalEntity> getAllChemicalEntities() {
        Set<ChemicalEntity> entities = new HashSet<>();
        for (ChemicalEntity entity : chemicalEntities.values()) {
            entities.add(entity);
            if (entity instanceof ComplexEntity) {
                entities.addAll(((ComplexEntity) entity).getAllData());
            }
        }
        return entities;
    }

    public ChemicalEntity getChemicalEntity(String primaryIdentifier) {
        return chemicalEntities.get(new SimpleStringIdentifier(primaryIdentifier));
    }

    public void addReferencedEntity(ChemicalEntity chemicalEntity) {
        chemicalEntities.put(chemicalEntity.getIdentifier(), chemicalEntity);
    }

    public void observe(Updatable updatable) {
        observedUpdatables.add(updatable);
        updatable.setObserved(true);
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

    public void setConcentrationInitializer(ConcentrationInitializer concentrationInitializer) {
        this.concentrationInitializer = concentrationInitializer;
    }
}
