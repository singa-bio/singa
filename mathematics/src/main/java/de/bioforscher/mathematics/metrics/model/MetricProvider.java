package de.bioforscher.mathematics.metrics.model;

import de.bioforscher.mathematics.metrics.implementations.AngularDistance;
import de.bioforscher.mathematics.metrics.implementations.CosineSimilarity;
import de.bioforscher.mathematics.metrics.implementations.MinkowskiMetric;
import de.bioforscher.mathematics.vectors.Vector;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MetricProvider {

    private final Set<Metric<?>> metrics = new HashSet<>();

    private static final MetricProvider INSTANCE = new MetricProvider();

    public Set<Metric<?>> getElements() {
        return Collections.unmodifiableSet(metrics);
    }

    private static <M extends Metric<?>> M addElement(M element) {
        INSTANCE.metrics.add(element);
        return element;
    }

    public static final MinkowskiMetric<Vector> MANHATTAN_METRIC = addElement(new MinkowskiMetric<>(1));
    public static final MinkowskiMetric<Vector> EUCLIDEAN_METRIC = addElement(new MinkowskiMetric<>(2));
    public static final MinkowskiMetric<Vector> CHEBYCHEV_METRIC = addElement(new MinkowskiMetric<>(Double.POSITIVE_INFINITY));
    public static final CosineSimilarity<Vector> COSINE_SIMILARITY = addElement(new CosineSimilarity<>());
    public static final AngularDistance<Vector> ANGULAR_DISTANCE = addElement(new AngularDistance<>());

}
