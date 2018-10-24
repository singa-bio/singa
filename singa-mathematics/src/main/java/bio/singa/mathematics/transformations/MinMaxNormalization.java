package bio.singa.mathematics.transformations;

import bio.singa.mathematics.concepts.Ring;
import bio.singa.mathematics.matrices.Matrix;
import bio.singa.mathematics.matrices.RegularMatrix;
import bio.singa.mathematics.vectors.RegularVector;
import bio.singa.mathematics.vectors.Vector;
import bio.singa.mathematics.vectors.Vectors;

/**
 * A min-max normalization for {@link Matrix} according to the standard deviation of elements.
 * <pre>
 *     x' = (x - min(X)) / (max(X)-min(X))
 * </pre>
 *
 * @author fk
 */
public class MinMaxNormalization implements MatrixTransformation, VectorTransformation {
    @Override
    public Matrix applyTo(Matrix matrix) {
        Vector elementsVector = new RegularVector(matrix.streamElements().toArray());
        double min = elementsVector.getElement(Vectors.getIndexWithMinimalElement(elementsVector));
        double max = elementsVector.getElement(Vectors.getIndexWithMaximalElement(elementsVector));
        double[][] normalizedElements = new double[matrix.getRowDimension()][matrix.getColumnDimension()];
        for (int i = 0; i < matrix.getRowDimension(); i++) {
            for (int j = 0; j < matrix.getColumnDimension(); j++) {
                normalizedElements[i][j] = (matrix.getElement(i, j) - min) / (max - min);
            }
        }
        return new RegularMatrix(normalizedElements);
    }

    @Override
    public Vector applyTo(Vector vector) {
        double min = vector.getElement(Vectors.getIndexWithMinimalElement(vector));
        double max = vector.getElement(Vectors.getIndexWithMaximalElement(vector));
        double[] normalizedElements = new double[vector.getDimension()];
        double[] elements = vector.getElements();
        for (int i = 0; i < elements.length; i++) {
            double element = vector.getElement(i);
            normalizedElements[i] = (element - min) / (max - min);
        }
        return new RegularVector(normalizedElements);
    }

    @Override
    public Ring applyTo(Ring concept) {
        throw new UnsupportedOperationException("Issues with generics");
    }
}
