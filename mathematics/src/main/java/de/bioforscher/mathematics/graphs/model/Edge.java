package de.bioforscher.mathematics.graphs.model;

public interface Edge<NodeType extends Node<?, ?>> {

    int getIdentifier();

    void setIdentifier(int identifier);

    NodeType getSource();

    void setSource(NodeType node);

    NodeType getTarget();

    void setTarget(NodeType node);

    boolean containsNode(int identifier);

}
