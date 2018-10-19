package bio.singa.mathematics.transformations;

import bio.singa.mathematics.concepts.Ring;
import bio.singa.mathematics.vectors.Vector;

public interface VectorTransformation<V extends Vector> extends Transformation<Vector> {

    @Override
    V applyTo(Vector v);
}
