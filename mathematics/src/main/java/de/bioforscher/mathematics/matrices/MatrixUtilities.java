package de.bioforscher.mathematics.matrices;

import de.bioforscher.core.utility.Pair;
import de.bioforscher.mathematics.algorithms.matrix.QRDecomposition;
import de.bioforscher.mathematics.algorithms.matrix.SVDecomposition;
import de.bioforscher.mathematics.vectors.Vector;
import de.bioforscher.mathematics.vectors.VectorUtilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class MatrixUtilities {

    /**
     * prevent instantiation
     */
    private MatrixUtilities() {
    }

    /**
     * Creates a new identity matrix of the desired size. <br>
     * <p>
     * For example the call:
     * <pre>
     * MatrixUtilities.generateIdentityMatrix(4) </pre>
     * result in the matrix:
     * <pre>
     * 1.0 0.0 0.0 0.0
     * 0.0 1.0 0.0 0.0
     * 0.0 0.0 1.0 0.0
     * 0.0 0.0 0.0 1.0 </pre>
     *
     * @param size The size of the resulting matrix.
     * @return A identity matrix.
     */
    public static SquareMatrix generateIdentityMatrix(int size) {
        double[][] values = new double[size][size];
        for (int diagonalIndex = 0; diagonalIndex < size; diagonalIndex++) {
            values[diagonalIndex][diagonalIndex] = 1.0;
        }
        return new SquareMatrix(values);
    }

    /**
     * Divides the matrix into columns. The resulting list contains all columns of the original matrix in vectors. The
     * order is maintained (the column with index 0 is in the list at index 0).
     *
     * @param matrix The matrix to be divided.
     * @return The columns as vectors.
     */
    public static List<Vector> divideIntoColumns(Matrix matrix) {
        List<Vector> columns = new ArrayList<>();
        for (int column = 0; column < matrix.getColumnDimension(); column++) {
            columns.add(matrix.getColumn(column));
        }
        return columns;
    }

    /**
     * Creates a new matrix from vectors in a list. Each vector is a new column in the new matrix. The order of the list
     * is maintained.
     *
     * @param columnVectors The columns as vectors.
     * @return The columns combined to a matrix.
     */
    public static Matrix assembleMatrixFromColumns(List<Vector> columnVectors) {
        if (!VectorUtilities.haveSameDimension(columnVectors)) {
            throw new IllegalArgumentException("All vectors need to have the same dimension in order to create a" +
                    " matrix out of them.");
        }
        double[][] elements = new double[columnVectors.size()][columnVectors.get(0).getDimension()];
        for (int row = 0; row < columnVectors.size(); row++) {
            elements[row] = columnVectors.get(row).getElements();
        }
        return new RegularMatrix(elements);
    }

    /**
     * Divides the matrix into rows. The resulting list contains all rows of the original matrix in vectors. The
     * order is maintained (the column with index 0 is in the list at index 0).
     *
     * @param matrix The matrix to be divided.
     * @return The rows as vectors.
     */
    public static List<Vector> divideIntoRows(Matrix matrix) {
        List<Vector> rows = new ArrayList<>();
        for (int row = 0; row < matrix.getRowDimension(); row++) {
            rows.add(matrix.getRow(row));
        }
        return rows;
    }

    /**
     * Creates a new matrix from vectors in a list. Each vector is a new row in the new matrix. The order of the list
     * is maintained.
     *
     * @param rowVectors The rows as vectors.
     * @return The rows combined to a matrix.
     */
    public static Matrix assembleMatrixFromRows(List<Vector> rowVectors) {
        if (!VectorUtilities.haveSameDimension(rowVectors)) {
            throw new IllegalArgumentException("All vectors need to have the same dimension in order to create a" +
                    " matrix out of them.");
        }
        double[][] elements = new double[rowVectors.size()][rowVectors.get(0).getDimension()];
        for (int column = 0; column < rowVectors.size(); column++) {
            elements[column] = rowVectors.get(column).getElements();
        }
        return new RegularMatrix(elements);
    }


    /**
     * Returns a list of positions of the minimal elements of a {@link Matrix}
     *
     * @return positions of the minimal elements represented as a {@link Pair} (i,j) of {@link Integer} values
     */
    public static List<Pair<Integer>> getPositionsOfMinimalElement(Matrix matrix) {
        double minimalElement = Double.MAX_VALUE;
        List<Pair<Integer>> minimalElementPositions = new ArrayList<>();
        for (int i = 0; i < matrix.getRowDimension(); i++) {
            for (int j = 0; j < matrix.getColumnDimension(); j++) {
                double currentMatrixElement = matrix.getElement(i, j);
                if (Double.compare(currentMatrixElement, minimalElement) == 0)
                    minimalElementPositions.add(new Pair<>(i, j));
                else if (Double.compare(currentMatrixElement, minimalElement) < 0) {
                    minimalElement = currentMatrixElement;
                    minimalElementPositions.clear();
                    minimalElementPositions.add(new Pair<>(i, j));
                }
            }
        }
        return minimalElementPositions;
    }

    /**
     * returns an {@link Optional} of a {@link Pair} that represents the position of the unique minimal element,
     * or an empty {@link Optional} if the minimal element is ambiguous
     *
     * @return position of the minimal element represented as a {@link Pair} (i,j) of {@link Integer} values
     */
    public static Optional<Pair<Integer>> getPositionOfMinimalElement(Matrix matrix) {
        List<Pair<Integer>> minimalElementPositions = getPositionsOfMinimalElement(matrix);
        return minimalElementPositions.size() == 1 ? Optional.of(minimalElementPositions.get(0)) : Optional.empty();
    }

    /**
     * returns a list of positions of the maximal elements of a {@link Matrix}
     *
     * @return positions of the maximal elements represented as a {@link Pair} (i,j) of {@link Integer} values
     */
    public static List<Pair<Integer>> getPositionsOfMaximalElement(Matrix matrix) {
        double maximalElement = -Double.MAX_VALUE;
        List<Pair<Integer>> maximalElementsPositions = new ArrayList<>();
        for (int i = 0; i < matrix.getColumnDimension(); i++) {
            for (int j = 0; j < matrix.getRowDimension(); j++) {
                double currentMatrixElement = matrix.getElement(i, j);
                if (Double.compare(currentMatrixElement, maximalElement) == 0)
                    maximalElementsPositions.add(new Pair<>(i, j));
                else if (Double.compare(currentMatrixElement, maximalElement) > 0) {
                    maximalElement = currentMatrixElement;
                    maximalElementsPositions.clear();
                    maximalElementsPositions.add(new Pair<>(i, j));
                }
            }
        }
        return maximalElementsPositions;
    }

    /**
     * returns an {@link Optional} of a {@link Pair} that represents the position of the unique maximal element,
     * or an empty {@link Optional} if the maximal element is ambiguous
     *
     * @return position of the maximal element represented as a {@link Pair} (i,j) of {@link Integer} values
     */
    public static Optional<Pair<Integer>> getPositionOfMaximalElement(Matrix matrix) {
        List<Pair<Integer>> maximalElementPositions = getPositionsOfMaximalElement(matrix);
        return maximalElementPositions.size() == 1 ? Optional.of(maximalElementPositions.get(0)) : Optional.empty();
    }

    public static QRDecomposition performQRDecomposition(Matrix matrix) {
        return QRDecomposition.calculateQRDecomposition(matrix);

    }

    /**
     * calculates the covariance matrix between to matrices in respect to matrix A
     * cov(A) = B'*A
     *
     * @param a matrix A to which covariance should be calculated
     * @param b matrix
     * @return the covariance matrix (A and B are not modified)
     */
    public static Matrix calculateCovarianceMatrix(Matrix a, Matrix b) {
        return b.transpose().multiply(a);
    }

    public static SVDecomposition performSVDecomposition(Matrix matrix) {
        return new SVDecomposition(matrix);
    }
}
