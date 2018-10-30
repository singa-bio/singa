package bio.singa.mathematics.transformations.implementations;

import bio.singa.mathematics.matrices.RegularMatrix;
import bio.singa.mathematics.transformations.model.MatrixTransformation;
import bio.singa.mathematics.vectors.RegularVector;
import bio.singa.mathematics.vectors.Vector;
import bio.singa.mathematics.vectors.Vectors;

/**
 * @author cl
 */
public class MatrixTransformations {

    private MatrixTransformations() {
    }

    public static final MatrixTransformation MIN_MAX = matrix -> {
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
    };

    public static final MatrixTransformation Z_SCORE = matrix -> {
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
    };


}
