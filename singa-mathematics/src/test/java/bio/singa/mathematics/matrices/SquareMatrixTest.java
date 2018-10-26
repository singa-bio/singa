package bio.singa.mathematics.matrices;

import bio.singa.mathematics.vectors.Vector;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SquareMatrixTest {

    private static SquareMatrix regularSquareMatrix;
    private static SquareMatrix determinantMatrix;

    @BeforeAll
    static void initialize() {
        double[][] regularSquareMatrixValues = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}, {7.0, 8.0, 9.0}};
        regularSquareMatrix = new SquareMatrix(regularSquareMatrixValues);
        double[][] determinantMatrixValues = {{1.0, 2.0, 3.0}, {4.0, -5.0, 6.0}, {7.0, 8.0, 9.0}};
        determinantMatrix = new SquareMatrix(determinantMatrixValues);
    }

    @Test
    void shouldInitializeIncorrectly() {
        double[][] values = {{1.0, 2.0}, {4.0, 5.0}, {7.0, 8.0}};
        assertThrows(IllegalArgumentException.class,
                () -> new SquareMatrix(values));
    }

    @Test
    void testTraceCalculation() {
        double trace = regularSquareMatrix.trace();
        assertEquals(15, trace);
    }

    @Test
    void testMainDiagonalExtraction() {
        Vector diagonal = regularSquareMatrix.getMainDiagonal();
        assertArrayEquals(new double[]{1.0, 5.0, 9.0}, diagonal.getElements());
    }

    @Test
    void testIdentityMatrixConstruction() {
        SquareMatrix squareMatrix = Matrices.generateIdentityMatrix(5);
        for (int diagonalIndex = 0; diagonalIndex < squareMatrix.getColumnDimension(); diagonalIndex++) {
            assertEquals(1.0, squareMatrix.getElement(diagonalIndex, diagonalIndex));
        }
    }

    @Test
    void testDeterminantCalculation() {
        double actual = determinantMatrix.determinant();
        assertEquals(120.0, actual);
    }

    @Test
    void shouldCopy() {
        Matrix firstCopy = regularSquareMatrix.getCopy();
        Matrix secondCopy = regularSquareMatrix.getCopy();
        firstCopy.getElements()[0][0] = 50.0;
        assertEquals(1.0, secondCopy.getElements()[0][0]);
        assertEquals(50.0, firstCopy.getElements()[0][0]);
    }
}
