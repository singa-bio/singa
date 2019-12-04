package bio.singa.mathematics.graphs.model;

import bio.singa.mathematics.vectors.Vector2D;

import static bio.singa.mathematics.graphs.model.GraphPredicates.*;

/**
 * The graph class contains {@link Node}s connected by {@link Edge}s.
 *
 * @author cl
 */
public class UndirectedGraph extends AbstractMapGraph<RegularNode, UndirectedEdge, Vector2D, Integer> {

    /**
     * Adds a node with the given position to the graph.
     *
     * @param position The position of the node.
     * @return The identifier of the added node.
     */
    public int addNode(Vector2D position) {
        return addNode(new RegularNode(nextNodeIdentifier(), position));
    }

    /**
     * Adds a node with the given position to the graph, but only if no node with the exact same position is not
     * already present.
     *
     * @param position The position of the node.
     * @return The identifier of the added node.
     */
    public RegularNode snapNode(Vector2D position) {
        return addNodeIf(node -> nodeHasPosition(node, position),
                new RegularNode(nextNodeIdentifier(), position));
    }

    @Override
    public int addEdgeBetween(int identifier, RegularNode source, RegularNode target) {
        return addEdgeBetween(new UndirectedEdge(identifier), source, target);
    }

    @Override
    public int addEdgeBetween(RegularNode source, RegularNode target) {
        return addEdgeBetween(nextEdgeIdentifier(), source, target);
    }

    public int addEdgeBetween(int sourceIdentifier, int targetIdentifier) {
        return addEdgeBetween(getNode(sourceIdentifier), getNode(targetIdentifier));
    }

    @Override
    public Integer nextNodeIdentifier() {
        if (getNodes().isEmpty()) {
            return 0;
        }
        return getNodes().size();
    }

}
