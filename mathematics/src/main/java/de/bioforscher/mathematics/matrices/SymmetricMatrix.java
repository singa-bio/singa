package de.bioforscher.mathematics.matrices;

import de.bioforscher.mathematics.exceptions.IncompatibleDimensionsException;
import de.bioforscher.mathematics.exceptions.MalformedMatrixException;


/**
 * The {@code SymmetricMatrix} implementation only stores a the main diagonal and one copy of the symmetric values.
 */
public class SymmetricMatrix extends SquareMatrix {

    /**
     * Creates a new {@code SymmetricMatrix} with the given double values. The first index of the double array
     * represents the row index and the second index represents the column index. <br>
     * <p>
     * The following array:
     * <pre>
     * {{1.0, 2.0, 3.0}, {2.0, 5.0, 6.0}, {3.0, 6.0, 9.0}} </pre>
     * result in the matrix:
     * <pre>
     * 1.0  2.0  3.0
     * 2.0  5.0  6.0
     * 3.0  6.0  9.0 </pre>
     *
     * @param values The values of the matrix.
     */
    public SymmetricMatrix(double[][] values) {
        super(values, true);
    }

    /**
     * Returns {@code true} if the potential values are square and symmetric (mirrored at the main diagonal) and
     * {@code false} otherwise.
     *
     * @param potentialValues The potential values of a symmetric matrix.
     * @return {@code true} if the potential values are square and symmetric and {@code false} otherwise.
     */
    public static boolean isSymmetric(double[][] potentialValues) {
        if (!SquareMatrix.isSquare(potentialValues)) {
            return false;
        } else {
            for (int rowIndex = 0; rowIndex < potentialValues.length; rowIndex++) {
                for (int columnIndex = 0; columnIndex < potentialValues[0].length; columnIndex++) {
                    if (potentialValues[columnIndex][rowIndex] != potentialValues[rowIndex][columnIndex]) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    /**
     * Returns {@code true} if the given matrix is square and symmetric (mirrored at the main diagonal) and {@code
     * false} otherwise.
     *
     * @param matrix The matrix to be checked.
     * @return {@code true} if the given matrix is square and symmetric and {@code false} otherwise.
     */
    public static boolean isSymmetric(Matrix matrix) {
        return SymmetricMatrix.isSymmetric(matrix.getElements());
    }

    /**
     * Asserts that the given potential values are square and symmetric nd throws an
     * {@link IncompatibleDimensionsException} otherwise.
     *
     * @param potentialValues The potential values of a symmetric matrix.
     * @throws IncompatibleDimensionsException if the given matrix is not square and symmetric.
     */
    public static void assertThatValuesAreSymmetric(double[][] potentialValues) {
        if (!SymmetricMatrix.isSymmetric(potentialValues)) {
            throw new MalformedMatrixException(potentialValues);
        }
    }

    /**
     * Compacts the values of a symmetric matrix into a jagged array, that represents the lower triangular part of a
     * symmetric matrix. <br>
     * <p>
     * The following array:
     * <pre>
     * {{1.0, 2.0, 3.0}, {2.0, 5.0, 6.0}, {3.0, 6.0, 9.0}} </pre>
     * compacts to the array:
     * <pre>
     * {{1.0}, {2.0, 5.0}, {3.0, 6.0, 9.0}} </pre>
     * which results in the matrix:
     * <pre>
     * 1.0
     * 2.0  5.0
     * 3.0  6.0  9.0 </pre>
     *
     * @param potentialValues
     * @return
     */
    public static double[][] compactToSymmetricMatrix(double[][] potentialValues) {
        assertThatValuesAreSymmetric(potentialValues);
        // initialize jagged array
        double[][] compactedValues = new double[potentialValues.length][];
        for (int rowIndex = 0; rowIndex < potentialValues.length; rowIndex++) {
            compactedValues[rowIndex] = new double[rowIndex + 1];
        }
        // fill array with values
        for (int rowIndex = 0; rowIndex < potentialValues.length; rowIndex++) {
            System.arraycopy(potentialValues[rowIndex], 0, compactedValues[rowIndex], 0, rowIndex + 1);
        }
        return compactedValues;
    }

    @Override
    public double getElement(int rowIndex, int columnIndex) {
        if (rowIndex >= columnIndex) {
            return super.getElement(rowIndex, columnIndex);
        } else {
            return super.getElement(columnIndex, rowIndex);
        }
    }

    @Override
    public Matrix transpose() {
        return this;
    }

}
