package de.bioforscher.singa.mathematics.graphs.model;

import de.bioforscher.singa.mathematics.vectors.Vector;

import java.lang.reflect.InvocationTargetException;

/**
 * The edge object connects two nodes of the same type.
 *
 * @param <NodeType> The type of the node to connect with this edge.
 * @author cl
 */
public interface Edge<NodeType extends Node<NodeType, ? extends Vector, ?>> {

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
     * @param node The node to look for.
     * @return true if a node with the given identifier is either source or target of the node.
     */
    boolean containsNode(NodeType node);

    default <E extends Edge<NodeType>> E getCopy() {
        try {
            return (E) getClass().getConstructor(getClass()).newInstance(this);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
            throw new UnsupportedOperationException("Instance types must match to copy successfully.");
        }
    }

}
