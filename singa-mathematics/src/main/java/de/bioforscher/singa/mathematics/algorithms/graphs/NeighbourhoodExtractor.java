package de.bioforscher.singa.mathematics.algorithms.graphs;

import de.bioforscher.singa.mathematics.graphs.model.Edge;
import de.bioforscher.singa.mathematics.graphs.model.Graph;
import de.bioforscher.singa.mathematics.graphs.model.Node;
import de.bioforscher.singa.mathematics.vectors.Vector;

import java.util.*;

/**
 * Given a graph and a reference node the static methods con be used to analyze the neighborhood of the reference node.
 * The resulting subgraphs are copies and changes are not reflected back into the original graph, but node and edge
 * identifiers, as well as attached data is conserved.
 *
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

    /**
     * Given a graph and a reference node, this method returns a subgraph that contains the reference node and all its
     * neighboring nodes until a certain depth of iteration. The graph contains also all edges that are present in the
     * original graph and whose start and end nodes have been extracted. The subgraphs are copies and changes are not
     * reflected back into the original graph, but node and edge identifiers, as well as attached data is conserved.
     *
     * @param graph The original graphs to extract from.
     * @param referenceNode The reference node that is the centre of the neighborhood.
     * @param depth The depth of iteration. (depth 1 is the immediate neighborhood, depth 2 also includes all nodes that
     * are neighbours to depth 1, and so on)
     * @param <NodeType> The type of the nodes.
     * @param <EdgeType> The type of the edges.
     * @param <GraphType> The type of the graph.
     * @param <VectorType> The type of the position.
     * @param <IdentifierType> The type of the identifier.
     * @return The extracted neighbourhood as a graph.
     */
    public static <NodeType extends Node<NodeType, VectorType, IdentifierType>,
            EdgeType extends Edge<NodeType>, VectorType extends Vector, IdentifierType,
            GraphType extends Graph<NodeType, EdgeType, IdentifierType>> GraphType extractNeighborhood(GraphType graph, NodeType referenceNode, int depth) {
        // create new instance for the given graph
        NeighbourhoodExtractor<NodeType, EdgeType, VectorType, IdentifierType, GraphType> finder = new NeighbourhoodExtractor<>(graph, referenceNode);
        finder.extractNeighborhood(depth, false);
        return finder.createSubgraph();
    }

    /**
     * Given a graph and a reference node, this method returns a list of nodes that are present in a certain shell
     * around the reference node. The nth shell can be thought of as the set nodes that have the shortest path distance
     * of n to the reference node.
     *
     * @param graph The original graphs to extract from.
     * @param referenceNode The reference node that is the centre of the neighborhood.
     * @param shell The shell that is to be extracted. (shell 1 are all direct neighbours, depth 2 includes all nodes
     * that are neighbours to the first shell 1, but not the previous shell (shell 0), and so on)
     * @param <NodeType> The type of the nodes.
     * @param <EdgeType> The type of the edges.
     * @param <GraphType> The type of the graph.
     * @param <VectorType> The type of the position.
     * @param <IdentifierType> The type of the identifier.
     * @return The nodes in the given shell.
     */
    public static <NodeType extends Node<NodeType, VectorType, IdentifierType>,
            EdgeType extends Edge<NodeType>, VectorType extends Vector, IdentifierType,
            GraphType extends Graph<NodeType, EdgeType, IdentifierType>> List<NodeType> extractShell(GraphType graph, NodeType referenceNode, int shell) {
        NeighbourhoodExtractor<NodeType, EdgeType, VectorType, IdentifierType, GraphType> finder = new NeighbourhoodExtractor<>(graph, referenceNode);
        finder.extractNeighborhood(shell, true);
        return finder.nodesOfSubgraph;
    }

    /**
     * Extracts the neighborhood of the graph up to a given shell. If the "onlyShell" flag is set only nodes are
     * extracted that have a distance of the given shell value to the reference node. Otherwise, the graph until and
     * including that shell is extracted.
     *
     * @param shell The shell that is to be extracted (after that depth the algorithm terminates).
     * @param onlyShell If the "onlyShell" flag is set only nodes are extracted that have a distance of the given shell
     * value to the reference node. Otherwise, the graph until and including that shell is extracted.
     */
    private void extractNeighborhood(int shell, boolean onlyShell) {
        // initialize with given node
        if (!onlyShell) {
            this.nodesOfSubgraph.add(this.referenceNode);
        }
        this.currentWave.offer(this.referenceNode);
        // reduce the remaining depth with each wave
        while (shell > 0) {
            // process current wave
            while (!this.currentWave.isEmpty()) {
                NodeType currentNode = this.currentWave.poll();
                this.visitedNodes.add(currentNode);
                // process neighbors of the current wave
                for (NodeType neighbour : currentNode.getNeighbours()) {
                    // if this neighbor has not already been processed
                    if (!this.visitedNodes.contains(neighbour)) {
                        if (!onlyShell) {
                            this.nodesOfSubgraph.add(neighbour);
                            this.edgesOfSubgraph.add(this.graph.getEdgeBetween(currentNode, neighbour).get());
                            addConnectionsToVisitedNodes(neighbour);
                        }
                        this.nextWave.add(neighbour);
                        this.visitedNodes.add(neighbour);
                    }
                }
            }
            // current wave has been processed and is updated with the nodes that have been found
            this.currentWave = this.nextWave;
            if (onlyShell && shell == 1) {
                this.nodesOfSubgraph.addAll(this.nextWave);
            }
            this.nextWave = new ArrayDeque<>();
            shell--;
        }
    }

    /**
     * Adds edges to the newly added neighbour to eventually already visited nodes.
     * @param neighbour The neighbour to add edges to.
     */
    private void addConnectionsToVisitedNodes(NodeType neighbour) {
        for (NodeType visitedNode : this.visitedNodes) {
            Optional<EdgeType> edge;
            if ((edge = this.graph.getEdgeBetween(neighbour, visitedNode)).isPresent()) {
                this.edgesOfSubgraph.add(edge.get());
            }
        }
    }

    /**
     * Assembles the subgraph from the components collected during the algorithm.
     *
     * @return The extracted subgraph.
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
