package bio.singa.mathematics.transformations;

import bio.singa.mathematics.concepts.Ring;
import bio.singa.mathematics.matrices.Matrix;
import bio.singa.mathematics.matrices.RegularMatrix;
import bio.singa.mathematics.vectors.RegularVector;
import bio.singa.mathematics.vectors.Vector;
import bio.singa.mathematics.vectors.Vectors;

/**
 * A transformation according to the standard deviation of elements.
 *
 * <pre>
 *     x' = (x - mean(X)) / std(X)
 * </pre>
 * @author fk
 */
public class ZScoreTransformation implements MatrixTransformation, VectorTransformation {

    // FIXME: Chris please help, I can't figure our how to use the generics here :<
    @Override
    public Ring applyTo(Ring concept) {
        throw new UnsupportedOperationException("Issues with generics");
    }

    @Override
    public RegularMatrix applyTo(Matrix matrix) {
        Vector elementsVector = new RegularVector(matrix.streamElements().toArray());
        double variance = Vectors.getVariance(elementsVector);
        double expectedValue = Vectors.getAverage(elementsVector);
        double[][] transformedValues = new double[matrix.getRowDimension()][matrix.getColumnDimension()];
        for (int i = 0; i < matrix.getRowDimension(); i++) {
            for (int j = 0; j < matrix.getColumnDimension(); j++) {
                transformedValues[i][j] = (matrix.getElement(i, j) - expectedValue) / Math.sqrt(variance);
            }
        }
        return new RegularMatrix(transformedValues);
    }

    @Override
    public RegularVector applyTo(Vector vector) {
        double variance = Vectors.getVariance(vector);
        double expectedValue = Vectors.getAverage(vector);
        double[] transformedValues = new double[vector.getDimension()];
        for (int i = 0; i < vector.getDimension(); i++) {
            transformedValues[i] = (vector.getElement(i) - expectedValue) / Math.sqrt(variance);
        }
        return new RegularVector(transformedValues);
    }
}
