package bio.singa.mathematics.algorithms.topology;

import bio.singa.mathematics.topology.grids.rectangular.RectangularCoordinate;
import bio.singa.mathematics.topology.grids.rectangular.RectangularGrid;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author cl
 */
public class FloodFillTest {


    @Test
    public void shouldFillCentre() {
        // initialize
        RectangularGrid<Integer> grid = new RectangularGrid<>(10, 10);
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                if (row == 2 || row == 7 || col == 2 || col == 7) {
                    grid.setValue(col, row, 1);
                } else {
                    grid.setValue(col, row, 0);
                }
            }
        }
        // fill with twos
        FloodFill.fill(grid, new RectangularCoordinate(4, 4), value -> value.equals(1), position -> grid.setValue(position, 2), value -> value.equals(2));
        // assert
        Integer two = 2;
        for (int row = 3; row < 7; row++) {
            for (int col = 3; col < 7; col++) {
                assertEquals(grid.getValue(col,row), two);
            }
        }

    }

}