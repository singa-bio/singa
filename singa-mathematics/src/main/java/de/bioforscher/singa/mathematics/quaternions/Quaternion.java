package de.bioforscher.singa.mathematics.quaternions;

import de.bioforscher.singa.mathematics.concepts.MultiDimensional;
import de.bioforscher.singa.mathematics.concepts.Ring;
import de.bioforscher.singa.mathematics.matrices.SquareMatrix;

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
