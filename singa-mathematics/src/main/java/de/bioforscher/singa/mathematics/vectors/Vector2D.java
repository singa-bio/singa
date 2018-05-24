package de.bioforscher.singa.mathematics.vectors;

import de.bioforscher.singa.mathematics.geometry.edges.Line;
import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;

/**
 * The {@code Vector2D} class handles the general properties and operations of
 * two dimensional vectors. Basically this is an regular vector with the
 * constraint, that it can only contain two values. This fascade can be used to
 * handle two dimensional vectors without dimensional violations. Additionally
 * it is able to perform operations that are solely defined for 2D vectors.
 *
 * @author cl
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
     * Additively inverts (negates) the whole vector.
     *
     * @return A new vector where each element is inverted.
     */
    @Override
    public Vector2D additivelyInvert() {
        return new Vector2D(-getX(), -getY());
    }

    /**
     * Returns a new vector where the x coordinate is additively inverted
     * (negated).
     *
     * @return A new vector with inverted x coordinate.
     */
    public Vector2D invertX() {
        return new Vector2D(-getX(), getY());
    }

    /**
     * Returns a new vector where the y coordinate is additively inverted
     * (negated).
     *
     * @return A new vector with inverted y coordinate.
     */
    public Vector2D invertY() {
        return new Vector2D(getX(), -getY());
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
        return new Vector2D(getX() + vector.getX(), getY() + vector.getY());
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
        return new Vector2D(getX() - vector.getX(), getY() - vector.getY());
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
        return new Vector2D(getX() * vector.getX(), getY() * vector.getY());
    }

    @Override
    public Vector2D multiply(double scalar) {
        return new Vector2D(getX() * scalar, getY() * scalar);
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
        return new Vector2D(getX() / vector.getX(), getY() / vector.getY());
    }

    @Override
    public Vector2D divide(double scalar) {
        return new Vector2D(getX() / scalar, getY() / scalar);
    }

    @Override
    public Vector2D normalize() {
        return divide(getMagnitude());
    }

    /**
     * The dot product or scalar product is an algebraic operation that returns
     * the sum of the products of the corresponding elements of the two vectors.
     *
     * @param vector Another 2D vector.
     * @return The dot product.
     */
    public double dotProduct(Vector2D vector) {
        return getX() * vector.getX() + getY() * vector.getY();
    }

    /**
     * Returns the angle between this vector and the given vector, in relation to the origin (0,0) in radians.
     *
     * @param vector Another 2D vector.
     * @return The angle in radians.
     */
    public double angleTo(Vector2D vector) {
        return Math.acos(dotProduct(vector) / (getMagnitude() * vector.getMagnitude()));
    }

    /**
     * Returns the angle between this vector and the given target vector, in relation to the given origin in radians.
     *
     * @param origin The origin.
     * @param target The target vector.
     * @return The angle in radians.
     */
    public double angleTo(Vector2D origin, Vector2D target) {
        return Math.atan2(target.getY() - origin.getY(), target.getX() - origin.getX()) - Math.atan2(getY() - origin.getY(), getX() - origin.getY());
    }

    /**
     * Returns the Midpoint between this vector and the given vector.
     *
     * @param vector Another vector.
     * @return The Midpoint
     */
    public Vector2D getMidpointTo(Vector2D vector) {
        return new Vector2D((getX() + vector.getX()) / 2, (getY() + vector.getY()) / 2);
    }

    /**
     * Returns the distance between this vector and a {@link Line}.
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
        double dx = Math.abs(vector.getX() - getX() - radius);
        double dy = Math.abs(vector.getY() - getY() - radius);
        return dx * dx + dy * dy <= radius * radius;
    }

    /**
     * Validates, if this vector can be placed in the given {@link Rectangle}.
     *
     * @param rectangle The rectangle.
     * @return {@code true} if vector can be placed in the rectangle.
     */
    public boolean canBePlacedIn(Rectangle rectangle) {
        return valueOfXIsBetween(rectangle.getLeftMostXPosition(), rectangle.getRightMostXPosition())
                && valueOfYIsBetween(rectangle.getBottomMostYPosition(), rectangle.getTopMostYPosition());
    }

    public boolean isAbove(Vector2D vector) {
        return getY() < vector.getY();
    }

    public boolean isBelow(Vector2D vector) {
        return getY() > vector.getY();
    }

    public boolean isLeftOf(Vector2D vector2D) {
        return getX() < vector2D.getX();
    }

    public boolean isRightOf(Vector2D vector2D) {
        return getX() > vector2D.getX();
    }

    public boolean valueOfXIsBetween(double origin, double boundary) {
        return getX() < boundary && getX() > origin;
    }

    private boolean valueOfYIsBetween(double origin, double boundary) {
        return getY() < boundary && getY() > origin;
    }

}
