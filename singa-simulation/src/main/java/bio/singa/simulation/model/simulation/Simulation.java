package bio.singa.simulation.model.simulation;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.ComplexedChemicalEntity;
import bio.singa.features.identifiers.SimpleStringIdentifier;
import bio.singa.features.parameters.Environment;
import bio.singa.mathematics.geometry.faces.Circle;
import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.geometry.model.Polygon;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.UpdateModule;
import bio.singa.simulation.model.modules.displacement.Vesicle;
import bio.singa.simulation.model.modules.displacement.VesicleLayer;
import bio.singa.simulation.model.agents.membranes.MembraneLayer;
import bio.singa.simulation.model.rules.AssignmentRule;
import bio.singa.simulation.model.rules.AssignmentRules;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.CellRegions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Area;
import javax.measure.quantity.Time;
import java.util.*;

import static bio.singa.mathematics.geometry.model.Polygon.ON_LINE;

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
     * The layer for vesicles
     */
    private VesicleLayer vesicleLayer;

    private MembraneLayer membraneLayer;

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

    private boolean initializationDone;

    private Quantity<Time> maximalTimeStep;

    private CellRegion standardRegion;

    /**
     * Creates a new plain simulation.
     */
    public Simulation() {
        modules = new ArrayList<>();
        assignmentRules = new ArrayList<>();
        chemicalEntities = new HashMap<>();
        elapsedTime = Quantities.getQuantity(0.0, Environment.getTimeStep().getUnit());
        epoch = 0;
        initializationDone = false;
        observedUpdatables = new HashSet<>();
        vesicleLayer = new VesicleLayer(this);
        scheduler = new UpdateScheduler(this);
        standardRegion = CellRegions.EXTRACELLULAR_REGION;
    }

    /**
     * Calculates the next epoch.
     */
    public void nextEpoch() {
        logger.debug("Starting epoch {} ({}).", epoch, elapsedTime);
        if (!initializationDone) {
            initializeModules();
            initializeGraph();
            initializeSpatialRepresentations();
            initializeVesicleLayer();
            scheduler.rescaleParameters();
            initializationDone = true;
        }
        // clear observed nodes if necessary
        if (!observedUpdatables.isEmpty()) {
            for (Updatable observedUpdatable : observedUpdatables) {
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
            associateVesicles();
        }
        // update epoch and elapsed time
        updateEpoch();
        // if time step did not change it can possibly be increased
        if (!scheduler.timeStepWasRescaled()) {
            // if no maximal time step is given or time step is not already maximal
            if (maximalTimeStep == null || Environment.getTimeStep().to(maximalTimeStep.getUnit()).getValue().doubleValue() < maximalTimeStep.getValue().doubleValue()) {
                // if error was below tolerance threshold (10 percent of epsilon)
                if (scheduler.getRecalculationCutoff() - scheduler.getLargestError().getValue() > 0.1 * scheduler.getRecalculationCutoff()) {
                    // try larger time step next time
                    scheduler.increaseTimeStep();
                }
            }
        }
    }

    private void initializeModules() {
        logger.info("Initializing features required for each module.");
        for (UpdateModule module : modules) {
            module.checkFeatures();
        }
    }

    public void initializeGraph() {
        logger.info("Initializing chemical entities.");
        if (graph == null) {
            throw new IllegalStateException("No graph has been assigned to the simulation.");
        } else {
//            for (AutomatonNode node : graph.getNodes()) {
//                node.setCellRegion(standardRegion);
//            }
//            simulationRegion = new Rectangle(Environment.getSimulationExtend(), Environment.getSimulationExtend());
        }
    }

    public void initializeSpatialRepresentations() {
        logger.info("Initializing spatial representations of automaton nodes.");
        // TODO initialize via voronoi diagrams
        // or rectangles
        for (AutomatonNode node : graph.getNodes()) {
            // create rectangles centered on the nodes with side length of node distance
            Vector2D position = node.getPosition();
            double offset = Environment.convertSystemToSimulationScale(Environment.getNodeDistance()) * 0.5;
            Vector2D topLeft = new Vector2D(position.getX() - offset, position.getY() - offset);
            Vector2D bottomRight = new Vector2D(position.getX() + offset, position.getY() + offset);
            node.setSpatialRepresentation(new Rectangle(topLeft, bottomRight));
        }
    }

    private void initializeVesicleLayer() {
        logger.info("Initializing vesicle layer and individual vesicles.");
        // initialize simulation space
        vesicleLayer.setSimulation(this);
        vesicleLayer.setSimulationArea(new Rectangle(Environment.getSimulationExtend(), Environment.getSimulationExtend()));
        associateVesicles();
    }

    private void associateVesicles() {
        // clear previous vesicle associations
        vesicleLayer.getVesicles().forEach(Vesicle::clearAssociatedNodes);
        // associate vesicles to nodes
        for (Vesicle vesicle : vesicleLayer.getVesicles()) {
            // convert vesicle from system to simulation scale
            Circle vesicleCircle = vesicle.getCircleRepresentation();
            double radius = vesicleCircle.getRadius();
            // determine representative and associated nodes
            AutomatonNode representativeNode = null;
            Map<AutomatonNode, Set<Vector2D>> associatedNodes = new HashMap<>();
            for (AutomatonNode node : graph.getNodes()) {
                // get representative region of the node
                Polygon polygon = node.getSpatialRepresentation();
                // associate vesicle to the node with the largest part of the vesicle (midpoint is inside)
                if (representativeNode == null && polygon.evaluatePointPosition(vesicle.getCurrentPosition()) >= ON_LINE) {
                    representativeNode = node;
                }
                // associate partial containment to other nodes
                Set<Vector2D> intersections = polygon.getIntersections(vesicleCircle);
                if (!intersections.isEmpty()) {
                    associatedNodes.put(node, intersections);
                }
            }
            // remove potentially doubly assigned representative node
            associatedNodes.remove(representativeNode);
            // calculate the total surface of the implicit sphere
            final double totalSurface = 4.0 * Math.PI * radius * radius;
            double reducedSurface = totalSurface;
            // distribute the sphere according to the intersections with different representative regions
            for (Map.Entry<AutomatonNode, Set<Vector2D>> entry : associatedNodes.entrySet()) {
                Set<Vector2D> intersections = entry.getValue();
                // if there is an actual region to intersect
                // FIXME handle cases where more than two intersection points are present
                if (intersections.size() == 2) {
                    Iterator<Vector2D> iterator = intersections.iterator();
                    Vector2D first = iterator.next();
                    Vector2D second = iterator.next();
                    // calculate angle associated to the node
                    double theta = vesicleCircle.getCentralAngleBetween(first, second);
                    // and the spherical lune of the implicit sphere associated to the automaton node
                    double associatedSurface = 2 * radius * radius * theta;
                    // use fraction to get actual system area
                    double fraction = associatedSurface / totalSurface;
                    // reduce area of representative node
                    reducedSurface -= associatedSurface;
                    // associate vesicle and its corresponding area to the node
                    Quantity<Area> nodeSurface = vesicle.getArea().multiply(fraction);
                    AutomatonNode node = entry.getKey();
                    vesicle.addAssociatedNode(node, nodeSurface);
                }
            }
            // set area for representative
            double fraction = reducedSurface / totalSurface;
            Quantity<Area> nodeSurface = vesicle.getArea().multiply(fraction);
            assert representativeNode != null;
            vesicle.addAssociatedNode(representativeNode, nodeSurface);
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

    public Rectangle getSimulationRegion() {
        return simulationRegion;
    }

    public void setSimulationRegion(Rectangle simulationRegion) {
        this.simulationRegion = simulationRegion;
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

    public UpdateScheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(UpdateScheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void setMaximalTimeStep(Quantity<Time> maximalTimeStep) {
        this.maximalTimeStep = maximalTimeStep;
    }

    /**
     * Update the epoch counter and elapsed time.
     */
    private void updateEpoch() {
        epoch++;
        elapsedTime = elapsedTime.add(Environment.getTimeStep());
    }

    public ComparableQuantity<Time> getElapsedTime() {
        return elapsedTime;
    }

    public AutomatonGraph getGraph() {
        return graph;
    }

    public void setGraph(AutomatonGraph graph) {
        this.graph = graph;
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
            if (entity instanceof ComplexedChemicalEntity) {
                entities.addAll(((ComplexedChemicalEntity) entity).getAssociatedChemicalEntities());
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

    public void observeNode(Updatable updatable) {
        observedUpdatables.add(updatable);
        updatable.setObserved(true);
    }

    public Set<Updatable> getObservedUpdatables() {
        return observedUpdatables;
    }

}
