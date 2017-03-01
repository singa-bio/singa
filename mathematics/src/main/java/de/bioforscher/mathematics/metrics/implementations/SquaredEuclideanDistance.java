package de.bioforscher.mathematics.metrics.implementations;

import de.bioforscher.mathematics.metrics.model.Metric;
import de.bioforscher.mathematics.vectors.Vector;

/**
 * Calculates the squared euclidean distance between two {@link Vector}s. This implementation should be used if the
 * distance calculation is time critical and only the oder of distance is relevant.
 *
 * @param <VectorType> The type of vector that the distance is applied to.
 * @author cl
 * @see <a href="https://en.wikipedia.org/wiki/Euclidean_distance">Wikipedia: Euclidean distance</a>
 */
public class SquaredEuclideanDistance<VectorType extends Vector> implements Metric<VectorType> {

    @Override
    public double calculateDistance(VectorType first, VectorType second) {
        double sum = 0;
        for (int i = 0; i < first.getDimension(); i++) {
            sum += (first.getElement(i) - second.getElement(i)) * (first.getElement(i) - second.getElement(i));
        }
        return sum;
    }

}
