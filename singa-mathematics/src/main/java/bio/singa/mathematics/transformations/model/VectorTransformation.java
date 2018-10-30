package bio.singa.mathematics.transformations.model;

import bio.singa.mathematics.vectors.Vector;

public interface VectorTransformation extends Transformation<Vector> {

    @Override
    Vector applyTo(Vector vector);

}
