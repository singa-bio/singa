package bio.singa.mathematics.transformations;

import bio.singa.mathematics.matrices.Matrix;
import bio.singa.mathematics.matrices.RegularMatrix;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class ZScoreTransformationTest {

    @Test
    public void shouldZTransform() {
        Matrix matrix = new RegularMatrix(new double[][]{{1}, {3}, {5}, {4}, {7}, {9}});
        Matrix transformedMatrix = new ZScoreTransformation().applyTo(matrix);
        assertArrayEquals(new double[]{-1.3414, -0.6415, 0.0583, -0.2916, 0.7582, 1.45803},
                transformedMatrix.getColumn(0).getElements(), 1E-4);
    }

}