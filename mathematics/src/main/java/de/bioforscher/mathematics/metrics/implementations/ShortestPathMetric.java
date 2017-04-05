package de.bioforscher.mathematics.metrics.implementations;

import de.bioforscher.mathematics.exceptions.DegenerateCaseException;
import de.bioforscher.mathematics.graphs.model.Graph;
import de.bioforscher.mathematics.graphs.model.Node;
import de.bioforscher.mathematics.metrics.model.Metric;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * Calculates the distance between two nodes specified by the number of edges
 * between them with a hybrid implementation of Breadth-first search and
 * Dijkstra's algorithm.
 *
 * @author cl
 */
public class ShortestPathMetric implements Metric<Node<?, ?>> {

    private Graph<?, ?> graph;

    public ShortestPathMetric(Graph<?, ?> graph) {
        this.graph = graph;
    }

    @Override
    public double calculateDistance(Node<?, ?> first, Node<?, ?> second) {

        // registers distances of the nodes that have already been processed
        Map<Node<?, ?>, Integer> distance = new HashMap<>(this.graph.getNodes().size());

        // degenerate case: graph does not contain one or both of the requested
        // nodes
        if (!(this.graph.containsNode(first) && this.graph.containsNode(second))) {
            throw new DegenerateCaseException("The graph has to contain both nodes " + first + " and " + second
                    + " in order to calculate the shortest path between both.");
        }

        // trivial solution: first is equal to first2DVector
        if (first.equals(second)) {
            return 0.0;
        }

        // setup FIFO queue
        Queue<Node<?, ?>> queue = new LinkedList<>();
        queue.offer(first);
        distance.put(first, 0);

        // processes all direct and indirect neighbours of the first node
        while (!queue.isEmpty()) {
            Node<?, ?> currentNode = queue.poll();
            for (Node<?, ?> adjacentNode : currentNode.getNeighbours()) {
                // distance has already been determined
                if (!distance.containsKey(adjacentNode)) {
                    // until first2DVector node has been found
                    if (adjacentNode.equals(second)) {
                        return distance.get(currentNode) + 1;
                    }
                    // calculate distance and offer to queue
                    distance.put(adjacentNode, distance.get(currentNode) + 1);
                    queue.offer(adjacentNode);
                }
            }
        }

        // trivial solution: both nodes lie in different subgraphs
        return Double.POSITIVE_INFINITY;

    }

}
