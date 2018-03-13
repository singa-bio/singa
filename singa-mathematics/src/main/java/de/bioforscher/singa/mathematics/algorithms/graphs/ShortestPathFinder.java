package de.bioforscher.singa.mathematics.algorithms.graphs;

import de.bioforscher.singa.mathematics.graphs.model.Edge;
import de.bioforscher.singa.mathematics.graphs.model.Graph;
import de.bioforscher.singa.mathematics.graphs.model.GraphPath;
import de.bioforscher.singa.mathematics.graphs.model.Node;
import de.bioforscher.singa.mathematics.vectors.Vector;

import java.util.*;
import java.util.function.Predicate;

/**
 * This class provides methods to find the shortest path in a {@link Graph} using Dijkstra's algorithm. Based on
 * predicates the satisfiable condition is tested and the shortest path is returned as a {@link LinkedList}.
 *
 * @author cl
 * @see <a href="https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm">Wikipedia: Dijkstra's algorithm</a>
 */
public class ShortestPathFinder<NodeType extends Node<NodeType, VectorType, IdentifierType>, EdgeType extends Edge<NodeType>,
        VectorType extends Vector, IdentifierType, GraphType extends Graph<NodeType, EdgeType, IdentifierType>> {

    private final Queue<NodeType> queue;
    private final Map<NodeType, Integer> distances;
    private final Map<NodeType, NodeType> predecessors;
    private final GraphType graph;

    /**
     * Private constructor to prevent external instantiation.
     */
    private ShortestPathFinder(GraphType graph, NodeType sourceNode) {
        this.graph = graph;
        distances = new HashMap<>();
        predecessors = new HashMap<>();
        queue = new LinkedList<>();
        queue.offer(sourceNode);
        distances.put(sourceNode, 0);
    }

    /**
     * Returns the shortest path originating from the source node. The target node is the first node that satisfies
     * target predicate. E.g. To to search for a specific node in the graph it is possible to use the identifier in the
     * predicate. If no path can be found null is returned.
     *
     * @param sourceNode The source node.
     * @param targetPredicate The predicate the target has to fulfill.
     * @param <VectorType> The type of the position of the node.
     * @param <NodeType> The type of the node.
     * @param <IdentifierType> The type of the identifier.
     * @return The shortest path.
     */
    public static <NodeType extends Node<NodeType, VectorType, IdentifierType>,
            EdgeType extends Edge<NodeType>, VectorType extends Vector, IdentifierType,
            GraphType extends Graph<NodeType, EdgeType, IdentifierType>> GraphPath<NodeType, EdgeType> findBasedOnPredicate(GraphType graph, NodeType sourceNode, Predicate<NodeType> targetPredicate) {
        ShortestPathFinder<NodeType, EdgeType, VectorType, IdentifierType, GraphType> pathfinder = new ShortestPathFinder<>(graph, sourceNode);
        // processes
        while (!pathfinder.queue.isEmpty()) {
            NodeType currentNode = pathfinder.queue.poll();
            for (NodeType neighbour : currentNode.getNeighbours()) {
                GraphPath<NodeType, EdgeType> path = pathfinder.checkTarget(currentNode, neighbour, targetPredicate);
                if (path != null) {
                    return path;
                }
            }
        }
        return null;
    }

    /**
     * Returns the shortest path originating from the source node. The target node is the first node that satisfies
     * target predicate. Additionally all nodes on the path to the target predicate have to fulfill the track predicate.
     * If no path can be found null is returned.
     *
     * @param sourceNode The source node.
     * @param targetPredicate The predicate the target has to fulfill.
     * @param trackPredicate The predicate all nodes on the path have to fulfill.
     * @param <VectorType> The type of the position of the node.
     * @param <NodeType> The type of the node.
     * @param <IdentifierType> The type of the identifier.
     * @return The shortest path.
     */
    public static <NodeType extends Node<NodeType, VectorType, IdentifierType>,
            EdgeType extends Edge<NodeType>, VectorType extends Vector, IdentifierType,
            GraphType extends Graph<NodeType, EdgeType, IdentifierType>> GraphPath<NodeType, EdgeType> trackBasedOnPredicates(GraphType graph, NodeType sourceNode, Predicate<NodeType> targetPredicate, Predicate<NodeType> trackPredicate) {
        ShortestPathFinder<NodeType, EdgeType, VectorType, IdentifierType, GraphType> pathfinder = new ShortestPathFinder<>(graph, sourceNode);
        // processes
        while (!pathfinder.queue.isEmpty()) {
            NodeType currentNode = pathfinder.queue.poll();
            for (NodeType neighbour : currentNode.getNeighbours()) {
                if (trackPredicate.test(currentNode)) {
                    GraphPath<NodeType, EdgeType> path = pathfinder.checkTarget(currentNode, neighbour, targetPredicate);
                    if (path != null) {
                        return path;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Checks whether the current target fulfills the predicate. If this is the case the path from the source to the
     * target is returned. Otherwise the target is added to the queue, it is referenced in the predecessors map and the
     * distance is set in the distance map.
     *
     * @param source The source node.
     * @param target The target node.
     * @param targetPredicate The predicate to fulfill.
     * @return The shortest path if the target fulfills the predicate and null otherwise.
     */
    private GraphPath<NodeType, EdgeType> checkTarget(NodeType source, NodeType target, Predicate<NodeType> targetPredicate) {
        if (!distances.containsKey(target)) {
            // until predicate is fulfilled the first time
            if (targetPredicate.test(target)) {
                predecessors.put(target, source);
                return getPath(target);
            }
            // calculate distance and offer to queue
            distances.put(target, distances.get(source) + 1);
            predecessors.put(target, source);
            queue.offer(target);
        }
        return null;
    }

    /**
     * Builds the path to the given target node.
     *
     * @param targetNode The target node.
     * @return The path to the node.
     */
    private GraphPath<NodeType, EdgeType> getPath(NodeType targetNode) {
        GraphPath<NodeType, EdgeType> path = new GraphPath<>();
        NodeType step = targetNode;
        if (predecessors.get(step) == null) {
            return null;
        }
        // add nodes on path
        path.addNode(step);
        while (predecessors.get(step) != null) {
            step = predecessors.get(step);
            path.addNode(step);
        }
        Collections.reverse(path.getNodes());
        // add edges
        LinkedList<NodeType> nodes = path.getNodes();
        Iterator<NodeType> iterator = nodes.iterator();
        NodeType current = iterator.next();
        while (iterator.hasNext()) {
            NodeType next = iterator.next();
            Optional<EdgeType> edge;
            if ((edge = graph.getEdgeBetween(current, next)).isPresent()) {
                path.addEdge(edge.get());
            } else {
                throw new IllegalStateException("Unable to determine edge between "+current+" and "+next+".");
            }
            current = next;
        }
        return path;
    }


}
