package bio.singa.mathematics.algorithms.graphs;

import bio.singa.mathematics.graphs.model.Edge;
import bio.singa.mathematics.graphs.model.Graph;
import bio.singa.mathematics.graphs.model.Node;
import bio.singa.mathematics.vectors.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of Smallest Set of Smallest Rings (SSSR) identification according to:
 *
 * <pre>
 *     Fan, B. T., Panaye, A., Doucet, J. P., & Barbu, A. (1993).
 *     Ring perception. A new algorithm for directly finding the smallest set of smallest rings from a connection table.
 *     Journal of chemical information and computer sciences, 33(5), 657-662.
 * </pre>
 *
 * @param <NodeType>       The type of the {@link Node}s of the graph.
 * @param <EdgeType>       The type of the {@link Edge}s of the graph.
 * @param <VectorType>     The type of the {@link Vector} describing the {@link Node} position.
 * @param <IdentifierType> The type of the {@link Node} identifier.
 * @param <GraphType>      The type of the graph.
 */
public class SmallestSetOfSmallestRingsFinder<NodeType extends Node<NodeType, VectorType, IdentifierType>, EdgeType extends Edge<NodeType>,
        VectorType extends Vector, IdentifierType, GraphType extends Graph<NodeType, EdgeType, IdentifierType>> {

    private static final Logger logger = LoggerFactory.getLogger(SmallestSetOfSmallestRingsFinder.class);
    private final GraphType graph;
    private List<Set<NodeType>> rings;

    public SmallestSetOfSmallestRingsFinder(GraphType graph) {
        this.graph = graph;
        findSmallestRings();
    }

    /**
     * Returns the found smallest rings.
     *
     * @return List of sets of nodes describing the smallest rings in increasing size.
     */
    public List<Set<NodeType>> getRings() {
        return rings;
    }

    private void findSmallestRings() {

        // reduce nodes that only have a single connection (cannot be part of any ring)
        List<NodeType> nodesToRemove = graph.getNodes().stream()
                .filter(node -> node.getNeighbours().size() < 2)
                .collect(Collectors.toList());

        GraphType graphCopy = (GraphType) graph.getCopy();
        nodesToRemove.forEach(graphCopy::removeNode);

        rings = new ArrayList<>();
        for (NodeType node : graphCopy.getNodes()) {
            // BFS for each node in the graph
            Set<NodeType> ring = findRing(graphCopy, node);
            if (ring != null && !rings.contains(ring)) {
                rings.add(ring);
            }
        }

        if (!rings.isEmpty()) {
            rings.sort(Comparator.comparing(Set::size));
            logger.info("identified {} rings in the input graph (smallest one of size: {})", rings.size(), rings.get(0).size());
        }
    }

    private Set<NodeType> findRing(GraphType graph, NodeType rootNode) {

        // create storage for paths
        Map<NodeType, List<NodeType>> paths = new HashMap<>();
        graph.getNodes()
                .forEach(node -> paths.put(node, new ArrayList<>()));
        paths.get(rootNode).add(rootNode);

        Deque<NodeType> deque = new ArrayDeque<>();
        deque.add(rootNode);

        Set<NodeType> visitedNodes = new HashSet<>();
        visitedNodes.add(rootNode);

        while (!deque.isEmpty()) {

            NodeType currentNode = deque.pollFirst();
            List<NodeType> neighbours = currentNode.getNeighbours();

            for (NodeType neighbour : neighbours) {

                // do not duplicate paths
                if (visitedNodes.contains(neighbour)) {
                    continue;
                }

                Set<NodeType> currentPath = new HashSet<>(paths.get(currentNode));
                Set<NodeType> nextPath = new HashSet<>(paths.get(neighbour));
                Set<NodeType> intersection = new HashSet<>(new ArrayList<>(currentPath));
                intersection.retainAll(nextPath);

                // new ring was found if current and next path have one node in common
                if (nextPath.size() > 0 && intersection.size() == 1) {
                    Set<NodeType> newRing = new HashSet<>(currentPath);
                    newRing.addAll(nextPath);
                    return new HashSet<>(newRing);
                }

                if (nextPath.size() == 0) {
                    paths.put(neighbour, new ArrayList<>(currentPath));
                    paths.get(neighbour).add(neighbour);
                }

                deque.add(neighbour);
            }

            visitedNodes.add(currentNode);
        }
        return null;
    }
}
