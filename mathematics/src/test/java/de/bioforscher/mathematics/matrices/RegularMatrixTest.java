package de.bioforscher.mathematics.matrices;

import de.bioforscher.core.utility.Pair;
import de.bioforscher.mathematics.algorithms.matrix.QRDecomposition;
import de.bioforscher.mathematics.algorithms.matrix.SVDecomposition;
import de.bioforscher.mathematics.concepts.Addable;
import de.bioforscher.mathematics.exceptions.IncompatibleDimensionsException;
import de.bioforscher.mathematics.exceptions.MalformedMatrixException;
import de.bioforscher.mathematics.vectors.RegularVector;
import de.bioforscher.mathematics.vectors.Vector;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class RegularMatrixTest {

    private RegularMatrix squareMatrix;
    private RegularMatrix firstRectangularMatrix;
    private Matrix secondRectangularMatrix;

    private Matrix fourTimesTwo;
    private Matrix twoTimesThree;

    private Matrix matrixWithUniqueExtrema;
    private Matrix matrixWithAmbiguousExtrema;
    private Matrix matrixWithNoExtrema;

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

        double[][] uniqueValues = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}, {7.0, 8.0, 9.0}};
        this.matrixWithUniqueExtrema = new RegularMatrix(uniqueValues);
        double[][] ambiguousValues = {{1.0, 1.0, 3.0}, {4.0, 5.0, 6.0}, {7.0, 9.0, 9.0}};
        this.matrixWithAmbiguousExtrema = new RegularMatrix(ambiguousValues);
        double[][] unspecifiedValues = {{1.0, 1.0, 1.0}, {1.0, 1.0, 1.0}, {1.0, 1.0, 1.0}};
        this.matrixWithNoExtrema = new RegularMatrix(unspecifiedValues);

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
        Matrix addition = Addable.sum(Arrays.asList(firstRectangularMatrix, firstRectangularMatrix, firstRectangularMatrix));
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
        String expected = " 9.00 10.00 11.00\n12.00 13.00 14.00\n";
        assertEquals(expected, this.twoTimesThree.toString());
    }

    @Test
    public void shouldGetStringRepresentation() {

        LabeledRegularMatrix<String> lrm = new LabeledRegularMatrix<>(this.firstRectangularMatrix.getElements());
        lrm.setRowLabel("R3", 2);
        lrm.setRowLabel("R2", 1);
        lrm.setRowLabel("R1", 0);
        lrm.setColumnLabel("C1", 0);
        lrm.setColumnLabel("C2", 1);

        assertEquals(lrm.getValueForLabel("R3", "C2"), 6, 0);
        assertEquals(",C1,C2\n" +
                "R1,1.000000,2.000000\n" +
                "R2,3.000000,4.000000\n" +
                "R3,5.000000,6.000000", lrm.getStringRepresentation());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldEnsureLabelCapacity() {

        LabeledRegularMatrix<String> lrm = new LabeledRegularMatrix<>(this.firstRectangularMatrix.getElements());
        lrm.setRowLabel("R7", 7);
    }

    @Test
    public void shouldFindMatrixExtrema() {

        Optional<Pair<Integer>> minimalPosition = Matrices.getPositionOfMinimalElement(this.matrixWithUniqueExtrema);
        assertTrue(minimalPosition.isPresent());
        assertEquals(0L, (long) minimalPosition.get().getFirst());
        assertEquals(0L, (long) minimalPosition.get().getSecond());
        minimalPosition = Matrices.getPositionOfMinimalElement(this.matrixWithAmbiguousExtrema);
        assertEquals(Optional.empty(), minimalPosition);
        minimalPosition = Matrices.getPositionOfMinimalElement(this.matrixWithNoExtrema);
        assertEquals(Optional.empty(), minimalPosition);

        Optional<Pair<Integer>> maximalPosition = Matrices.getPositionOfMaximalElement(this.matrixWithUniqueExtrema);
        assertTrue(maximalPosition.isPresent());
        assertEquals(2L, (long) maximalPosition.get().getFirst());
        assertEquals(2L, (long) maximalPosition.get().getSecond());
        maximalPosition = Matrices.getPositionOfMaximalElement(this.matrixWithAmbiguousExtrema);
        assertEquals(Optional.empty(), maximalPosition);
        maximalPosition = Matrices.getPositionOfMaximalElement(this.matrixWithNoExtrema);
        assertEquals(Optional.empty(), maximalPosition);

        List<Pair<Integer>> minimalPositions = Matrices.getPositionsOfMinimalElement(this.matrixWithAmbiguousExtrema);
        assertTrue(minimalPositions.get(0).getFirst() == 0 && minimalPositions.get(0).getSecond() == 0);
        assertTrue(minimalPositions.get(1).getFirst() == 0 && minimalPositions.get(1).getSecond() == 1);

        List<Pair<Integer>> maximalPositions = Matrices.getPositionsOfMaximalElement(this.matrixWithAmbiguousExtrema);
        assertTrue(maximalPositions.get(0).getFirst() == 2 && maximalPositions.get(0).getSecond() == 1);
        assertTrue(maximalPositions.get(1).getFirst() == 2 && maximalPositions.get(1).getSecond() == 2);
    }

    @Test
    public void shouldPerformQRDecomposition() {
        Matrix expectedQ = new RegularMatrix(new double[][]{{6.0 / 7.0, 3.0 / 7.0, -2.0 / 7.0},
                {-69.0 / 175.0, 158.0 / 175.0, 6.0 / 35.0}, {-58.0 / 175.0, 6.0 / 175.0, -33.0 / 35.0}});
        Matrix expectedR = new RegularMatrix(new double[][]{{14, 21, -14}, {0, 175, -70}, {0, 0, 35}});

        Matrix originalMatrix = new RegularMatrix(new double[][]{{12, -51, 4}, {6, 167, -68}, {-4, 24, -41}});
        QRDecomposition decomposition = Matrices.performQRDecomposition(originalMatrix);

        for (int row = 0; row < expectedQ.getRowDimension(); row++) {
            assertArrayEquals(expectedQ.getElements()[row], decomposition.getMatrixQ().getElements()[row], 1E-16);
        }
        for (int row = 0; row < expectedQ.getRowDimension(); row++) {
            assertArrayEquals(expectedR.getElements()[row], decomposition.getMatrixR().getElements()[row], 1E-14);
        }
    }

    @Test
    public void shouldPerformSVDecomposition() {
        Matrix expectedU = new RegularMatrix(new double[][]{
                {-0.8571428571428572, -0.42857142857142855, 0.2857142857142857},
                {0.3942857142857143, -0.9028571428571428, -0.1714285714285714},
                {0.3314285714285714, -0.03428571428571428, 0.9428571428571428}});
        Matrix expectedV = new RegularMatrix(new double[][]{
                {-1.0, -0.0, -0.0},
                {-0.0, -1.0, -0.0},
                {-0.0, -0.0, -1.0}});
        Matrix inputMatrix = new RegularMatrix(new double[][]{
                {6.0 / 7.0, 3.0 / 7.0, -2.0 / 7.0},
                {-69.0 / 175.0, 158.0 / 175.0, 6.0 / 35.0},
                {-58.0 / 175.0, 6.0 / 175.0, -33.0 / 35.0}});

        SVDecomposition svd = Matrices.performSVDecomposition(inputMatrix);

        for (int row = 0; row < expectedU.getRowDimension(); row++) {
            assertArrayEquals(expectedU.getElements()[row], svd.getMatrixU().getElements()[row], 1E-16);
        }
        for (int row = 0; row < expectedV.getRowDimension(); row++) {
            assertArrayEquals(expectedV.getElements()[row], svd.getMatrixV().getElements()[row], 1E-14);
        }
    }

    @Test
    public void shouldCopy(){
        RegularMatrix copy1 = this.squareMatrix.getCopy();
        RegularMatrix copy2 = this.squareMatrix.getCopy();
        copy1.getElements()[0][0] = Double.MIN_VALUE;
        assertTrue(copy2.getElements()[0][0] != Double.MIN_VALUE);
    }

    @Test
    public void shouldGetColumnAndRowByLabel() {
        LabeledRegularMatrix<String> lrm = new LabeledRegularMatrix<>(this.firstRectangularMatrix.getElements());
        lrm.setRowLabel("R3", 2);
        lrm.setRowLabel("R2", 1);
        lrm.setRowLabel("R1", 0);
        lrm.setColumnLabel("C1", 0);
        lrm.setColumnLabel("C2", 1);
        RegularVector column = lrm.getRowByLabel("R1");
        assertTrue(column.equals(new RegularVector(1.0, 2.0)));
        RegularVector row = lrm.getColumnByLabel("C1");
        assertTrue(row.equals(new RegularVector(1.0, 3.0, 5.0)));
    }

    @Test
    public void shouldRetrieveLabelsOfSymmetricMatrix(){
        LabeledRegularMatrix<String> lrm = new LabeledRegularMatrix<>(this.firstRectangularMatrix.getElements());
        lrm.setRowLabel("R3", 2);
        lrm.setRowLabel("R2", 1);
        lrm.setRowLabel("R1", 0);
        lrm.setColumnLabel("C1", 0);
        lrm.setColumnLabel("C2", 1);
        List<String> rowLabelsToCheck = new ArrayList<>();
        rowLabelsToCheck.add("R1");
        rowLabelsToCheck.add("R2");
        rowLabelsToCheck.add("R3");
        List<String> columnLabelsToCheck = new ArrayList<>();
        columnLabelsToCheck.add("C1");
        columnLabelsToCheck.add("C2");
        assertTrue(lrm.getRowLabels().equals(rowLabelsToCheck));
        assertTrue(lrm.getColumnsLabels().equals(columnLabelsToCheck));
    }
}
