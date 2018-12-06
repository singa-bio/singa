package bio.singa.mathematics.topology.grids.rectangular;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static bio.singa.mathematics.topology.grids.rectangular.MooreRectangularDirection.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author cl
 */
class MooreRectangularDirectionTest {

    private static RectangularGrid<String> rectangularGrid;

    @BeforeAll
    static void initialize() {
        rectangularGrid = new RectangularGrid<>(3, 3);
        rectangularGrid.setValue(0,0, "NW");
        rectangularGrid.setValue(1,0, "N");
        rectangularGrid.setValue(2,0, "NE");
        rectangularGrid.setValue(0,1, "W");
        rectangularGrid.setValue(1,1, "C");
        rectangularGrid.setValue(2,1, "E");
        rectangularGrid.setValue(0,2, "SW");
        rectangularGrid.setValue(1,2, "S");
        rectangularGrid.setValue(2,2, "SE");
    }

    @Test
    void testRelativeDirectionAssignment() {
        RectangularCoordinate c1 = getNeighborOf(new RectangularCoordinate(1, 1), NORTH_WEST, SOUTH_EAST);
        RectangularCoordinate c2 = getNeighborOf(new RectangularCoordinate(1, 1), NORTH_WEST, NORTH_WEST);
        RectangularCoordinate c3 = getNeighborOf(new RectangularCoordinate(1, 1), NORTH_WEST, NORTH_EAST);
        RectangularCoordinate c4 = getNeighborOf(new RectangularCoordinate(1, 1), NORTH_WEST, SOUTH_WEST);
        System.out.println(rectangularGrid.getValue(c1));
        System.out.println(rectangularGrid.getValue(c2));
        System.out.println(rectangularGrid.getValue(c3));
        System.out.println(rectangularGrid.getValue(c4));
    }

}