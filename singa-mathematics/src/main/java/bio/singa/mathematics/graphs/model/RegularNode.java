package bio.singa.mathematics.graphs.model;

import bio.singa.mathematics.vectors.Vector2D;

import java.util.List;

/**
 * A regular node is the simplest instantiable implementation of nodes for graphs.
 *
 * @author cl
 */
public class RegularNode extends AbstractNode<RegularNode, Vector2D, Integer> {

    private List<String> bindingSites;

    public RegularNode(int identifier) {
        super(identifier);
    }

    public RegularNode(int identifier, Vector2D position) {
        super(identifier, position);
    }

    private RegularNode(RegularNode node) {
        super(node);
    }

    public List<String> getBindingSites() {
        return bindingSites;
    }

    public void setBindingSites(List<String> bindingSites) {
        this.bindingSites = bindingSites;
    }

    @Override
    public RegularNode getCopy() {
        return new RegularNode(this);
    }
}
