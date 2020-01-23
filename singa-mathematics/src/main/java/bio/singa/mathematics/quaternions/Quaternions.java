package bio.singa.mathematics.quaternions;

import bio.singa.mathematics.algorithms.matrix.EigenvalueDecomposition;
import bio.singa.mathematics.matrices.Matrix;
import bio.singa.mathematics.matrices.SquareMatrix;
import bio.singa.mathematics.vectors.Vector3D;

import java.util.List;

/**
 * @author fk
 */
public class Quaternions {

    public static Quaternion normalize(Quaternion quaternion) {
        double norm = (quaternion.getX() * quaternion.getX() + quaternion.getY() * quaternion.getY() +
                quaternion.getZ() * quaternion.getZ() + quaternion.getW() * quaternion.getW());
        double x = quaternion.getX();
        double y = quaternion.getY();
        double z = quaternion.getZ();
        double w = quaternion.getW();
        if (norm > 0.0) {
            norm = 1.0 / Math.sqrt(norm);
            x *= norm;
            y *= norm;
            z *= norm;
            w *= norm;
        } else {
            x = 0.0;
            y = 0.0;
            z = 0.0;
            w = 0.0;
        }
        return new RegularQuaternion(x, y, z, w);
    }

    /**
     * Calculates the relative orientation between two point clouds: a candidate (moved) to a reference (fixed) represented by a {@link Quaternion}.
     * <b>WARNING:</b> The point clouds should be centered at the origin.
     *
     * @param reference The reference point cloud.
     * @param candidate The candidate point cloud.
     * @return A {@link Quaternion} representing the orientations between the point clouds.
     */
    public static Quaternion relativeOrientation(List<Vector3D> reference, List<Vector3D> candidate) {

        double xx = 0.0, xy = 0.0, xz = 0.0;
        double yx = 0.0, yy = 0.0, yz = 0.0;
        double zx = 0.0, zy = 0.0, zz = 0.0;

        for (int i = 0; i < reference.size(); i++) {
            xx += reference.get(i).getX() * candidate.get(i).getX();
            xy += reference.get(i).getX() * candidate.get(i).getY();
            xz += reference.get(i).getX() * candidate.get(i).getZ();
            yx += reference.get(i).getY() * candidate.get(i).getX();
            yy += reference.get(i).getY() * candidate.get(i).getY();
            yz += reference.get(i).getY() * candidate.get(i).getZ();
            zx += reference.get(i).getZ() * candidate.get(i).getX();
            zy += reference.get(i).getZ() * candidate.get(i).getY();
            zz += reference.get(i).getZ() * candidate.get(i).getZ();
        }

        double[][] f = new double[4][4];
        f[0][0] = xx + yy + zz;
        f[0][1] = zy - yz;
        f[1][0] = f[0][1];
        f[1][1] = xx - yy - zz;
        f[0][2] = xz - zx;
        f[2][0] = f[0][2];
        f[1][2] = xy + yx;
        f[2][1] = f[1][2];
        f[2][2] = yy - zz - xx;
        f[0][3] = yx - xy;
        f[3][0] = f[0][3];
        f[1][3] = zx + xz;
        f[3][1] = f[1][3];
        f[2][3] = yz + zy;
        f[3][2] = f[2][3];
        f[3][3] = zz - xx - yy;

        Matrix matrix = new SquareMatrix(f);
        EigenvalueDecomposition eig = new EigenvalueDecomposition(matrix);
        double[][] v = eig.getV().getElements();
        Quaternion q = new RegularQuaternion(v[1][3], v[2][3], v[3][3], v[0][3]);
        return q.additivelyInvert();
    }
}
