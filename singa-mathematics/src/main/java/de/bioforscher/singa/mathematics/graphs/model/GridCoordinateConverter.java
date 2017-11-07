package de.bioforscher.singa.mathematics.graphs.model;

import de.bioforscher.singa.mathematics.vectors.Vector2D;

/**
 * @author cl
 */
public class GridCoordinateConverter {

    private final int numberOfColumns;
    private final int numberOfRows;

    public GridCoordinateConverter(int numberOfColumns, int numberOfRows) {
        this.numberOfColumns = numberOfColumns;
        this.numberOfRows = numberOfRows;
    }

    public int getNumberOfColumns() {
        return numberOfColumns;
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }

    public Vector2D convert(int nodeIdentifier) {
        if (nodeIdentifier > numberOfColumns * numberOfRows) {
            throw new IndexOutOfBoundsException(
                    "The node identifier " + nodeIdentifier + " is out of the possible range (" + (numberOfColumns * numberOfRows) +
                            ") of a rectangular grid graph with " + numberOfRows + " numberOfRows and " + numberOfColumns +
                            " numberOfColumns.");
        }
        int y = (int) (nodeIdentifier / (double) numberOfColumns);
        double x = nodeIdentifier % numberOfColumns;
        return new Vector2D(x, y);
    }

    public int convert(Vector2D coordinate) {
        if (coordinate.getX() > numberOfColumns) {
            throw new IndexOutOfBoundsException(
                    "The x coordinate " + coordinate.getX() + " is out of the possible range " + numberOfColumns + ".");
        } else if (coordinate.getY() > numberOfRows) {
            throw new IndexOutOfBoundsException(
                    "The x coordinate " + coordinate.getY() + " is out of the possible range " + numberOfRows + ".");
        }
        return (int) (coordinate.getY() * numberOfColumns + coordinate.getX());
    }
}
