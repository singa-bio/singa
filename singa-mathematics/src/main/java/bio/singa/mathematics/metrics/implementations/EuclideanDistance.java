package bio.singa.mathematics.metrics.implementations;

import bio.singa.mathematics.metrics.model.Metric;
import bio.singa.mathematics.vectors.Vector;

/**
 * @author cl
 */
public class EuclideanDistance<VectorType extends Vector> implements Metric<VectorType> {

    @Override
    public double calculateDistance(VectorType first, VectorType second) {
        double sum = 0;
        for (int i = 0; i < first.getDimension(); i++) {
            sum += Math.pow(first.getElement(i) - second.getElement(i), 2);
        }
        return Math.sqrt(sum);
    }

}
