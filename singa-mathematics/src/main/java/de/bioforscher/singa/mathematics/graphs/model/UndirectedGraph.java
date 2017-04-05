package de.bioforscher.singa.mathematics.graphs.model;

import de.bioforscher.singa.mathematics.vectors.Vector2D;

/**
 * The graph class contains {@link Node}s connected by {@link Edge}s.
 *
 * @author cl
 */
public class UndirectedGraph extends AbstractGraph<RegularNode, UndirectedEdge, Vector2D> {

    @Override
    public int addEdgeBetween(int identifier, RegularNode source, RegularNode target) {
        return addEdgeBetween(new UndirectedEdge(identifier), source, target);
    }

    @Override
    public int addEdgeBetween(RegularNode source, RegularNode target) {
        return addEdgeBetween(nextNodeIdentifier(), source, target);
    }


}
