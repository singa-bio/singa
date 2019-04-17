package bio.singa.simulation.model.agents.surfacelike;

import bio.singa.mathematics.algorithms.geometry.SutherandHodgmanClipping;
import bio.singa.mathematics.algorithms.topology.FloodFill;
import bio.singa.mathematics.geometry.edges.LineSegment;
import bio.singa.mathematics.geometry.edges.SimpleLineSegment;
import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.geometry.faces.VertexPolygon;
import bio.singa.mathematics.geometry.model.Polygon;
import bio.singa.mathematics.topology.grids.rectangular.NeumannRectangularDirection;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.mathematics.vectors.Vectors;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.sections.CellRegion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static bio.singa.mathematics.topology.grids.rectangular.NeumannRectangularDirection.*;

/**
 * @author cl
 */
public class MembraneFactory {

    private static final Logger logger = LoggerFactory.getLogger(MembraneFactory.class);

    private Collection<Vector2D> membraneVectors;
    private Polygon polygon;
    private NeumannRectangularDirection direction;
    private AutomatonGraph graph;
    private Map<Vector2D, CellRegion> regions;
    private Membrane membrane;

    static Membrane createLinearMembrane(Collection<Vector2D> vectors, CellRegion innerRegion, CellRegion membraneRegion, NeumannRectangularDirection innerDirection, AutomatonGraph graph, Map<Vector2D, CellRegion> regions, Rectangle globalClipper, boolean isSorted) {
        logger.info("Initializing linear membrane from {} vectors", vectors.size());
        MembraneFactory factory = new MembraneFactory(vectors, graph, regions);
        factory.direction = innerDirection;
        factory.initializeMembrane(innerRegion, membraneRegion);
        factory.membrane.setInnerDirection(innerDirection);
        NeumannRectangularDirection vectorDirection;
        if (innerDirection == NORTH || innerDirection == SOUTH) {
            vectorDirection = WEST;
        } else {
            vectorDirection = NORTH;
        }
        List<Vector2D> sortedVectors;
        if (isSorted) {
            sortedVectors = new ArrayList<>(vectors);
        } else {
            sortedVectors = Vectors.sortByCloseness(factory.membraneVectors, vectorDirection);
        }
        List<LineSegment> segments = Vectors.connectToSegments(sortedVectors);
        factory.associateToGraph(segments);
        if (graph.getNodes().size() > 1) {
            factory.fillInternalNodes();
        }
        factory.createPolygonForLinearMembrane(globalClipper, sortedVectors);
        factory.setupSubsectionRepresentations(factory.polygon);
        factory.membrane.setRegionMap(factory.reconstructRegionMap());
        return factory.membrane;
    }

    public static Membrane createClosedMembrane(Collection<Vector2D> vectors, CellRegion innerRegion, CellRegion membraneRegion, AutomatonGraph graph, Map<Vector2D, CellRegion> regions) {
        logger.info("Initializing closed membrane from {} vectors", vectors.size());
        MembraneFactory factory = new MembraneFactory(vectors, graph, regions);
        factory.initializeMembrane(innerRegion, membraneRegion);
        factory.polygon = factory.connectPolygonVectors(vectors);
        factory.associateToGraph(factory.polygon.getEdges());
        factory.fillInternalNodes();
        factory.setupSubsectionRepresentations(factory.polygon);
        factory.membrane.setRegionMap(factory.reconstructRegionMap());
        return factory.membrane;
    }

    private MembraneFactory(Collection<Vector2D> membraneVectors, AutomatonGraph graph, Map<Vector2D, CellRegion> regions) {
        this.membraneVectors = membraneVectors;
        this.graph = graph;
        this.regions = regions;
    }

    private void initializeMembrane(CellRegion innerRegion, CellRegion membraneRegion) {
        membrane = new Membrane(membraneRegion.getIdentifier());
        membrane.setInnerRegion(innerRegion);
        membrane.setMembraneRegion(membraneRegion);
    }

    private Map<CellRegion, Set<Vector2D>> reconstructRegionMap() {
        Map<CellRegion, Set<Vector2D>> regionMap = new HashMap<>();
        for (Map.Entry<Vector2D, CellRegion> entry : regions.entrySet()) {
            if (!regionMap.containsKey(entry.getValue())) {
                regionMap.put(entry.getValue(), new HashSet<>());
            }
            regionMap.get(entry.getValue()).add(entry.getKey());
        }
        return regionMap;
    }

    private Polygon connectPolygonVectors(Collection<Vector2D> vectors) {
        return new VertexPolygon(vectors);
    }

    /**
     * Associates membranes to their nodes and nodes to their membranes. This means regions are set for each node
     * depending on the membrane the the region associated to each membrane segment.
     *
     * @param segments the path
     */
    private void associateToGraph(List<LineSegment> segments) {
        if (graph.getNodes().size() == 1) {
            // graph has only one node
            membrane = associateOneNodeGraph(segments);
        } else {
            // graph has multiple nodes
            // flag if all path are contained in a single node
            boolean isContained = true;
            // determine and setup membrane cells
            for (LineSegment lineSegment : segments) {
                Vector2D startingPoint = lineSegment.getStartingPoint();
                Vector2D endingPoint = lineSegment.getEndingPoint();
                for (AutomatonNode node : graph.getNodes()) {
                    Polygon spatialRepresentation = node.getSpatialRepresentation();
                    boolean startIsInside = spatialRepresentation.isInside(startingPoint);
                    boolean endIsInside = spatialRepresentation.isInside(endingPoint);
                    Set<Vector2D> intersections = spatialRepresentation.getIntersections(lineSegment);
                    if (startIsInside && endIsInside) {
                        // completely inside
                        membrane.addSegment(node, lineSegment);
                        node.setCellRegion(regions.get(startingPoint));
                        break;
                    } else if (startIsInside && intersections.size() != 2) {
                        // end outside or on line
                        Vector2D intersectionPoint = intersections.iterator().next();
                        if (!intersectionPoint.equals(startingPoint)) {
                            membrane.addSegment(node, new SimpleLineSegment(startingPoint, intersectionPoint));
                            node.setCellRegion(regions.get(startingPoint));
                            isContained = false;
                        }
                    } else if (endIsInside && intersections.size() != 2) {
                        // start outside or on line
                        Vector2D intersectionPoint = intersections.iterator().next();
                        if (!intersectionPoint.equals(endingPoint)) {
                            membrane.addSegment(node, new SimpleLineSegment(intersectionPoint, endingPoint));
                            node.setCellRegion(regions.get(startingPoint));
                            isContained = false;
                        }
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
                // therefore check if all path of the representative region are inside
                boolean allPointsAreInside = true;
                for (Vector2D vector : node.getSpatialRepresentation().getVertices()) {
                    // break is any point is not inside
                    if (!polygon.isInside(vector)) {
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

