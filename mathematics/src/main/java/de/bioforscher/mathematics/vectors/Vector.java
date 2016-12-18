package de.bioforscher.mathematics.vectors;

import de.bioforscher.mathematics.concepts.Divisible;
import de.bioforscher.mathematics.concepts.MultiDimensional;
import de.bioforscher.mathematics.concepts.Ring;
import de.bioforscher.mathematics.exceptions.IncompatibleDimensionsException;
import de.bioforscher.mathematics.matrices.RegularMatrix;
import de.bioforscher.mathematics.metrics.model.Metrizable;

import java.lang.reflect.InvocationTargetException;
import java.util.function.BiConsumer;
import java.util.stream.DoubleStream;

/**
 * The {@code Vector} interface represents a collection of values where multiple
 * operations are defined.
 * <p>
 * Each implementation is: addable, subtractable,  additively invertible, multipliable,
 * divisible, and metrizable.
 *
 * @author Christoph Leberecht
 */
public interface Vector extends Ring<Vector>, MultiDimensional<Vector>, Divisible<Vector>, Metrizable<Vector> {

    <V extends Vector> V as(Class<V> vectorClass);

    /**
     * Returns an element of this vector.
     *
     * @return A single element of this vector at the given position.
     */
    double getElement(int index);

    /**
     * Returns all elements of this vector.
     *
     * @return All elements of this vector as an array.
     */
    double[] getElements();

    /**
     * Returns a stream of all elements in the vector.
     *
     * @return
     */
    default DoubleStream streamElements() {
        return DoubleStream.of(this.getElements());
    }

    /**
     * Returns a stream of all positions and the respective elements.
     *
     * @param action
     */
    default void forEach(BiConsumer<Integer, Double> action) {
        for (int i = 0; i < this.getDimension(); i++) {
            action.accept(i, this.getElement(i));
        }
    }

    /**
     * Returns an explicit copy of this vector. A new array is created and filled with values.
     *
     * @return An exact copy of and as a unrelated copy (safe to modify).
     */
    default <V extends Vector> V getCopy() {
        final double[] copyOfElements = new double[getElements().length];
        System.arraycopy(getElements(), 0, copyOfElements, 0, getElements().length);
        try {
            return (V) getClass().getConstructor(double[].class).newInstance((Object) copyOfElements);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
            throw new UnsupportedOperationException("Instance types must match to copy successfully.");
        }
    }


    /**
     * Returns the dimension of this vector.
     *
     * @return The dimension of this vector.
     */
    int getDimension();

    /**
     * Additively inverts (negates) the element at the given index.
     *
     * @param index The index of the element to be inverted.
     * @return A new vector where the specified element is inverted.
     */
    Vector additiveleyInvertElement(int index);

    /**
     * The scalar multiplication is an algebraic operation that returns a new
     * vector where each element is multiplied by the given scalar. This
     * operation can also be thought of as "stretching" of the vector.
     *
     * @param scalar The scalar.
     * @return The scalar multiplication.
     */
    Vector multiply(double scalar);

    /**
     * The scalar division is an algebraic operation that returns a new vector
     * where each element is divided by the given scalar. This operation can
     * also be thought of as "compressing" of the vector.
     *
     * @param scalar The scalar.
     * @return The scalar multiplication.
     */
    Vector divide(double scalar);

    /**
     * The normalization or unit vector of a vector is a vector that points in
     * the same direction as the original vector but has a magnitude of one.
     *
     * @return The normalized vector.
     */
    Vector normalize();

    /**
     * The dot product or scalar product is an algebraic operation that returns
     * the sum of the products of the corresponding elements of the two vectors.
     *
     * @param vector Another vector of the same dimension.
     * @return The dot product.
     * @throws IncompatibleDimensionsException if this vector has another dimension than the given vector.
     */
    double dotProduct(Vector vector);

    /**
     * The magnitude or size gives the ordinary distance from the coordinate
     * origin to the point represented by this vector.
     *
     * @return The magnitude or size of this vector.
     */
    double getMagnitude();

    /**
     * The numerical components of vectors can be arranged into row and column
     * vectors. The dyadic product returns a matrix, where the element (i,j) is
     * obtained by multiplying the i'th element of this vector with the j'th
     * element of the other vector.
     *
     * @return The matrix of the dyadic product
     */
    RegularMatrix dyadicProduct(Vector vector);

    /**
     * Checks if this vector contains only Zeros.
     *
     * @return Thrue, if this vector contains only Zeros.
     */
    default boolean isZero() {
        for (double element : this.getElements()) {
            if (element != 0.0) {
                return false;
            }
        }
        return true;
    }

}
