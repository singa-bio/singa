package de.bioforscher.singa.mathematics.graphs.util;

import de.bioforscher.singa.mathematics.vectors.Vector2D;

/**
 * @author cl
 */
public class RectangularGridCoordinateConverter {

    private final int numberOfColumns;
    private final int numberOfRows;

    public RectangularGridCoordinateConverter(int numberOfColumns, int numberOfRows) {
        this.numberOfColumns = numberOfColumns;
        this.numberOfRows = numberOfRows;
    }

    public int getNumberOfColumns() {
        return this.numberOfColumns;
    }

    public int getNumberOfRows() {
        return this.numberOfRows;
    }

    public Vector2D convert(int nodeIdentifier) {
        if (nodeIdentifier > this.numberOfColumns * this.numberOfRows) {
            throw new IndexOutOfBoundsException(
                    "The node identifier " + nodeIdentifier + " is out of the possible range (" + (this.numberOfColumns * this.numberOfRows) +
                            ") of a rectangular grid graph with " + this.numberOfRows + " numberOfRows and " + this.numberOfColumns +
                            " numberOfColumns.");
        }
        // TODO maybe reversed
        int x = (int) (nodeIdentifier / (double) this.numberOfColumns);
        double y = nodeIdentifier % this.numberOfColumns;
        return new Vector2D(x, y);
    }

    public int convert(Vector2D coordinate) {
        if (coordinate.getX() > this.numberOfColumns) {
            throw new IndexOutOfBoundsException(
                    "The x coordinate " + coordinate.getX() + " is out of the possible range " + this.numberOfColumns + ".");
        } else if (coordinate.getY() > this.numberOfRows) {
            throw new IndexOutOfBoundsException(
                    "The x coordinate " + coordinate.getY() + " is out of the possible range " + this.numberOfRows + ".");
        }
        return (int) (coordinate.getY() * this.numberOfColumns + coordinate.getX());
    }
}
