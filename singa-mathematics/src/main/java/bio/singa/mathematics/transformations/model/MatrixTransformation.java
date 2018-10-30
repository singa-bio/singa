package bio.singa.mathematics.transformations.model;

import bio.singa.mathematics.matrices.Matrix;

public interface MatrixTransformation extends Transformation<Matrix> {

    @Override
    Matrix applyTo(Matrix matrix);

}
