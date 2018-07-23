package bio.singa.mathematics.matrices;

import bio.singa.mathematics.vectors.Vector;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SquareMatrixTest {

    private SquareMatrix regularSquareMatrix;
    private SquareMatrix determinantMatrix;

    @Before
    public void initialize() {
        double[][] regularSquareMatrixValues = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}, {7.0, 8.0, 9.0}};
        regularSquareMatrix = new SquareMatrix(regularSquareMatrixValues);
        double[][] determinantMatrixValues = {{1.0, 2.0, 3.0}, {4.0, -5.0, 6.0}, {7.0, 8.0, 9.0}};
        determinantMatrix = new SquareMatrix(determinantMatrixValues);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldInitializeIncorrectly() {
        double[][] values = {{1.0, 2.0}, {4.0, 5.0}, {7.0, 8.0}};
        SquareMatrix squareMatrix = new SquareMatrix(values);
        assertNull(squareMatrix);
    }

    @Test
    public void testTraceCalculation() {
        double trace = regularSquareMatrix.trace();
        assertEquals(15, trace, 0.0);
    }

    @Test
    public void testMainDiagonalExtraction() {
        Vector diagonal = regularSquareMatrix.getMainDiagonal();
        assertArrayEquals(new double[]{1.0, 5.0, 9.0}, diagonal.getElements(), 0.0);
    }

    @Test
    public void testIdentityMatrixConstruction() {
        SquareMatrix squareMatrix = Matrices.generateIdentityMatrix(5);
        for (int diagonalIndex = 0; diagonalIndex < squareMatrix.getColumnDimension(); diagonalIndex++) {
            assertEquals(1.0, squareMatrix.getElement(diagonalIndex, diagonalIndex), 0.0);
        }
    }

    @Test
    public void testDeterminantCalculation() {
        double actual = determinantMatrix.determinant();
        assertEquals(120.0, actual, 0.0);
    }

    @Test
    public void shouldCopy() {
        Matrix firstCopy = regularSquareMatrix.getCopy();
        Matrix secondCopy = regularSquareMatrix.getCopy();
        firstCopy.getElements()[0][0] = 50;
        assertTrue(secondCopy.getElements()[0][0] != 50);
        assertTrue(firstCopy.getElements()[0][0] == 50);
    }
}
