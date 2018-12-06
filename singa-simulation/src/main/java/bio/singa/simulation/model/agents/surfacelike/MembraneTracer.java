package bio.singa.simulation.model.agents.surfacelike;

import bio.singa.features.parameters.Environment;
import bio.singa.mathematics.geometry.edges.Line;
import bio.singa.mathematics.geometry.edges.SimpleLineSegment;
import bio.singa.mathematics.geometry.model.Polygon;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.mathematics.vectors.Vectors;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.CellTopology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static bio.singa.mathematics.vectors.Vector2D.X_INDEX;
import static bio.singa.mathematics.vectors.Vector2D.Y_INDEX;

/**
 * @author cl
 */
public class MembraneTracer {

    private static final Logger logger = LoggerFactory.getLogger(MembraneTracer.class);

    // input
    private HashMap<CellRegion, List<AutomatonNode>> regionNodeMapping;
    private AutomatonGraph graph;

    // output
    private List<Membrane> membranes;

    // working
    private LinkedList<AutomatonNode> currentNodes;
    private Deque<AutomatonNode> queue;
    private List<AutomatonNode> unprocessedNodes;

    public static CellRegion getRegion(Map<CellRegion, Set<Vector2D>> regionMap, Vector2D vector) {
        for (Map.Entry<CellRegion, Set<Vector2D>> entry : regionMap.entrySet()) {
            if (entry.getValue().contains(vector)) {
                return entry.getKey();
            }
        }
        throw new IllegalArgumentException("The segment is not contained in this membrane");
    }

    public static List<Membrane> regionsToMembrane(AutomatonGraph graph) {
        MembraneTracer composer = new MembraneTracer(graph);
        return composer.membranes;
    }

    public MembraneTracer(AutomatonGraph graph) {
        logger.info("Initializing membranes from assigned regions.");
        this.graph = graph;
        membranes = new ArrayList<>();
        if (graph.getNodes().size() == 1) {
            traceSingleNode(graph.getNode(0, 0));
        } else {
            currentNodes = new LinkedList<>();
            queue = new ArrayDeque<>();
            unprocessedNodes = new ArrayList<>();
            initializeRegionNodeMapping();
            for (CellRegion cellRegion : regionNodeMapping.keySet()) {
                while (!regionNodeMapping.get(cellRegion).isEmpty()) {
                    traverseRegion(cellRegion);
                    currentNodes.clear();
                    queue.clear();
                }
            }
        }
    }

    private void traceSingleNode(AutomatonNode node) {
        if (!node.getCellRegion().hasMembrane()) {
            logger.warn("The graph contains only one node that has no membrane region assigned. No Membrane will be created.");
            return;
        }
        double simulationExtend = Environment.getSimulationExtend();
        // add horizontal membrane segment
        Vector2D start = new Vector2D(0, simulationExtend / 2.0);
        Vector2D end = new Vector2D(simulationExtend, simulationExtend / 2.0);
        Membrane membrane = new Membrane(node.getCellRegion().getIdentifier());
        CellRegion region = node.getCellRegion();
        membrane.setMembraneRegion(region);
        CellRegion innerRegion = new CellRegion(region.getInnerSubsection().getIdentifier(), region.getInnerSubsection().getGoTerm());
        innerRegion.addSubSection(CellTopology.INNER, region.getInnerSubsection());
        membrane.setInnerRegion(innerRegion);
        SimpleLineSegment segment = new SimpleLineSegment(start, end);
        membrane.addSegment(node, segment);
        // construct region map
        Map<CellRegion, Set<Vector2D>> regionMap = new HashMap<>();
        Set<Vector2D> vectors = new HashSet<>();
        vectors.add(segment.getStartingPoint());
        vectors.add(segment.getEndingPoint());
        regionMap.put(membrane.getMembraneRegion(), vectors);
        membrane.setRegionMap(regionMap);
        membranes.add(membrane);
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
        logger.debug("Traversing nodes of {}, creating membrane.", region.getIdentifier());
        unprocessedNodes = regionNodeMapping.get(region);
        AutomatonNode startingNode = null;
        boolean cyclic = true;
        // see if the membrane is a cycle or linear
        Iterator<AutomatonNode> currentIterator = unprocessedNodes.iterator();
        while (currentIterator.hasNext()) {
            AutomatonNode currentNode = currentIterator.next();
            // count neighbours
            int neighbouringRegions = 0;
            for (AutomatonNode node : currentNode.getNeighbours()) {
                if (node.getCellRegion().equals(region)) {
                    neighbouringRegions++;
                }
            }
            // evaluate neighbour counts
            if (neighbouringRegions == 0) {
                // a single "piece" of membrane
                // so this is a simulation with just on line of cells in horizontal or vertical direction
                if (currentNode.getNeighbours().size() == 2) {
                    Membrane membrane = new Membrane(region.getIdentifier());
                    membrane.setMembraneRegion(region);
                    CellRegion innerRegion = new CellRegion(region.getInnerSubsection().getIdentifier(), region.getInnerSubsection().getGoTerm());
                    innerRegion.addSubSection(CellTopology.INNER, region.getInnerSubsection());
                    membrane.setInnerRegion(innerRegion);
                    // check the direction of the membrane
                    Iterator<AutomatonNode> neighbours = currentNode.getNeighbours().iterator();
                    AutomatonNode first = neighbours.next();
                    AutomatonNode second = neighbours.next();
                    Line line = new Line(first.getPosition(), second.getPosition());
                    Polygon spatialRepresentation = currentNode.getSpatialRepresentation();
                    SimpleLineSegment segment;
                    if (Double.isInfinite(line.getSlope())) {
                        // horizontal membrane
                        double minX = Vectors.getMaximalValueForIndex(X_INDEX, spatialRepresentation.getVertices());
                        double maxX = Vectors.getMinimalValueForIndex(X_INDEX, spatialRepresentation.getVertices());
                        double y = currentNode.getPosition().getY();
                        segment = new SimpleLineSegment(minX, y, maxX, y);

                    } else {
                        // vertical membrane
                        double minY = Vectors.getMaximalValueForIndex(Y_INDEX, spatialRepresentation.getVertices());
                        double maxY = Vectors.getMinimalValueForIndex(Y_INDEX, spatialRepresentation.getVertices());
                        double x = currentNode.getPosition().getX();
                        segment = new SimpleLineSegment(x, minY, x, maxY);
                    }
                    membrane.addSegment(currentNode, segment);
                    // construct region map
                    Map<CellRegion, Set<Vector2D>> regionMap = new HashMap<>();
                    Set<Vector2D> vectors = new HashSet<>();
                    vectors.add(segment.getStartingPoint());
                    vectors.add(segment.getEndingPoint());
                    regionMap.put(membrane.getMembraneRegion(), vectors);
                    membrane.setRegionMap(regionMap);
                    membranes.add(membrane);
                    currentIterator.remove();
                    return;
                } else {
                    // or it is invalid
                    throw new IllegalStateException("To create a membrane for a region, at least two neighbouring cells" +
                            " must be assigned to the region.");
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
        Membrane membrane = new Membrane(region.getIdentifier());
        // add full path first
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

        // cut path to size
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
