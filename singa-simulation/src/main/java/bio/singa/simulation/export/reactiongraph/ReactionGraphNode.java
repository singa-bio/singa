package bio.singa.simulation.export.reactiongraph;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.graphs.model.AbstractNode;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.mathematics.vectors.Vectors;

/**
 * @author cl
 */
public class ReactionGraphNode extends AbstractNode<ReactionGraphNode, Vector2D, Integer> {

    private ChemicalEntity entity;

    public ReactionGraphNode(Integer identifier) {
        super(identifier, Vectors.generateRandom2DVector(new Rectangle(200, 200)));
    }

    public ReactionGraphNode(Integer identifier, ChemicalEntity entity) {
        this(identifier);
        this.entity = entity;
    }

    public ChemicalEntity getEntity() {
        return entity;
    }

    public void setEntity(ChemicalEntity entity) {
        this.entity = entity;
    }

    @Override
    public ReactionGraphNode getCopy() {
        return null;
    }
}
