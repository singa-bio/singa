package bio.singa.mathematics.graphs.model;

import bio.singa.mathematics.vectors.Vector2D;

/**
 * @author cl
 */
public abstract class DirectedWeightedGraph<NodeType extends Node<NodeType, Vector2D, Integer>, EdgeType extends DirectedWeightedEdge<NodeType>> extends AbstractMapGraph<NodeType, EdgeType, Vector2D, Integer> {

    @Override
    public Integer nextNodeIdentifier() {
        if (getNodes().isEmpty()) {
            return 0;
        }
        return getNodes().size();
    }

}
