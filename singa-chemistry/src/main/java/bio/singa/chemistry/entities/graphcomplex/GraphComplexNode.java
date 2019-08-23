package bio.singa.chemistry.entities.graphcomplex;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.graphs.model.AbstractNode;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.mathematics.vectors.Vectors;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class GraphComplexNode extends AbstractNode<GraphComplexNode, Vector2D, Integer> {

    private static Rectangle rectange = new Rectangle(1,1);

    private ChemicalEntity entity;
    private List<BindingSite> bindingSites;

    public GraphComplexNode(Integer identifier) {
        super(identifier, Vectors.generateRandom2DVector(rectange));
        bindingSites = new ArrayList<>();
    }

    public GraphComplexNode(Integer identifier, Vector2D position) {
        super(identifier, position);
    }

    private GraphComplexNode(GraphComplexNode node, int identifierOffset) {
        super(node.getIdentifier()+identifierOffset, node.getPosition().getCopy());
        entity = node.getEntity();
        bindingSites = new ArrayList<>(node.getBindingSites());
    }

    public ChemicalEntity getEntity() {
        return entity;
    }

    public void setEntity(ChemicalEntity entity) {
        this.entity = entity;
    }

    public List<BindingSite> getBindingSites() {
        return bindingSites;
    }

    public void setBindingSites(List<BindingSite> bindingSites) {
        this.bindingSites = bindingSites;
    }

    public void addBindingSite(BindingSite bindingSite) {
        bindingSites.add(bindingSite);
    }

    public boolean hasBindingSite(BindingSite bindingSite) {
        return bindingSites.contains(bindingSite);
    }

    public boolean hasOccupiedBindingSite(BindingSite bindingSite) {
        if (!hasBindingSite(bindingSite)) {
            return false;
        }
        for (GraphComplexNode neighbour : getNeighbours()) {
            if (neighbour.hasBindingSite(bindingSite)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasUnoccupiedBindingSite(BindingSite bindingSite) {
        if (!hasBindingSite(bindingSite)) {
            return false;
        }
        for (GraphComplexNode neighbour : getNeighbours()) {
            if (neighbour.hasBindingSite(bindingSite)) {
                return false;
            }
        }
        return true;
    }


    public boolean hasMatchingBindingSites(GraphComplexNode other) {
        for (BindingSite thisSite : bindingSites) {
            for (BindingSite thatSite : other.bindingSites) {
                if (thisSite.equals(thatSite)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public GraphComplexNode getCopy() {
        return new GraphComplexNode(this, 0);
    }

    public GraphComplexNode getCopy(int identifierOffset) {
        return new GraphComplexNode(this, identifierOffset);
    }
}
