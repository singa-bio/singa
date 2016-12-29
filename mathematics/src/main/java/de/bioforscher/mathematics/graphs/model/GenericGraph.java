package de.bioforscher.mathematics.graphs.model;

import de.bioforscher.mathematics.vectors.Vector;

/**
 * @author cl
 */
public class GenericGraph<ContentType> extends AbstractGraph<GenericNode<ContentType>, GenericEdge<ContentType>, Vector> {

    @Override
    public int addEdgeBetween(int identifier, GenericNode<ContentType> source, GenericNode<ContentType> target) {
        return addEdgeBetween(new GenericEdge<>(identifier), source, target);
    }

    @Override
    public int addEdgeBetween(GenericNode<ContentType> source, GenericNode<ContentType> target) {
        return addEdgeBetween(nextEdgeIdentifier(), source, target);
    }


}
