package de.bioforscher.mathematics.graphs.model;

import java.util.Set;

public interface Graph<NodeType extends Node<NodeType, ?>, EdgeType extends Edge<NodeType>> {

    Set<NodeType> getNodes();

    NodeType getNode(int identifier);

    void addNode(NodeType node);

    void removeNode(int identifier);

    Set<EdgeType> getEdges();

    EdgeType getEdge(int identifier);

    void connect(int identifier, NodeType source, NodeType target, Class<EdgeType> edgeClass);

    boolean containsNode(Object node);

    boolean containsEdge(Object edge);

}
