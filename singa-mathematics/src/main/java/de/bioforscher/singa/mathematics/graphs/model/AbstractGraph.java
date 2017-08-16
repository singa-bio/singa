package de.bioforscher.singa.mathematics.graphs.model;


import de.bioforscher.singa.mathematics.vectors.Vector;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This is a simple implementation of the graph interface, that handles the most common operations defined for adding
 * and removing edges as well as nodes. Nodes and edge are referenced in a HashMap with integer keys and can therefor
 * quickly be retrieved and inserted.
 *
 * @param <NodeType>   The type of the nodes in the graph.
 * @param <EdgeType>   The type of the edges in the graph.
 * @param <VectorType> The vector that is used to define the position of this node.
 * @author cl
 */
public abstract class AbstractGraph <NodeType extends Node<NodeType, VectorType, IdentifierType>,
        EdgeType extends Edge<NodeType>, VectorType extends Vector, IdentifierType>
        implements Graph<NodeType, EdgeType, IdentifierType> {



    /**
     * A iterating variable to add a new edge.
     */
    private int nextEdgeIdentifier;

    /**
     * The nodes of the graph.
     */
    private Map<IdentifierType, NodeType> nodes;

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
    public Collection<NodeType> getNodes() {
        return this.nodes.values();
    }

    @Override
    public NodeType getNode(IdentifierType identifier) {
        return this.nodes.get(identifier);
    }

    @Override
    public IdentifierType addNode(NodeType node) {
        this.nodes.put(node.getIdentifier(), node);
        return node.getIdentifier();
    }

    @Override
    public NodeType removeNode(NodeType node) {
        NodeType nodeToBeRemoved = this.nodes.values().stream()
                .filter(entry -> entry.equals(node))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Could not remove node " + node + "."));

        for (NodeType neighbor : nodeToBeRemoved.getNeighbours()) {
            neighbor.getNeighbours().remove(nodeToBeRemoved);
        }

        this.nodes.remove(node.getIdentifier());
        this.edges.entrySet().removeIf(edge -> edge.getValue().containsNode(node));
        return nodeToBeRemoved;
    }

    @Override
    public NodeType removeNode(IdentifierType identifier) {
        NodeType nodeToBeRemoved = this.nodes.values().stream()
                .filter(entry -> entry.getIdentifier().equals(identifier))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Could not remove node with identifier" + identifier + "."));

        for (NodeType neighbor : nodeToBeRemoved.getNeighbours()) {
            neighbor.getNeighbours().remove(nodeToBeRemoved);
        }

        this.nodes.remove(identifier);
        this.edges.entrySet().removeIf(edge -> edge.getValue().containsNode(nodeToBeRemoved));
        return nodeToBeRemoved;
    }


    @Override
    public int nextEdgeIdentifier() {
        return this.nextEdgeIdentifier++;
    }

    @Override
    public Collection<EdgeType> getEdges() {
        return this.edges.values();
    }

    @Override
    public EdgeType getEdge(int identifier) {
        return this.edges.get(identifier);
    }

    /**
     * Adds a new edge to the graph, connecting source and target nodes. This method also references source and target
     * as neighbors to each other.
     *
     * @param edge   The edge to be added.
     * @param source The source node.
     * @param target The target node.
     * @return The identifier of the added edge.
     */
    public int addEdgeBetween(EdgeType edge, NodeType source, NodeType target) {
        edge.setSource(source);
        edge.setTarget(target);
        this.edges.put(edge.getIdentifier(), edge);
        source.addNeighbour(target);
        target.addNeighbour(source);
        return edge.getIdentifier();
    }

    /**
     * Adds a new edge with the given identifier to the graph, connecting source and target nodes. This method also
     * references source and target as neighbors to each other.
     *
     * @param identifier The edge identifier.
     * @param source     The source node.
     * @param target     The target node.
     * @return The identifier of the added edge.
     */
    public abstract int addEdgeBetween(int identifier, NodeType source, NodeType target);

    /**
     * Adds a new edge with the next free identifier to the graph, connecting source and target nodes. This method also
     * references source and target as neighbors to each other.
     *
     * @param source The source node.
     * @param target The target node.
     * @return The identifier of the added edge.
     */
    public abstract int addEdgeBetween(NodeType source, NodeType target);

    @Override
    public boolean containsNode(Object node) {
        return this.nodes.containsValue(node);
    }

    @Override
    public boolean containsEdge(Object edge) {
        return this.edges.containsValue(edge);
    }

    /**
     * Returns the degree of the node with the highest degree in the graph. If no maximal degree exists, zero is
     * returned.
     *
     * @return The maximal degree of the graph.
     */
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
