package de.bioforscher.singa.mathematics.topology.grids.rectangular;

import de.bioforscher.singa.mathematics.topology.model.DiscreteCoordinate;

/**
 * @author cl
 */
public class RectangularCoordinate implements DiscreteCoordinate<RectangularCoordinate, RectangularDirection> {

    private final int column;
    private final int row;

    public RectangularCoordinate(int column, int row) {
        this.column = column;
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    @Override
    public RectangularCoordinate getNeighbour(RectangularDirection rectangularDirection) {
        switch (rectangularDirection) {
            case NORTH:
                return new RectangularCoordinate(row - 1, column);
            case SOUTH:
                return new RectangularCoordinate(row + 1, column);
            case EAST:
                return new RectangularCoordinate(row, column + 1);
            case WEST:
                return new RectangularCoordinate(row, column - 1);
            default:
                throw new IllegalStateException("The direction " + rectangularDirection + " is invalid for this coordinate type");
        }
    }

    @Override
    public RectangularDirection[] getAllDirections() {
        return RectangularDirection.values();
    }

}
