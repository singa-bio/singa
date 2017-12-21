package de.bioforscher.singa.mathematics.graphs.model;

import de.bioforscher.singa.mathematics.vectors.Vector;

import java.util.List;

/**
 * The node is the elementary object contained in graphs.
 *
 * @param <NodeType>   The node type that is defined for neighbors of this node.
 * @param <VectorType> The vector that is used to define the position of this node.
 * @author cl
 */
public interface Node<NodeType extends Node<NodeType, VectorType, IdentifierType>, VectorType extends Vector, IdentifierType> {

    /**
     * Returns the identifier of the node.
     *
     * @return The identifier of the node.
     */
    IdentifierType getIdentifier();

    /**
     * Returns the position of the node.
     *
     * @return The position of the node.
     */
    VectorType getPosition();

    /**
     * Sets the position of the node.
     *
     * @param position The position.
     */
    void setPosition(VectorType position);

    /**
     * Adds a neighbour to this node. This method should always be called in conjunction with adding an edge to the
     * graph.
     *
     * @param node The new neighbour
     */
    void addNeighbour(NodeType node);

    /**
     * Returns all neighbors of this node.
     *
     * @return The neighbours.
     */
    List<NodeType> getNeighbours();

    /**
     * Returns the degree (the number of neighbours) of the node.
     *
     * @return The degree of this node.
     */
    int getDegree();

    NodeType getCopy();
}
