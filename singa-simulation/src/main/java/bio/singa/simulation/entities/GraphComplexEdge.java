package bio.singa.simulation.entities;

import bio.singa.mathematics.graphs.model.AbstractEdge;

/**
 * @author cl
 */
public class GraphComplexEdge extends AbstractEdge<GraphComplexNode> {

    private BindingSite connectedSite;

    public GraphComplexEdge(int identifier) {
        super(identifier);
    }

    public GraphComplexEdge(GraphComplexEdge edge, int identifierOffset) {
        this(edge.getIdentifier()+identifierOffset);
        connectedSite = edge.getConnectedSite();
    }

    @Override
    public GraphComplexEdge getCopy() {
        return new GraphComplexEdge(this, 0);
    }

    public GraphComplexEdge getCopy(int identifierOffset) {
        return new GraphComplexEdge(this, identifierOffset);
    }

    public BindingSite getConnectedSite() {
        return connectedSite;
    }

    public void setConnectedSite(BindingSite connectedSite) {
        this.connectedSite = connectedSite;
    }
}
