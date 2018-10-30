package bio.singa.mathematics.matrices;

import bio.singa.core.utility.Pair;
import bio.singa.mathematics.algorithms.matrix.QRDecomposition;
import bio.singa.mathematics.algorithms.matrix.SVDecomposition;
import bio.singa.mathematics.concepts.Addable;
import bio.singa.mathematics.exceptions.IncompatibleDimensionsException;
import bio.singa.mathematics.exceptions.MalformedMatrixException;
import bio.singa.mathematics.vectors.RegularVector;
import bio.singa.mathematics.vectors.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class RegularMatrixTest {

    private RegularMatrix squareMatrix;
    private RegularMatrix firstRectangularMatrix;
    private Matrix secondRectangularMatrix;

    private Matrix fourTimesTwo;
    private Matrix twoTimesThree;

    private Matrix matrixWithUniqueExtrema;
    private Matrix matrixWithAmbiguousExtrema;
    private Matrix matrixWithNoExtrema;

    @BeforeEach
    void initialize() {
        double[][] squareValues = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}, {7.0, 8.0, 9.0}};
        squareMatrix = new RegularMatrix(squareValues);
        double[][] firstRectangularValues = {{1.0, 2.0}, {3.0, 4.0}, {5.0, 6.0}};
        firstRectangularMatrix = new RegularMatrix(firstRectangularValues);
        double[][] secondRectangularValues = {{7.0, 8.0}, {9.0, 10.0}, {11.0, 12.0}};
        secondRectangularMatrix = new RegularMatrix(secondRectangularValues);

        double[][] fourTimesTwoValues = {{1.0, 2.0}, {3.0, 4.0}, {5.0, 6.0}, {7.0, 8.0}};
        fourTimesTwo = new RegularMatrix(fourTimesTwoValues);
        double[][] twoTimesThreeValues = {{9.0, 10.0, 11.0}, {12.0, 13.0, 14.0}};
        twoTimesThree = new RegularMatrix(twoTimesThreeValues);

        double[][] uniqueValues = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}, {7.0, 8.0, 9.0}};
        matrixWithUniqueExtrema = new RegularMatrix(uniqueValues);
        double[][] ambiguousValues = {{1.0, 1.0, 3.0}, {4.0, 5.0, 6.0}, {7.0, 9.0, 9.0}};
        matrixWithAmbiguousExtrema = new RegularMatrix(ambiguousValues);
        double[][] unspecifiedValues = {{1.0, 1.0, 1.0}, {1.0, 1.0, 1.0}, {1.0, 1.0, 1.0}};
        matrixWithNoExtrema = new RegularMatrix(unspecifiedValues);

    }

    @Test
    void testValuesWellFormed() {
        double[][] values = {{1.0, 2.0, 3.0}, {4.0, 5.0}, {7.0, 8.0, 9.0}};
        assertFalse(RegularMatrix.isWellFormed(values));
    }

    @Test
    void ensureWellFormedInput() {
        double[][] values = {{1.0, 2.0, 3.0}, {4.0, 5.0}, {7.0, 8.0, 9.0}};
        assertThrows(MalformedMatrixException.class,
                () -> new RegularMatrix(values));
    }

    @Test
    void testSameDimension() {
        boolean sameDimensions = firstRectangularMatrix.hasSameDimensions(secondRectangularMatrix);
        assertTrue(sameDimensions);
    }

    @Test
    void testRowDimension() {
        int rowVector = firstRectangularMatrix.getRow(0).getDimension();
        int rowDimension = firstRectangularMatrix.getColumnDimension();
        assertEquals(rowVector, rowDimension);
    }

    @Test
    void testColumnDimension() {
        int columnVector = firstRectangularMatrix.getColumn(0).getDimension();
        int columnDimension = firstRectangularMatrix.getRowDimension();
        assertEquals(columnVector, columnDimension);
    }

    @Test
    void testRowExtraction() {
        RegularVector row = squareMatrix.getRow(0);
        assertArrayEquals(new double[]{1.0, 2.0, 3.0}, row.getElements());
    }

    @Test
    void testColumnExtraction() {
        RegularVector column = squareMatrix.getColumn(0);
        assertArrayEquals(new double[]{1.0, 4.0, 7.0}, column.getElements());
    }

    @Test
    void testElementExtraction() {
        double element = squareMatrix.getElement(0, 1);
        assertEquals(2.0, element);
    }

    @Test
    void testAddition() {
        Matrix addition = firstRectangularMatrix.add(secondRectangularMatrix);
        assertTrue(Arrays.deepEquals(new double[][]{{8.0, 10.0}, {12.0, 14.0}, {16.0, 18.0}},
                addition.getElements()));
    }

    @Test
    void testSubtraction() {
        Matrix addition = firstRectangularMatrix.subtract(secondRectangularMatrix);
        assertTrue(Arrays.deepEquals(new double[][]{{-6.0, -6.0}, {-6.0, -6.0}, {-6.0, -6.0}},
                addition.getElements()));
    }

    @Test
    void testSummation() {
        Matrix addition = Addable.sum(Arrays.asList(firstRectangularMatrix, firstRectangularMatrix, firstRectangularMatrix));
        assertTrue(Arrays.deepEquals(new double[][]{{3.0, 6.0}, {9.0, 12.0}, {15.0, 18.0}},
                addition.getElements()));
    }

    @Test
    void testAdditiveInversion() {
        Matrix inversion = firstRectangularMatrix.additivelyInvert();
        assertTrue(Arrays.deepEquals(new double[][]{{-1.0, -2.0}, {-3.0, -4.0}, {-5.0, -6.0}},
                inversion.getElements()));
    }

    @Test
    void testTransposition() {
        Matrix transposition = firstRectangularMatrix.transpose();
        assertTrue(Arrays.deepEquals(new double[][]{{1.0, 3.0, 5.0}, {2.0, 4.0, 6.0}},
                transposition.getElements()));
    }

    @Test
    void ensureSameDimensions() {
        assertThrows(IncompatibleDimensionsException.class,
                () -> squareMatrix.add(secondRectangularMatrix));
    }

    @Test
    void ensureSameInnerDimensions() {
        assertThrows(IncompatibleDimensionsException.class,
                () -> twoTimesThree.multiply(fourTimesTwo));
    }

    @Test
    void testMultiplicationWithScalar() {
        Matrix multiplication = firstRectangularMatrix.multiply(2);
        assertTrue(Arrays.deepEquals(new double[][]{{2.0, 4.0}, {6.0, 8.0}, {10.0, 12.0}},
                multiplication.getElements()));
    }

    @Test
    void testMultiplicationWithVector() {
        Vector multiplicand = new RegularVector(2.0, 3.0);
        Vector multiplication = fourTimesTwo.multiply(multiplicand);
        assertArrayEquals(new double[]{8.0, 18.0, 28.0, 38.0}, multiplication.getElements());
    }

    @Test
    void testMultiplicationWithMatrix() {
        Matrix multiplication = fourTimesTwo.multiply(twoTimesThree);
        assertTrue(Arrays.deepEquals(new double[][]{{33.0, 36.0, 39.0}, {75.0, 82.0, 89.0},
                {117.0, 128.0, 139.0}, {159.0, 174.0, 189.0}}, multiplication.getElements()));
    }

    @Test
    void testHadamardMultiplication() {
        Matrix multiplication = fourTimesTwo.hadamardMultiply(fourTimesTwo);
        assertTrue(Arrays.deepEquals(new double[][]{{1.0, 4.0}, {9.0, 16.0}, {25.0, 36.0}, {49.0, 64.0}},
                multiplication.getElements()));
    }

    @Test
    void shouldConvertRegularToSquared() {
        SquareMatrix actual = squareMatrix.as(SquareMatrix.class);
        assertTrue(Arrays.deepEquals(squareMatrix.getElements(), actual.getElements()));
    }

    @Test
    void shouldNotConvertRegularToSquared() {
        assertThrows(IllegalArgumentException.class,
                () -> firstRectangularMatrix.as(SquareMatrix.class));
    }

    @Test
    void testToString() {
        String expected = " 9.00 10.00 11.00\n12.00 13.00 14.00\n";
        assertEquals(expected, twoTimesThree.toString());
    }

    @Test
    void shouldGetStringRepresentation() {

        LabeledRegularMatrix<String> lrm = new LabeledRegularMatrix<>(firstRectangularMatrix.getElements());
        lrm.setRowLabel("R3", 2);
        lrm.setRowLabel("R2", 1);
        lrm.setRowLabel("R1", 0);
        lrm.setColumnLabel("C1", 0);
        lrm.setColumnLabel("C2", 1);

        assertEquals(lrm.getValueForLabel("R3", "C2"), 6);
        assertEquals(",C1,C2\n" +
                "R1,1.000000,2.000000\n" +
                "R2,3.000000,4.000000\n" +
                "R3,5.000000,6.000000", lrm.getStringRepresentation());
    }

    @Test
    void shouldFindMatrixExtrema() {

        Optional<Pair<Integer>> minimalPosition = Matrices.getPositionOfMinimalElement(matrixWithUniqueExtrema);
        assertTrue(minimalPosition.isPresent());
        assertEquals(0L, (long) minimalPosition.get().getFirst());
        assertEquals(0L, (long) minimalPosition.get().getSecond());
        minimalPosition = Matrices.getPositionOfMinimalElement(matrixWithAmbiguousExtrema);
        assertEquals(Optional.empty(), minimalPosition);
        minimalPosition = Matrices.getPositionOfMinimalElement(matrixWithNoExtrema);
        assertEquals(Optional.empty(), minimalPosition);

        Optional<Pair<Integer>> maximalPosition = Matrices.getPositionOfMaximalElement(matrixWithUniqueExtrema);
        assertTrue(maximalPosition.isPresent());
        assertEquals(2L, (long) maximalPosition.get().getFirst());
        assertEquals(2L, (long) maximalPosition.get().getSecond());
        maximalPosition = Matrices.getPositionOfMaximalElement(matrixWithAmbiguousExtrema);
        assertEquals(Optional.empty(), maximalPosition);
        maximalPosition = Matrices.getPositionOfMaximalElement(matrixWithNoExtrema);
        assertEquals(Optional.empty(), maximalPosition);

        List<Pair<Integer>> minimalPositions = Matrices.getPositionsOfMinimalElement(matrixWithAmbiguousExtrema);
        assertTrue(minimalPositions.get(0).getFirst() == 0 && minimalPositions.get(0).getSecond() == 0);
        assertTrue(minimalPositions.get(1).getFirst() == 0 && minimalPositions.get(1).getSecond() == 1);

        List<Pair<Integer>> maximalPositions = Matrices.getPositionsOfMaximalElement(matrixWithAmbiguousExtrema);
        assertTrue(maximalPositions.get(0).getFirst() == 2 && maximalPositions.get(0).getSecond() == 1);
        assertTrue(maximalPositions.get(1).getFirst() == 2 && maximalPositions.get(1).getSecond() == 2);
    }

    @Test
    void shouldPerformQRDecomposition() {
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
    void shouldPerformSVDecomposition() {
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
    void shouldCopy() {
        RegularMatrix copy1 = squareMatrix.getCopy();
        RegularMatrix copy2 = squareMatrix.getCopy();
        copy1.getElements()[0][0] = Double.MIN_VALUE;
        assertTrue(copy2.getElements()[0][0] != Double.MIN_VALUE);
    }

    @Test
    void shouldGetColumnAndRowByLabel() {
        LabeledRegularMatrix<String> lrm = new LabeledRegularMatrix<>(firstRectangularMatrix.getElements());
        lrm.setRowLabel("R3", 2);
        lrm.setRowLabel("R2", 1);
        lrm.setRowLabel("R1", 0);
        lrm.setColumnLabel("C1", 0);
        lrm.setColumnLabel("C2", 1);
        RegularVector column = lrm.getRowByLabel("R1");
        assertEquals(column, new RegularVector(1.0, 2.0));
        RegularVector row = lrm.getColumnByLabel("C1");
        assertEquals(row, new RegularVector(1.0, 3.0, 5.0));
    }

    @Test
    void shouldStreamAllElements() {
        List<Double> expectedElements = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0);
        List<Double> actualElements = firstRectangularMatrix.streamElements().boxed().collect(Collectors.toList());
        for (int i = 0; i < expectedElements.size(); i++) {
            assertEquals(expectedElements.get(i), actualElements.get(i));
        }
    }


    @Test
    void shouldRetrieveLabelsOfSymmetricMatrix() {
        LabeledRegularMatrix<String> lrm = new LabeledRegularMatrix<>(firstRectangularMatrix.getElements());
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
        assertEquals(lrm.getRowLabels(), rowLabelsToCheck);
        assertEquals(lrm.getColumnLabels(), columnLabelsToCheck);
    }
}
