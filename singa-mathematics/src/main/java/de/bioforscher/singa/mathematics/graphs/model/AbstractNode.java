package de.bioforscher.singa.mathematics.graphs.model;

import de.bioforscher.singa.mathematics.vectors.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple implementation of the node interface. References to neighboring nodes are stored in a list. Every node is
 * positioned using a vector.
 *
 * @param <NodeType>   The node type that is defined for neighbors of this node.
 * @param <VectorType> The vector that is used to define the position of this node.
 * @author cl
 */
public abstract class AbstractNode<NodeType extends Node<NodeType, VectorType, IdentifierType>, VectorType extends Vector, IdentifierType>
        implements Node<NodeType, VectorType, IdentifierType> {

    /**
     * The identifier.
     */
    private IdentifierType identifier;

    /**
     * The neighbours.
     */
    private List<NodeType> neighbours;

    /**
     * A positional representation.
     */
    private VectorType position;

    /**
     * Creates a new node with the given identifier. The position in not initialized.
     *
     * @param identifier The identifier.
     */
    public AbstractNode(IdentifierType identifier) {
        this.identifier = identifier;
        neighbours = new ArrayList<>();
    }

    /**
     * Creates a new node with the given position.
     *
     * @param identifier The identifier
     * @param position The position
     */
    public AbstractNode(IdentifierType identifier, VectorType position) {
        this(identifier);
        this.position = position;
    }

    public AbstractNode(NodeType node) {
        this(node.getIdentifier(), node.getPosition().getCopy());
    }

    @Override
    public IdentifierType getIdentifier() {
        return identifier;
    }

    /**
     * Sets the identifier.
     *
     * @param identifier The identifier.
     */
    public void setIdentifier(IdentifierType identifier) {
        this.identifier = identifier;
    }

    @Override
    public List<NodeType> getNeighbours() {
        return neighbours;
    }

    /**
     * Sets the list of neighbours.
     *
     * @param neighbours The neighbours.
     */
    public void setNeighbours(List<NodeType> neighbours) {
        this.neighbours = neighbours;
    }

    @Override
    public void addNeighbour(NodeType node) {
        neighbours.add(node);
    }

    /**
     * Removes a neighbouring node.
     *
     * @param node The node to remove.
     */
    public void removeNeighbour(NodeType node) {
        neighbours.remove(node);
    }

    /**
     * Returns true if the list of neighbors contains the given node.
     *
     * @param node The node.
     * @return true if the list of neighbors contains the given node.
     */
    public boolean hasNeighbour(NodeType node) {
        return neighbours.contains(node);
    }


    @Override
    public VectorType getPosition() {
        return position;
    }

    @Override
    public void setPosition(VectorType position) {
        this.position = position;
    }

    @Override
    public int getDegree() {
        return neighbours.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractNode<?, ?, ?> that = (AbstractNode<?, ?, ?>) o;

        return identifier != null ? identifier.equals(that.identifier) : that.identifier == null;
    }

    @Override
    public int hashCode() {
        return identifier != null ? identifier.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Node " + identifier;
    }

}
