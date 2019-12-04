package bio.singa.mathematics.graphs.model;

import java.util.LinkedList;

/**
 * @author cl
 */
public class GraphPath<NodeType extends Node<NodeType, ?, ?>, EdgeType extends Edge<NodeType>>  {

    private static final GraphPath emptyPath = new GraphPath();

    private LinkedList<NodeType> nodes;
    private LinkedList<EdgeType> edges;

    public static <N extends Node<N, ?, ?>, E extends Edge<N>> GraphPath<N,E> emptyPath() {
        return (GraphPath<N,E>) emptyPath;
    }

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

    public int size() {
        return nodes.size();
    }

}
