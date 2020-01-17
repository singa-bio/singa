package bio.singa.simulation.model.agents.pointlike;

import bio.singa.features.parameters.Environment;
import bio.singa.mathematics.geometry.bodies.Spheres;
import bio.singa.mathematics.geometry.edges.LineSegment;
import bio.singa.mathematics.geometry.edges.SimpleLineSegment;
import bio.singa.mathematics.geometry.faces.Circle;
import bio.singa.mathematics.geometry.faces.Circles;
import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.geometry.model.Polygon;
import bio.singa.mathematics.matrices.LabeledSymmetricMatrix;
import bio.singa.mathematics.topology.grids.rectangular.MooreRectangularDirection;
import bio.singa.mathematics.topology.grids.rectangular.NeumannRectangularDirection;
import bio.singa.mathematics.topology.grids.rectangular.RectangularCoordinate;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.agents.surfacelike.Membrane;
import bio.singa.simulation.model.agents.surfacelike.MembraneSegment;
import bio.singa.simulation.model.agents.volumelike.VolumeLikeAgent;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.displacement.DisplacementBasedModule;
import bio.singa.simulation.model.modules.displacement.implementations.VesicleConfinedDiffusion;
import bio.singa.simulation.model.modules.qualitative.implementations.EndocytoticPit;
import bio.singa.simulation.model.simulation.Simulation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static bio.singa.mathematics.metrics.model.VectorMetricProvider.SQUARED_EUCLIDEAN_METRIC;

/**
 * @author cl
 */
public class VesicleLayer {

    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(DisplacementBasedModule.class);

    private Simulation simulation;
    private Rectangle simulationRegion;

    private List<Vesicle> vesicles;
    private List<EndocytoticPit> collectingPits;
    private List<EndocytoticPit> maturingPits;

    public VesicleLayer(Simulation simulation) {
        setSimulation(simulation);
        vesicles = new ArrayList<>();
        collectingPits = new ArrayList<>();
        maturingPits = new ArrayList<>();
    }

    public Rectangle getSimulationRegion() {
        return simulationRegion;
    }

    public void setSimulationRegion(Rectangle simulationRegion) {
        this.simulationRegion = simulationRegion;
    }

