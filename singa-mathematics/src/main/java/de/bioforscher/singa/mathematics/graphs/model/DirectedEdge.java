package de.bioforscher.singa.mathematics.graphs.model;

import de.bioforscher.singa.mathematics.vectors.Vector;

/**
 * @author fk
 */
public class DirectedEdge<NodeType extends Node<NodeType, ? extends Vector, ?>> extends AbstractEdge<NodeType> {
    public DirectedEdge(int identifier) {
        super(identifier);
    }
}
