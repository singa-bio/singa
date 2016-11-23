package de.bioforscher.mathematics.graphs.model;

import de.bioforscher.mathematics.vectors.Vector2D;

/**
 * The graph class contains {@link Node}s connected by {@link Edge}s.
 *
 * @author Christoph Leberecht
 * @version 1.0.1
 */
public class UndirectedGraph extends AbstractGraph<RegularNode, UndirectedEdge, Vector2D> {

    public UndirectedGraph() {

    }

    public UndirectedGraph(int nodeCapacity, int edgeCapacity) {
        super(nodeCapacity, edgeCapacity);
    }

    @Override
    public void addEdgeBetween(RegularNode source, RegularNode target) {

    }

    public void connect(int identifier, RegularNode source, RegularNode target) {
        super.connect(identifier, source, target, UndirectedEdge.class);
    }



}
