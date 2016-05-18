package de.bioforscher.mathematics.matrices;

import de.bioforscher.mathematics.exceptions.MalformedMatrixException;

public class SymmetricMatrix extends SquareMatrix {

    public SymmetricMatrix(double[][] values) {
        super(values, true);
    }

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

    public static boolean isSymmetric(Matrix matrix) {
        return SymmetricMatrix.isSymmetric(matrix.getElements());
    }

    public static void assertThatValuesAreSymmetric(double[][] potentialValues) {
        if (!SymmetricMatrix.isSymmetric(potentialValues)) {
            throw new MalformedMatrixException(potentialValues);
        }
    }

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
