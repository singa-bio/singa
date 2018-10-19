package bio.singa.simulation.model.agents.membranes;

import bio.singa.mathematics.algorithms.geometry.SutherandHodgmanClipping;
import bio.singa.mathematics.algorithms.topology.FloodFill;
import bio.singa.mathematics.geometry.edges.LineSegment;
import bio.singa.mathematics.geometry.edges.SimpleLineSegment;
import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.geometry.faces.VertexPolygon;
import bio.singa.mathematics.geometry.model.Polygon;
import bio.singa.mathematics.topology.grids.rectangular.RectangularDirection;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.sections.CellRegion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static bio.singa.mathematics.geometry.model.Polygon.*;
import static bio.singa.mathematics.metrics.model.VectorMetricProvider.EUCLIDEAN_METRIC;

/**
 * @author cl
 */
public class MembraneFactory {

    private static final Logger logger = LoggerFactory.getLogger(MembraneFactory.class);

    private Collection<Vector2D> membraneVectors;
    private Polygon polygon;
    private RectangularDirection direction;
    private AutomatonGraph graph;
    private Map<Vector2D, CellRegion> regions;
    private Membrane membrane;

    public static Membrane createLinearMembrane(Collection<Vector2D> vectors, CellRegion innerRegion, CellRegion membraneRegion, RectangularDirection innerDirection, AutomatonGraph graph, Map<Vector2D, CellRegion> regions, Rectangle globalClipper) {
        MembraneFactory factory = new MembraneFactory(vectors, graph, regions);
        factory.direction = innerDirection;
        factory.initializeMembrane(innerRegion, membraneRegion);
        List<Vector2D> sortedVectors = factory.sortLinearVectors();
        List<LineSegment> segments = factory.connectLinearVectors(sortedVectors);
        factory.associateToGraph(segments);
        factory.fillInternalNodes();
        factory.createPolygonForLinearMembrane(globalClipper, sortedVectors);
        factory.setupSubsectionRepresentations(factory.polygon);
        return factory.membrane;
    }

    public static Membrane createClosedMembrane(Collection<Vector2D> vectors, CellRegion innerRegion, CellRegion membraneRegion, AutomatonGraph graph, Map<Vector2D, CellRegion> regions) {
        MembraneFactory factory = new MembraneFactory(vectors, graph, regions);
        factory.initializeMembrane(innerRegion, membraneRegion);
        factory.polygon = factory.connectPolygonVectors(vectors);
        factory.associateToGraph(factory.polygon.getEdges());
        factory.fillInternalNodes();
        factory.setupSubsectionRepresentations(factory.polygon);
        return factory.membrane;
    }

    public MembraneFactory(Collection<Vector2D> membraneVectors, AutomatonGraph graph, Map<Vector2D, CellRegion> regions) {
        this.membraneVectors = membraneVectors;
        this.graph = graph;
        this.regions = regions;
    }

    private void initializeMembrane(CellRegion innerRegion, CellRegion membraneRegion) {
        logger.info("Initializing membrane.");
        membrane = new Membrane(membraneRegion.getIdentifier());
        membrane.setInnerRegion(innerRegion);
        membrane.setMembraneRegion(membraneRegion);
    }

    private List<Vector2D> sortLinearVectors() {
        TreeSet<Vector2D> sortedCopy = new TreeSet<>(Comparator.comparingDouble(Vector2D::getY).thenComparing(Vector2D::getX));
        sortedCopy.addAll(membraneVectors);
        final Vector2D first = sortedCopy.iterator().next();
        List<Vector2D> copy = new ArrayList<>(membraneVectors);
        List<Vector2D> result = new ArrayList<>();
        result.add(first);
        copy.remove(first);
        Vector2D previous = first;
        // for each vector (and omit last connection)
        while (copy.size() > 1) {
            // determine closest neighbour
            Map.Entry<Vector2D, Double> entry = EUCLIDEAN_METRIC.calculateClosestDistance(copy, previous);
            // add line segment
            Vector2D next = entry.getKey();
            result.add(next);
            copy.remove(next);
            previous = next;
        }
        return result;
    }

    private List<LineSegment> connectLinearVectors(List<Vector2D> vectors) {
        Iterator<Vector2D> iterator = vectors.iterator();
        Vector2D previous = iterator.next();
        List<LineSegment> lineSegments = new ArrayList<>();
        while (iterator.hasNext()) {
            Vector2D next = iterator.next();
            lineSegments.add(new SimpleLineSegment(previous, next));
            previous = next;
        }
        return lineSegments;
    }


    private Polygon connectPolygonVectors(Collection<Vector2D> vectors) {
        return new VertexPolygon(vectors);
    }

