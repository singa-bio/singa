package de.bioforscher.mathematics.graphs.model;

import de.bioforscher.mathematics.vectors.Vector;

import java.util.*;

public abstract class AbstractGraph<NodeType extends Node<NodeType, VectorType>, EdgeType extends
        Edge<NodeType>, VectorType extends Vector>
        implements Graph<NodeType, EdgeType> {

    /**
     * A iterating variable to add a new node.
     */
    private int nextNodeIdentifier;

    /**
     * A iterating variable to add a new edge.
     */
    private int nextEdgeIdentifier;

    /**
     * The nodes of the graph.
     */
    private Map<Integer, NodeType> nodes;

    /**
     * The edges of the graph.
     */
    private Map<Integer, EdgeType> edges;

    /**
     * Creates a new graph object.
     */
    public AbstractGraph() {
        this(10, 10);
    }

    /**
     * Creates a new Graph object with an initial load capacity for the node and
     * edge list.
     *
     * @param nodeCapacity The initial capacity for the node list.
     * @param edgeCapacity The initial capacity for the edge list.
     */
    public AbstractGraph(int nodeCapacity, int edgeCapacity) {
        this.nodes = new HashMap<>(nodeCapacity);
        this.edges = new HashMap<>(edgeCapacity);
    }

    @Override
    public int nextNodeIdentifier() {
        return this.nextNodeIdentifier++;
    }

    /**
     * Gets all the nodes in the graph.
     *
     * @return All the nodes in the graph.
     */
    @Override
    public Set<NodeType> getNodes() {
        return new HashSet<>(this.nodes.values());
    }

    /**
     * Gets a node by its identifier.
     *
     * @param identifier The identifier.
     * @return The node.
     */
    @Override
    public NodeType getNode(int identifier) {
        return this.nodes.get(identifier);
    }

    /**
     * Adds a single node.
     *
     * @param node The node.
     */
    @Override
    public int addNode(NodeType node) {
        this.nodes.put(node.getIdentifier(), node);
        return node.getIdentifier();
    }

    /**
     * Removes the node with the given identifier from the Graph. Also removes
     * all edges, that refer to this node.
     *
     * @param identifier The identifier of the node.
     */
    @Override
    public void removeNode(int identifier) {
        this.nodes.entrySet().removeIf(node -> node.getValue().getIdentifier() == identifier);
        this.edges.entrySet().removeIf(edge -> edge.getValue().containsNode(identifier));
    }

    @Override
    public int nextEdgeIdentifier() {
        return this.nextEdgeIdentifier++;
    }

    /**
     * Gets all the edges in the graph.
     *
     * @return All the edges in the graph.
     */
    @Override
    public Set<EdgeType> getEdges() {
        return new HashSet<>(this.edges.values());
    }

    /**
     * Gets a edge by its identifier.
     *
     * @param identifier The identifier.
     * @return The node.
     */
    @Override
    public EdgeType getEdge(int identifier) {
        return this.edges.get(identifier);
    }

    public int addEdgeBetween(EdgeType edge, NodeType source, NodeType target) {
        edge.setSource(source);
        edge.setTarget(target);
        this.edges.put(edge.getIdentifier(), edge);
        source.addNeighbour(target);
        target.addNeighbour(source);
        return edge.getIdentifier();
    }

    public abstract int addEdgeBetween(int identifier, NodeType source, NodeType target);

    public abstract int addEdgeBetween(NodeType source, NodeType target);

    /**
     * @param node The node.
     * @return {@code true} only if the node is in the graph.
     */
    @Override
    public boolean containsNode(Object node) {
        return this.nodes.containsValue(node);
    }

    /**
     * @param edge The edge.
     * @return {@code true} only if the edge is in the graph.
     */
    @Override
    public boolean containsEdge(Object edge) {
        return this.edges.containsValue(edge);
    }

    public int getMaximumDegree() {
        return this.nodes.values().stream()
                         .mapToInt(Node::getDegree)
                         .max()
                         .orElse(0);
    }

    @Override
    public String toString() {
        return "Graph [contains " + this.nodes.size() + " nodes and " + this.edges.size() + " edges]";
    }

}
