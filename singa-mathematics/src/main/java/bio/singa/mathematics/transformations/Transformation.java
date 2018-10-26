package bio.singa.mathematics.transformations;

import bio.singa.mathematics.concepts.*;
import bio.singa.mathematics.matrices.Matrix;
import bio.singa.mathematics.vectors.Vector;

public interface Transformation<RingType extends Ring<RingType>> {

    RingType applyTo(RingType concept);
}
