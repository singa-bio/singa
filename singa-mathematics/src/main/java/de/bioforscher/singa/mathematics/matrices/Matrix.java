package de.bioforscher.singa.mathematics.matrices;

import de.bioforscher.singa.core.utility.Pair;
import de.bioforscher.singa.mathematics.concepts.MultiDimensional;
import de.bioforscher.singa.mathematics.concepts.Ring;
import de.bioforscher.singa.mathematics.exceptions.IncompatibleDimensionsException;
import de.bioforscher.singa.mathematics.vectors.RegularVector;
import de.bioforscher.singa.mathematics.vectors.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.stream.DoubleStream;

/**
 * The {@code Matrix} interface represents a two-dimensional collection of values (like a table with rows and columns)
 * where multiple operations are defined.
 * <p>
 * Each implementation is: addable, subtractable, additively invertible and multipliable.
 *
 * @author cl
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
     * Returns a stream of all elements in the matrix, where columns are traversed first and rows second. So e.g.
     * a matrix with 3 rows and 4 columns is traversed column: c1,r1 - c1,r2 - c1,r3 - c2,r1 - ... c4,r3
     *
     * @return a stream of all elements in the matrix.
     */
    default DoubleStream streamElements() {
        return Arrays.stream(getElements()).flatMapToDouble(Arrays::stream);
    }

    /**
     * Returns a stream of all positions in the form of pairs and the respective elements. In the pairs, the first
     * element represents the row index and the second element the column index.
     *
     * @param action The action.
     */
    default void forEach(BiConsumer<Pair<Integer>, Double> action) {
        for (int rowIndex = 0; rowIndex < getRowDimension(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < getColumnDimension(); columnIndex++) {
                action.accept(new Pair<>(rowIndex, columnIndex), getElement(rowIndex, columnIndex));
            }
        }
    }


    /**
     * Returns an explicit copy of this matrix. A new array is created and filled with values.
     *
     * @param <M> The concrete implementation of this vector.
     * @return An exact copy of and as a unrelated copy (safe to modify).
     */
    default <M extends Matrix> M getCopy() {
        final double[][] copyOfElements = new double[getElements().length][];
        for (int i = 0; i < getElements().length; i++) {
            final double[] row = getElements()[i];
            copyOfElements[i] = new double[row.length];
            System.arraycopy(row, 0, copyOfElements[i], 0, row.length);
        }
        try {
            return (M) getClass().getConstructor(double[][].class).newInstance((Object) copyOfElements);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
            throw new UnsupportedOperationException("Instance types must match to copy successfully.");
        }
    }

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
        return getColumnDimension() == matrix.getRowDimension();
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
        return getColumnDimension() == vector.getDimension();
    }

    /**
     * Asserts that the inner dimensions of the matrix and the dimension of the vector match and throws an
     * {@link IncompatibleDimensionsException} otherwise.
     *
     * @param vector The vector.
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
     * @param matrix The matrix.
     * @return {@code true} if this matrix and the given matrix have identical dimensionality and {@code false}
     * otherwise.
     */
    @Override
    default boolean hasSameDimensions(Matrix matrix) {
        return getRowDimension() == matrix.getRowDimension()
                && getColumnDimension() == matrix.getColumnDimension();
    }

}
