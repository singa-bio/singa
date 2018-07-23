package bio.singa.mathematics.quaternions;

import bio.singa.mathematics.concepts.MultiDimensional;
import bio.singa.mathematics.concepts.Ring;
import bio.singa.mathematics.matrices.SquareMatrix;

/**
 * @author fk
 */
public interface Quaternion extends Ring<Quaternion>, MultiDimensional<Quaternion> {

    double getX();

    double getY();

    double getZ();

    double getW();

    double[] getElements();

    SquareMatrix toMatrixRepresentation();
}
