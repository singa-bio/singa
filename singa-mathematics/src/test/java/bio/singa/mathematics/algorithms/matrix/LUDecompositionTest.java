package bio.singa.mathematics.algorithms.matrix;

import bio.singa.mathematics.matrices.Matrix;
import bio.singa.mathematics.matrices.RegularMatrix;
import org.junit.jupiter.api.Test;

import static bio.singa.mathematics.NumberConceptAssertion.assertMatrixEquals;

/**
 * @author cl
 */
class LUDecompositionTest {

    @Test
    void shouldFindRowEcholonFromSimpleMatrix() {
        Matrix matrix = new RegularMatrix(new double[][]{{2, 1, -1, 8}, {-3, -1, 2, -11}, {-2, 1, 2, -3}});
        Matrix expected = new RegularMatrix(new double[][]{{-3, -1, 2, -11}, {0, 5.0 / 3.0, 2.0 / 3.0, 13.0 / 3.0}, {0, 0, 1.0 / 5.0, -1.0 / 5.0}});
        Matrix actual = LUDecomposition.calculateRowEchelonMatrix(matrix);
        assertMatrixEquals(expected, actual, 1e-10);
    }

    @Test
    void shouldFindRowEcholonFromPotentialDivisionByZero() {
        Matrix matrix = new RegularMatrix(new double[][]{{1, 2, 3}, {0, 5, 4}, {0, 10, 2}});
        Matrix expected = new RegularMatrix(new double[][]{{1, 2, 3}, {0, 10, 2}, {0, 0, 3}});
        Matrix actual = LUDecomposition.calculateRowEchelonMatrix(matrix);
        assertMatrixEquals(expected, actual, 0.0);
    }

    @Test
    void shouldFindRowEcholonFromLinearDependentRows() {
        Matrix matrix = new RegularMatrix(new double[][]{{1, 2, 3}, {0, 6, 4}, {0, 3, 2}});
        Matrix expected = new RegularMatrix(new double[][]{{1, 2, 3}, {0, 6, 4}, {0, 0, 0}});
        Matrix actual = LUDecomposition.calculateRowEchelonMatrix(matrix);
        assertMatrixEquals(expected, actual, 0.0);
    }

    @Test
    void shouldFindRowEcholonFromTwoByThree() {
        Matrix matrix = new RegularMatrix(new double[][]{{2, 3}, {0, 1}, {4, -1}});
        Matrix expected = new RegularMatrix(new double[][]{{4, -1}, {0, 7.0 / 2.0}, {0, 0}});
        Matrix actual = LUDecomposition.calculateRowEchelonMatrix(matrix);
        assertMatrixEquals(expected, actual, 0.0);
    }

}