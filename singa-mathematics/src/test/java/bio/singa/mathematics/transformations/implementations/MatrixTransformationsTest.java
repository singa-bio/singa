package bio.singa.mathematics.transformations.implementations;

import bio.singa.mathematics.matrices.Matrix;
import bio.singa.mathematics.matrices.RegularMatrix;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * @author cl
 */
class MatrixTransformationsTest {

    @Test
    void testMinMax() {
        Matrix matrix = new RegularMatrix(new double[][]{{1}, {3}, {5}, {4}, {7}, {9}});
        Matrix transformedMatrix = matrix.applyTransformation(MatrixTransformations.MIN_MAX);
        assertArrayEquals(new double[]{0.0, 0.25, 0.50, 0.375, 0.75, 1.0},
                transformedMatrix.getColumn(0).getElements(), 1E-3);
    }

    @Test
    void testZScore() {
        Matrix matrix = new RegularMatrix(new double[][]{{1}, {3}, {5}, {4}, {7}, {9}});
        Matrix transformedMatrix = matrix.applyTransformation(MatrixTransformations.Z_SCORE);
        assertArrayEquals(new double[]{-1.3414, -0.6415, 0.0583, -0.2916, 0.7582, 1.45803},
                transformedMatrix.getColumn(0).getElements(), 1E-4);
    }
}