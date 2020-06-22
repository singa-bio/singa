package bio.singa.mathematics.vectors;

import bio.singa.mathematics.matrices.Matrices;
import bio.singa.mathematics.matrices.Matrix;
import bio.singa.mathematics.matrices.RegularMatrix;

import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author cl
 */
public class Vectors3D {

    /**
     * Calculate the reflection of a vector given a mirror vector.
     *
     * @param vector The vector to reflect.
     * @param mirror The mirror.
     * @return The mirrored vector.
     */
    public static Vector3D calculateReflection(Vector3D vector, Vector3D mirror) {
        return vector.subtract(mirror.multiply(2 * vector.dotProduct(mirror) / mirror.dotProduct(mirror)));
    }

    /**
     * Calculates the rotation necessary to rotate the first vector onto the second, no scaling is done.
     *
     * @param first The vector to be rotated.
     * @param second The target of the rotation.
     * @return The rotation matrix able to rotate the first onto the second vector.
     * @see <a href="https://math.stackexchange.com/a/476311">Stackexchange</a>
     */
    public static Matrix calculateRotation(Vector3D first, Vector3D second) {
        Vector3D a = first.normalize();
        Vector3D b = second.normalize();
        Vector3D v = a.crossProduct(b);
        double s = v.getMagnitude();
        double c = a.dotProduct(b);
        double[][] values = {
                {0, -v.getZ(), v.getY()},
                {v.getZ(), 0, -v.getX()},
                {-v.getY(), v.getX(), 0}
        };
        Matrix m = new RegularMatrix(values);
        return Matrices.generateIdentityMatrix(3).add(m).add(m.multiply(m).multiply((1.0 - c) / (s * s)));
    }

    public static Vector3D generateRandomVector3D() {
        double x = ThreadLocalRandom.current().nextDouble();
        double y = ThreadLocalRandom.current().nextDouble();
        double z = ThreadLocalRandom.current().nextDouble();
        return new Vector3D(x, y, z);
    }

    /**
     * Computes the centroid of all vectors in the collection by summing them and dividing by the number of vectors in
     * the collection. This is faster than using the general implementation from the {@link Vectors} class.
     *
     * @param vectors The vectors to calculate the centroid from.
     * @return The centroid.
     */
    public static Vector3D get3DCentroid(Collection<Vector3D> vectors) {
        int vectorCount = vectors.size();
        double[] sum = new double[3];
        for (Vector3D vector : vectors) {
            sum[0] += vector.getX();
            sum[1] += vector.getY();
            sum[2] += vector.getZ();
        }
        return new Vector3D(sum[0] / vectorCount, sum[1] / vectorCount, sum[2] / vectorCount);
    }

    /**
     * Calculates the dihedral angle between the two planes defined by (a,b,c) and (b,c,d)
     *
     * @param a Point of plane 1.
     * @param b Point of plane 1 and 2.
     * @param c Point of plane 1 and 2.
     * @param d Point of plane 2.
     * @return The dihedral angle in degrees.
     */
    public static double dihedralAngle(Vector3D a, Vector3D b, Vector3D c, Vector3D d) {

        Vector3D ab = a.subtract(b);
        Vector3D cb = c.subtract(b);
        Vector3D bc = b.subtract(c);
        Vector3D dc = d.subtract(c);

        Vector3D abc = ab.crossProduct(cb);
        Vector3D bcd = bc.crossProduct(dc);

        double angle = abc.angleToInDegrees(bcd);

        Vector vector = abc.crossProduct(bcd);
        double v = cb.dotProduct(vector);
        if (v < 0.0) {
            return -angle;
        } else {
            return angle;
        }
    }
}
