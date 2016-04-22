package de.bioforscher.mathematics.matrices;

import de.bioforscher.mathematics.vectors.RegularVector;
import de.bioforscher.mathematics.vectors.Vector;

public class SquareMatrix extends RegularMatrix {

    public SquareMatrix(double[][] values) {
        super(values);
        if (this.getNumberOfColumnDimensions() != this.getNumberOfRowDimensions()) {
            throw new IllegalArgumentException(
                    "The SquareMatrix class is designed to handle matrices, where the coulumn dimension is the same as the row dimension. The given array contains "
                            + getNumberOfRowDimensions() + " rows and " + getNumberOfColumnDimensions() + " columns.");
        }
    }

    protected SquareMatrix(double[][] values, boolean isSymmetric) {
        super(values, isSymmetric);
    }

    public static boolean isSquare(double[][] values) {
        int requiredLength = values.length;
        for (int rowIndex = 0; rowIndex < requiredLength; rowIndex++) {
            if (values[rowIndex].length != requiredLength) {
                return false;
            }
        }
        return true;
    }

    public static boolean isSquare(Matrix matrix) {
        return matrix.getNumberOfColumnDimensions() == matrix.getNumberOfRowDimensions();
    }

    public Vector getMainDiagonal() {
        double[] values = new double[getNumberOfRowDimensions()];
        for (int diagonalIndex = 0; diagonalIndex < getNumberOfRowDimensions(); diagonalIndex++) {
            values[diagonalIndex] = getElement(diagonalIndex, diagonalIndex);
        }
        return new RegularVector(values);
    }

    public double trace() {
        double sum = 0.0;
        for (int diagonalIndex = 0; diagonalIndex < getNumberOfRowDimensions(); diagonalIndex++) {
            sum += this.getElement(diagonalIndex, diagonalIndex);
        }
        return sum;
    }

    public double determinant() {
        // https://technomanor.wordpress.com/2012/03/04/determinant-of-n-x-n-square-matrix/
        return determinant(this.getElements(), this.getNumberOfColumnDimensions());
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
