package de.bioforscher.singa.mathematics.algorithms.graphs;

import de.bioforscher.singa.mathematics.graphs.model.Edge;
import de.bioforscher.singa.mathematics.graphs.model.Graph;
import de.bioforscher.singa.mathematics.graphs.model.Node;
import de.bioforscher.singa.mathematics.vectors.Vector2D;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * @author cl
 */
public class DisconnectedSubgraphFinder<NodeType extends Node<NodeType, Vector2D>, EdgeType extends Edge<NodeType>,
        GraphType extends Graph<NodeType, EdgeType>> {

    private Queue<NodeType> queue;
    private GraphType graph;

    private List<NodeType> processedNodes;
    private List<List<NodeType>> nodesOfSubgraphs;


    private DisconnectedSubgraphFinder(GraphType graph) {
        this.queue = new ArrayDeque<>();
        this.processedNodes = new ArrayList<>();
        this.nodesOfSubgraphs = new ArrayList<>();
        this.graph = graph;
    }

    public static <NodeType extends Node<NodeType, Vector2D>, EdgeType extends Edge<NodeType>,
            GraphType extends Graph<NodeType, EdgeType>> List<List<NodeType>> findDisconnectedSubgraphs(GraphType graph) {

        DisconnectedSubgraphFinder<NodeType, EdgeType, GraphType> finder = new DisconnectedSubgraphFinder<>(graph);
        NodeType current = finder.graph.getNodes().iterator().next();

        while (finder.processedNodes.size() != finder.graph.getNodes().size()) {
            List<NodeType> currentNodes = new ArrayList<>();
            currentNodes.add(current);
            finder.processedNodes.add(current);
            boolean firstIteration = true;
            while (current != null || firstIteration) {
                for (NodeType neighbor : current.getNeighbours()) {
                    if (!currentNodes.contains(neighbor)) {
                        finder.queue.offer(neighbor);
                        finder.processedNodes.add(neighbor);
                        currentNodes.add(neighbor);
                    }
                }
                current = finder.queue.poll();
                if (firstIteration) {
                    firstIteration = false;
                }
            }
            finder.nodesOfSubgraphs.add(currentNodes);

            List<NodeType> remainingNodes = new ArrayList<>(graph.getNodes());
            remainingNodes.removeAll(finder.processedNodes);

            if (!remainingNodes.isEmpty()) {
                current = remainingNodes.iterator().next();
            }
        }

        return finder.nodesOfSubgraphs;

    }


}
