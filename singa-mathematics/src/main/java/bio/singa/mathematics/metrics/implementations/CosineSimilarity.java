package bio.singa.mathematics.metrics.implementations;

import bio.singa.mathematics.metrics.model.Metric;
import bio.singa.mathematics.vectors.Vector;

/**
 * The resulting similarity ranges from −1 meaning exactly opposite, to 1
 * meaning exactly the same, with 0 indicating orthogonality (decorrelation),
 * and in-between values indicating intermediate similarity or dissimilarity.
 * <p>
 * Watch out!
 * <p>
 * This is a similarity Measure, not a distance measure.
 * <p>
 * This is not a proper distance metric, since it does not satisfy the triangle
 * inequality.
 *
 * @param <VectorDimension> The type of vector that the distance is applied to.
 * @author cl
 * @see <a href="https://en.wikipedia.org/wiki/Cosine_similarity">Wikipedia: Cosine similarity</a>
 */
public class CosineSimilarity<VectorDimension extends Vector> implements Metric<VectorDimension> {

    @Override
    public double calculateDistance(VectorDimension first, VectorDimension second) {
        return first.dotProduct(second) / (first.getMagnitude() * second.getMagnitude());
    }

}
