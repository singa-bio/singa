package de.bioforscher.singa.mathematics.algorithms.graphs;

import de.bioforscher.singa.mathematics.graphs.model.Edge;
import de.bioforscher.singa.mathematics.graphs.model.Graph;
import de.bioforscher.singa.mathematics.graphs.model.Node;
import de.bioforscher.singa.mathematics.vectors.Vector;

import java.util.*;

/**
 * Given a graph, the static method method returns a list of all disconnected subgraphs. The subgraphs are copies and
 * changes are not reflected back into the original graph, but node and edge identifiers, as well as attached data is
 * conserved.
 *
 * @author cl
 * @author fk
 * @see <a href="https://en.wikipedia.org/wiki/Connectivity_(graph_theory)">Wikipedia: Connectivity of Graphs</a>
 */
public class DisconnectedSubgraphFinder<NodeType extends Node<NodeType, VectorType>, EdgeType extends Edge<NodeType>,
        VectorType extends Vector, GraphType extends Graph<NodeType, EdgeType>> {

    private Queue<NodeType> queue;
    private GraphType graph;

    private Collection<NodeType> unprocessedNodes;
    private ArrayList<NodeType> currentNodes;

    private List<List<NodeType>> nodesOfSubgraphs;
    private List<List<EdgeType>> edgesOfSubgraphs;

    private DisconnectedSubgraphFinder(GraphType graph) {
        this.queue = new ArrayDeque<>();
        this.unprocessedNodes = new HashSet<>(graph.getNodes());
        this.nodesOfSubgraphs = new ArrayList<>();
        this.edgesOfSubgraphs = new ArrayList<>();
        this.graph = graph;
    }

    /**
     * Given a graph, this method returns a list of all disconnected subgraphs. The subgraphs are copies and changes are
     * not reflected back into the original graph, but node and edge identifiers, as well as attached data is conserved.
     *
     * @param graph The graph to decompose.
     * @param <NodeType> The type of the nodes.
     * @param <EdgeType> The type of the edges.
     * @param <GraphType> The type of the graph.
     * @return A list of all disconnected subgraphs.
     */
    public static <NodeType extends Node<NodeType, VectorType>,
            EdgeType extends Edge<NodeType>, VectorType extends Vector,
            GraphType extends Graph<NodeType, EdgeType>> List<GraphType> findDisconnectedSubgraphs(GraphType graph) {
        // create new instance for the given graph
        DisconnectedSubgraphFinder<NodeType, EdgeType, VectorType, GraphType> finder = new DisconnectedSubgraphFinder<>(graph);
        // while not every node has been assigned to a subgraph
        Optional<NodeType> nextSubgraphOrigin;
        while ((nextSubgraphOrigin = finder.getNextSubgraphOrigin()).isPresent()) {
            // use origin node to determine surrounding subgraph
            finder.processSubgraph(nextSubgraphOrigin.get());
        }
        // return the assembled subgraphs
        return finder.createSubgraphs();
    }

    /**
     * Determines the next starting point to process a subgraph.
     *
     * @return The next node, that has not already ben processed.
     */
    private Optional<NodeType> getNextSubgraphOrigin() {
        if (!unprocessedNodes.isEmpty()) {
            return Optional.of(unprocessedNodes.iterator().next());
        }
        return Optional.empty();
    }

    /**
     * Process a subgraph, that is initialized by a node and adds it to the internal list of subgraphs. The neighbors
     * are traversed in breath first.
     *
     * @param initialNode The initial node.
     */
    private void processSubgraph(NodeType initialNode) {
        // initialize collecting lists
        this.currentNodes = new ArrayList<>();
        ArrayList<EdgeType> currentEdges = new ArrayList<>();
        // add to que and subgraph, remove from unprocessed nodes
        processNode(initialNode);
        // as long as there are nodes on the queue (as long as there are connected nodes in this subgraph)
        NodeType currentNode;
        while ((currentNode = this.queue.poll()) != null) {
            // process neighbours
            for (NodeType neighbor : currentNode.getNeighbours()) {
                // if neighbour is not already in the subgraph
                if (!this.currentNodes.contains(neighbor)) {
                    // add to que and subgraph, remove from unprocessed nodes
                    processNode(neighbor);
                    // remember edge
                    currentEdges.add(graph.getEdgeBetween(currentNode, neighbor));
                }
            }
        }
        // add complete subgraph to nodes
        this.nodesOfSubgraphs.add(this.currentNodes);
        this.edgesOfSubgraphs.add(currentEdges);
    }

    /**
     * Adds a node to the queue and subgraph, and removes it from the unprocessed nodes.
     *
     * @param node The nodes.
     */
    private void processNode(NodeType node) {
        // add initial node to the current subgraph nodes
        this.currentNodes.add(node);
        // add initial node to queue
        this.queue.offer(node);
        // and remove it from the unprocessed stack
        this.unprocessedNodes.remove(node);
    }

    /**
     * Assembles the subgraphs from the paring node and edge lists.
     *
     * @return A list of all extracted subgraphs.
     */
    private List<GraphType> createSubgraphs() {
        List<GraphType> subgraphs = new ArrayList<>();
        // for each extracted list of connected nodes
        for (int i = 0; i < this.nodesOfSubgraphs.size(); i++) {
            // create a new graph
            GraphType subgraph = null;
            try {
                subgraph = (GraphType) graph.getClass().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            // copy and add nodes
            Objects.requireNonNull(subgraph);
            List<NodeType> nodes = this.nodesOfSubgraphs.get(i);
            for (NodeType node : nodes) {
                NodeType copy = node.getCopy();
                subgraph.addNode(copy);
            }
            // create and add edges for the nodes (preserving edge identifier)
            List<EdgeType> edges = this.edgesOfSubgraphs.get(i);
            for (EdgeType edge : edges) {
                NodeType source = subgraph.getNode(edge.getSource().getIdentifier());
                NodeType target = subgraph.getNode(edge.getTarget().getIdentifier());
                subgraph.addEdgeBetween(edge.getIdentifier(), source, target);
            }
            // add to list of subgraphs
            subgraphs.add(subgraph);
        }
        return subgraphs;
    }

}
