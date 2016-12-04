package de.bioforscher.mathematics.graphs.model;

import java.util.Collection;
import java.util.Set;

public interface Graph<NodeType extends Node<NodeType, ?>, EdgeType extends Edge<NodeType>> {

    Collection<NodeType> getNodes();

    NodeType getNode(int identifier);

    int addNode(NodeType node);

    void removeNode(int identifier);

    Set<EdgeType> getEdges();

    EdgeType getEdge(int identifier);

    int addEdgeBetween(int identifier, NodeType source, NodeType target);

    int addEdgeBetween(EdgeType edge, NodeType source, NodeType target);

    int addEdgeBetween(NodeType source, NodeType target);

    boolean containsNode(Object node);

    boolean containsEdge(Object edge);

    default int nextNodeIdentifier() {
        if (getNodes().isEmpty()) {
            return 0;
        }
        return getEdges().size();
    }

    default int nextEdgeIdentifier() {
        if (getEdges().isEmpty()) {
            return 0;
        }
        return getEdges().size()+1;
    }


}
