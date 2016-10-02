package de.bioforscher.mathematics.matrices;

import de.bioforscher.mathematics.vectors.Vector;

import java.util.ArrayList;
import java.util.List;

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
        for (int column = 0; column < matrix.getColumnDimension(); column++){
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
        for (int row = 0; row < matrix.getRowDimension(); row++){
            rows.add(matrix.getColumn(row));
        }
        return rows;
    }


}
