package de.bioforscher.mathematics.graphs.model;

/**
 * The graph class contains {@link Node}s connected by {@link Edge}s.
 *
 * @param <NodeType> The type of node.
 * @param <EdgeType> The type of edge.
 * @author Christoph Leberecht
 * @version 1.0.1
 */
public class UndirectedGraph extends AbstractGraph<RegularNode, UndirectedEdge> {

    public UndirectedGraph() {

    }

    public UndirectedGraph(int nodeCapacity, int edgeCapacity) {
        super(nodeCapacity, edgeCapacity);
    }

    public void connect(int identifier, RegularNode source, RegularNode target) {
        super.connect(identifier, source, target, UndirectedEdge.class);
    }

}
