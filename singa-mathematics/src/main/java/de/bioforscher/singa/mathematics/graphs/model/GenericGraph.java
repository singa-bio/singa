package de.bioforscher.singa.mathematics.graphs.model;

import de.bioforscher.singa.mathematics.vectors.Vector;

/**
 * The generic graph class connects generic nodes with generic edges. Generic in this sense means, that any content can
 * be associated to the nodes, connected by the edges.
 *
 * @param <ContentType> The content type of the nodes.
 * @author cl
 */
public class GenericGraph<ContentType> extends AbstractGraph<GenericNode<ContentType>, GenericEdge<ContentType>, Vector, Integer> {

    /**
     * A iterating variable to add a new node.
     */
    private int nextNodeIdentifier;

    @Override
    public int addEdgeBetween(int identifier, GenericNode<ContentType> source, GenericNode<ContentType> target) {
        return addEdgeBetween(new GenericEdge<>(identifier), source, target);
    }

    @Override
    public int addEdgeBetween(GenericNode<ContentType> source, GenericNode<ContentType> target) {
        return addEdgeBetween(nextEdgeIdentifier(), source, target);
    }

    @Override
    public Integer nextNodeIdentifier() {
        return nextNodeIdentifier++;
    }

}
