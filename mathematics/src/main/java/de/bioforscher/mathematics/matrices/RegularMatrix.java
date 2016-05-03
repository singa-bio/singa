package de.bioforscher.mathematics.matrices;

import de.bioforscher.mathematics.exceptions.MalformedMatrixException;
import de.bioforscher.mathematics.vectors.RegularVector;
import de.bioforscher.mathematics.vectors.Vector;

import java.lang.reflect.InvocationTargetException;

public class RegularMatrix implements Matrix {

    private final double[][] elements;

    private final int rowDimension;
    private final int columnDimension;

    public RegularMatrix(double[][] values) {
        this(values, false);
    }

    RegularMatrix(double[][] values, boolean isSymmetric) {
        if (!isSymmetric) {
            if (!RegularMatrix.isWellFormed(values)) {
                throw new MalformedMatrixException(values);
            }
            this.elements = values;
        } else {
            this.elements = SymmetricMatrix.compactToSymmetricMatrix(values);
        }

        this.rowDimension = values.length;
        this.columnDimension = values[0].length;
    }

    public static <MatrixClass extends Matrix> MatrixClass createNewMatrix(double[][] values,
                                                                           Class<MatrixClass> matrixClass) {
        try {
            return matrixClass.getConstructor(double[][].class).newInstance(values);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public <MatrixClass extends Matrix> MatrixClass as(Class<MatrixClass> matrixClass) {
        if (this.getClass().equals(matrixClass)) {
            return matrixClass.cast(this);
        } else if (SquareMatrix.isSquare(this) && matrixClass.getSimpleName().equals("SquareMatrix")) {
            return createNewMatrix(this.getElements(), matrixClass);
        } else if (SymmetricMatrix.isSymmetric(this) && matrixClass.getSimpleName().equals("SymmetricMatrix")) {
            return createNewMatrix(this.getElements(), matrixClass);
        } else {
            throw new IllegalArgumentException("Could not create desired matrix, from this instance.");
        }
    }

    public static boolean isWellFormed(double[][] potentialValues) {
        int requiredLength = potentialValues[0].length;
        for (int rowIndex = 1; rowIndex < potentialValues.length; rowIndex++) {
            if (potentialValues[rowIndex].length != requiredLength) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getDimensionAsString() {
        return String.valueOf(this.getRowDimension()) + "x"
                + String.valueOf(this.getRowDimension());
    }

    @Override
    public double getElement(int rowIndex, int columnIndex) {
        return this.elements[rowIndex][columnIndex];
    }

    @Override
    public double[][] getElements() {
        return this.elements;
    }

    @Override
    public RegularVector getRow(int rowNumber) {
        return new RegularVector(this.elements[rowNumber]);
    }

    @Override
    public int getRowDimension() {
        return this.rowDimension;
    }

    @Override
    public RegularVector getColumn(int columnNumber) {
        double[] columnValues = new double[getRowDimension()];
        for (int rowIndex = 0; rowIndex < getRowDimension(); rowIndex++) {
            columnValues[rowIndex] = this.elements[rowIndex][columnNumber];
        }
        return new RegularVector(columnValues);
    }

    @Override
    public int getColumnDimension() {
        return this.columnDimension;
    }

    @Override
    public Matrix add(Matrix summand) {
        assertThatDimensionsMatch(summand);
        double[][] values = new double[getRowDimension()][getColumnDimension()];
        for (int rowIndex = 0; rowIndex < getRowDimension(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < getColumnDimension(); columnIndex++) {
                values[rowIndex][columnIndex] = this.getElement(rowIndex, columnIndex)
                        + summand.getElement(rowIndex, columnIndex);
            }
        }
        return new RegularMatrix(values);
    }

    @Override
    public Matrix subtract(Matrix subtrahend) {
        assertThatDimensionsMatch(subtrahend);
        double[][] values = new double[getRowDimension()][getColumnDimension()];
        for (int rowIndex = 0; rowIndex < getRowDimension(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < getColumnDimension(); columnIndex++) {
                values[rowIndex][columnIndex] = this.getElement(rowIndex, columnIndex)
                        - subtrahend.getElement(rowIndex, columnIndex);
            }
        }
        return new RegularMatrix(values);
    }

    @Override
    public Matrix multiply(double multiplicand) {
        double[][] values = new double[getRowDimension()][getColumnDimension()];
        for (int rowIndex = 0; rowIndex < getRowDimension(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < getColumnDimension(); columnIndex++) {
                values[rowIndex][columnIndex] = this.getElement(rowIndex, columnIndex) * multiplicand;
            }
        }
        return new RegularMatrix(values);
    }

    @Override
    public Vector multiply(Vector multiplicand) {
        assertThatInnerDimensionsMatch(multiplicand);
        double[] values = new double[getRowDimension()];
        for (int matrixRowIndex = 0; matrixRowIndex < getRowDimension(); matrixRowIndex++) {
            for (int vectorColumnIndex = 0; vectorColumnIndex < multiplicand.getDimension(); vectorColumnIndex++) {
                values[matrixRowIndex] += multiplicand.getElement(vectorColumnIndex)
                        * this.getElement(matrixRowIndex, vectorColumnIndex);
            }
        }
        return new RegularVector(values);
    }

    @Override
    public Matrix multiply(Matrix multiplicand) {
        assertThatInnerDimensionsMatch(multiplicand);
        double[][] values = new double[getRowDimension()][multiplicand.getColumnDimension()];
        for (int multiplierRowIndex = 0; multiplierRowIndex < getRowDimension(); multiplierRowIndex++) {
            for (int multiplicandColumnIndex = 0; multiplicandColumnIndex < multiplicand.getColumnDimension();
                 multiplicandColumnIndex++) {
                for (int multiplierColumnIndex = 0; multiplierColumnIndex < this.getColumnDimension();
                     multiplierColumnIndex++) { // aColumn
                    values[multiplierRowIndex][multiplicandColumnIndex] += this.getElement(multiplierRowIndex,
                            multiplierColumnIndex)
                            * multiplicand.getElement(multiplierColumnIndex, multiplicandColumnIndex);
                }
            }
        }
        return new RegularMatrix(values);
    }

    @Override
    public Matrix hadamardMultiply(Matrix multiplicand) {
        assertThatDimensionsMatch(multiplicand);
        double[][] values = new double[getRowDimension()][getColumnDimension()];
        for (int rowIndex = 0; rowIndex < getRowDimension(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < getColumnDimension(); columnIndex++) {
                values[rowIndex][columnIndex] = this.getElement(rowIndex, columnIndex)
                        * multiplicand.getElement(rowIndex, columnIndex);
            }
        }
        return new RegularMatrix(values);
    }

    @Override
    public Matrix additivelyInvert() {
        double[][] values = new double[getRowDimension()][getColumnDimension()];
        for (int rowIndex = 0; rowIndex < getRowDimension(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < getColumnDimension(); columnIndex++) {
                values[rowIndex][columnIndex] = -this.getElement(rowIndex, columnIndex);
            }
        }
        return new RegularMatrix(values);
    }

    @Override
    public Matrix transpose() {
        double[][] values = new double[getColumnDimension()][getRowDimension()];
        for (int rowIndex = 0; rowIndex < getRowDimension(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < getColumnDimension(); columnIndex++) {
                values[columnIndex][rowIndex] = this.getElement(rowIndex, columnIndex);
            }
        }
        return new RegularMatrix(values);
    }

    @Override
    public String toString() {

        StringBuffer resultString = new StringBuffer();

        // Convert to String[][]
        int cols = getColumnDimension();
        int rows = getRowDimension();
        String[][] cells = new String[rows][];
        for (int rowIndex = 0; rowIndex < rows; rowIndex++) {
            cells[rowIndex] = new String[cols];
            for (int columnIndex = 0; columnIndex < cols; columnIndex++) {
                cells[rowIndex][columnIndex] = String.format("%.2f", this.elements[rowIndex][columnIndex]);
            }
        }

        // Compute widths
        int[] widths = new int[cols];
        for (int rowIndex = 0; rowIndex < rows; rowIndex++) {
            for (int columnIndex = 0; columnIndex < cols; columnIndex++) {
                widths[columnIndex] = Math.max(widths[columnIndex], cells[rowIndex][columnIndex].length());
            }
        }

        // to string
        for (int rowIndex = 0; rowIndex < rows; rowIndex++) {
            for (int columnIndex = 0; columnIndex < cols; columnIndex++) {
                resultString = resultString.append(String.format("%" + widths[columnIndex] + "s%s",
                        cells[rowIndex][columnIndex], columnIndex == cols - 1 ? "\n" : " "));
            }
        }

        return resultString.toString();
    }

}
