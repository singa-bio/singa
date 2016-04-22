package de.bioforscher.mathematics.graphs.model;

import de.bioforscher.mathematics.vectors.Vector;

import java.util.*;

public abstract class AbstractGraph<NodeType extends Node<NodeType, ? extends Vector>, EdgeType extends Edge<NodeType>>
        implements Graph<NodeType, EdgeType> {

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
        this.nodes = new HashMap<Integer, NodeType>(nodeCapacity);
        this.edges = new HashMap<Integer, EdgeType>(edgeCapacity);
    }

    public int getNextNodeIdentifier() {
        if (this.nodes.keySet().isEmpty()) {
            return 0;
        }
        return Collections.max(this.nodes.keySet()) + 1;
    }

    /**
     * Gets all the nodes in the graph.
     *
     * @return All the nodes in the graph.
     */
    @Override
    public Set<NodeType> getNodes() {
        return new HashSet<NodeType>(this.nodes.values());
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
    public void addNode(NodeType node) {
        this.nodes.put(node.getIdentifier(), node);
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

    public void removeNode(RegularNode node) {
        removeNode(node.getIdentifier());
    }

    public int getNextEdgeIdentifier() {
        if (this.edges.keySet().isEmpty()) {
            return 0;
        }
        return Collections.max(this.edges.keySet()) + 1;
    }

    /**
     * Gets all the edges in the graph.
     *
     * @return All the edges in the graph.
     */
    @Override
    public Set<EdgeType> getEdges() {
        return new HashSet<EdgeType>(this.edges.values());
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

    @Override
    public void connect(int identifier, NodeType source, NodeType target, Class<EdgeType> edgeClass) {
        EdgeType edge = null;
        try {
            edge = edgeClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        edge.setIdentifier(identifier);
        edge.setSource(source);
        edge.setTarget(target);
        connectWithEdge(identifier, source, target, edge);
    }

    protected void connectWithEdge(int identifier, NodeType source, NodeType target, EdgeType edge) {
        this.edges.put(identifier, edge);
        source.addNeighbour(target);
        target.addNeighbour(source);
    }

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

    @Override
    public String toString() {
        return "Graph [contains " + this.nodes.size() + " nodes and " + this.edges.size() + " edges]";
    }

}
