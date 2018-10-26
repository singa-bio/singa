package bio.singa.mathematics.transformations.implementations;

import bio.singa.mathematics.transformations.model.VectorTransformation;
import bio.singa.mathematics.vectors.RegularVector;
import bio.singa.mathematics.vectors.Vectors;

/**
 * @author cl
 */
public class VectorTransformations {

    private VectorTransformations() {
    }

    public static final VectorTransformation MIN_MAX = vector -> {
        double min = vector.getElement(Vectors.getIndexWithMinimalElement(vector));
        double max = vector.getElement(Vectors.getIndexWithMaximalElement(vector));
        double[] normalizedElements = new double[vector.getDimension()];
        double[] elements = vector.getElements();
        for (int i = 0; i < elements.length; i++) {
            double element = vector.getElement(i);
            normalizedElements[i] = (element - min) / (max - min);
        }
        return new RegularVector(normalizedElements);
    };

    public static final VectorTransformation Z_SCORE = vector -> {
        double variance = Vectors.getVariance(vector);
        double expectedValue = Vectors.getAverage(vector);
        double[] transformedValues = new double[vector.getDimension()];
        for (int i = 0; i < vector.getDimension(); i++) {
            transformedValues[i] = (vector.getElement(i) - expectedValue) / Math.sqrt(variance);
        }
        return new RegularVector(transformedValues);
    };

}
