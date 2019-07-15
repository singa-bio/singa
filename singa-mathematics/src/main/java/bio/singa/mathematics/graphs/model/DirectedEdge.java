package bio.singa.mathematics.graphs.model;

import bio.singa.mathematics.vectors.Vector;

/**
 * @author fk
 */
public class DirectedEdge<NodeType extends Node<NodeType, ? extends Vector, ?>> extends AbstractEdge<NodeType> {

    public DirectedEdge(DirectedEdge<NodeType> directedEdge) {
        super(directedEdge);
    }

    public DirectedEdge(int identifier) {
        super(identifier);
    }
}
