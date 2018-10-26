package bio.singa.mathematics.algorithms.matrix;

import bio.singa.mathematics.matrices.Matrix;
import bio.singa.mathematics.matrices.RegularMatrix;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * @author fk
 */
class EigenvalueDecompositionTest {

    @Test
    void shouldDoEigenvalueDecomposition() {
        Matrix matrix = new RegularMatrix(new double[][]{{1, 3, 5, 7}, {2, 4, 4, 8}, {3, 1, 2, 3}, {4, 3, 2, 1}});
        EigenvalueDecomposition eigenvalueDecomposition = new EigenvalueDecomposition(matrix);
       assertArrayEquals(new double[]{12.7448, -4.6849, -1.3752, 1.3153}, eigenvalueDecomposition.getRealEigenvalues(), 1E-4);
    }
}