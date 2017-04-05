package de.bioforscher.singa.mathematics.graphs.model;

import java.util.Collection;
import java.util.Set;

/**
 * The graph contains nodes connected by edges of a certain type.
 *
 * @param <NodeType> The type of the nodes in the graph.
 * @param <EdgeType> The type of the edges in the graph.
 * @author cl
 */
public interface Graph<NodeType extends Node<NodeType, ?>, EdgeType extends Edge<NodeType>> {

    /**
     * Returns all nodes.
     *
     * @return All nodes.
     */
    Collection<NodeType> getNodes();

    /**
     * Returns the node with the given identifier.
     *
     * @param identifier The identifier of the node.
     * @return The node with the given identifier.
     */
    NodeType getNode(int identifier);

    /**
     * Adds a node to the graph.
     *
     * @param node The node to be added.
     * @return The identifier of the node.
     */
    int addNode(NodeType node);

    /**
     * Removes the node with the given identifier from the node. Edges connected to this node will also be removed.
     *
     * @param identifier The identifer of the node to be removed.
     */
    void removeNode(int identifier);

    /**
     * Returns all edges.
     *
     * @return All edges.
     */
    Set<EdgeType> getEdges();

    /**
     * Returns the node with the given identifier.
     *
     * @param identifier The identifier of the node.
     * @return The edge with the given identifier.
     */
    EdgeType getEdge(int identifier);

    /**
     * Adds an edge with the given identifier between the two given nodes. If an edge with the identifier is already
     * present in the graph it is overwritten.
     *
     * @param identifier The identifier of the edge to be inserted.
     * @param source     The source node.
     * @param target     The target node.
     * @return The identifier of the added edge.
     */
    int addEdgeBetween(int identifier, NodeType source, NodeType target);

    /**
     * Adds the given edge to the graph setting the given source an target nodes. If an edge with the identifier is
     * already present in the graph it is overwritten.
     *
     * @param edge   The edge to be added.
     * @param source The source node.
     * @param target The target node.
     * @return The identifier of the added edge.
     */
    int addEdgeBetween(EdgeType edge, NodeType source, NodeType target);

    /**
     * Adds a new edge between the source and target nodes to the graph using the next free edge identifier.
     *
     * @param source The source node.
     * @param target The target node.
     * @return The identifier of the added edge.
     */
    int addEdgeBetween(NodeType source, NodeType target);

    /**
     * Returns true, if the graph contains the node and false otherwise.
     *
     * @param node The node to be searched.
     * @return true, if the graph contains the node and false otherwise.
     */
    boolean containsNode(Object node);

    /**
     * Returns true, if the graph contains the edge and false otherwise.
     *
     * @param edge The edge to be searched.
     * @return true, if the graph contains the edge and false otherwise.
     */
    boolean containsEdge(Object edge);

    /**
     * Returns the next free node identifier.
     *
     * @return The next free node identifier.
     */
    default int nextNodeIdentifier() {
        if (getNodes().isEmpty()) {
            return 0;
        }
        return getEdges().size();
    }

    /**
     * Returns the next free edge identifier.
     *
     * @return The next free edge identifier.
     */
    default int nextEdgeIdentifier() {
        if (getEdges().isEmpty()) {
            return 0;
        }
        return getEdges().size() + 1;
    }


}
