package bio.singa.simulation.model.modules.macroscopic.membranes;

import bio.singa.mathematics.algorithms.geometry.SutherandHodgmanClipping;
import bio.singa.mathematics.algorithms.topology.FloodFill;
import bio.singa.mathematics.geometry.edges.LineSegment;
import bio.singa.mathematics.geometry.edges.SimpleLineSegment;
import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.geometry.model.Polygon;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.macroscopic.organelles.Organelle;
import bio.singa.simulation.model.sections.CellRegion;

import java.util.*;

import static bio.singa.mathematics.geometry.model.Polygon.*;

/**
 * @author cl
 */
public class MembraneTracer {

    // input
    private HashMap<CellRegion, List<AutomatonNode>> regionNodeMapping;
    private AutomatonGraph graph;

    // output
    private List<Membrane> membranes;

    // working
    private LinkedList<AutomatonNode> currentNodes;
    private Deque<AutomatonNode> queue;
    private List<AutomatonNode> unprocessedNodes;

    public static Membrane membraneToRegion(Organelle organelle, AutomatonGraph graph, Rectangle simulationExtend) {
        Membrane membrane = new Membrane(organelle.getMembraneRegion().getIdentifier(), organelle.getMembraneRegion());
        Polygon organellePolygon = organelle.getPolygon();

        // check if all segments are contained in a single node
        boolean isContained = true;

        // determine membrane cells
        for (LineSegment lineSegment : organellePolygon.getEdges()) {
            Vector2D startingPoint = lineSegment.getStartingPoint();
            Vector2D endingPoint = lineSegment.getEndingPoint();
            for (AutomatonNode node : graph.getNodes()) {
                Polygon spatialRepresentation = node.getSpatialRepresentation();
                // evaluate line segment
                int startingPosition = spatialRepresentation.evaluatePointPosition(startingPoint);
                int endingPosition = spatialRepresentation.evaluatePointPosition(endingPoint);
                Set<Vector2D> intersections = spatialRepresentation.getIntersections(lineSegment);
                if (startingPosition >= ON_LINE && endingPosition >= ON_LINE) {
                    // completely inside
                    membrane.addSegment(node, lineSegment);
                    node.setCellRegion(organelle.getMembraneRegion());
                    break;
                } else if (startingPosition == INSIDE && endingPosition == OUTSIDE) {
                    // end outside or on line
                    Vector2D intersectionPoint = intersections.iterator().next();
                    membrane.addSegment(node, new SimpleLineSegment(startingPoint, intersectionPoint));
                    node.setCellRegion(organelle.getMembraneRegion());
                    isContained = false;
                } else if (startingPosition == OUTSIDE && endingPosition == INSIDE) {
                    // start outside or on line
                    Vector2D intersectionPoint = intersections.iterator().next();
                    membrane.addSegment(node, new SimpleLineSegment(intersectionPoint, endingPoint));
                    node.setCellRegion(organelle.getMembraneRegion());
                    isContained = false;
                } else if (intersections.size() == 2) {
                    // line only crosses the membrane
                    Iterator<Vector2D> iterator = intersections.iterator();
                    Vector2D first = iterator.next();
                    Vector2D second = iterator.next();
                    membrane.addSegment(node, new SimpleLineSegment(first, second));
                    node.setCellRegion(organelle.getMembraneRegion());
                    isContained = false;
                }
            }
        }

        if (isContained) {
            AutomatonNode containingNode = membrane.getSegments().iterator().next().getNode();
            containingNode.addSubsectionRepresentation(organelle.getMembraneRegion().getInnerSubsection(), organellePolygon);
            return membrane;
        }

        // determine cell that is completely inside of the membrane as starting point
        for (AutomatonNode node : graph.getNodes()) {
            // therefore check if all segments of the representative region are inside
            boolean allPointsAreIside = true;
            for (Vector2D vector : node.getSpatialRepresentation().getVertices()) {
                if (organellePolygon.evaluatePointPosition(vector) == OUTSIDE) {
                    allPointsAreIside = false;
                    break;
                }
            }
            if (allPointsAreIside) {
                FloodFill.fill(graph.getGrid(), node.getIdentifier(),
                        currentNode -> currentNode.getCellRegion().equals(organelle.getMembraneRegion()),
                        currentCoordiante -> graph.getNode(currentCoordiante).setCellRegion(organelle.getInternalRegion()),
                        reccurentNode -> reccurentNode.getCellRegion().equals(organelle.getInternalRegion()));
                break;
            }
        }


        for (AutomatonNode automatonNode : graph.getNodes()) {
            if (automatonNode.getCellRegion().equals(organelle.getMembraneRegion())) {

                // inner
                Polygon nodePolygon = automatonNode.getSpatialRepresentation();
                Polygon innerPolygon = SutherandHodgmanClipping.clip(organellePolygon, nodePolygon);
                automatonNode.addSubsectionRepresentation(organelle.getMembraneRegion().getInnerSubsection(), innerPolygon);
                // outer
//                List<Vector2D> outerVectors = new ArrayList<>();
//                List<Vector2D> insideNode = new ArrayList<>();
//                for (Vector2D polygonVertex : innerPolygon.getVertices()) {
//                    boolean differentToAll = true;
//                    for (Vector2D nodeVertex : nodePolygon.getVertices()) {
//                        if (polygonVertex.isSimilarTo(nodeVertex, 1e-6)) {
//                            insideNode.add(nodeVertex);
//                            differentToAll = false;
//                        }
//                    }
//                    if (differentToAll) {
//                        outerVectors.add(polygonVertex);
//                    }
//                }
//
//
//                for (Vector2D nodeVertex : nodePolygon.getVertices()) {
//                    if (!insideNode.contains(nodeVertex)) {
//                        outerVectors.add(nodeVertex);
//                    }
//                }
//                // FIXME some subsections are not calculated correctly
//                automatonNode.addSubsectionRepresentation(organelle.getMembraneRegion().getOuterSubsection(), new VertexPolygon(outerVectors));
            }
        }
        return membrane;
    }

