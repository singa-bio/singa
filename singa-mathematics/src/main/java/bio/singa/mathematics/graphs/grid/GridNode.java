package bio.singa.mathematics.graphs.grid;

import bio.singa.mathematics.graphs.model.AbstractNode;
import bio.singa.mathematics.topology.grids.rectangular.RectangularCoordinate;
import bio.singa.mathematics.vectors.Vector2D;

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
