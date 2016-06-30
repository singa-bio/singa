package de.bioforscher.mathematics.graphs.util;

import de.bioforscher.mathematics.vectors.Vector2D;

/**
 * Created by Christoph on 23.05.2016.
 */
public class RectangularGridCoordinateConverter {

    private int columns;
    private int rows;

    public RectangularGridCoordinateConverter(int columns, int rows) {
        this.columns = columns;
        this.rows = rows;
    }

    public Vector2D convert(int nodeIdentifier) {
        if (nodeIdentifier > columns * rows) {
            throw new IndexOutOfBoundsException(
                    "The node identifier " + nodeIdentifier + " is out of the possible range (" + (columns * rows) +
                            ") of a rectangular grid graph with " + this.rows + " rows and " + this.columns +
                            " columns.");
        }
        // TODO eventually reversed
        int x = (int) (nodeIdentifier / (double) this.columns);
        double y = nodeIdentifier % this.columns;
        return new Vector2D(x, y);
    }

    public int convert(Vector2D coordinate) {
        if (coordinate.getX() > columns) {
            throw new IndexOutOfBoundsException(
                    "The x coordinate " + coordinate.getX() + " is out of the possible range " + columns + ".");
        } else if (coordinate.getY() > rows) {
            throw new IndexOutOfBoundsException(
                    "The x coordinate " + coordinate.getY() + " is out of the possible range " + rows + ".");
        }
        return (int) (coordinate.getX() * columns + coordinate.getY());
    }
}
