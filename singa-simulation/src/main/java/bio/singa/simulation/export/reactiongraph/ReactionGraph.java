package bio.singa.simulation.export.reactiongraph;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.mathematics.graphs.model.DirectedWeightedGraph;

import java.util.Optional;

/**
 * @author cl
 */
public class ReactionGraph extends DirectedWeightedGraph<ReactionGraphNode, ReactionGraphEdge> {

    public Optional<ReactionGraphNode> getNodeWithContent(ChemicalEntity content) {
        for (ReactionGraphNode genericNode : getNodes()) {
            if (genericNode.getEntity().equals(content)) {
                return Optional.of(genericNode);
            }
        }
        return Optional.empty();
    }

    public ReactionGraphNode addNode(ChemicalEntity entity) {
        Optional<ReactionGraphNode> nodeWithContent = getNodeWithContent(entity);
        if (nodeWithContent.isPresent()) {
            return nodeWithContent.get();
        } else {
            ReactionGraphNode node = new ReactionGraphNode(nextNodeIdentifier(), entity);
            addNode(node);
            return node;
        }
    }

    @Override
    public int addEdgeBetween(int identifier, ReactionGraphNode source, ReactionGraphNode target) {
        return addEdgeBetween(new ReactionGraphEdge(identifier), source, target);
    }

    @Override
    public int addEdgeBetween(ReactionGraphNode source, ReactionGraphNode target) {
        return addEdgeBetween(nextEdgeIdentifier(), source, target);
    }

}
