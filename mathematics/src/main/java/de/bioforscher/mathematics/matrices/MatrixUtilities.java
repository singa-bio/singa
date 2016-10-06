package de.bioforscher.mathematics.matrices;

import de.bioforscher.core.utility.Pair;
import de.bioforscher.mathematics.vectors.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class MatrixUtilities {

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

    public static List<Vector> divideIntoColumns(Matrix matrix) {
        List<Vector> columns = new ArrayList<>();
        for (int column = 0; column < matrix.getColumnDimension(); column++) {
            columns.add(matrix.getColumn(column));
        }
        return columns;
    }

    public static Matrix matrixFromColumns(List<Vector> columnVectors) {
        double[][] elements = new double[columnVectors.size()][columnVectors.get(0).getDimension()];
        for (int row = 0; row < columnVectors.size(); row++) {
            elements[row] = columnVectors.get(row).getElements();
        }
        return new RegularMatrix(elements);
    }

    public static List<Vector> divideIntoRows(Matrix matrix) {
        List<Vector> rows = new ArrayList<>();
        for (int row = 0; row < matrix.getRowDimension(); row++) {
            rows.add(matrix.getColumn(row));
        }
        return rows;
    }


    /**
     * returns a list of positions of the minimal elements of a {@link Matrix}
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
}
