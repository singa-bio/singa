package de.bioforscher.singa.mathematics.algorithms.graphs;

import de.bioforscher.singa.mathematics.graphs.model.Edge;
import de.bioforscher.singa.mathematics.graphs.model.Graph;
import de.bioforscher.singa.mathematics.graphs.model.Node;
import de.bioforscher.singa.mathematics.vectors.Vector;

import java.util.*;

/**
 * @author cl
 */
public class NeighbourhoodExtractor<NodeType extends Node<NodeType, VectorType, IdentifierType>, EdgeType extends Edge<NodeType>,
        VectorType extends Vector, IdentifierType, GraphType extends Graph<NodeType, EdgeType, IdentifierType>> {

    private GraphType graph;
    private NodeType referenceNode;

    private Queue<NodeType> currentWave;
    private Queue<NodeType> nextWave;
    private Set<NodeType> visitedNodes;

    private List<NodeType> nodesOfSubgraph;
    private List<EdgeType> edgesOfSubgraph;

    public NeighbourhoodExtractor(GraphType graph, NodeType referenceNode) {
        this.referenceNode = referenceNode;
        this.graph = graph;

        this.currentWave = new ArrayDeque<>();
        this.nextWave = new ArrayDeque<>();
        this.visitedNodes = new HashSet<>();

        this.nodesOfSubgraph = new ArrayList<>();
        this.edgesOfSubgraph = new ArrayList<>();
    }

    public static <NodeType extends Node<NodeType, VectorType, IdentifierType>,
            EdgeType extends Edge<NodeType>, VectorType extends Vector, IdentifierType,
            GraphType extends Graph<NodeType, EdgeType, IdentifierType>> GraphType extractNeighborhood(GraphType graph, NodeType referenceNode, int depth) {
        // create new instance for the given graph
        NeighbourhoodExtractor<NodeType, EdgeType, VectorType, IdentifierType, GraphType> finder = new NeighbourhoodExtractor<>(graph, referenceNode);
        finder.extractNeighborhood(depth, false);
        return finder.createSubgraph();
    }

    public static <NodeType extends Node<NodeType, VectorType, IdentifierType>,
            EdgeType extends Edge<NodeType>, VectorType extends Vector, IdentifierType,
            GraphType extends Graph<NodeType, EdgeType, IdentifierType>> List<NodeType> extractShell(GraphType graph, NodeType referenceNode, int shell) {
        NeighbourhoodExtractor<NodeType, EdgeType, VectorType, IdentifierType, GraphType> finder = new NeighbourhoodExtractor<>(graph, referenceNode);
        finder.extractNeighborhood(shell, true);
        return finder.nodesOfSubgraph;
    }

    private void extractNeighborhood(int shell, boolean onlyShell) {
        // initialize with given node
        if (!onlyShell) {
            nodesOfSubgraph.add(referenceNode);
        }
        currentWave.offer(referenceNode);
        // reduce the remaining depth with each wave
        while (shell > 0) {
            // process current wave
            while (!currentWave.isEmpty()) {
                NodeType currentNode = currentWave.poll();
                visitedNodes.add(currentNode);
                // process neighbors of the current wave
                for (NodeType neighbour : currentNode.getNeighbours()) {
                    // if this neighbor has not already been processed
                    if (!visitedNodes.contains(neighbour)) {
                        if (!onlyShell) {
                            nodesOfSubgraph.add(neighbour);
                            edgesOfSubgraph.add(graph.getEdgeBetween(currentNode, neighbour).get());
                            addConnectionsToVisitedNodes(neighbour);
                        }
                        nextWave.add(neighbour);
                        visitedNodes.add(neighbour);
                    }
                }
            }
            // current wave has been processed and is updated with the nodes that have been found
            currentWave = nextWave;
            if (onlyShell && shell == 1) {
                nodesOfSubgraph.addAll(nextWave);
            }
            nextWave = new ArrayDeque<>();
            shell--;
        }
    }

    private void addConnectionsToVisitedNodes(NodeType neighbour) {
        for (NodeType visitedNode : visitedNodes) {
            Optional<EdgeType> edge;
            if ((edge = graph.getEdgeBetween(neighbour, visitedNode)).isPresent()) {
                edgesOfSubgraph.add(edge.get());
            }
        }
    }

    /**
     * Assembles the subgraphs from the paring node and edge lists.
     *
     * @return A list of all extracted subgraphs.
     */
    private GraphType createSubgraph() {
        // create a new graph
        GraphType subgraph;
        try {
            subgraph = (GraphType) this.graph.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Failed to create a new graph.");
        }
        // copy and add nodes
        Objects.requireNonNull(subgraph);
        for (NodeType node : this.nodesOfSubgraph) {
            NodeType copy = node.getCopy();
            subgraph.addNode(copy);
        }
        // create and add edges for the nodes (preserving edge identifier)
        List<EdgeType> edges = this.edgesOfSubgraph;
        for (EdgeType edge : edges) {
            NodeType source = subgraph.getNode(edge.getSource().getIdentifier());
            NodeType target = subgraph.getNode(edge.getTarget().getIdentifier());
            subgraph.addEdgeBetween(edge.getIdentifier(), source, target);
        }
        return subgraph;
    }


}