    /**
     * Associates membranes to their nodes and nodes to their membranes. This means regions are set for each node
     * depending on the membrane the the region associated to each membrane segment.
     *
     * @param segments the segments
     */
    private void associateToGraph(List<LineSegment> segments) {
        if (graph.getNodes().size() == 1) {
            // graph has only one node
            membrane = associateOneNodeGraph(segments);
        } else {
            // graph has multiple nodes
            // flag if all segments are contained in a single node
            boolean isContained = true;
            // determine and setup membrane cells
            for (LineSegment lineSegment : segments) {
                Vector2D startingPoint = lineSegment.getStartingPoint();
                Vector2D endingPoint = lineSegment.getEndingPoint();
                for (AutomatonNode node : graph.getNodes()) {
                    Polygon spatialRepresentation = node.getSpatialRepresentation();
                    // evaluate line segment (-1 outside, 0 on line, 1 inside)
                    int startingPosition = spatialRepresentation.evaluatePointPosition(startingPoint);
                    int endingPosition = spatialRepresentation.evaluatePointPosition(endingPoint);
                    Set<Vector2D> intersections = spatialRepresentation.getIntersections(lineSegment);
                    if (startingPosition >= ON_LINE && endingPosition >= ON_LINE) {
                        // completely inside
                        membrane.addSegment(node, lineSegment);
                        node.setCellRegion(regions.get(startingPoint));
                        break;
                    } else if (startingPosition == INSIDE && endingPosition == OUTSIDE) {
                        // end outside or on line
                        Vector2D intersectionPoint = intersections.iterator().next();
                        membrane.addSegment(node, new SimpleLineSegment(startingPoint, intersectionPoint));
                        node.setCellRegion(regions.get(startingPoint));
                        isContained = false;
                    } else if (startingPosition == OUTSIDE && endingPosition == INSIDE) {
                        // start outside or on line
                        Vector2D intersectionPoint = intersections.iterator().next();
                        membrane.addSegment(node, new SimpleLineSegment(intersectionPoint, endingPoint));
                        node.setCellRegion(regions.get(startingPoint));
                        isContained = false;
                    } else if (intersections.size() == 2) {
                        // line only crosses the membrane
                        Iterator<Vector2D> iterator = intersections.iterator();
                        Vector2D first = iterator.next();
                        Vector2D second = iterator.next();
                        membrane.addSegment(node, new SimpleLineSegment(first, second));
                        node.setCellRegion(regions.get(startingPoint));
                        isContained = false;
                    }
                }
            }

            if (isContained) {
                membrane = associateContainedMembrane();
            }
        }
    }

    private void fillInternalNodes() {
        // TODO maybe this can be improved
        Set<CellRegion> region = new HashSet<>(regions.values());
        // fill region inside of the organelle
        AutomatonNode startingNode = null;
        if (polygon != null) {
            // if this is a closed membrane section
            for (AutomatonNode node : graph.getNodes()) {
                // determine cell that is completely inside of the membrane as starting point
                // therefore check if all segments of the representative region are inside
                boolean allPointsAreInside = true;
                for (Vector2D vector : node.getSpatialRepresentation().getVertices()) {
                    if (polygon.evaluatePointPosition(vector) == OUTSIDE) {
                        allPointsAreInside = false;
                        break;
                    }
                }
                if (allPointsAreInside) {
                    startingNode = node;
                }
            }
        } else {
            for (AutomatonNode node : graph.getNodes()) {
                if (node.getSubsectionRepresentations().isEmpty() && node.getCellRegion().equals(membrane.getMembraneRegion())) {
                    startingNode = graph.getNode(node.getIdentifier().getNeighbour(direction));
                    break;
                }
            }
        }

        if (startingNode != null) {
            // use flood fill algorithm
            FloodFill.fill(graph.getGrid(), startingNode.getIdentifier(),
                    currentNode -> currentNode.getCellRegion().equals(membrane.getMembraneRegion()) || region.contains(currentNode.getCellRegion()),
                    rectangularCoordinate -> graph.getNode(rectangularCoordinate).setCellRegion(membrane.getInnerRegion()),
                    recurrentNode -> recurrentNode.getCellRegion().equals(membrane.getInnerRegion()));
        }

    }

    private void createPolygonForLinearMembrane(Rectangle globalClipper, List<Vector2D> sortedVectors) {
        List<Vector2D> vectors = new ArrayList<>();
        switch (direction) {
            case NORTH:
                vectors.add(globalClipper.getTopLeftVertex());
                vectors.add(globalClipper.getTopRightVertex());
                break;
            case SOUTH:
                vectors.add(globalClipper.getBottomLeftVertex());
                vectors.add(globalClipper.getBottomRightVertex());
                break;
            case EAST:
                vectors.add(globalClipper.getBottomRightVertex());
                vectors.add(globalClipper.getTopRightVertex());
                break;
            case WEST:
                vectors.add(globalClipper.getBottomLeftVertex());
                vectors.add(globalClipper.getTopLeftVertex());
                break;
        }
        vectors.addAll(sortedVectors);
        polygon = new VertexPolygon(vectors, false);
    }

    private void setupSubsectionRepresentations(Polygon polygon) {
        // TODO maybe this can be improved
        Set<CellRegion> region = new HashSet<>(regions.values());
        // setup subsection representation for nodes compartmentalized by membranes
        for (AutomatonNode node : graph.getNodes()) {
            if (clippingCondition(region, node)) {
                // use sutherland hodgman to clip inner region
                Polygon nodePolygon = node.getSpatialRepresentation();
                Polygon innerPolygon = SutherandHodgmanClipping.clip(polygon, nodePolygon);
                node.addSubsectionRepresentation(membrane.getMembraneRegion().getInnerSubsection(), innerPolygon);
            }
        }
    }

    private boolean clippingCondition(Set<CellRegion> regions, AutomatonNode node) {
        return node.getSubsectionRepresentations().isEmpty() &&
                (node.getCellRegion().equals(membrane.getMembraneRegion()) || regions.contains(node.getCellRegion()));
    }

    private Membrane associateOneNodeGraph(List<LineSegment> segments) {
        AutomatonNode node = graph.getNodes().iterator().next();
        for (LineSegment lineSegment : segments) {
            membrane.addSegment(node, lineSegment);
        }
        node.setCellRegion(membrane.getMembraneRegion());
        return membrane;
    }

    private Membrane associateContainedMembrane() {
        // setup subsection representation for contained organelles
        AutomatonNode containingNode = membrane.getSegments().iterator().next().getNode();
        containingNode.addSubsectionRepresentation(membrane.getMembraneRegion().getInnerSubsection(), polygon);
        return membrane;
    }

}

