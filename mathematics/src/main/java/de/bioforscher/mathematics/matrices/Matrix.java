package de.bioforscher.mathematics.matrices;

import de.bioforscher.mathematics.concepts.MultiDimensional;
import de.bioforscher.mathematics.concepts.Ring;
import de.bioforscher.mathematics.exceptions.IncompatibleDimensionsException;
import de.bioforscher.mathematics.vectors.RegularVector;
import de.bioforscher.mathematics.vectors.Vector;

/**
 * The {@code Matrix} interface represents a two-dimensional collection of values (like a table with rows and columns)
 * where multiple operations are defined.
 * <p>
 * Each implementation is: addable, subtractable, additively invertible and multipliable.
 *
 * @author Christoph Leberecht
 * @version 1.0.0
 */
public interface Matrix extends MultiDimensional<Matrix>, Ring<Matrix> {

    /**
     * Converts this matrix into a specific implementation. This only works if the "form" of this class is suitable to
     * be converted. The new matrix is a copy of the old one.
     *
     * @param matrixClass The new class of the matrix.
     * @param <M>         Any implementation of this matrix.
     * @return The new converted matrix.
     */
    <M extends Matrix> M as(Class<M> matrixClass);

    /**
     * The transposition of this m-by-n matrix is a new matrix n-by-m formed by turning columns into rows and vice
     * versa.
     *
     * @return The transposition of this matrix.
     */
    Matrix transpose();

    /**
     * The scalar multiplication is an algebraic operation that returns a new matrix where each element is multiplied by
     * the given scalar.
     *
     * @param scalar The scalar.
     * @return The scalar multiplication.
     */
    Matrix multiply(double scalar);

    /**
     * The multiplication of this matrix with the given vector is an algebraic operation that returns a new vector that
     * is transformed with this matrix. The new vector will have the same dimensionality as the row dimension of the
     * matrix. The column dimension of this matrix and the dimension of the vector must agree.
     *
     * @param multiplicand The vector to be transformed.
     * @return The transformed vector.
     * @throws IncompatibleDimensionsException if the column dimension of the matrix and the dimension of the vector do
     *                                         not agree.
     */
    Vector multiply(Vector multiplicand);

    /**
     * The Hadamard multiplication is the element-wise multiplication of two matrices. This operation takes two matrices
     * of the same dimensions, and produces another matrix where each element ij is the product of elements ij of the
     * original two matrices.
     *
     * @param multiplicand The multiplicand.
     * @return The Hadamard product.
     * @throws IncompatibleDimensionsException if the column dimension and the row dimension of this matrix and the
     *                                         given matrix do not agree.
     */
    Matrix hadamardMultiply(Matrix multiplicand);

    /**
     * Returns all elements of this matrix.
     *
     * @return All elements of this matrix as an two-dimensional array.
     */
    double[][] getElements();

    /**
     * Returns an element of this matrix.
     *
     * @param rowIndex    The row index.
     * @param columnIndex The column index.
     * @return A single element of this matrix at the given position.
     */
    double getElement(int rowIndex, int columnIndex);

    /**
     * Returns a column of this matrix.
     *
     * @param columnIndex The column index.
     * @return A column of this matrix as an vector.
     */
    RegularVector getColumn(int columnIndex);

    /**
     * Returns the column dimension.
     *
     * @return The column dimension.
     */
    int getColumnDimension();

    /**
     * Returns a row of this matrix.
     *
     * @param rowIndex The row index.
     * @return A row of this matrix as an vector.
     */
    RegularVector getRow(int rowIndex);

    /**
     * Returns the row dimension.
     *
     * @return The row dimension.
     */
    int getRowDimension();

    /**
     * Returns {@code true} if the inner dimensions of both matrices match and {@code false} otherwise.
     *
     * @param matrix The other matrix.
     * @return {@code true} if the inner dimensions of both matrices match and {@code false} otherwise.
     */
    default boolean hasSameInnerDimension(Matrix matrix) {
        return this.getColumnDimension() == matrix.getRowDimension();
    }

    /**
     * Asserts that the inner dimensions of both matrices match and throws an {@link IncompatibleDimensionsException}
     * otherwise.
     *
     * @param matrix The other matrix.
     * @throws IncompatibleDimensionsException if the column dimension and the row dimension of this matrix and the
     *                                         given matrix do not agree.
     */
    default void assertThatInnerDimensionsMatch(Matrix matrix) {
        if (!hasSameInnerDimension(matrix)) {
            throw new IncompatibleDimensionsException(this, matrix);
        }
    }

    /**
     * Returns {@code true} if the inner dimensions of the matrix and the dimension of the vector match and {@code
     * false} otherwise.
     *
     * @param vector The vector.
     * @return {@code true} if the inner (column) dimensions of the matrix and the dimension of the vector match and
     * {@code
     * false} otherwise.
     */
    default boolean hasSameInnerDimension(Vector vector) {
        return this.getColumnDimension() == vector.getDimension();
    }

    /**
     * Asserts that the inner dimensions of the matrix and the dimension of the vector match and throws an
     * {@link IncompatibleDimensionsException} otherwise.
     *
     * @param vector
     * @throws IncompatibleDimensionsException if the inner (column) dimensions of the matrix and the dimension of
     *                                         the vector do not agree.
     */
    default void assertThatInnerDimensionsMatch(Vector vector) {
        if (!hasSameInnerDimension(vector)) {
            throw new IncompatibleDimensionsException(this, vector);
        }
    }

    /**
     * Returns {@code true} if this matrix and the given matrix have identical dimensionality and {@code false}
     * otherwise.
     *
     * @param matrix
     * @return
     */
    @Override
    default boolean hasSameDimensions(Matrix matrix) {
        return this.getRowDimension() == matrix.getRowDimension()
                && this.getColumnDimension() == matrix.getColumnDimension();
    }

}
