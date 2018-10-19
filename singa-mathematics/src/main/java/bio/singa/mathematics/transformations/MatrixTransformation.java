package bio.singa.mathematics.transformations;

import bio.singa.mathematics.matrices.Matrix;

public interface MatrixTransformation<M extends Matrix> extends Transformation<Matrix> {
    @Override
    M applyTo(Matrix matrix);
}
