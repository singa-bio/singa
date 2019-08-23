package bio.singa.chemistry.entities.graphcomplex;

import bio.singa.mathematics.graphs.model.AbstractEdge;

/**
 * @author cl
 */
public class GraphComplexEdge extends AbstractEdge<GraphComplexNode> {

    public GraphComplexEdge(int identifier) {
        super(identifier);
    }

    public GraphComplexEdge(GraphComplexEdge edge, int identifierOffset) {
        this(edge.getIdentifier()+identifierOffset);
    }

    @Override
    public GraphComplexEdge getCopy() {
        return new GraphComplexEdge(this, 0);
    }

    public GraphComplexEdge getCopy(int identifierOffset) {
        return new GraphComplexEdge(this, identifierOffset);
    }

}
