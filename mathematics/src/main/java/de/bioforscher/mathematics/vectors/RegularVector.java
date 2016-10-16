package de.bioforscher.mathematics.vectors;

import de.bioforscher.mathematics.exceptions.IncompatibleDimensionsException;
import de.bioforscher.mathematics.matrices.RegularMatrix;
import de.bioforscher.mathematics.metrics.implementations.MinkowskiMetric;
import de.bioforscher.mathematics.metrics.model.Metric;
import de.bioforscher.mathematics.metrics.model.VectorMetricProvider;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.stream.DoubleStream;

import static de.bioforscher.mathematics.metrics.model.VectorMetricProvider.EUCLIDEAN_METRIC;


/**
 * The {@code RegularVector} class handles the general properties and operations of vectors. A vector is a composition
 * of elements (double values) that specify the position of a point in a multidimensional topology.
 *
 * @author Christoph Leberecht
 * @version 2.0.1
 */
public class RegularVector implements Vector {

    private final double[] elements;
    private final int dimension;

    /**
     * Creates a new vector with the given elements.
     *
     * @param elements The values in the order they will be in the vector.
     */
    public RegularVector(double... elements) {
        this.elements = elements;
        this.dimension = elements.length;
    }

    public RegularVector(int dimension){
        this.elements =  new double[dimension];
        this.dimension = dimension;
    }

    /**
     * A factory method for the creation of a new specific vector. This method can be used when the dimensionality of
     * the resulting vector is known in advance.
     *
     * @param elements The elements of this vector.
     * @param typeClass The class of the resulting vector.
     * @return A new vector with the specified class and values.
     */
    public static <VectorDimension extends Vector> VectorDimension createNewVector(double[] elements,
                                                                                   Class<VectorDimension>
                                                                                                  typeClass) {
        try {
            return typeClass.getConstructor(double[].class).newInstance(elements);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
            return null;
        }
    }

    public <VectorClass extends Vector> VectorClass as(Class<VectorClass> vectorClass) {
        if (Vector2D.isVector2D(this) && vectorClass.equals(Vector2D.class)) {
            return createNewVector(this.getElements(), vectorClass);
        } else if (Vector3D.isVector3D(this) && vectorClass.equals(Vector3D.class)) {
            return createNewVector(this.getElements(), vectorClass);
        }
        return null;
    }

    @Override
    public double getElement(int index) {
        return this.elements[index];
    }

    @Override
    public double[] getElements() {
        return this.elements;
    }

    @Override
    public int getDimension() {
        return this.dimension;
    }

    /**
     * Returns the string representation of the dimension of this vector.
     *
     * @return The string representation of the dimension of this vector.
     */
    @Override
    public String getDimensionAsString() {
        return String.valueOf(this.dimension);
    }

    /**
     * Determines whether this vector has the same dimension as the given vector.
     *
     * @param vector Another vector.
     * @return {@code true} if, and only if this vector has the same dimension as the given vector.
     */
    @Override
    public boolean hasSameDimensions(Vector vector) {
        return vector.getDimension() == this.dimension;
    }

    /**
     * The addition is an algebraic operation that returns a new vector where each element of this vector is added to
     * the corresponding element in the given vector.
     *
     * @param summand Another vector of the same dimension.
     * @return The addition.
     * @throws IncompatibleDimensionsException if this vector has another dimension than the given vector.
     */
    @Override
    public Vector add(Vector summand) {
        assertThatDimensionsMatch(summand);
        double[] values = new double[this.getDimension()];
        for (int d = 0; d < this.getDimension(); d++) {
            values[d] = this.getElement(d) + summand.getElement(d);
        }
        return new RegularVector(values);
    }

    @Override
    public Vector subtract(Vector subtrahend) {
        assertThatDimensionsMatch(subtrahend);
        double[] values = new double[this.getDimension()];
        for (int d = 0; d < this.getDimension(); d++) {
            values[d] = this.getElement(d) - subtrahend.getElement(d);
        }
        return new RegularVector(values);
    }

    /**
     * Additively inverts (negates) the whole vector.
     *
     * @return A new vector where each element is inverted.
     */
    @Override
    public Vector additivelyInvert() {
        double[] values = new double[this.getDimension()];
        for (int d = 0; d < this.getDimension(); d++) {
            values[d] = -this.getElement(d);
        }
        return new RegularVector(values);
    }

    @Override
    public Vector additiveleyInvertElement(int index) {
        double[] values = new double[this.getDimension()];
        System.arraycopy(this.elements, 0, values, 0, this.getDimension());
        values[index] = -values[index];
        return new RegularVector(values);
    }

