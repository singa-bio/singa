package de.bioforscher.mathematics.matrices;

import de.bioforscher.mathematics.concepts.Addable;
import de.bioforscher.mathematics.exceptions.IncompatibleDimensionsException;
import de.bioforscher.mathematics.exceptions.MalformedMatrixException;
import de.bioforscher.mathematics.vectors.RegularVector;
import de.bioforscher.mathematics.vectors.Vector;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class RegularMatrixTest {

    private RegularMatrix squareMatrix;
    private RegularMatrix firstRectangularMatrix;
    private Matrix secondRectangularMatrix;

    private Matrix fourTimesTwo;
    private Matrix twoTimesThree;

    @Before
    public void initialize() {
        double[][] squareValues = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}, {7.0, 8.0, 9.0}};
        this.squareMatrix = new RegularMatrix(squareValues);
        double[][] firstRectangularValues = {{1.0, 2.0}, {3.0, 4.0}, {5.0, 6.0}};
        this.firstRectangularMatrix = new RegularMatrix(firstRectangularValues);
        double[][] secondRectangularValues = {{7.0, 8.0}, {9.0, 10.0}, {11.0, 12.0}};
        this.secondRectangularMatrix = new RegularMatrix(secondRectangularValues);

        double[][] fourTimesTwoValues = {{1.0, 2.0}, {3.0, 4.0}, {5.0, 6.0}, {7.0, 8.0}};
        this.fourTimesTwo = new RegularMatrix(fourTimesTwoValues);
        double[][] twoTimesThreeValues = {{9.0, 10.0, 11.0}, {12.0, 13.0, 14.0}};
        this.twoTimesThree = new RegularMatrix(twoTimesThreeValues);
    }

    @Test
    public void testValuesWellFormed() {
        double[][] values = {{1.0, 2.0, 3.0}, {4.0, 5.0}, {7.0, 8.0, 9.0}};
        assertFalse(RegularMatrix.isWellFormed(values));
    }

    @Test(expected = MalformedMatrixException.class)
    public void ensureWellFormedInput() {
        double[][] values = {{1.0, 2.0, 3.0}, {4.0, 5.0}, {7.0, 8.0, 9.0}};
        Matrix matrix = new RegularMatrix(values);
        assertNotNull(matrix);
    }

    @Test
    public void testSameDimension() {
        boolean sameDimensions = this.firstRectangularMatrix.hasSameDimensions(this.secondRectangularMatrix);
        assertEquals(true, sameDimensions);
    }

    @Test
    public void testRowDimension() {
        int rowVector = this.firstRectangularMatrix.getRow(0).getDimension();
        int rowDimension = this.firstRectangularMatrix.getColumnDimension();
        assertEquals(rowVector, rowDimension, 0);
    }

    @Test
    public void testColumnDimension() {
        int columnVector = this.firstRectangularMatrix.getColumn(0).getDimension();
        int columnDimension = this.firstRectangularMatrix.getRowDimension();
        assertEquals(columnVector, columnDimension, 0);
    }

    @Test
    public void testRowExtraction() {
        RegularVector row = this.squareMatrix.getRow(0);
        assertArrayEquals(new double[]{1.0, 2.0, 3.0}, row.getElements(), 0.0);
    }

    @Test
    public void testColumnExtraction() {
        RegularVector column = this.squareMatrix.getColumn(0);
        assertArrayEquals(new double[]{1.0, 4.0, 7.0}, column.getElements(), 0.0);
    }

    @Test
    public void testElementExtraction() {
        double element = this.squareMatrix.getElement(0, 1);
        assertEquals(2.0, element, 0.0);
    }

    @Test
    public void testAddition() {
        Matrix addition = this.firstRectangularMatrix.add(this.secondRectangularMatrix);
        assertTrue(Arrays.deepEquals(new double[][]{{8.0, 10.0}, {12.0, 14.0}, {16.0, 18.0}},
                addition.getElements()));
    }

    @Test
    public void testSubstraction() {
        Matrix addition = this.firstRectangularMatrix.subtract(this.secondRectangularMatrix);
        assertTrue(Arrays.deepEquals(new double[][]{{-6.0, -6.0}, {-6.0, -6.0}, {-6.0, -6.0}},
                addition.getElements()));
    }

    @Test
    public void testSummation() {
        Matrix addition = Addable.sum(this.firstRectangularMatrix, this.firstRectangularMatrix,
                this.firstRectangularMatrix);
        assertTrue(Arrays.deepEquals(new double[][]{{3.0, 6.0}, {9.0, 12.0}, {15.0, 18.0}},
                addition.getElements()));
    }

    @Test
    public void testAdditiveInversion() {
        Matrix inversion = this.firstRectangularMatrix.additivelyInvert();
        assertTrue(Arrays.deepEquals(new double[][]{{-1.0, -2.0}, {-3.0, -4.0}, {-5.0, -6.0}},
                inversion.getElements()));
    }

    @Test
    public void testTransposition() {
        Matrix transposition = this.firstRectangularMatrix.transpose();
        assertTrue(Arrays.deepEquals(new double[][]{{1.0, 3.0, 5.0}, {2.0, 4.0, 6.0}},
                transposition.getElements()));
    }

    @Test(expected = IncompatibleDimensionsException.class)
    public void ensureSameDimensions() {
        Matrix addition = this.squareMatrix.add(this.secondRectangularMatrix);
        assertNull(addition);
    }

    @Test(expected = IncompatibleDimensionsException.class)
    public void ensureSameInnerDimensions() {
        Matrix multiplication = this.twoTimesThree.multiply(this.fourTimesTwo);
        assertNull(multiplication);
    }

    @Test
    public void testMultiplicationWithScalar() {
        Matrix multiplication = this.firstRectangularMatrix.multiply(2);
        assertTrue(Arrays.deepEquals(new double[][]{{2.0, 4.0}, {6.0, 8.0}, {10.0, 12.0}},
                multiplication.getElements()));
    }

    @Test
    public void testMultiplicationWithVector() {
        Vector multiplicand = new RegularVector(2.0, 3.0);
        Vector multiplication = this.fourTimesTwo.multiply(multiplicand);
        assertArrayEquals(new double[]{8.0, 18.0, 28.0, 38.0}, multiplication.getElements(), 0.0);
    }

    @Test
    public void testMultiplicationWithMatrix() {
        Matrix multiplication = this.fourTimesTwo.multiply(this.twoTimesThree);
        assertTrue(Arrays.deepEquals(new double[][]{{33.0, 36.0, 39.0}, {75.0, 82.0, 89.0},
                {117.0, 128.0, 139.0}, {159.0, 174.0, 189.0}}, multiplication.getElements()));
    }

    @Test
    public void testHadamardMultiplication() {
        Matrix multiplication = this.fourTimesTwo.hadamardMultiply(this.fourTimesTwo);
        assertTrue(Arrays.deepEquals(new double[][]{{1.0, 4.0}, {9.0, 16.0}, {25.0, 36.0}, {49.0, 64.0}},
                multiplication.getElements()));
    }

    @Test
    public void shouldConvertRegularToSquared() {
        SquareMatrix actual = this.squareMatrix.as(SquareMatrix.class);
        assertTrue(Arrays.deepEquals(this.squareMatrix.getElements(), actual.getElements()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotConvertRegularToSquared() {
        SquareMatrix actual = this.firstRectangularMatrix.as(SquareMatrix.class);
        assertNull(actual);
    }

    @Test
    public void testToString() {
        String expected = " 9,00 10,00 11,00\n12,00 13,00 14,00\n";
        assertEquals(expected, this.twoTimesThree.toString());
    }

}
