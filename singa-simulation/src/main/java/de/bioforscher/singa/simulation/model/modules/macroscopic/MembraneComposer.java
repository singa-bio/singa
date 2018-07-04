package de.bioforscher.singa.simulation.model.modules.macroscopic;

import de.bioforscher.singa.mathematics.geometry.edges.LineSegment;
import de.bioforscher.singa.mathematics.geometry.edges.SimpleLineSegment;
import de.bioforscher.singa.mathematics.geometry.model.Polygon;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import de.bioforscher.singa.simulation.model.sections.CellRegion;

import java.util.*;

/**
 * @author cl
 */
public class MembraneComposer {

    // input
    private HashMap<CellRegion, List<AutomatonNode>> regionNodeMapping;
    private AutomatonGraph graph;

    // output
    private List<MacroscopicMembrane> membranes;

    // working
    private LinkedList<AutomatonNode> currentNodes;
    private Deque<AutomatonNode> queue;
    private List<AutomatonNode> unprocessedNodes;

    public static List<MacroscopicMembrane> composeMacroscopicMembrane(AutomatonGraph graph) {
        MembraneComposer composer = new MembraneComposer(graph);
        return composer.membranes;
    }

    public MembraneComposer(AutomatonGraph graph) {
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
        MacroscopicMembrane membrane = new MacroscopicMembrane(region.getIdentifier(), region);
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
        for (Map.Entry<AutomatonNode, List<LineSegment>> entry : membrane.getSegments().entrySet()) {
            Polygon spatialRepresentation = entry.getKey().getSpatialRepresentation();
            for (LineSegment lineSegment : entry.getValue()) {
                Set<Vector2D> intersections = spatialRepresentation.getIntersections(lineSegment);
                // starting point should be always associated node
                lineSegment.setEndingPoint(intersections.iterator().next());
            }
        }

        // merge segments
        for (Map.Entry<AutomatonNode, List<LineSegment>> entry : membrane.getSegments().entrySet()) {
            List<LineSegment> segments = entry.getValue();
            Iterator<LineSegment> lineSegmentIterator = segments.iterator();
            LineSegment firstSegment = lineSegmentIterator.next();
            LineSegment secondSegment = lineSegmentIterator.next();
            segments.clear();
            segments.add(new SimpleLineSegment(firstSegment.getEndingPoint(), secondSegment.getEndingPoint()));
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