    public Simulation getSimulation() {
        return simulation;
    }

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
        simulationRegion = simulation.getSimulationRegion();
    }

    public void addVesicle(Vesicle vesicle) {
        vesicles.add(vesicle);
    }

    public void addVesicles(Collection<Vesicle> vesicles) {
        this.vesicles.addAll(vesicles);
    }

    public void removeVesicle(Vesicle vesicle) {
        vesicles.remove(vesicle);
    }

    public List<Vesicle> getVesicles() {
        return vesicles;
    }

    public List<EndocytoticPit> getAspiringPits() {
        return collectingPits;
    }

    public List<EndocytoticPit> getMaturingPits() {
        return maturingPits;
    }

    private void checkForCollisions() {
        //compare the distance to combined radii
        LabeledSymmetricMatrix<Vesicle> distances = SQUARED_EUCLIDEAN_METRIC.calculateDistancesPairwise(vesicles, Vesicle::getNextPosition);
        vesicleLoop:
        for (Vesicle vesicle1 : vesicles) {
            // check collisions with other vesicles
            double firstRadius = Environment.convertSystemToSimulationScale(vesicle1.getRadius());
            for (Vesicle vesicle2 : vesicles) {
                // TODO speed up by comparing only to close vesicles with referenced automaton nodes
                if (vesicle1 != vesicle2) {
                    double distance = distances.getValueForLabel(vesicle1, vesicle2);
                    double combinedRadii = firstRadius + Environment.convertSystemToSimulationScale(vesicle2.getRadius());
                    if (distance < combinedRadii) {
                        if (ThreadLocalRandom.current().nextDouble() < 0.5) {
                            vesicle1.resetNextPosition();
                            continue vesicleLoop;
                        }
                    }
                }
            }
            // check collisions with membranes
            if (simulation.getMembraneLayer() != null) {
                for (Membrane macroscopicMembrane : simulation.getMembraneLayer().getMembranes()) {
                    for (MembraneSegment membraneSegment : macroscopicMembrane.getSegments()) {
                        // do not check attached vesicles
                        // otherwise there is a problem when they are very close to membranes,
                        // resulting in them getting stuck
                        // they are considered "squeezed" by the membrane
                        if (vesicle1.getState().equals(VesicleStateRegistry.ACTIN_ATTACHED) ||
                                vesicle1.getState().equals(VesicleStateRegistry.MICROTUBULE_ATTACHED)) {
                            continue ;
                        }
                        // check if the circle representation with slightly bigger radius for numerical reasons
                        // of the next position intersects with any membrane segment
                        Circle nextRepresentation = new Circle(vesicle1.getNextPosition(), firstRadius+1);
                        if (Circles.intersect(nextRepresentation, membraneSegment)) {
                            vesicle1.resetNextPosition();
                            continue vesicleLoop;
                        }
                    }
                }
            }
            // check for collisions with confined volumes
            boolean exceedsConfinement = simulation.getModules().stream()
                    .filter(VesicleConfinedDiffusion.class::isInstance)
                    .map(VesicleConfinedDiffusion.class::cast)
                    .anyMatch(vesicleConfinedDiffusion -> {
                        // has the confining state
                        if (vesicle1.getState().equals(vesicleConfinedDiffusion.getConfiningState())) {
                            for (VolumeLikeAgent agent : simulation.getVolumeLayer().getAgents()) {
                                if (agent.getCellRegion().equals(vesicleConfinedDiffusion.getConfinedVolume())) {
                                    // and is not inside the volume
                                    return !agent.getArea().containsVector(vesicle1.getNextPosition());
                                }
                            }
                        }
                        return false;
                    });
            if (exceedsConfinement) {
                vesicle1.resetNextPosition();
                continue;
            }

            // check collisions with border
            double x = vesicle1.getNextPosition().getX();
            double y = vesicle1.getNextPosition().getY();
            if (x - firstRadius < simulationRegion.getLeftMostXPosition() || x + firstRadius > simulationRegion.getRightMostXPosition() ||
                    y - firstRadius < simulationRegion.getTopMostYPosition() || y + firstRadius > simulationRegion.getBottomMostYPosition()) {
                vesicle1.resetNextPosition();
            }
        }
    }



    public void associateVesicles() {
        // clear previous vesicle associations
        vesicles.forEach(Vesicle::clearAssociatedNodes);
        // associate vesicles to nodes
        vesicles.forEach(this::associateVesicle);
    }

    private void associateVesicle(Vesicle vesicle) {
        // convert vesicle from system to simulation scale
        Circle vesicleCircle = vesicle.getCircleRepresentation();
        double vesicleRadius = vesicleCircle.getRadius();
        Vector2D vesicleCentre = vesicleCircle.getMidpoint();
        // determine the node that contains the vesicle
        AutomatonGraph graph = simulation.getGraph();
        for (AutomatonNode node : graph.getNodes()) {
            // get representative region of the node
            Polygon polygon = node.getSpatialRepresentation();
            // associate vesicle to the node with the largest part of the vesicle (midpoint is inside)
            if (polygon.containsVector(vesicle.getPosition())) {
                // check if vesicle intersects with more than two regions at once
                for (Vector2D polygonVertex : polygon.getVertices()) {
                    // this is the case if the distance to the edge is smaller than the radius
                    if (vesicleCentre.distanceTo(polygonVertex) < vesicleRadius) {
                        Map<MooreRectangularDirection, Double> slices = Spheres.calculateSphereSlice(vesicleCentre, vesicleRadius, polygonVertex);
                        // get biggest slice, this is the representative node
                        MooreRectangularDirection coordinateDirection = null;
                        double biggestSurface = 0;
                        for (Map.Entry<MooreRectangularDirection, Double> entry : slices.entrySet()) {
                            if (entry.getValue() > biggestSurface) {
                                coordinateDirection = entry.getKey();
                                biggestSurface = entry.getValue();
                            }
                        }
                        if (coordinateDirection == null) {
//                            throw new IllegalStateException("Tried to associate vesicle " + vesicle + " with " + node + " but no areas could be determined.");
                            logger.warn("Tried to associate vesicle " + vesicle + " with " + node + " but no areas could be determined.");
                            return;
                        }
                        // assign other corresponding nodes to neighbors
                        for (Map.Entry<MooreRectangularDirection, Double> entry : slices.entrySet()) {
                            RectangularCoordinate neighbor = MooreRectangularDirection.getNeighborOf(node.getIdentifier(), coordinateDirection, entry.getKey());
                            if (neighbor.getRow() < 0 || neighbor.getColumn() < 0 || neighbor.getRow() > graph.getNumberOfRows()-1 || neighbor.getColumn() > graph.getNumberOfColumns()-1) {
                                continue;
                            }
                            vesicle.addAssociatedNode(graph.getNode(neighbor), entry.getValue());
                        }
                        // all neighbors have been associated
                        return;
                    }
                }
                // (else) check if vesicle intersects with exactly two regions
                double totalSurface = Spheres.calculateSurface(vesicleRadius);
                for (LineSegment polygonEdge : polygon.getEdges()) {
                    // this is the case if there are at least two intersections
                    Set<Vector2D> intersection = polygonEdge.getIntersectionWith(vesicleCircle);
                    if (intersection.size() > 1) {
                        Iterator<Vector2D> iterator = intersection.iterator();
                        LineSegment sliceSegment = new SimpleLineSegment(iterator.next(), iterator.next());
                        double sliceSurface = Spheres.calculateSphereSlice(vesicleCentre, vesicleRadius, sliceSegment) / totalSurface;
                        double remainingSurface = 1 - sliceSurface;
                        if (sliceSegment.isVertical()) {
                            if (sliceSegment.getStartingPoint().isLeftOf(node.getPosition())) {
                                vesicle.addAssociatedNode(node, remainingSurface);
                                if (node.getIdentifier().getColumn() - 1 > 0) {
                                    vesicle.addAssociatedNode(graph.getNode(node.getIdentifier().getNeighbour(NeumannRectangularDirection.WEST)), sliceSurface);
                                }
                            } else {
                                vesicle.addAssociatedNode(node, remainingSurface);
                                if (node.getIdentifier().getColumn() + 1 < graph.getNumberOfColumns()) {
                                    vesicle.addAssociatedNode(graph.getNode(node.getIdentifier().getNeighbour(NeumannRectangularDirection.EAST)), sliceSurface);
                                }
                            }
                        } else {
                            if (sliceSegment.getStartingPoint().isBelow(node.getPosition())) {
                                vesicle.addAssociatedNode(node, remainingSurface);
                                if (node.getIdentifier().getRow() + 1 < graph.getNumberOfRows()) {
                                    vesicle.addAssociatedNode(graph.getNode(node.getIdentifier().getNeighbour(NeumannRectangularDirection.SOUTH)), sliceSurface);
                                }
                            } else {
                                vesicle.addAssociatedNode(node, remainingSurface);
                                if (node.getIdentifier().getRow() - 1 > 0) {
                                    vesicle.addAssociatedNode(graph.getNode(node.getIdentifier().getNeighbour(NeumannRectangularDirection.NORTH)), sliceSurface);
                                }
                            }
                        }
                        return;
                    }
                }
                // else the vesicle if fully contained
                vesicle.addAssociatedNode(node, 1.0);
                return;
            }
        }
    }


    public void clearUpdates() {
        for (Vesicle vesicle : vesicles) {
            // vesicle.clearPotentialConcentrationDeltas();
            vesicle.clearPotentialDisplacementDeltas();
            vesicle.resetNextPosition();
        }
    }

    public void applyDeltas() {
        checkForCollisions();
        for (Vesicle vesicle : vesicles) {
            vesicle.clearPotentialDisplacementDeltas();
            vesicle.updatePosition();
        }
    }

}
