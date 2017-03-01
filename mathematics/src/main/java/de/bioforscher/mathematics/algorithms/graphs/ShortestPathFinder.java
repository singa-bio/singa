package de.bioforscher.mathematics.algorithms.graphs;

import de.bioforscher.mathematics.graphs.model.Node;
import de.bioforscher.mathematics.vectors.Vector;

import java.util.*;
import java.util.function.Predicate;

/**
 * @author cl
 */
public class ShortestPathFinder<VectorType extends Vector, NodeType extends Node<NodeType, VectorType>> {

    private Queue<NodeType> queue;
    private Map<NodeType, Integer> distances;
    private Map<NodeType, NodeType> predecessors;

    private ShortestPathFinder() {

    }

    private void initialize(NodeType sourceNode) {
        this.distances = new HashMap<>();
        this.predecessors = new HashMap<>();
        this.queue = new LinkedList<>();
        this.queue.offer(sourceNode);
        this.distances.put(sourceNode, 0);
    }

    public static <VectorType extends Vector, NodeType extends Node<NodeType, VectorType>> LinkedList<NodeType> findBasedOnPredicate(NodeType sourceNode, Predicate<NodeType> targetPredicate) {
        ShortestPathFinder<VectorType, NodeType> pathfinder = new ShortestPathFinder<>();
        pathfinder.initialize(sourceNode);
        // processes
        while (!pathfinder.queue.isEmpty()) {
            NodeType currentNode = pathfinder.queue.poll();
            for (NodeType neighbour : currentNode.getNeighbours()) {
                LinkedList<NodeType> path = pathfinder.checkTarget(currentNode, neighbour, targetPredicate);
                if (path!= null) {
                    return path;
                }
            }
        }
        return null;
    }

    public static <VectorType extends Vector, NodeType extends Node<NodeType, VectorType>> LinkedList<NodeType> trackBasedOnPredicates(NodeType sourceNode, Predicate<NodeType> targetPredicate, Predicate<NodeType> trackPredicate) {
        ShortestPathFinder<VectorType, NodeType> pathfinder = new ShortestPathFinder<>();
        pathfinder.initialize(sourceNode);
        // processes
        while (!pathfinder.queue.isEmpty()) {
            NodeType currentNode = pathfinder.queue.poll();
            for (NodeType neighbour : currentNode.getNeighbours()) {
                if (trackPredicate.test(currentNode)) {
                    LinkedList<NodeType> path = pathfinder.checkTarget(currentNode, neighbour, targetPredicate);
                    if (path!= null) {
                        return path;
                    }
                }
            }
        }
        return null;
    }

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
