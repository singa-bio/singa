package de.bioforscher.singa.mathematics.metrics.model;

import de.bioforscher.singa.mathematics.metrics.implementations.*;
import de.bioforscher.singa.mathematics.vectors.Vector;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * This library provides some of the most used metrics ready to receive values to calculate distances of.
 *
 * @author cl
 */
public final class VectorMetricProvider {

    private static final VectorMetricProvider INSTANCE = new VectorMetricProvider();
    /**
     * The Manhattan metric (also known as taxicab geometry, rectilinear distance, L1 distance, snake distance or city
     * block distance) defines a distance where the length between two points (p1 and p2) is equal to the length of all
     * paths connecting p1 and p2 along horizontal and vertical segments, without ever going back.
     *
     * @see <a href="https://en.wikipedia.org/wiki/Taxicab_geometry">Wikipedia: Taxicab geometry</a>
     */
    public static final MinkowskiMetric<Vector> MANHATTAN_METRIC = addElement(new MinkowskiMetric<>(1));
    /**
     * The Euclidean metric is the "ordinary" (i.e. straight-line) distance between two points in Euclidean space.
     *
     * @see <a href="https://en.wikipedia.org/wiki/Euclidean_distance">Wikipedia: Euclidean distance</a>
     */
    public static final EuclideanDistance<Vector> EUCLIDEAN_METRIC = addElement(new EuclideanDistance<>());
    /**
     * Calculates the squared euclidean distance between two {@link Vector}s.
     */
    public static final SquaredEuclideanDistance<Vector> SQUARED_EUCLIDEAN_METRIC = addElement(new SquaredEuclideanDistance<>());
    /**
     * The Chebyshev metric (also known as Tchebychev metric or maximum metric) is a metric defined on a vector space
     * where the distance between two vectors is the greatest of their differences along any coordinate dimension.
     *
     * @see <a href="https://en.wikipedia.org/wiki/Chebyshev_distance">Wikipedia: Chebyshev metric</a>
     */
    public static final MinkowskiMetric<Vector> CHEBYCHEV_METRIC = addElement(new MinkowskiMetric<>(Double.POSITIVE_INFINITY));
    /**
     * The cosine similarity is a similarity measure not a distance measure. The resulting similarity ranges from −1
     * meaning exactly opposite, to 1 meaning exactly the same, with 0 indicating orthogonality (decorrelation), and
     * in-between values indicating intermediate similarity or dissimilarity.
     *
     * @see <a href="https://en.wikipedia.org/wiki/Cosine_similarity">Wikipedia: Cosine similarity</a>
     * @see CosineSimilarity
     */
    public static final CosineSimilarity<Vector> COSINE_SIMILARITY = addElement(new CosineSimilarity<>());
    /**
     * Using the same calculation of similarity as the {@link CosineSimilarity}, the normalised angle between the
     * vectors can be used as a bounded similarity function within [0,1].
     *
     * @see <a href="https://en.wikipedia.org/wiki/Cosine_similarity">Wikipedia: Cosine similarity</a>
     * @see AngularDistance
     * @see CosineSimilarity
     */
    public static final AngularDistance<Vector> ANGULAR_DISTANCE = addElement(new AngularDistance<>());
    private final Set<Metric<Vector>> metrics = new HashSet<>();

    private VectorMetricProvider() {
    }

    /**
     * Adds a metric to the internal library.
     *
     * @param element The metric to add.
     * @param <MetricType> The type of the metric to add.
     * @return The metric.
     */
    private static <MetricType extends Metric<Vector>> MetricType addElement(MetricType element) {
        INSTANCE.metrics.add(element);
        return element;
    }

    /**
     * Contains all the metrics of this library. This set can be used to quickly calculate all provided distance
     * measures for some vectors.
     *
     * @return All metrics in this library.
     */
    public Set<Metric<Vector>> getElements() {
        return Collections.unmodifiableSet(metrics);
    }

}
