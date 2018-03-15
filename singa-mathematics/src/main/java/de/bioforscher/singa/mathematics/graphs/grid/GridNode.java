package de.bioforscher.singa.mathematics.graphs.grid;

import de.bioforscher.singa.mathematics.graphs.model.AbstractNode;
import de.bioforscher.singa.mathematics.topology.grids.rectangular.RectangularCoordinate;
import de.bioforscher.singa.mathematics.vectors.Vector2D;

/**
 * @author cl
 */
public class GridNode extends AbstractNode<GridNode, Vector2D, RectangularCoordinate> {

    public GridNode(RectangularCoordinate identifier) {
        super(identifier);
    }

    private GridNode(GridNode node) {
        super(node);
    }

    @Override
    public GridNode getCopy() {
        return new GridNode(this);
    }
}
