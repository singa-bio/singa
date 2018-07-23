package bio.singa.mathematics.vectors;

import bio.singa.mathematics.matrices.FastMatrices;
import bio.singa.mathematics.matrices.SquareMatrix;

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
public class Vector3D implements Vector {

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

    private double x;
    private double y;
    private double z;

    /**
     * Creates a new vector with the given elements.
     *
     * @param elements The values in the order they will be in the vector.
     * @throws IllegalArgumentException if the double array has more than 3 elements.
     */
    public Vector3D(double[] elements) {
        if (elements.length != 3) {
            throw new IllegalArgumentException("The Vector3D class is designed to handle 3 values, "
                    + " but the given array contains " + elements.length + ".");
        }
        x = elements[0];
        y = elements[1];
        z = elements[2];
    }

    /**
     * Creates a new vector with the given elements.
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @param z The z coordinate.
     */
    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
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
     * @param source The vector to copy.
     */
    public Vector3D(Vector3D source) {
        this(source.x, source.y, source.z);
    }

    public static boolean isVector3D(Vector vector) {
        return vector.getDimension() == 3;
    }

    @Override
    public <VectorType extends Vector> VectorType as(Class<VectorType> vectorClass) {
        if (vectorClass.equals(Vector3D.class)) {
            return (VectorType) this;
        }
        if (vectorClass.equals(RegularVector.class)) {
            return (VectorType) new RegularVector(x, y, z);
        }
        throw new IllegalArgumentException("Can not convert Vector3D to " + vectorClass.getSimpleName());
    }

    /**
     * Returns the x coordinate of this vector.
     *
     * @return The x coordinate of this vector.
     */
    public double getX() {
        return x;
    }

    /**
     * Returns the y coordinate of this vector.
     *
     * @return The y coordinate of this vector.
     */
    public double getY() {
        return y;
    }

    /**
     * Returns the z coordinate of this vector.
     *
     * @return The z coordinate of this vector.
     */
    public double getZ() {
        return z;
    }

    @Override
    public double getElement(int index) {
        switch (index) {
            case X_INDEX:
                return x;
            case Y_INDEX:
                return y;
            case Z_INDEX:
                return z;
            default:
                throw new IllegalArgumentException("Can only get values for available indices (0 - 3)");
        }
    }

    @Override
    public double[] getElements() {
        return new double[]{x, y, z};
    }

    @Override
    public int getDimension() {
        return 3;
    }

    @Override
    public boolean hasSameDimensions(Vector element) {
        return element.getDimension() == 3;
    }

    @Override
    public String getDimensionAsString() {
        return "3D";
    }

    @Override
    public Vector3D additivelyInvert() {
        return new Vector3D(-x, -y, -z);
    }

    @Override
    public Vector additiveleyInvertElement(int index) {
        switch (index) {
            case X_INDEX:
                return invertX();
            case Y_INDEX:
                return invertY();
            case Z_INDEX:
                return invertZ();
            default:
                throw new IllegalArgumentException("Can only invert available indices (0 - 3)");
        }
    }

    /**
     * Returns a new vector where the x coordinate is additively inverted
     * (negated).
     *
     * @return A new vector with inverted x coordinate.
     */
    public Vector3D invertX() {
        return new Vector3D(-x, y, z);
    }

    /**
     * Returns a new vector where the y coordinate is additively inverted
     * (negated).
     *
     * @return A new vector with inverted y coordinate.
     */
    public Vector3D invertY() {
        return new Vector3D(x, -y, z);
    }

    /**
     * Returns a new vector where the z coordinate is additively inverted
     * (negated).
     *
     * @return A new vector with inverted z coordinate.
     */
    public Vector3D invertZ() {
        return new Vector3D(x, y, -z);
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
        return new Vector3D(x + vector.x, y + vector.y, z + vector.z);
    }

    @Override
    public Vector3D add(Vector summand) {
        if (summand.getDimension() != 3) {
            throw new IllegalArgumentException("The dimensions have to be equal to perform this operation.");
        }
        return new Vector3D(
                x + summand.getElement(X_INDEX),
                y + summand.getElement(Y_INDEX),
                z + summand.getElement(Z_INDEX));
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
        return new Vector3D(x - vector.x, y - vector.y, z - vector.z);
    }

    @Override
    public Vector3D subtract(Vector subtrahend) {
        if (subtrahend.getDimension() != 3) {
            throw new IllegalArgumentException("The dimensions have to be equal to perform this operation.");
        }
        return new Vector3D(
                x - subtrahend.getElement(X_INDEX),
                y - subtrahend.getElement(Y_INDEX),
                z - subtrahend.getElement(Z_INDEX));
    }

    @Override
    public Vector3D multiply(double scalar) {
        return new Vector3D(x * scalar, y * scalar, z * scalar);
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
        return new Vector3D(x * vector.x, y * vector.y, z * vector.z);
    }

    @Override
    public Vector3D multiply(Vector multiplicand) {
        if (multiplicand.getDimension() != 3) {
            throw new IllegalArgumentException("The dimensions have to be equal to perform this operation.");
        }
        return new Vector3D(
                x * multiplicand.getElement(X_INDEX),
                y * multiplicand.getElement(Y_INDEX),
                z * multiplicand.getElement(Z_INDEX));
    }

    @Override
    public Vector3D divide(double scalar) {
        return new Vector3D(x / scalar, y / scalar, z / scalar);
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
        return new Vector3D(x / vector.x, y / vector.y, z / vector.z);
    }

    @Override
    public Vector3D divide(Vector divisor) {
        if (divisor.getDimension() != 3) {
            throw new IllegalArgumentException("The dimensions have to be equal to perform this operation.");
        }
        return new Vector3D(
                x / divisor.getElement(X_INDEX),
                y / divisor.getElement(Y_INDEX),
                z / divisor.getElement(Z_INDEX));
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
        return x * vector.x + y * vector.y + z * vector.z;
    }


    @Override
    public double dotProduct(Vector vector) {
        return x * vector.getElement(X_INDEX) + y * vector.getElement(Y_INDEX) + z * vector.getElement(Z_INDEX);
    }

    @Override
    public double getMagnitude() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    @Override
    public SquareMatrix dyadicProduct(Vector vector) {
        if (vector.getDimension() != 3) {
            throw new IllegalArgumentException("The dimensions have to be equal to perform this operation.");
        }
        double[][] values =
                {{x * vector.getElement(X_INDEX)}, {x * vector.getElement(Y_INDEX)}, {x * vector.getElement(Z_INDEX)},
                        {y * vector.getElement(X_INDEX)}, {y * vector.getElement(Y_INDEX)}, {y * vector.getElement(Z_INDEX)},
                        {z * vector.getElement(X_INDEX)}, {z * vector.getElement(Y_INDEX)}, {z * vector.getElement(Z_INDEX)}};
        return FastMatrices.createSquareMatrix(values);
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
        return new Vector3D(
                y * vector.z - z * vector.y,
                z * vector.x - x * vector.z,
                x * vector.y - y * vector.x
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vector3D vector3D = (Vector3D) o;

        if (Double.compare(vector3D.x, x) != 0) return false;
        if (Double.compare(vector3D.y, y) != 0) return false;
        return Double.compare(vector3D.z, z) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(z);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ')';
    }
}
