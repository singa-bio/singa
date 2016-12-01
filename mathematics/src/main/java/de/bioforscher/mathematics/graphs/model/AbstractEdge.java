package de.bioforscher.mathematics.graphs.model;

import de.bioforscher.mathematics.vectors.Vector;

public abstract class AbstractEdge<NodeType extends Node<NodeType, ? extends Vector>> implements Edge<NodeType> {

    /**
     * An unique identifier.
     */
    protected int identifier;

    /**
     * The source {@link Node}.
     */
    protected NodeType source;

    /**
     * The target {@link Node}.
     */
    protected NodeType target;

    protected AbstractEdge() {
    }

    public AbstractEdge(int identifier) {
        this.identifier = identifier;
    }

    public AbstractEdge(NodeType source, NodeType target) {
        this.source = source;
        this.target = target;
    }

    /**
     * Creates a new Edge connecting two {@link Node}.
     *
     * @param source The source {@link Node}.
     * @param target The target {@link Node}.
     */
    public AbstractEdge(int identifier, NodeType source, NodeType target) {
        this.identifier = identifier;
        this.source = source;
        this.target = target;
    }

    /**
     * Gets the identifier.
     *
     * @return The identifier.
     */
    @Override
    public int getIdentifier() {
        return this.identifier;
    }

    /**
     * Sets the identifier. Should be used with caution!
     *
     * @param identifier The identifier.
     */
    @Override
    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    /**
     * Gets the source {@link Node}.
     *
     * @return The source {@link Node}.
     */
    @Override
    public NodeType getSource() {
        return this.source;
    }

    @Override
    public void setSource(NodeType source) {
        this.source = source;
    }

    /**
     * Gets the target {@link Node}.
     *
     * @return The target {@link Node}.
     */
    @Override
    public NodeType getTarget() {
        return this.target;
    }

    /**
     * Sets the target {@link Node}.
     *
     * @param target The target {@link Node}.
     */
    @Override
    public void setTarget(NodeType target) {
        this.target = target;
    }

    /**
     * Checks, if a {@link Node} with the given identifier is source or target
     * of this edge.
     *
     * @param identifier The identifier of the {@link Node}.
     * @return {@code true} only if the node is source or target of this edge.
     */
    @Override
    public boolean containsNode(int identifier) {
        return this.source.getIdentifier() == identifier || this.target.getIdentifier() == identifier;
    }

    /**
     * Checks, if a {@link Node} equals the source or target of this edge.
     *
     * @param node The {@link Node}.
     * @return {@code true} only if the node is source or target of this edge.
     */
    public boolean containsNode(RegularNode node) {
        return this.source.equals(node) || this.target.equals(node);
    }

    @Override
    public String toString() {
        return "Edge connecting " + this.source + " and " + this.target;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractEdge<?> that = (AbstractEdge<?>) o;
        return this.identifier == that.identifier;

    }

    @Override
    public int hashCode() {
        return this.identifier;
    }

}
