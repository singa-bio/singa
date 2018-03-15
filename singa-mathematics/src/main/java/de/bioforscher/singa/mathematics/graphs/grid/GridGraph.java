package de.bioforscher.singa.mathematics.graphs.grid;

import de.bioforscher.singa.mathematics.vectors.Vector2D;

/**
 * @author cl
 */
public class GridGraph extends AbstractGridGraph<GridNode, GridEdge, Vector2D> {

    public GridGraph(int width, int height) {
        super(width, height);
    }

    @Override
    public int addEdgeBetween(int identifier, GridNode source, GridNode target) {
        return addEdgeBetween(new GridEdge(identifier), source, target);
    }

    @Override
    public int addEdgeBetween(GridNode source, GridNode target) {
        return addEdgeBetween(nextEdgeIdentifier(), source, target);
    }
}
