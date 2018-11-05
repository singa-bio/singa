package bio.singa.mathematics.topology.grids.rectangular;

import bio.singa.mathematics.topology.model.DiscreteDirection;

/**
 * @author cl
 */
public enum MooreRectangularDirection implements DiscreteDirection {

    NORTH(0, -1),
    NORTH_EAST(1, -1),
    EAST(1, 0),
    SOUTH_EAST(1, 1),
    SOUTH(0, 1),
    SOUTH_WEST(-1, 1),
    WEST(-1, 0),
    NORTH_WEST(-1, -1);

    int directionX;
    int directionY;

    MooreRectangularDirection(int directionX, int directionY) {
        this.directionX = directionX;
        this.directionY = directionY;
    }

    public static RectangularCoordinate getNeighborOf(RectangularCoordinate coordinate, MooreRectangularDirection coordinateDirection, MooreRectangularDirection relativeDirection) {
        int addX = 0;
        if (coordinateDirection.directionX != relativeDirection.directionX) {
            addX = relativeDirection.directionX;
        }
        int addY = 0;
        if (coordinateDirection.directionY != relativeDirection.directionY) {
            addY = relativeDirection.directionY;
        }
        return new RectangularCoordinate(coordinate.getColumn() + addX, coordinate.getRow() + addY);
    }

}
