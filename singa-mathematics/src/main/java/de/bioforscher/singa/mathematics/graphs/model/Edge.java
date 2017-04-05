package de.bioforscher.singa.mathematics.graphs.model;

/**
 * The edge object connects two nodes of the same type.
 *
 * @param <NodeType> The type of the node to connect with this edge.
 * @author cl
 */
public interface Edge<NodeType extends Node<NodeType, ?>> {

    /**
     * Returns the identifier of the edge in the graph.
     *
     * @return The identifier of the edge in the graph.
     */
    int getIdentifier();

    /**
     * Sets the identifier or the edge in the graph.
     *
     * @param identifier The identifier.
     */
    void setIdentifier(int identifier);

    /**
     * Returns the source node of the edge.
     *
     * @return The source node of the edge.
     */
    NodeType getSource();

    /**
     * Sets the source node of the edge.
     *
     * @param node The source node.
     */
    void setSource(NodeType node);

    /**
     * Returns the target node of the edge.
     *
     * @return The target node of the edge.
     */
    NodeType getTarget();

    /**
     * Sets the target node of the edge.
     *
     * @param node The target node.
     */
    void setTarget(NodeType node);

    /**
     * Returns true if a node with the given identifier is either source or target of the node.
     *
     * @param identifier The identifier of the node to look for.
     * @return true if a node with the given identifier is either source or target of the node.
     */
    boolean containsNode(int identifier);

}
