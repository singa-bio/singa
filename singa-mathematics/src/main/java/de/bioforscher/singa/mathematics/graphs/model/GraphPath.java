package de.bioforscher.singa.mathematics.graphs.model;

import java.util.LinkedList;

/**
 * @author cl
 */
public class GraphPath<NodeType extends Node<NodeType, ?, ?>, EdgeType extends Edge<NodeType>>  {

    private LinkedList<NodeType> nodes;
    private LinkedList<EdgeType> edges;

    public GraphPath() {
        nodes = new LinkedList<>();
        edges = new LinkedList<>();
    }

    public void addNode(NodeType node) {
        nodes.add(node);
    }

    public void addEdge(EdgeType edge) {
        edges.add(edge);
    }

    public LinkedList<NodeType> getNodes() {
        return nodes;
    }

    public LinkedList<EdgeType> getEdges() {
        return edges;
    }

}