    public static List<Membrane> regionsToMembrane(AutomatonGraph graph) {
        MembraneTracer composer = new MembraneTracer(graph);
        return composer.membranes;
    }

    public MembraneTracer(AutomatonGraph graph) {
        this.graph = graph;
        currentNodes = new LinkedList<>();
        queue = new ArrayDeque<>();
        unprocessedNodes = new ArrayList<>();
        membranes = new ArrayList<>();
        initializeRegionNodeMapping();
        for (CellRegion cellRegion : regionNodeMapping.keySet()) {
            while (!regionNodeMapping.get(cellRegion).isEmpty()) {
                traverseRegion(cellRegion);
                currentNodes.clear();
                queue.clear();
            }
        }
    }

    private void initializeRegionNodeMapping() {
        // get different regions and associate nodes
        regionNodeMapping = new HashMap<>();
        for (AutomatonNode node : graph.getNodes()) {
            // initialize region if it contains a membrane and it is not already present
            CellRegion cellRegion = node.getCellRegion();
            if (!regionNodeMapping.containsKey(cellRegion) && cellRegion.hasMembrane()) {
                regionNodeMapping.put(cellRegion, new ArrayList<>());
            }
            // add node if it has a membrane
            if (cellRegion.hasMembrane()) {
                regionNodeMapping.get(cellRegion).add(node);
            }
        }
    }

    private void traverseRegion(CellRegion region) {
        unprocessedNodes = regionNodeMapping.get(region);
        AutomatonNode startingNode = null;
        boolean cyclic = true;
        // see if the membrane is a cycle or linear
        for (AutomatonNode currentNode : unprocessedNodes) {
            int neighbouringRegions = 0;
            for (AutomatonNode node : currentNode.getNeighbours()) {
                if (node.getCellRegion().equals(region)) {
                    neighbouringRegions++;
                }
            }
            if (neighbouringRegions == 1) {
                // membrane is linear
                startingNode = currentNode;
                cyclic = false;
            }
            if (neighbouringRegions > 2) {
                throw new IllegalStateException("The automaton graph has membrane that has more than two neighboring " +
                        "nodes that are also membrane and the same region.");
            }
        }
        // membrane is cyclic
        if (startingNode == null) {
            startingNode = unprocessedNodes.iterator().next();
        }

        // initialize starting point
        queue.push(startingNode);
        // as long as there are nodes on the queue
        AutomatonNode currentNode;
        // depth first traversal
        while ((currentNode = queue.poll()) != null) {
            if (!currentNodes.contains(currentNode)) {
                currentNodes.add(currentNode);
                for (AutomatonNode neighbour : currentNode.getNeighbours()) {
                    if (neighbour.getCellRegion().equals(region)) {
                        processNode(neighbour);
                    }
                }
            }
        }

        // create membrane section
        Membrane membrane = new Membrane(region.getIdentifier(), region);
        // add full segments first
        // get first node
        ListIterator<AutomatonNode> iterator = currentNodes.listIterator();
        AutomatonNode previousNode = iterator.next();
        currentNode = iterator.next();
        while (iterator.hasNext()) {
            AutomatonNode nextNode = iterator.next();
            membrane.addSegment(currentNode, new SimpleLineSegment(currentNode.getPosition(), previousNode.getPosition()));
            membrane.addSegment(currentNode, new SimpleLineSegment(currentNode.getPosition(), nextNode.getPosition()));
            previousNode = currentNode;
            currentNode = nextNode;
        }

        // add first and last node for cyclic membranes
        if (cyclic) {
            Iterator<AutomatonNode> forwardIterator = currentNodes.iterator();
            AutomatonNode first = forwardIterator.next();
            AutomatonNode second = forwardIterator.next();
            membrane.addSegment(first, new SimpleLineSegment(first.getPosition(), currentNodes.getLast().getPosition()));
            membrane.addSegment(first, new SimpleLineSegment(first.getPosition(), second.getPosition()));

            Iterator<AutomatonNode> backwardIterator = currentNodes.descendingIterator();
            AutomatonNode last = backwardIterator.next();
            AutomatonNode beforeLast = backwardIterator.next();
            membrane.addSegment(last, new SimpleLineSegment(last.getPosition(), beforeLast.getPosition()));
            membrane.addSegment(last, new SimpleLineSegment(last.getPosition(), first.getPosition()));
        }
        membranes.add(membrane);

        // cut segments to size
        for (MembraneSegment entry : membrane.getSegments()) {
            Polygon spatialRepresentation = entry.getNode().getSpatialRepresentation();
            Set<Vector2D> intersections = spatialRepresentation.getIntersections(entry);
            // starting point should be always associated node
            entry.setEndingPoint(intersections.iterator().next());
        }

    }

    /**
     * Adds a node to the queue and subgraph, and removes it from the unprocessed nodes.
     *
     * @param node The nodes.
     */
    private void processNode(AutomatonNode node) {
        // add initial node to queue
        queue.push(node);
        // and remove it from the unprocessed stack
        unprocessedNodes.remove(node);
    }

}
