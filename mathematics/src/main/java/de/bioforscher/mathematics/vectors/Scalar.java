package de.bioforscher.mathematics.vectors;

import de.bioforscher.mathematics.concepts.Dimension;
import de.bioforscher.mathematics.exceptions.IncompatibleDimensionsException;
import de.bioforscher.mathematics.matrices.RegularMatrix;

/**
 * This class is a wrapper class for one dimensional vectors that may be used
 * instead of double values to avoid mixing of different levels of abstraction.
 *
 * @author Christoph Leberecht
 * @version 1.0.1
 */
public class Scalar implements Vector {

    private final double value;
    private static final Dimension dimension = new Dimension(1);

    /**
     * Creates a new scalar with the given value.
     *
     * @param value The value of this scalar.
     */
    public Scalar(double value) {
        this.value = value;
    }

    /**
     * Returns the value of this scalar.
     *
     * @return The value of this scalar.
     */
    public double getValue() {
        return this.value;
    }

    /**
     * Returns the value of this vector.
     * <p>
     * This method exists solely for compatibility to the vector interface. If
     * you want to get the value of this scalar it is better to use the
     * {@code getValue()} Method.
     *
     * @return A single element of this vector at the given position.
     * @throws IndexOutOfBoundsException if the index is different from 0
     */
    @Override
    public double getElement(int index) {
        if (index != 0) {
            throw new IndexOutOfBoundsException("Scalar can only contain a single value at index 0.");
        }
        return this.value;
    }

    @Override
    public double[] getElements() {
        return new double[]{this.value};
    }

    @Override
    public Dimension getDimension() {
        return Scalar.dimension;
    }

    /**
     * Returns the string representation of the dimension of this vector.
     *
     * @return The string representation of the dimension of this vector. ( Can
     * only result in "1".)
     * @see Dimension
     */
    @Override
    public String getDimensionAsString() {
        return Scalar.dimension.toString();
    }

    @Override
    public boolean hasSameDimensions(Vector element) {
        return element.getDimension().getDegreesOfFreedom() == 1;
    }

    /**
     * The addition is an algebraic operation that returns a new vector with
     * their values added.
     *
     * @param summand Another 1D vector.
     * @return The addition.
     * @throws IncompatibleDimensionsException if this vector has more than one dimension.
     */
    @Override
    public Scalar add(Vector summand) {
        assertThatDimensionsMatch(summand);
        return new Scalar(this.value + summand.getElement(0));
    }

    @Override
    public Scalar subtract(Vector subtrahend) {
        assertThatDimensionsMatch(subtrahend);
        return new Scalar(this.value - subtrahend.getElement(0));
    }

    /**
     * Additively inverts (negates) the value.
     *
     * @return A new vector with its value inverted.
     */
    @Override
    public Scalar additivelyInvert() {
        return new Scalar(-this.value);
    }

    @Override
    public Scalar additiveleyInvertElement(int index) {
        if (index != 0) {
            throw new IndexOutOfBoundsException("A scalar can only contain a single value at index 0.");
        }
        return additivelyInvert();
    }

    /**
     * The multiplication is an algebraic operation that returns a new vector
     * with their values multiplied.
     *
     * @param multiplicand Another 1D vector.
     * @return The multiplication.
     * @throws IncompatibleDimensionsException if this vector has more than one dimension.
     */
    @Override
    public Scalar multiply(Vector multiplicand) {
        assertThatDimensionsMatch(multiplicand);
        return multiply(multiplicand.getElement(0));
    }

    @Override
    public Scalar multiply(double scalar) {
        return new Scalar(this.value * scalar);
    }

    /**
     * The division is an algebraic operation that returns a new vector where
     * the value of the given vector is subtracted from the value of this
     * vector.
     *
     * @param divisor Another 1D vector.
     * @return The subtraction.
     * @throws IncompatibleDimensionsException if this vector has more than one dimension.
     */
    @Override
    public Scalar divide(Vector divisor) {
        assertThatDimensionsMatch(divisor);
        return divide(divisor.getElement(0));
    }

    @Override
    public Scalar divide(double scalar) {
        return new Scalar(this.value / scalar);
    }

    @Override
    public Scalar normalize() {
        return new Scalar(this.value);
    }

    @Override
    public double dotProduct(Vector vector) {
        assertThatDimensionsMatch(vector);
        return multiply(vector.getElement(0)).getValue();
    }

    @Override
    public RegularMatrix dyadicProduct(Vector vector) {
        return new RegularMatrix(new double[][]{{multiply(vector.getElement(0)).getValue()}});
    }

    @Override
    public double getMagnitude() {
        return this.value;
    }

    /**
     * This method calculates the Eucledian distance between this vector and the
     * given vector.
     * <p>
     * The Euclidean distance for one dimension is the absolute difference
     * between their values.
     *
     * @param vector Another 1D vector.
     * @return The Euclidean distance.
     * @throws IncompatibleDimensionsException if this vector has more than one dimension.
     */
    @Override
    public double distanceTo(Vector vector) {
        assertThatDimensionsMatch(vector);
        return Math.abs(this.subtract(vector).getValue());
    }

}
