package de.bioforscher.mathematics.vectors;

import de.bioforscher.mathematics.geometry.edges.Line;
import de.bioforscher.mathematics.geometry.faces.Rectangle;

/**
 * The {@code Vector2D} class handles the general properties and operations of
 * two dimensional vectors. Basically this is an regular vector with the
 * constraint, that it can only contain two values. This fascade can be used to
 * handle two dimensional vectors without dimensional violations. Additionally
 * it is able to perform operations that are solely defined for 2D vectors.
 *
 * @author Christoph Leberecht
 */
public class Vector2D extends RegularVector {

    /**
     * The index of the x (first) element or coordinate.
     */
    public static final int X_INDEX = 0;

    /**
     * The index of the y (first2DVector) element or coordinate.
     */
    public static final int Y_INDEX = 1;

    /**
     * Creates a new vector with the given elements.
     *
     * @param elements The values in the order they will be in the vector.
     * @throws IllegalArgumentException if the double array has more than 2 elements.
     */
    public Vector2D(double[] elements) {
        super(elements);
        if (elements.length != 2) {
            throw new IllegalArgumentException("The Vector2D class is designed to handle 2 values, "
                    + " but the given array contains " + elements.length + ".");
        }
    }

    /**
     * Creates a new vector with the given elements.
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     */
    public Vector2D(double x, double y) {
        this(new double[]{x, y});
    }

    /**
     * Creates a new vector with both coordinates set to {@code 0.0}.
     */
    public Vector2D() {
        this(0.0, 0.0);
    }

    public static boolean isVector2D(Vector vector) {
        return vector.getDimension() == 2;
    }

    /**
     * Returns the x coordinate of this vector.
     *
     * @return The x coordinate of this vector.
     */
    public double getX() {
        return this.getElement(X_INDEX);
    }

    /**
     * Returns the y coordinate of this vector.
     *
     * @return The y coordinate of this vector.
     */
    public double getY() {
        return this.getElement(Y_INDEX);
    }

    /**
     * Additively inverts (negates) the whole vector.
     *
     * @return A new vector where each element is inverted.
     */
    @Override
    public Vector2D additivelyInvert() {
        return createNewVector(super.additivelyInvert().getElements(), this.getClass());
    }

    /**
     * Returns a new vector where the x coordinate is additively inverted
     * (negated).
     *
     * @return A new vector with inverted x coordinate.
     */
    public Vector2D invertX() {
        return createNewVector(super.additiveleyInvertElement(X_INDEX).getElements(), this.getClass());
    }

    /**
     * Returns a new vector where the y coordinate is additively inverted
     * (negated).
     *
     * @return A new vector with inverted y coordinate.
     */
    public Vector2D invertY() {
        return createNewVector(super.additiveleyInvertElement(Y_INDEX).getElements(), this.getClass());
    }

    /**
     * The addition is an algebraic operation that returns a new vector where
     * each element of this vector is added to the corresponding element in the
     * given vector.
     *
     * @param vector Another 2D vector.
     * @return The addition.
     */
    public Vector2D add(Vector2D vector) {
        return createNewVector(super.add(vector).getElements(), this.getClass());
    }

    /**
     * The subtraction is an algebraic operation that returns a new vector where
     * each element in the given vector is subtracted from the corresponding
     * element in this vector.
     *
     * @param vector Another 2D vector.
     * @return The subtraction.
     */
    public Vector2D subtract(Vector2D vector) {
        return createNewVector(super.subtract(vector).getElements(), this.getClass());
    }

    /**
     * The element-wise multiplication is an algebraic operation that returns a
     * new vector where each element of the calling vector is multiplied by the
     * corresponding element of the called vector.
     *
     * @param vector Another 2D vector.
     * @return The element-wise multiplication.
     */
    public Vector2D multiply(Vector2D vector) {
        return createNewVector(super.multiply(vector).getElements(), this.getClass());
    }

    @Override
    public Vector2D multiply(double scalar) {
        return createNewVector(super.multiply(scalar).getElements(), this.getClass());
    }

    /**
     * The element-wise division is an algebraic operation that returns a new
     * vector where each element of the calling vector is divided by the
     * corresponding element of the called vector.
     *
     * @param vector Another 2D vector.
     * @return The element-wise division.
     */
    public Vector2D divide(Vector2D vector) {
        return createNewVector(super.divide(vector).getElements(), this.getClass());
    }

    @Override
    public Vector2D divide(double scalar) {
        return createNewVector(super.divide(scalar).getElements(), this.getClass());
    }

    @Override
    public Vector2D normalize() {
        return createNewVector(super.normalize().getElements(), this.getClass());
    }

    /**
     * The dot product or scalar product is an algebraic operation that returns
     * the sum of the products of the corresponding elements of the two vectors.
     *
     * @param vector Another 2D vector.
     * @return The dot product.
     */
    public double dotProduct(Vector2D vector) {
        return super.dotProduct(vector);
    }

    /**
     * Returns the angle between this vector and the given vector in radians.
     *
     * @param vector Another 2D vector.
     * @return The angle in radians.
     */
    public double angleBetween(Vector2D vector) {
        return Math.acos(dotProduct(vector) / (this.getMagnitude() * vector.getMagnitude()));
    }

    /**
     * Returns the Midpoint between this vector and the given vector.
     *
     * @param vector Another vector.
     * @return The Midpoint
     */
    public Vector2D getMidpointTo(Vector2D vector) {
        return new Vector2D((this.getX() + vector.getX()) / 2, (this.getY() + vector.getY()) / 2);
    }

    /**
     * Returns the distance between this vector and a {@link HorizontalLine}.
     *
     * @param line A HorizontalLine.
     * @return The distance
     */
    public double distanceTo(Line line) {
        // TODO implement me
        return 0.0;
    }

    /**
     * Checks, if the given point is contained in a circle with the given radius
     * around the position of this vector.
     *
     * @param vector The vector to check.
     * @param radius The radius of the circle.
     * @return {@code true} only if the given point is within the given radius
     * of this vector.
     */
    public boolean isNearVector(Vector2D vector, double radius) {
        double dx = Math.abs(vector.getX() - this.getX() - radius);
        double dy = Math.abs(vector.getY() - this.getY() - radius);
        return dx * dx + dy * dy <= radius * radius;
    }

    /**
     * Validates, if this vector can be placed in the given {@link Rectangle}.
     *
     * @param rectangle The rectangle.
     * @return {@code true} if the rectangle can be placed in the vector.
     */
    public boolean canBePlacedIn(Rectangle rectangle) {
        return valueOfXIsBetween(rectangle.getLeftMostXPosition(), rectangle.getRightMostXPosition())
                && valueOfYIsBetween(rectangle.getBottomMostYPosition(), rectangle.getTopMostYPosition());
    }

    private boolean valueOfXIsBetween(double origin, double boundary) {
        return this.getX() < boundary && this.getX() > origin;
    }

    private boolean valueOfYIsBetween(double origin, double boundary) {
        return this.getY() < boundary && this.getY() > origin;
    }

}
