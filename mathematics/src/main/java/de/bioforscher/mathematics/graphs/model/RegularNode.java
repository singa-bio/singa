package de.bioforscher.mathematics.graphs.model;

import de.bioforscher.mathematics.vectors.Vector2D;

public class RegularNode extends AbstractNode<RegularNode, Vector2D> {

    public RegularNode(int identifier) {
        super(identifier);
    }

    public RegularNode(int identifier, Vector2D position) {
        super(identifier, position);
    }

}
