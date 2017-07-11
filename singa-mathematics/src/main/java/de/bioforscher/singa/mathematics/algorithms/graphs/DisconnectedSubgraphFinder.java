package de.bioforscher.singa.mathematics.algorithms.graphs;

import de.bioforscher.singa.mathematics.graphs.model.Edge;
import de.bioforscher.singa.mathematics.graphs.model.Graph;
import de.bioforscher.singa.mathematics.graphs.model.Node;
import de.bioforscher.singa.mathematics.vectors.Vector2D;

import java.util.*;

/**
 * @author cl
 */
public class DisconnectedSubgraphFinder<NodeType extends Node<NodeType, Vector2D>, EdgeType extends Edge<NodeType>,
        GraphType extends Graph<NodeType, EdgeType>> {

    private Queue<NodeType> queue;
    private GraphType graph;

    private List<NodeType> processedNodes;
    private List<List<NodeType>> nodesOfSubgraphs;
    private List<List<EdgeType>> edgesOfSubgraphs;

    private DisconnectedSubgraphFinder(GraphType graph) {
        this.queue = new ArrayDeque<>();
        this.processedNodes = new ArrayList<>();
        this.nodesOfSubgraphs = new ArrayList<>();
        this.edgesOfSubgraphs = new ArrayList<>();
        this.graph = graph;
    }

    public static <NodeType extends Node<NodeType, Vector2D>, EdgeType extends Edge<NodeType>,
            GraphType extends Graph<NodeType, EdgeType>> List<GraphType> findDisconnectedSubgraphs(GraphType graph) {

        DisconnectedSubgraphFinder<NodeType, EdgeType, GraphType> finder = new DisconnectedSubgraphFinder<>(graph);

        NodeType current;
        if (finder.graph.getNodes().iterator().hasNext()) {
            current = finder.graph.getNodes().iterator().next();
        } else {
            throw new IllegalStateException("The graph seems to be empty.");
        }

        while (finder.processedNodes.size() != finder.graph.getNodes().size()) {
            List<NodeType> currentNodes = new ArrayList<>();
            List<EdgeType> curentEdges = new ArrayList<>();
            currentNodes.add(current);
            finder.processedNodes.add(current);
            boolean firstIteration = true;
            while (current != null || firstIteration) {
                for (NodeType neighbor : current.getNeighbours()) {
                    if (!currentNodes.contains(neighbor)) {
                        finder.queue.offer(neighbor);
                        finder.processedNodes.add(neighbor);
                        curentEdges.add(graph.getEdgeBetween(current, neighbor));
                        currentNodes.add(neighbor);
                    }

                }
                current = finder.queue.poll();
                if (firstIteration) {
                    firstIteration = false;
                }
            }
            finder.nodesOfSubgraphs.add(currentNodes);
            finder.edgesOfSubgraphs.add(curentEdges);

            List<NodeType> remainingNodes = new ArrayList<>(graph.getNodes());
            remainingNodes.removeAll(finder.processedNodes);

            if (!remainingNodes.isEmpty()) {
                current = remainingNodes.iterator().next();
            }
        }

        List<GraphType> subgraphs = new ArrayList<>();

        for (int i = 0; i < finder.nodesOfSubgraphs.size(); i++) {
            GraphType subgraph = null;
            try {
                subgraph = (GraphType) graph.getClass().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }

            Objects.requireNonNull(subgraph);

            List<NodeType> nodes = finder.nodesOfSubgraphs.get(i);
            for (NodeType node : nodes) {
                NodeType copy = node.getCopy();
                subgraph.addNode(copy);
            }

            List<EdgeType> edges = finder.edgesOfSubgraphs.get(i);
            for (EdgeType edge : edges) {
                NodeType source = subgraph.getNode(edge.getSource().getIdentifier());
                NodeType target = subgraph.getNode(edge.getTarget().getIdentifier());
                subgraph.addEdgeBetween(edge.getIdentifier(), source, target);
            }

            subgraphs.add(subgraph);

        }

        return subgraphs;

    }


}
