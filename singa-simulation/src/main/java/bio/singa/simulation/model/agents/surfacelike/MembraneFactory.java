package bio.singa.simulation.model.agents.surfacelike;

import bio.singa.core.utility.Pair;
import bio.singa.features.parameters.Environment;
import bio.singa.features.units.UnitRegistry;
import bio.singa.mathematics.algorithms.graphs.ShortestPathFinder;
import bio.singa.mathematics.algorithms.topology.FloodFill;
import bio.singa.mathematics.geometry.edges.LineSegment;
import bio.singa.mathematics.geometry.edges.SimpleLineSegment;
import bio.singa.mathematics.geometry.faces.ComplexPolygon;
import bio.singa.mathematics.geometry.faces.Polygons;
import bio.singa.mathematics.geometry.model.Polygon;
import bio.singa.mathematics.geometry.model.Polygon.IntersectionFragment;
import bio.singa.mathematics.graphs.model.*;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.mathematics.vectors.Vectors;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.CellSubsection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.Quantity;
import javax.measure.quantity.Area;
import javax.measure.quantity.Length;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class MembraneFactory {

    private static final Logger logger = LoggerFactory.getLogger(MembraneFactory.class);

    private Vector2D innerPoint;
    private Polygon polygon;
    private AutomatonGraph graph;
    private Map<Vector2D, CellRegion> regions;
    private Membrane membrane;

    private Map<AutomatonNode, UndirectedGraph> subsectionMapping;

    static Membrane createLinearMembrane(List<Vector2D> vectors, CellRegion innerRegion, CellRegion membraneRegion, Vector2D innerPoint, AutomatonGraph graph, Map<Vector2D, CellRegion> regions) {
        logger.info("Initializing linear membrane from {} vectors", vectors.size());
        MembraneFactory factory = new MembraneFactory(graph, regions);
        factory.innerPoint = innerPoint;
        factory.initializeMembrane(innerRegion, membraneRegion);
        List<LineSegment> segments = Vectors.connectToSegments(vectors);
        factory.associateToGraph(segments);
        factory.setupSubsectionRepresentations();
        factory.membrane.setRegionMap(factory.reconstructRegionMap());
        return factory.membrane;
    }

    public static Membrane createClosedMembrane(List<Vector2D> vectors, CellRegion innerRegion, CellRegion membraneRegion, AutomatonGraph graph, Map<Vector2D, CellRegion> regions) {
        logger.info("Initializing closed membrane from {} vectors", vectors.size());
        MembraneFactory factory = new MembraneFactory(graph, regions);
        factory.polygon = new ComplexPolygon(vectors);
        factory.innerPoint = factory.polygon.getCentroid();
        factory.initializeMembrane(innerRegion, membraneRegion);
        factory.associateToGraph(factory.polygon.getEdges());
        factory.fillInternalNodes();
        factory.setupSubsectionRepresentations();
        factory.membrane.setRegionMap(factory.reconstructRegionMap());
        return factory.membrane;
    }

    private MembraneFactory(AutomatonGraph graph, Map<Vector2D, CellRegion> regions) {
        this.graph = graph;
        this.regions = regions;
        subsectionMapping = new HashMap<>();
    }

    private void initializeMembrane(CellRegion innerRegion, CellRegion membraneRegion) {
        membrane = new Membrane(membraneRegion.getIdentifier());
        membrane.setInnerRegion(innerRegion);
        membrane.setMembraneRegion(membraneRegion);
        membrane.setInnerPoint(innerPoint);
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
            // determine and setup membrane cells
            for (LineSegment lineSegment : segments) {
                Vector2D startingPoint = lineSegment.getStartingPoint();
                Vector2D endingPoint = lineSegment.getEndingPoint();
                for (AutomatonNode node : graph.getNodes()) {

                    Polygon spatialRepresentation = node.getSpatialRepresentation();
                    boolean startIsInside = spatialRepresentation.isInside(startingPoint);
                    boolean endIsInside = spatialRepresentation.isInside(endingPoint);
                    List<IntersectionFragment> intersections = spatialRepresentation.getIntersectionFragments(lineSegment);
                    if (startIsInside && endIsInside) {
                        // completely internal
                        handleNoIntersection(node, startingPoint, endingPoint);
                    } else if (startIsInside && intersections.size() == 1) {
                        // start inside or on line
                        handleSingleIntersection(startingPoint, node, intersections.iterator().next());
                    } else if (endIsInside && intersections.size() == 1) {
                        // end inside or on line
                        handleSingleIntersection(endingPoint, node, intersections.iterator().next());
                    } else if (intersections.size() == 2) {
                        // line only crosses the membrane
                        initializeNodeSubsectionMapping(node, regions.get(startingPoint));
                        UndirectedGraph subsectionGraph = subsectionMapping.get(node);

                        Iterator<IntersectionFragment> iterator = intersections.iterator();
                        IntersectionFragment firstFragment = iterator.next();
                        IntersectionFragment secondFragment = iterator.next();

                        Vector2D firstIntersection = firstFragment.getIntersection();
                        Vector2D secondIntersection = secondFragment.getIntersection();

                        // TODO handle if first and second intersection are equal, the segment passes through a vertex

                        membrane.addSegment(node, new SimpleLineSegment(firstIntersection, secondIntersection));
                        RegularNode firstIntersectionNode = createIntersectionNode(subsectionGraph, firstFragment);
                        RegularNode secondIntersectionNode = createIntersectionNode(subsectionGraph, secondFragment);

                        subsectionGraph.addEdgeBetween(firstIntersectionNode, secondIntersectionNode);
                    }
                }
            }
        }
    }

    private void handleNoIntersection(AutomatonNode node, Vector2D startingPoint, Vector2D endingPoint) {
        initializeNodeSubsectionMapping(node, regions.get(startingPoint));
        UndirectedGraph subsectionGraph = subsectionMapping.get(node);
        membrane.addSegment(node, new SimpleLineSegment(startingPoint, endingPoint));
        RegularNode startingNode = subsectionGraph.addNodeIf(graphNode -> graphNode.getPosition().equals(startingPoint),
                new RegularNode(subsectionGraph.nextNodeIdentifier(), startingPoint));
        RegularNode endingNode = subsectionGraph.addNodeIf(graphNode -> graphNode.getPosition().equals(endingPoint),
                new RegularNode(subsectionGraph.nextNodeIdentifier(), endingPoint));
        subsectionGraph.addEdgeBetween(startingNode, endingNode);
    }

    private void handleSingleIntersection(Vector2D internalPoint, AutomatonNode node, IntersectionFragment intersectionFragment) {
        Vector2D intersection = intersectionFragment.getIntersection();
        if (!intersection.equals(internalPoint)) {
            initializeNodeSubsectionMapping(node, regions.get(internalPoint));
            UndirectedGraph subsectionGraph = subsectionMapping.get(node);
            membrane.addSegment(node, new SimpleLineSegment(internalPoint, intersection));
            // add the node created by the intersection but only if it does not already exist
            RegularNode intersectionNode = createIntersectionNode(subsectionGraph, intersectionFragment);
            // add the dangling node
            RegularNode internalNode = subsectionGraph.addNodeIf(graphNode -> graphNode.getPosition().equals(internalPoint),
                    new RegularNode(subsectionGraph.nextNodeIdentifier(), internalPoint));
            subsectionGraph.addEdgeBetween(internalNode, intersectionNode);
        } else {
            initializeNodeSubsectionMapping(node, regions.get(internalPoint));
            UndirectedGraph subsectionGraph = subsectionMapping.get(node);
            // add the node created by the intersection
            createIntersectionNode(subsectionGraph, intersectionFragment);
        }
    }

    private static RegularNode createIntersectionNode(UndirectedGraph subsectionGraph, IntersectionFragment fragment) {
        Vector2D intersection = fragment.getIntersection();
        // add the node created by the intersection but only if it does not already exist
        RegularNode firstIntersectionNode = subsectionGraph.addNodeIf(graphNode -> graphNode.getPosition().equals(intersection),
                new RegularNode(subsectionGraph.nextNodeIdentifier(), intersection));
        // reconnect intersection node
        Optional<RegularNode> optionalFirstStart = subsectionGraph.getNode(graphNode -> graphNode.getPosition().equals(fragment.getIntersectedStart()));
        Optional<RegularNode> optionalFirstEnd = subsectionGraph.getNode(graphNode -> graphNode.getPosition().equals(fragment.getIntersectedEnd()));
        if (optionalFirstStart.isPresent() && optionalFirstEnd.isPresent()) {
            // remove previous connections between original nodes
            RegularNode startNode = optionalFirstStart.get();
            RegularNode endNode = optionalFirstEnd.get();
            subsectionGraph.removeEdge(startNode, endNode);
            // add node connections
            subsectionGraph.addEdgeBetween(startNode, firstIntersectionNode);
            subsectionGraph.addEdgeBetween(firstIntersectionNode, endNode);
        }
        return firstIntersectionNode;
    }

    private void initializeNodeSubsectionMapping(AutomatonNode node, CellRegion region) {
        if (!subsectionMapping.containsKey(node)) {
            subsectionMapping.put(node, node.getSpatialRepresentation().toGraph());
            node.setCellRegion(region);
            node.getSubsectionRepresentations().clear();
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
                    break;
                }
            }
        } else {
            for (AutomatonNode node : graph.getNodes()) {
                // use given internal point to determine inner subsection
                if (node.getSubsectionRepresentations().isEmpty() && node.getCellRegion().equals(membrane.getMembraneRegion())) {
                    node.getSpatialRepresentation().isInside(innerPoint);
                    break;
                }
            }
        }

        if (startingNode != null) {
            // use flood fill algorithm
            FloodFill.fill(graph.getGrid(), startingNode.getIdentifier(),
                    currentNode -> currentNode.getCellRegion().equals(membrane.getMembraneRegion()),
                    rectangularCoordinate -> {
                        AutomatonNode currentNode = graph.getNode(rectangularCoordinate);
                        currentNode.setCellRegion(membrane.getInnerRegion());
                        currentNode.getSubsectionRepresentations().clear();
                        currentNode.addSubsectionRepresentation(membrane.getMembraneRegion().getInnerSubsection(), currentNode.getSpatialRepresentation());
                    },
                    recurrentNode -> recurrentNode.getCellRegion().equals(membrane.getInnerRegion()));
        }

    }

    private void setupSubsectionRepresentations() {
        for (Map.Entry<AutomatonNode, UndirectedGraph> entry : subsectionMapping.entrySet()) {
            AutomatonNode node = entry.getKey();
            UndirectedGraph representationGraph = node.getSpatialRepresentation().toGraph();
            List<RegularNode> innerNodeList = entry.getValue().getNodes().stream()
                    .filter(subsectionNode -> !representationGraph.containsNode(representationNode -> representationNode.getPosition().equals(subsectionNode.getPosition())))
                    .collect(Collectors.toList());

            Iterator<RegularNode> iterator = innerNodeList.iterator();
            // get starting point
            RegularNode firstNode = iterator.next();
            RegularNode currentNode = firstNode;
            RegularNode targetNode = null;
            // get other node that is also in the inner section
            for (RegularNode neighbour : currentNode.getNeighbours()) {
                if (innerNodeList.contains(neighbour)) {
                    targetNode = neighbour;
                    break;
                }
            }
            List<RegularNode> circleNodes = new ArrayList<>();
            circleNodes.add(currentNode);
            entry.getValue().removeEdge(currentNode, targetNode);
            if (targetNode == null) {
                throw new IllegalStateException("Unable to crate subsection, no neighbouring internal nodes");
            }
            do {
                // determine next on path
                List<RegularNode> neighbours = currentNode.getNeighbours();
                RegularNode closestNeighbour = null;
                double closestNeighbourDistance = Double.MAX_VALUE;
                for (RegularNode neighbour : neighbours) {
                    if (!circleNodes.contains(neighbour)) {
                        double currentDistance = neighbour.getPosition().distanceTo(innerPoint);
                        if (currentDistance < closestNeighbourDistance) {
                            closestNeighbourDistance = currentDistance;
                            closestNeighbour = neighbour;
                        }
                    }
                }
                circleNodes.add(closestNeighbour);
                currentNode = closestNeighbour;
            } while (!currentNode.equals(targetNode));
            List<Vector2D> innerVectors = circleNodes.stream()
                    .map(Node::getPosition)
                    .collect(Collectors.toList());
            node.addSubsectionRepresentation(membrane.getMembraneRegion().getInnerSubsection(), new ComplexPolygon(innerVectors));

            // get last node (probably be last added intersection)
            circleNodes.clear();
            currentNode = firstNode;
            circleNodes.add(currentNode);
            do {
                // determine next on path
                List<RegularNode> neighbours = currentNode.getNeighbours();
                RegularNode farthestNeighbour = null;
                double farthestNeighbourDistance = -Double.MAX_VALUE;
                for (RegularNode neighbour : neighbours) {
                    if (!circleNodes.contains(neighbour)) {
                        double currentDistance = neighbour.getPosition().distanceTo(innerPoint);
                        if (currentDistance > farthestNeighbourDistance) {
                            farthestNeighbourDistance = currentDistance;
                            farthestNeighbour = neighbour;
                        }
                    }
                }
                circleNodes.add(farthestNeighbour);
                currentNode = farthestNeighbour;
            } while (!currentNode.equals(targetNode));
            List<Vector2D> outerVectors = circleNodes.stream()
                    .map(Node::getPosition)
                    .collect(Collectors.toList());
            node.addSubsectionRepresentation(membrane.getMembraneRegion().getOuterSubsection(), new ComplexPolygon(outerVectors));

        }
    }

    private Membrane associateOneNodeGraph(List<LineSegment> segments) {
        AutomatonNode node = graph.getNodes().iterator().next();
        for (LineSegment lineSegment : segments) {
            membrane.addSegment(node, lineSegment);
        }
        node.setCellRegion(membrane.getMembraneRegion());
        return membrane;
    }

    public static void handleMembraneGaps(AutomatonGraph graph) {
        // sorry for this method
        // ideally the subsections should be stored in double edge data structure
        for (AutomatonNode node : graph.getNodes()) {
            List<MembraneSegment> danglingSubsectionPositions = new ArrayList<>();
            // determine if any gap is present
            for (Map.Entry<CellSubsection, Polygon> cellSubsectionPolygonEntry : node.getSubsectionRepresentations().entrySet()) {
                if (cellSubsectionPolygonEntry.getValue().getVertices().size() == 2) {
                    logger.debug("detected subsection with non-spatial representation");
                    danglingSubsectionPositions.addAll(node.getMembraneSegments());
                    break;
                }
            }
            // if there are dangling ends
            if (!danglingSubsectionPositions.isEmpty()) {
                Polygon spatialRepresentation = node.getSpatialRepresentation();
                UndirectedGraph connectionGraph = spatialRepresentation.toGraph();
                // if there are exactly two ends
                if (danglingSubsectionPositions.size() == 2) {
                    List<RegularNode> internalNodes = new ArrayList<>();
                    // recreate the connection graph
                    for (MembraneSegment membraneSegment : danglingSubsectionPositions) {
                        Vector2D onLineVector = null;
                        Vector2D internalVector = null;
                        for (LineSegment lineSegment : spatialRepresentation.getEdges()) {
                            if (lineSegment.isOnLine(membraneSegment.getStartingPoint())) {
                                onLineVector = membraneSegment.getStartingPoint();
                                internalVector = membraneSegment.getEndingPoint();
                            } else if (lineSegment.isOnLine(membraneSegment.getEndingPoint())) {
                                onLineVector = membraneSegment.getEndingPoint();
                                internalVector = membraneSegment.getStartingPoint();
                            }
                            if (onLineVector != null) {
                                Vector2D internalPoint = internalVector;
                                // add the node created by the intersection but only if it does not already exist
                                RegularNode intersectionNode = createIntersectionNode(connectionGraph, new IntersectionFragment(onLineVector, lineSegment.getStartingPoint(), lineSegment.getEndingPoint()));
                                // add the dangling node
                                RegularNode internalNode = connectionGraph.addNodeIf(graphNode -> graphNode.getPosition().equals(internalPoint),
                                        new RegularNode(connectionGraph.nextNodeIdentifier(), internalPoint));
                                internalNodes.add(internalNode);
                                connectionGraph.addEdgeBetween(internalNode, intersectionNode);
                                break;
                            }
                        }
                    }
                    // connect internals
                    RegularNode firstInternal = internalNodes.get(0);
                    RegularNode secondInternal = internalNodes.get(1);
                    // connectionGraph.addEdgeBetween(firstInternal, secondInternal);

                    // split into two subsections
                    Graph<RegularNode, UndirectedEdge, Integer> workingCopy = connectionGraph.getCopy();
                    // the first cycle
                    GraphPath<RegularNode, UndirectedEdge> firstPath = ShortestPathFinder.findBasedOnPredicate(connectionGraph, firstInternal, predicateNode -> predicateNode.equals(secondInternal));
                    // remove the external nodes from the working copy
                    for (RegularNode pathNode : firstPath.getNodes()) {
                        if (spatialRepresentation.getVertices().contains(pathNode.getPosition())) {
                            workingCopy.removeNode(pathNode.getIdentifier());
                        }
                    }
                    // get other path
                    GraphPath<RegularNode, UndirectedEdge> secondPath = ShortestPathFinder.findBasedOnPredicate(workingCopy, workingCopy.getNode(firstInternal.getIdentifier()), predicateNode -> predicateNode.getIdentifier().equals(secondInternal.getIdentifier()));

                    // add subsections
                    node.getSubsectionRepresentations().clear();
                    // create first polygon
                    Polygon firstPolygon = new ComplexPolygon(firstPath.getNodes().stream().map(RegularNode::getPosition).collect(Collectors.toList()));
                    CellSubsection firstSubsection = setSubsectionByAdjacency(node, firstPolygon);
                    Polygon secondPolygon = new ComplexPolygon(secondPath.getNodes().stream().map(RegularNode::getPosition).collect(Collectors.toList()));
                    CellSubsection secondSubsection = setSubsectionByAdjacency(node, secondPolygon);

                    // calculate diffusive area
                    Quantity<Length> firstSide = Environment.convertSimulationToSystemScale(firstPolygon.getCentroid().distanceTo(secondPolygon.getCentroid()));
                    Quantity<Length> secondSide = Environment.convertSimulationToSystemScale(firstInternal.getPosition().distanceTo(secondInternal.getPosition()));
                    double relativeArea = firstSide.multiply(secondSide).asType(Area.class).divide(UnitRegistry.getArea()).getValue().doubleValue();

                    // add symmetric adjacency
                    AutomatonNode.AreaMapping firstMapping = new AutomatonNode.AreaMapping(node, secondSubsection, relativeArea);
                    List<AutomatonNode.AreaMapping> firstMappings = new ArrayList<>();
                    firstMappings.add(firstMapping);
                    node.getSubsectionAdjacency().put(firstSubsection, firstMappings);

                    AutomatonNode.AreaMapping secondMapping = new AutomatonNode.AreaMapping(node, firstSubsection, relativeArea);
                    List<AutomatonNode.AreaMapping> secondMappings = new ArrayList<>();
                    secondMappings.add(secondMapping);
                    node.getSubsectionAdjacency().put(secondSubsection, secondMappings);

                }
            }
        }


    }

    public static CellSubsection setSubsectionByAdjacency(AutomatonNode node, Polygon firstPolygon) {
        // find adjacent subsections
        for (AutomatonNode neighbour : node.getNeighbours()) {
            for (Map.Entry<CellSubsection, Polygon> cellSubsectionPolygonEntry : neighbour.getSubsectionRepresentations().entrySet()) {
                Map<Pair<LineSegment>, LineSegment> touchingLineSegments = Polygons.getTouchingLineSegments(firstPolygon, cellSubsectionPolygonEntry.getValue());
                if (!touchingLineSegments.isEmpty()) {
                    // setup subsection
                    node.getSubsectionRepresentations().put(cellSubsectionPolygonEntry.getKey(), firstPolygon);
                    return cellSubsectionPolygonEntry.getKey();
                }
            }
        }
        return null;
    }


}

