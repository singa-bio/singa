package de.bioforscher.singa.mathematics.graphs.model;

import de.bioforscher.singa.mathematics.vectors.Vector2D;

/**
 * A regular node is the simplest instantiable implementation of nodes for graphs.
 *
 * @author cl
 */
public class RegularNode extends AbstractNode<RegularNode, Vector2D, Integer> {

    /**
     * Creates a new regular node with the given identifier.
     *
     * @param identifier The identifier.
     */
    public RegularNode(int identifier) {
        super(identifier);
    }

    /**
     * Creates a new regular node with the given identifier and the specified position.
     *
     * @param identifier The identifier.
     * @param position   The position.
     */
    public RegularNode(int identifier, Vector2D position) {
        super(identifier, position);
    }

    private RegularNode(RegularNode node) {
        super(node);
    }

    @Override
    public RegularNode getCopy() {
        return new RegularNode(this);
    }
}
