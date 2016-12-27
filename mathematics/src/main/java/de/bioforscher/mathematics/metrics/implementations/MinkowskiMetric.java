package de.bioforscher.mathematics.metrics.implementations;

import de.bioforscher.mathematics.metrics.model.Metric;
import de.bioforscher.mathematics.metrics.model.Metrizable;
import de.bioforscher.mathematics.vectors.Vector;

/**
 * Calculates the distance between two {@link Vector}s of order "p". For p >= 1,
 * the Minkowski distance is a metric as a result of the Minkowski inequality.
 * For p = 1 the Minkowski metric is the Manhattan or Taxicab metric, and for p
 * = 2 it is the Euclidean metric.
 * <p>
 * Watch out!
 * <p>
 * If p < 1, this is not a proper distance metric, since it does not satisfy the
 * triangle inequality.
 *
 * @param <VectorType> The type of vector that the distance is applied to.
 * @author Christoph Leberecht
 * @version 1.0.0
 * @see <a href="https://en.wikipedia.org/wiki/Minkowski_distance">Wikipedia: Minkowski distance</a>
 * @see <a href="https://en.wikipedia.org/wiki/Euclidean_distance">Wikipedia: Euclidean distance</a>
 * @see <a href="https://en.wikipedia.org/wiki/Taxicab_geometry">Wikipedia: Taxicab geometry</a>
 */
public class MinkowskiMetric<VectorType extends Vector> implements Metric<VectorType> {

    private final double p;

    public MinkowskiMetric(double p) {
        this.p = p;
    }

    @Override
    public double calculateDistance(VectorType first, VectorType second) {
        if (this.p == Double.POSITIVE_INFINITY) {
            return getMaximalDifference(first, second);
        }
        return getRegularMinkowskiDifference(first, second);

    }

    private double getRegularMinkowskiDifference(VectorType first, VectorType second) {
        double sum = 0;
        for (int i = 0; i < first.getDimension(); i++) {
            sum += Math.pow(Math.abs(first.getElement(i) - second.getElement(i)), this.p);
        }
        return Math.pow(sum, 1.0 / this.p);
    }

    private double getMaximalDifference(VectorType first, VectorType second) {
        double maximalDistance = 0.0;
        for (int i = 0; i < first.getDimension(); i++) {
            double currentDistance = Math.abs(first.getElement(i) - second.getElement(i));
            if (currentDistance > maximalDistance) {
                maximalDistance = currentDistance;
            }
        }
        return maximalDistance;
    }

}