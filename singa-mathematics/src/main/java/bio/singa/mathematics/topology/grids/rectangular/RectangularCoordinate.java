package bio.singa.mathematics.topology.grids.rectangular;

import bio.singa.mathematics.topology.model.DiscreteCoordinate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static RectangularCoordinate fromString(String string) {
        Pattern pattern = Pattern.compile("\\((\\d+), (\\d+)\\)");
        Matcher matcher = pattern.matcher(string);
        if (matcher.matches()) {
            int column = Integer.valueOf(matcher.group(1));
            int row = Integer.valueOf(matcher.group(2));
            return new RectangularCoordinate(column, row);
        } else {
            throw new IllegalArgumentException("The string must be formatted as a rectangular coordinate: (column, row)");
        }
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
                return new RectangularCoordinate(column, row - 1);
            case SOUTH:
                return new RectangularCoordinate(column, row + 1);
            case EAST:
                return new RectangularCoordinate(column + 1, row);
            case WEST:
                return new RectangularCoordinate(column - 1, row);
            default:
                throw new IllegalStateException("The direction " + rectangularDirection + " is invalid for this coordinate type");
        }
    }

    @Override
    public RectangularDirection[] getAllDirections() {
        return RectangularDirection.values();
    }

    @Override
    public String toString() {
        return "(" + column + ", " + row + ")";
    }
}
