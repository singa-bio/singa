package de.bioforscher.singa.mathematics.quaternions;

import de.bioforscher.singa.mathematics.matrices.SquareMatrix;

/**
 * A regular quaternion that is always normalized.
 *
 * @author fk
 */
public class RegularQuaternion implements Quaternion {

    private final double x;
    private final double y;
    private final double z;
    private final double w;

    public RegularQuaternion(double[] elements) {
        if (elements.length != 4) {
            throw new IllegalArgumentException("The Quaternion class is designed to handle 4 values, "
                    + " but the given array contains " + elements.length + ".");
        }
        double mag = 1.0 / Math.sqrt(elements[0] * elements[0] + elements[1] * elements[1] + elements[2] * elements[2] + elements[3] * elements[3]);
        x = elements[0] * mag;
        y = elements[1] * mag;
        z = elements[2] * mag;
        w = elements[3] * mag;
    }

    public RegularQuaternion(double x, double y, double z, double w) {
        double mag = 1.0 / Math.sqrt(x * x + y * y + z * z + w * w);
        this.x = x * mag;
        this.y = y * mag;
        this.z = z * mag;
        this.w = w * mag;
    }

    @Override
    public Quaternion add(Quaternion summand) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public Quaternion additivelyInvert() {
        return new RegularQuaternion(-x, -y, -z, -w);
    }

    @Override
    public boolean hasSameDimensions(Quaternion element) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public String getDimensionAsString() {
        return "4D";
    }

    @Override
    public Quaternion multiply(Quaternion multiplicand) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public Quaternion subtract(Quaternion subtrahend) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double getZ() {
        return z;
    }

    @Override
    public double getW() {
        return w;
    }

    @Override
    public double[] getElements() {
        return new double[]{x, y, z, w};
    }

    @Override
    public SquareMatrix toMatrixRepresentation() {
        double m00 = (1.0 - 2.0 * y * y - 2.0 * z * z);
        double m10 = (2.0 * (x * y + w * z));
        double m20 = (2.0 * (x * z - w * y));

        double m01 = (2.0 * (x * y - w * z));
        double m11 = (1.0 - 2.0 * x * x - 2.0 * z * z);
        double m21 = (2.0 * (y * z + w * x));

        double m02 = (2.0 * (x * z + w * y));
        double m12 = (2.0 * (y * z - w * x));
        double m22 = (1.0 - 2.0 * x * x - 2.0 * y * y);

        double m03 = 0.0;
        double m13 = 0.0;
        double m23 = 0.0;

        double m30 = 0.0;
        double m31 = 0.0;
        double m32 = 0.0;
        double m33 = 1.0;

        double[][] elements = new double[][]{{m00, m01, m02, m03}, {m10, m11, m12, m13},
                {m20, m21, m22, m23}, {m30, m31, m32, m33}};

        return new SquareMatrix(elements);
    }
}
