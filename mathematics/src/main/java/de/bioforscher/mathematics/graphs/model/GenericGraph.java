package de.bioforscher.mathematics.graphs.model;

import de.bioforscher.mathematics.vectors.Vector;

/**
 * @author cl
 */
public class GenericGraph<ContentType> extends AbstractGraph<GenericNode<ContentType>, GenericEdge<ContentType>, Vector> {

    /**
     * A iterating variable to add a new bond.
     */
    private int nextBondIdentifier;

    @Override
    public void addEdgeBetween(GenericNode<ContentType> source, GenericNode<ContentType> target) {
        GenericEdge<ContentType> edge = new GenericEdge<>(this.nextBondIdentifier);
        edge.setSource(source);
        edge.setTarget(target);
        addEdge(this.nextBondIdentifier, edge);
        source.addNeighbour(target);
        target.addNeighbour(source);
        this.nextBondIdentifier++;
    }


}
