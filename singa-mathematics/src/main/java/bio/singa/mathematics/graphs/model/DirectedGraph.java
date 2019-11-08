package bio.singa.mathematics.graphs.model;

import bio.singa.mathematics.vectors.Vector2D;

import java.util.Optional;

/**
 * @author fk
 */
public class DirectedGraph<NodeType extends Node<NodeType, Vector2D, Integer>> extends AbstractMapGraph<NodeType, DirectedEdge<NodeType>, Vector2D, Integer> {
    @Override
    public int addEdgeBetween(int identifier, NodeType source, NodeType target) {
        return addEdgeBetween(new DirectedEdge<>(identifier), source, target);
    }

    @Override
    public int addEdgeBetween(NodeType source, NodeType target) {
        return addEdgeBetween(nextEdgeIdentifier(), source, target);
    }

    public int addEdgeBetween(DirectedEdge<NodeType> edge, NodeType source, NodeType target) {
        edge.setSource(source);
        edge.setTarget(target);
        edges.put(edge.getIdentifier(), edge);
        source.addNeighbour(target);
        return edge.getIdentifier();
    }

    @Override
    public Integer nextNodeIdentifier() {
        if (getNodes().isEmpty()) {
            return 0;
        }
        return getNodes().size();
    }

    @Override
    public Optional<DirectedEdge<NodeType>> getEdgeBetween(NodeType first, NodeType second) {
        return getEdges().stream()
                .filter(edge -> edge.getSource().equals(first) && edge.getTarget().equals(second))
                .findAny();
    }
}