    /**
     * The element-wise multiplication is an algebraic operation that returns a new vector where each element of the
     * calling vector is multiplied by the corresponding element of the called vector.
     *
     * @param multiplicand Another vector of the same dimension.
     * @return The element-wise multiplication.
     * @throws IncompatibleDimensionsException if this vector has another dimension than the given vector.
     */
    @Override
    public Vector multiply(Vector multiplicand) {
        assertThatDimensionsMatch(multiplicand);
        double[] values = new double[this.getDimension()];
        for (int dimension = 0; dimension < this.getDimension(); dimension++) {
            values[dimension] = this.getElement(dimension) * multiplicand.getElement(dimension);
        }
        return new RegularVector(values);
    }

    @Override
    public Vector multiply(double scalar) {
        double[] values = new double[this.getDimension()];
        for (int dimension = 0; dimension < this.getDimension(); dimension++) {
            values[dimension] = this.getElement(dimension) * scalar;
        }
        return new RegularVector(values);
    }

    /**
     * The element-wise division is an algebraic operation that returns a new vector where each element of the calling
     * vector is divided by the corresponding element of the called vector.
     *
     * @param divisor Another vector of the same dimension.
     * @return The element-wise division.
     * @throws IncompatibleDimensionsException if this vector has another dimension than the given vector.
     */
    @Override
    public Vector divide(Vector divisor) {
        assertThatDimensionsMatch(divisor);
        double[] values = new double[this.getDimension()];
        for (int dimension = 0; dimension < this.getDimension(); dimension++) {
            values[dimension] = this.getElement(dimension) / divisor.getElement(dimension);
        }
        return new RegularVector(values);
    }

    @Override
    public Vector divide(double scalar) {
        double[] values = new double[this.getDimension()];
        for (int dimension = 0; dimension < this.getDimension(); dimension++) {
            values[dimension] = this.getElement(dimension) / scalar;
        }
        return new RegularVector(values);
    }

    @Override
    public Vector normalize() {
        return this.multiply(1.0 / this.getMagnitude());
    }

    @Override
    public double dotProduct(Vector vector) {
        assertThatDimensionsMatch(vector);
        double product = 0;
        for (int dimension = 0; dimension < this.getDimension(); dimension++) {
            product += this.getElement(dimension) * vector.getElement(dimension);
        }
        return product;
    }

    @Override
    public RegularMatrix dyadicProduct(Vector vector) {
        assertThatDimensionsMatch(vector);
        double[][] values = new double[this.getDimension()][vector.getDimension()];
        for (int thisDimension = 0; thisDimension < this.getDimension(); thisDimension++) {
            for (int otherDimension = 0; otherDimension < vector.getDimension(); otherDimension++) {
                values[thisDimension][otherDimension] = this.getElement(thisDimension) * vector.getElement
                        (otherDimension);
            }
        }
        return new RegularMatrix(values);
    }

    @Override
    public double getMagnitude() {
        double sum = 0.0;
        for (int dimension = 0; dimension < this.getDimension(); dimension++) {
            sum += this.getElement(dimension) * this.getElement(dimension);
        }
        return Math.sqrt(sum);
    }

    /**
     * This method calculates the Eucledian distance between this vector and the given vector.
     * <p>
     * The Euclidean distance is the "ordinary" (i.e. straight-line) distance between two vectors in Euclidean space.
     *
     * @param another Another vector of the same dimension.
     * @return The Euclidean distance.
     * @throws IncompatibleDimensionsException if this vector has another dimension than the given vector.
     * @see MinkowskiMetric
     */
    @Override
    public double distanceTo(Vector another) {
        return EUCLIDEAN_METRIC.calculateDistance(this, another);
    }

    /**
     * This method calculates the distance between this vector and the given vector with the given metric.
     *
     * @param vector Another vector of the same dimension.
     * @param metric The metric to calculate the distance with.
     * @return The distance.
     * @throws IncompatibleDimensionsException if this vector has another dimension than the given vector.
     * @see VectorMetricProvider
     */
    public double distanceTo(Vector vector, Metric<Vector> metric) {
        return metric.calculateDistance(this, vector);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(this.elements);
        return result;
    }

    /**
     * Two vectors are equal if all of their coordinates are equal.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        RegularVector other = (RegularVector) obj;
        return Arrays.equals(this.elements, other.elements);
    }

    /**
     * Returns the string representation of this Vector. The string consists of the dimensionality and the actual
     * coordinates of the vector.
     */
    @Override
    public String toString() {
        return "Vector " + this.getDimension() + "D "
                + Arrays.toString(this.elements).replace("[", "(").replace("]", ")");
    }

}
