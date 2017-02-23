package de.bioforscher.mathematics.algorithms.graphs;

import de.bioforscher.mathematics.graphs.model.Node;
import de.bioforscher.mathematics.vectors.Vector2D;

import java.util.*;
import java.util.function.Predicate;

/**
 * @author cl
 */
public class ShortestPathFinder<NodeType extends Node<NodeType, Vector2D>> {

    private Queue<NodeType> queue;
    private Map<NodeType, Integer> distances;
    private Map<NodeType, NodeType> predecessors;

    public ShortestPathFinder() {

    }

    private void initialize(NodeType sourceNode) {
        this.distances = new HashMap<>();
        this.predecessors = new HashMap<>();
        this.queue = new LinkedList<>();
        this.queue.offer(sourceNode);
        this.distances.put(sourceNode, 0);
    }

    public LinkedList<NodeType> findBasedOnPredicate(NodeType sourceNode, Predicate<NodeType> targetPredicate) {
        initialize(sourceNode);
        // processes
        while (!this.queue.isEmpty()) {
            NodeType currentNode = this.queue.poll();
            for (NodeType neighbour : currentNode.getNeighbours()) {
                LinkedList<NodeType> path = checkTarget(currentNode, neighbour, targetPredicate);
                if (path!= null) {
                    return path;
                }
            }
        }
        return null;
    }

    public LinkedList<NodeType> trackBasedOnPredicates(NodeType sourceNode, Predicate<NodeType> targetPredicate, Predicate<NodeType> trackPredicate) {
        initialize(sourceNode);
        // processes
        while (!this.queue.isEmpty()) {
            NodeType currentNode = this.queue.poll();
            for (NodeType neighbour : currentNode.getNeighbours()) {
                if (trackPredicate.test(currentNode)) {
                    LinkedList<NodeType> path = checkTarget(currentNode, neighbour, targetPredicate);
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
