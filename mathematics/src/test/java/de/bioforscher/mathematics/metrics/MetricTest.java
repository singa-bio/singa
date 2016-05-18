package de.bioforscher.mathematics.metrics;

import de.bioforscher.mathematics.exceptions.IncompatibleDimensionsException;
import de.bioforscher.mathematics.metrics.implementations.JaccardMetric;
import de.bioforscher.mathematics.metrics.implementations.MinkowskiMetric;
import de.bioforscher.mathematics.metrics.model.Metric;
import de.bioforscher.mathematics.metrics.model.MetricProvider;
import de.bioforscher.mathematics.vectors.RegularVector;
import de.bioforscher.mathematics.vectors.Vector2D;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class MetricTest {

    private Metric<Collection<String>> jaccard;

    @Before
    public void initObjects() {
        this.jaccard = new JaccardMetric<>();
    }

    @Test
    public void testManhattanMetric() {
        Vector2D first = new Vector2D(0.0, 1.0);
        Vector2D second = new Vector2D(2.0, 5.0);
        double manhattenDistance = MetricProvider.MANHATTAN_METRIC.calculateDistance(first, second);
        assertEquals(6.0, manhattenDistance, 0.0);
    }

    @Test
    public void testEucledianMetric() {
        Vector2D first = new Vector2D(0.0, 0.0);
        Vector2D second = new Vector2D(1.0, 1.0);
        double euclideanDistance = MetricProvider.EUCLIDEAN_METRIC.calculateDistance(first, second);
        assertEquals(Math.sqrt(2), euclideanDistance, 0.0);
    }

    @Test
    public void testChebychefMetric() {
        Vector2D first = new Vector2D(0.0, 1.0);
        Vector2D second = new Vector2D(1.0, 5.0);
        double chebychefDistance = MetricProvider.CHEBYCHEV_METRIC.calculateDistance(first, second);
        assertEquals(4.0, chebychefDistance, 0.0);
    }

    @Test
    public void testCosineSimilarity() {
        Vector2D first = new Vector2D(-2.0, 1.0);
        Vector2D second = new Vector2D(1.0, 5.0);
        double cosineSimilarity = MetricProvider.COSINE_SIMILARITY.calculateDistance(first, second);
        assertEquals(0.2631174057921088, cosineSimilarity, 0.0);
    }

    @Test
    public void testAngularDistance() {
        Vector2D first = new Vector2D(-2.0, 1.0);
        Vector2D second = new Vector2D(1.0, 5.0);
        double angularDistance = MetricProvider.ANGULAR_DISTANCE.calculateDistance(first, second);
        assertEquals(0.584750659461432, angularDistance, 0.0);
    }

    public void testMinkowskiMetricWithPLessThanOne() {
        Vector2D first = new Vector2D(0.0, 0.0);
        Vector2D second = new Vector2D(1.0, 1.0);
        Metric<Vector2D> minkowski = new MinkowskiMetric<>(0.5);
        minkowski.calculateDistance(first, second);
    }

    @Test(expected = IncompatibleDimensionsException.class)
    public void testMinkowskiMetricWithVectorsOfDifferentDimension() {
        RegularVector first = new RegularVector(1.0, 1.0, 1.0);
        RegularVector second = new RegularVector(1.0, 1.0);
        Metric<RegularVector> minkowski = new MinkowskiMetric<>(2);
        minkowski.calculateDistance(first, second);
    }

    @Test
    public void testJaccardMetric() {
        Set<String> first = new HashSet<>();
        first.add("Apple");
        first.add("Pear");
        first.add("Banana");

        Set<String> second = new HashSet<>();
        second.add("Pear");
        second.add("Cucumber");
        second.add("Tomato");

        double jaccardDistance = this.jaccard.calculateDistance(first, second);
        assertEquals(0.8, jaccardDistance, 0.0);
    }

    @Test
    public void testJaccardMetricWithEmptySet() {
        Set<String> first = new HashSet<>();
        Set<String> second = new HashSet<>();
        double jaccardDistance = this.jaccard.calculateDistance(first, second);
        assertEquals(1.0, jaccardDistance, 0.0);
    }

}
