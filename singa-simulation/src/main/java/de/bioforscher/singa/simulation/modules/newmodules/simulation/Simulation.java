package de.bioforscher.singa.simulation.modules.newmodules.simulation;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.identifiers.SimpleStringIdentifier;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.mathematics.geometry.faces.Circle;
import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.geometry.model.Polygon;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import de.bioforscher.singa.simulation.model.layer.Vesicle;
import de.bioforscher.singa.simulation.model.layer.VesicleLayer;
import de.bioforscher.singa.simulation.modules.model.Updatable;
import de.bioforscher.singa.simulation.modules.newmodules.module.UpdateModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Area;
import javax.measure.quantity.Time;
import java.util.*;

import static de.bioforscher.singa.mathematics.geometry.model.Polygon.ON_LINE;

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

    /**
     * The chemical entities referenced in the graph.
     */
    private Map<SimpleStringIdentifier, ChemicalEntity> chemicalEntities;

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

    /**
     * Creates a new plain simulation.
     */
    public Simulation() {
        modules = new ArrayList<>();
        chemicalEntities = new HashMap<>();
        elapsedTime = Quantities.getQuantity(0.0, Environment.getTimeStep().getUnit());
        epoch = 0;
        initializationDone = false;
        observedUpdatables = new HashSet<>();
        vesicleLayer = VesicleLayer.EMPTY_LAYER;
        scheduler = new UpdateScheduler(this);
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
                observedUpdatable.clearPotentialDeltas();
            }
        }
        // apply all modules
        scheduler.nextEpoch();
        // apply generated deltas
        logger.debug("Applying deltas.");
        for (Updatable updatable : updatables) {
            logger.trace("Deltas in {}:", updatable);
            updatable.applyDeltas();
        }
        // move vesicles
        if (vesicleLayer != null) {
            // vesicleLayer.step();
            associateVesicles();
        }
        // update epoch and elapsed time
        updateEpoch();
        // if time step did not change
        if (!scheduler.timestepWasRescaled()) {
            // if error was below tolerance threshold (10 percent of epsilon)
            if (scheduler.getRecalculationCutoff() - scheduler.getLargestError().getValue() > 0.1 * scheduler.getRecalculationCutoff()) {
                // try larger time step next time
                scheduler.increaseTimeStep();
            }
        }
    }

    private void initializeModules() {
        logger.info("Initializing features required for each module.");
        for (UpdateModule module : modules) {
            module.checkFeatures();
        }
    }

    private void initializeGraph() {
        logger.info("Initializing chemical entities.");
        if (graph == null) {
            throw new IllegalStateException("No graph has been assigned to the simulation.");
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
            // FIXME this can potentially be improved by reducing the number of nodes to check (eg using a quadtree)
            for (AutomatonNode node : graph.getNodes()) {
                // get representative region of the node
                Polygon polygon = node.getSpatialRepresentation();
                // associate vesicle to the node with the largest part of the vesicle (midpoint is inside)
                if (representativeNode == null && polygon.evaluatePointPosition(vesicle.getPosition()) >= ON_LINE) {
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

    public void collectUpdatables() {
        updatables = new ArrayList<>(graph.getNodes());
        updatables.addAll(vesicleLayer.getVesicles());
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

    public ChemicalEntity getChemicalEntity(String primaryIdentifier) {
        return chemicalEntities.get(new SimpleStringIdentifier(primaryIdentifier));
    }

    public void addReferencedEntity(ChemicalEntity chemicalEntity) {
        chemicalEntities.put(chemicalEntity.getIdentifier(), chemicalEntity);
    }

}
