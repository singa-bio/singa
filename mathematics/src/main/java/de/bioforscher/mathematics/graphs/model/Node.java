package de.bioforscher.mathematics.graphs.model;

import de.bioforscher.mathematics.vectors.Vector;

import java.util.List;

/**
 * The node is the elementary object contained in graphs.
 *
 * @param <NodeType>
 * @param <VectorType>
 */
public interface Node<NodeType extends Node<NodeType, VectorType>, VectorType extends Vector> {

    /**
     * Returns the identifier of the node.
     *
     * @return The identifier of the node.
     */
    int getIdentifier();

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
     * @return The neighbours.
     */
    List<NodeType> getNeighbours();

    /**
     * Returns the degree (the number of neighbours) of the node.
     * @return The degree of this node.
     */
    int getDegree();

}
