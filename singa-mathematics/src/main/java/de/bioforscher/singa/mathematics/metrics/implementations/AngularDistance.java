package de.bioforscher.singa.mathematics.metrics.implementations;

import de.bioforscher.singa.mathematics.metrics.model.Metric;
import de.bioforscher.singa.mathematics.vectors.Vector;

/**
 * Using the same calculation of similarity as the {@link CosineSimilarity}, the normalised angle between the vectors
 * can be used as a bounded similarity function within [0,1].
 *
 * @param <VectorDimension> The type of vector that the distance is applied to.
 * @author cl
 * @see <a href="https://en.wikipedia.org/wiki/Cosine_similarity">Wikipedia: Cosine similarity</a>
 */
public class AngularDistance<VectorDimension extends Vector> implements Metric<VectorDimension> {

    @Override
    public double calculateDistance(VectorDimension first, VectorDimension second) {
        final double similarity = first.dotProduct(second) / (first.getMagnitude() * second.getMagnitude());
        return 1.0 - (Math.acos(similarity) / Math.PI);
    }

}
