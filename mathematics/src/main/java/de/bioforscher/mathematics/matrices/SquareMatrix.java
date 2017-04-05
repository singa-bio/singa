package de.bioforscher.mathematics.matrices;

import de.bioforscher.mathematics.vectors.RegularVector;
import de.bioforscher.mathematics.vectors.Vector;

/**
 * The {@code SquareMatrix} implementation allows the usage of methods that require a square form of the matrix.
 */
public class SquareMatrix extends RegularMatrix {

    private static final long serialVersionUID = -8834271370988935890L;

    /**
     * Creates a new {@code SquareMatrix} with the given double values. The first index of the double array
     * represents the row index and the second index represents the column index. <br>
     * <p>
     * The following array:
     * <pre>
     * {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}, {7.0, 8.0, 9.0}} </pre>
     * result in the matrix:
     * <pre>
     * 1.0  2.0  3.0
     * 4.0  5.0  6.0
     * 7.0  8.0  9.0 </pre>
     *
     * @param values The values of the matrix.
     */
    public SquareMatrix(double[][] values) {
        super(values);
        if (getColumnDimension() != getRowDimension()) {
            throw new IllegalArgumentException(
                    "The SquareMatrix class is designed to handle matrices, where the coulumn dimension is the same " +
                            "as the row dimension. The given array contains "
                            + getRowDimension() + " rows and " + getColumnDimension() + " columns.");
        }
    }

    protected SquareMatrix(double[][] values, boolean isSymmetric) {
        super(values, isSymmetric);
    }

    SquareMatrix(double[][] values, int rowDimension, int columnDimension) {
        super(values, rowDimension, columnDimension);
    }

    /**
     * Returns {@code true} if all potential columns are of the same length as the potential rows and {@code false}
     * otherwise.
     *
     * @param potentialValues The potential values of a matrix.
     * @return {@code true} if all columns are of the same length as the rows and {@code false} otherwise.
     */
    public static boolean isSquare(double[][] potentialValues) {
        int requiredLength = potentialValues.length;
        for (double[] value : potentialValues) {
            if (value.length != requiredLength) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns {@code true} if the given matrix has equal column and row dimension and {@code false} otherwise.
     *
     * @param matrix The matrix to be checked.
     * @return {@code true} if the given matrix has equal column and row dimension and {@code false} otherwise.
     */
    public static boolean isSquare(Matrix matrix) {
        return matrix.getColumnDimension() == matrix.getRowDimension();
    }

    /**
     * Returns the main diagonal (all values where the row index is equal to the column index) of the matrix as a
     * vector.
     *
     * @return The main diagonal of the matrix as a vector.
     */
    public Vector getMainDiagonal() {
        double[] values = new double[getRowDimension()];
        for (int diagonalIndex = 0; diagonalIndex < getRowDimension(); diagonalIndex++) {
            values[diagonalIndex] = getElement(diagonalIndex, diagonalIndex);
        }
        return new RegularVector(values);
    }

    /**
     * Calculates the trace (the sum of the main diagonal) of this matrix.
     *
     * @return The trace of this matrix.
     */
    public double trace() {
        double sum = 0.0;
        for (int diagonalIndex = 0; diagonalIndex < getRowDimension(); diagonalIndex++) {
            sum += getElement(diagonalIndex, diagonalIndex);
        }
        return sum;
    }

    /**
     * Calculates the determinant of this matrix.
     *
     * @return The determinant of this matrix.
     */
    public double determinant() {
        // https://technomanor.wordpress.com/2012/03/04/determinant-of-n-x-n-square-matrix/
        return determinant(getElements(), getColumnDimension());
    }

    private static double determinant(double[][] matrix, int order) {
        double determinant = 0;
        int sign = 1;
        int p = 0;
        int q = 0;

        if (order == 1) {
            return matrix[0][0];
        }

        double reducedMatrix[][] = new double[order - 1][order - 1];
        for (int x = 0; x < order; x++) {
            p = 0;
            q = 0;
            for (int i = 1; i < order; i++) {
                for (int j = 0; j < order; j++) {
                    if (j != x) {
                        reducedMatrix[p][q++] = matrix[i][j];
                        if (q % (order - 1) == 0) {
                            p++;
                            q = 0;
                        }
                    }
                }
            }
            determinant = determinant + matrix[0][x] * determinant(reducedMatrix, order - 1) * sign;
            sign = -sign;
        }

        return determinant;
    }

}
