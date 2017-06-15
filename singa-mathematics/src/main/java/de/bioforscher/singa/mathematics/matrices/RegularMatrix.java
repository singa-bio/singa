package de.bioforscher.singa.mathematics.matrices;

import de.bioforscher.singa.mathematics.exceptions.MalformedMatrixException;
import de.bioforscher.singa.mathematics.vectors.RegularVector;
import de.bioforscher.singa.mathematics.vectors.Vector;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

import static de.bioforscher.singa.mathematics.matrices.FastMatrices.createRegularMatrix;

/**
 * The {@code RegularMatrix} class is the primary implementation of the {@link Matrix} interface. Using double arrays
 * to store values it provides the fundamental operations of linear algebra. This implementation declares all values
 * as final.
 *
 * @author Christoph Leberecht
 * @see <a href="https://en.wikipedia.org/wiki/Matrix_(mathematics)">Wikipedia: Matrix</a>
 */
public class RegularMatrix implements Matrix, Serializable {

    private static final long serialVersionUID = -3809415443253404586L;

    private final double[][] elements;

    private final int rowDimension;
    private final int columnDimension;

    /**
     * Creates a new {@code RegularMatrix} with the given double values. The first index of the double array
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
    public RegularMatrix(double[][] values) {
        this(values, false);
    }

    protected RegularMatrix(double[][] values, boolean isSymmetric) {
        if (!isSymmetric) {
            if (!isWellFormed(values)) {
                throw new MalformedMatrixException(values);
            }
            // non symmetric, well formed, possibly rectangular
            this.elements = values;
            this.rowDimension = values.length;
            this.columnDimension = values[0].length;
        } else {
            if (SymmetricMatrix.isCompact(values)) {
                // symmetric, non well formed, compact
                this.elements = values;
            } else {
                if (!isWellFormed(values)) {
                    throw new MalformedMatrixException(values);
                }
                // symmetric, well formed, not compact
                this.elements = SymmetricMatrix.compactToSymmetricMatrix(values);
            }
            // square
            this.rowDimension = values.length;
            this.columnDimension = this.rowDimension;
        }
    }

    /**
     * A fast constructor usable from the fast Matrices class.
     */
    RegularMatrix(double[][] values, int rowDimension, int columnDimension) {
        this.elements = values;
        this.rowDimension = rowDimension;
        this.columnDimension = columnDimension;
    }

    /**
     * Tries to create a new Matrix of the given class with the given values. This method should be used sparingly! Only
     * matrices can be constructed that provide a double[][] constructor.
     *
     * @param values        The values of the matrix.
     * @param matrixClass   The class of the matrix.
     * @param <MatrixClass> An implementation of {@link Matrix} that extends the Matrix interface
     * @return The new matrix.
     */
    private static <MatrixClass extends Matrix> MatrixClass createNewMatrix(double[][] values,
                                                                            Class<MatrixClass> matrixClass) {
        try {
            return matrixClass.getConstructor(double[][].class).newInstance((Object) values);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns {@code true} if all rows are equal in length and {@code false} otherwise.
     *
     * @param potentialValues The potential values of a matrix.
     * @return {@code true} if all rows are equal in length and {@code false} otherwise.
     */
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

    @Override
    public String getDimensionAsString() {
        return String.valueOf(this.getRowDimension()) + "x"
                + String.valueOf(this.getColumnDimension());
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
        return createRegularMatrix(values);
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
        return createRegularMatrix(values);
    }

    @Override
    public Matrix multiply(double multiplicand) {
        double[][] values = new double[getRowDimension()][getColumnDimension()];
        for (int rowIndex = 0; rowIndex < getRowDimension(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < getColumnDimension(); columnIndex++) {
                values[rowIndex][columnIndex] = this.getElement(rowIndex, columnIndex) * multiplicand;
            }
        }
        return createRegularMatrix(values);
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
        return createRegularMatrix(values);
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
        return createRegularMatrix(values);
    }

    @Override
    public Matrix additivelyInvert() {
        double[][] values = new double[getRowDimension()][getColumnDimension()];
        for (int rowIndex = 0; rowIndex < getRowDimension(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < getColumnDimension(); columnIndex++) {
                values[rowIndex][columnIndex] = -this.getElement(rowIndex, columnIndex);
            }
        }
        return createRegularMatrix(values);
    }

    @Override
    public Matrix transpose() {
        double[][] values = new double[getColumnDimension()][getRowDimension()];
        for (int rowIndex = 0; rowIndex < getRowDimension(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < getColumnDimension(); columnIndex++) {
                values[columnIndex][rowIndex] = this.getElement(rowIndex, columnIndex);
            }
        }
        return createRegularMatrix(values);
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
                cells[rowIndex][columnIndex] = String.format("%.2f", this.getElement(rowIndex, columnIndex));
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
