package bio.singa.mathematics.transformations;

import bio.singa.mathematics.matrices.Matrix;
import bio.singa.mathematics.matrices.RegularMatrix;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class MinMaxNormalizationTest {

    @Test
    public void applyToMatrix() {
        Matrix matrix = new RegularMatrix(new double[][]{{1}, {3}, {5}, {4}, {7}, {9}});
        Matrix transformedMatrix = new MinMaxNormalization().applyTo(matrix);
        assertArrayEquals(new double[]{0.0, 0.25, 0.50, 0.375, 0.75, 1.0},
                transformedMatrix.getColumn(0).getElements(), 1E-3);
    }
}