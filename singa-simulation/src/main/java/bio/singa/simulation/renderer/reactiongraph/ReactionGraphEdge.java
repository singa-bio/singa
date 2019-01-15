package bio.singa.simulation.renderer.reactiongraph;

import bio.singa.mathematics.graphs.model.DirectedWeightedEdge;

/**
 * @author cl
 */
public class ReactionGraphEdge extends DirectedWeightedEdge<ReactionGraphNode> {

    public ReactionGraphEdge(int identifier) {
        super(identifier);
    }

    public ReactionGraphEdge(int identifier, double weight) {
        super(identifier, weight);
    }

}
