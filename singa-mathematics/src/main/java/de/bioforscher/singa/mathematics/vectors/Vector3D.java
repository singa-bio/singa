package de.bioforscher.singa.mathematics.vectors;

/**
 * The {@code Vector3D} class handles the general properties and operations of
 * three dimensional vectors. Basically this is an regular vector with the
 * constraint, that it can only contain three values. This facade can be used
 * to handle three dimensional vectors without dimensional violations.
 * Additionally it is able to perform operations that are solely defined for 3D
 * vectors.
 *
 * @author cl
 */
public class Vector3D extends RegularVector {

    /**
     * The index of the x (first) element or coordinate.
     */
    public static final int X_INDEX = 0;

    /**
     * The index of the y (second) element or coordinate.
     */
    public static final int Y_INDEX = 1;

    /**
     * The index of the z (third) element or coordinate.
     */
    public static final int Z_INDEX = 2;

    /**
     * Creates a new vector with the given elements.
     *
     * @param elements The values in the order they will be in the vector.
     * @throws IllegalArgumentException if the double array has more than 3 elements.
     */
    public Vector3D(double[] elements) {
        super(elements);
        if (elements.length != 3) {
            throw new IllegalArgumentException("The Vector3D class is designed to handle 3 values, "
                    + " but the given array contains " + elements.length + ".");
        }
    }

    /**
     * Creates a new vector with the given elements.
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @param z The z coordinate.
     */
    public Vector3D(double x, double y, double z) {
        this(new double[]{x, y, z});
    }

    /**
     * Creates a new vector with all coordinates set to {@code 0.0}.
     */
    public Vector3D() {
        this(0.0, 0.0, 0.0);
    }

    /**
     * This is a copy constructor. Creates a new Vector3D by copying the given vector.
     *
     * @param vector3D The vector to copy.
     */
    public Vector3D(Vector3D vector3D) {
        this(vector3D.getX(), vector3D.getY(), vector3D.getZ());
    }

    public static boolean isVector3D(Vector vector) {
        return vector.getDimension() == 3;
    }

    /**
     * Returns the x coordinate of this vector.
     *
     * @return The x coordinate of this vector.
     */
    public double getX() {
        return getElement(X_INDEX);
    }

    /**
     * Returns the y coordinate of this vector.
     *
     * @return The y coordinate of this vector.
     */
    public double getY() {
        return getElement(Y_INDEX);
    }

    /**
     * Returns the z coordinate of this vector.
     *
     * @return The z coordinate of this vector.
     */
    public double getZ() {
        return getElement(Z_INDEX);
    }

    /**
     * Additively inverts (negates) the whole vector.
     *
     * @return A new vector where each element is inverted.
     */
    @Override
    public Vector3D additivelyInvert() {
        return new Vector3D(-getX(), -getY(), -getZ());
    }

    /**
     * Returns a new vector where the x coordinate is additively inverted
     * (negated).
     *
     * @return A new vector with inverted x coordinate.
     */
    public Vector3D invertX() {
        return new Vector3D(-getX(), getY(), getZ());
    }

    /**
     * Returns a new vector where the y coordinate is additively inverted
     * (negated).
     *
     * @return A new vector with inverted y coordinate.
     */
    public Vector3D invertY() {
        return new Vector3D(getX(), -getY(), getZ());
    }

    /**
     * Returns a new vector where the z coordinate is additively inverted
     * (negated).
     *
     * @return A new vector with inverted z coordinate.
     */
    public Vector3D invertZ() {
        return new Vector3D(getX(), getY(), -getZ());
    }

    /**
     * The addition is an algebraic operation that returns a new vector where
     * each element of this vector is added to the corresponding element in the
     * given vector.
     *
     * @param vector Another 3D vector.
     * @return The addition.
     */
    public Vector3D add(Vector3D vector) {
        return new Vector3D(getX() + vector.getX(), getY() + vector.getY(), getZ() + vector.getZ());
    }

    /**
     * The subtraction is an algebraic operation that returns a new vector where
     * each element in the given vector is subtracted from the corresponding
     * element in this vector.
     *
     * @param vector Another 3D vector.
     * @return The subtraction.
     */
    public Vector3D subtract(Vector3D vector) {
        return new Vector3D(getX() - vector.getX(), getY() - vector.getY(), getZ() - vector.getZ());
    }

    @Override
    public Vector3D multiply(double scalar) {
        return new Vector3D(getX() * scalar, getY() * scalar, getZ() * scalar);
    }

    /**
     * The element-wise multiplication is an algebraic operation that returns a
     * new vector where each element of the calling vector is multiplied by the
     * corresponding element of the called vector.
     *
     * @param vector Another 3D vector.
     * @return The element-wise multiplication.
     */
    public Vector3D multiply(Vector3D vector) {
        return new Vector3D(getX() * vector.getX(), getY() * vector.getY(), getZ() * vector.getZ());
    }

    @Override
    public Vector3D divide(double scalar) {
        return new Vector3D(getX() / scalar, getY() / scalar, getZ() / scalar);
    }

    /**
     * The element-wise division is an algebraic operation that returns a new
     * vector where each element of the calling vector is divided by the
     * corresponding element of the called vector.
     *
     * @param vector Another 3D vector.
     * @return The element-wise division.
     */
    public Vector3D divide(Vector3D vector) {
        return new Vector3D(getX() / vector.getX(), getY() / vector.getY(), getZ() / vector.getZ());
    }

    @Override
    public Vector3D normalize() {
        return divide(getMagnitude());
    }

    /**
     * The dot product or scalar product is an algebraic operation that returns
     * the sum of the products of the corresponding elements of the two vectors.
     *
     * @param vector Another 3D vector.
     * @return The dot product.
     */
    public double dotProduct(Vector3D vector) {
        return getX() * vector.getX() + getY() * vector.getY() + getZ() * vector.getZ();
    }

    /**
     * Given two linearly independent vectors, the cross product, is a vector
     * that is perpendicular to both and therefore normal to the plane
     * containing them
     *
     * @param vector Another 3D vector.
     * @return The cross product.
     */
    public Vector3D crossProduct(Vector3D vector) {
        return new Vector3D(new double[]{
                getY() * vector.getZ() - getZ() * vector.getY(),
                getZ() * vector.getX() - getX() * vector.getZ(),
                getX() * vector.getY() - getY() * vector.getX()
        });
    }



}
