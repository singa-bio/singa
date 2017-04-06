package de.bioforscher.singa.mathematics.graphs.model;

import de.bioforscher.singa.mathematics.vectors.Vector;

/**
 * A simple implementation of th edge interface. References target and source nodes by source an target attributes.
 *
 * @param <NodeType> The type of nodes this edge connects.
 */
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

    /**
     * Creates a new empty edge.
     */
    protected AbstractEdge() {
    }

    /**
     * Creates a new edge with the given identifier.
     *
     * @param identifier The identifer.
     */
    public AbstractEdge(int identifier) {
        this.identifier = identifier;
    }

    /**
     * Creates a new edge without an identifer, but with source and target nodes defined.
     *
     * @param source The source.
     * @param target The target.
     */
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

    @Override
    public int getIdentifier() {
        return this.identifier;
    }

    @Override
    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    @Override
    public NodeType getSource() {
        return this.source;
    }

    @Override
    public void setSource(NodeType source) {
        this.source = source;
    }

    @Override
    public NodeType getTarget() {
        return this.target;
    }

    @Override
    public void setTarget(NodeType target) {
        this.target = target;
    }

    @Override
    public boolean containsNode(int identifier) {
        return this.source.getIdentifier() == identifier || this.target.getIdentifier() == identifier;
    }

    /**
     * Returns true only if the node is source or target of this edge.
     *
     * @param node The node.
     * @return true only if the node is source or target of this edge.
     */
    public boolean containsNode(NodeType node) {
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
