package de.bioforscher.singa.mathematics.algorithms.graphs;

import de.bioforscher.singa.mathematics.graphs.model.Graph;
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
public class ShortestPathFinder<NodeType extends Node<NodeType, VectorType, IdentifierType>, VectorType extends Vector, IdentifierType> {

    private Queue<NodeType> queue;
    private Map<NodeType, Integer> distances;
    private Map<NodeType, NodeType> predecessors;

    /**
     * Private constructor to prevent external instantiation.
     */
    private ShortestPathFinder(NodeType sourceNode) {
        this.distances = new HashMap<>();
        this.predecessors = new HashMap<>();
        this.queue = new LinkedList<>();
        this.queue.offer(sourceNode);
        this.distances.put(sourceNode, 0);
    }

    /**
     * Returns the shortest path originating from the source node. The target node is the first node that satisfies
     * target predicate. E.g. To to search for a specific node in the graph it is possible to use the identifier in the
     * predicate. If no path can be found null is returned.
     *
     * @param sourceNode      The source node.
     * @param targetPredicate The predicate the target has to fulfill.
     * @param <VectorType>    The type of the position of the node.
     * @param <NodeType>      The type of the node.
     * @return The shortest path.
     */
    public static <NodeType extends Node<NodeType, VectorType, IdentifierType>,
            VectorType extends Vector, IdentifierType> LinkedList<NodeType> findBasedOnPredicate(NodeType sourceNode, Predicate<NodeType> targetPredicate) {
        ShortestPathFinder<NodeType, VectorType, IdentifierType> pathfinder = new ShortestPathFinder<>(sourceNode);
        // processes
        while (!pathfinder.queue.isEmpty()) {
            NodeType currentNode = pathfinder.queue.poll();
            for (NodeType neighbour : currentNode.getNeighbours()) {
                LinkedList<NodeType> path = pathfinder.checkTarget(currentNode, neighbour, targetPredicate);
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
     * @param sourceNode      The source node.
     * @param targetPredicate The predicate the target has to fulfill.
     * @param trackPredicate  The predicate all nodes on the path have to fulfill.
     * @param <VectorType>    The type of the position of the node.
     * @param <NodeType>      The type of the node.
     * @return The shortest path.
     */
    public static <NodeType extends Node<NodeType, VectorType, IdentifierType>,
            VectorType extends Vector,IdentifierType> LinkedList<NodeType> trackBasedOnPredicates(NodeType sourceNode, Predicate<NodeType> targetPredicate, Predicate<NodeType> trackPredicate) {
        ShortestPathFinder<NodeType, VectorType, IdentifierType> pathfinder = new ShortestPathFinder<>(sourceNode);
        // processes
        while (!pathfinder.queue.isEmpty()) {
            NodeType currentNode = pathfinder.queue.poll();
            for (NodeType neighbour : currentNode.getNeighbours()) {
                if (trackPredicate.test(currentNode)) {
                    LinkedList<NodeType> path = pathfinder.checkTarget(currentNode, neighbour, targetPredicate);
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
     * target is returned. Otherwise the target is added to the queue, it is referenced in the predecessors map and
     * the distance is set in the distance map.
     *
     * @param source          The source node.
     * @param target          The target node.
     * @param targetPredicate The predicate to fulfill.
     * @return The shortest path if the target fulfills the predicate and null otherwise.
     */
    private LinkedList<NodeType> checkTarget(NodeType source, NodeType target, Predicate<NodeType> targetPredicate) {
        if (!this.distances.containsKey(target)) {
            // until predicate is fulfilled the first time
            if (targetPredicate.test(target)) {
                this.predecessors.put(target, source);
                return getPath(target);
            }
            // calculate distance and offer to queue
            this.distances.put(target, this.distances.get(source) + 1);
            this.predecessors.put(target, source);
            this.queue.offer(target);
        }
        return null;
    }

    /**
     * Builds the path to the given target node.
     *
     * @param targetNode The target node.
     * @return The path to the node.
     */
    private LinkedList<NodeType> getPath(NodeType targetNode) {
        LinkedList<NodeType> path = new LinkedList<>();
        NodeType step = targetNode;
        if (this.predecessors.get(step) == null) {
            return null;
        }
        path.add(step);
        while (this.predecessors.get(step) != null) {
            step = this.predecessors.get(step);
            path.add(step);
        }
        Collections.reverse(path);
        return path;
    }


}
