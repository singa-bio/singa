package bio.singa.mathematics.metrics;

import bio.singa.mathematics.exceptions.IncompatibleDimensionsException;
import bio.singa.mathematics.matrices.SymmetricMatrix;
import bio.singa.mathematics.metrics.implementations.JaccardMetric;
import bio.singa.mathematics.metrics.implementations.MinkowskiMetric;
import bio.singa.mathematics.metrics.implementations.TanimotoCoefficient;
import bio.singa.mathematics.metrics.model.Metric;
import bio.singa.mathematics.metrics.model.VectorMetricProvider;
import bio.singa.mathematics.vectors.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class MetricTest {

    private static Metric<Collection<String>> jaccard;

    @BeforeAll
    static void initialize() {
        jaccard = new JaccardMetric<>();
    }

    @Test
    void testManhattanMetric() {
        Vector2D first = new Vector2D(0.0, 1.0);
        Vector2D second = new Vector2D(2.0, 5.0);
        double manhattenDistance = VectorMetricProvider.MANHATTAN_METRIC.calculateDistance(first, second);
        assertEquals(6.0, manhattenDistance);
    }

    @Test
    void testEucledianMetric() {
        Vector2D first = new Vector2D(0.0, 0.0);
        Vector2D second = new Vector2D(1.0, 1.0);
        double euclideanDistance = VectorMetricProvider.EUCLIDEAN_METRIC.calculateDistance(first, second);
        assertEquals(Math.sqrt(2), euclideanDistance);
    }

    @Test
    void testChebychefMetric() {
        Vector2D first = new Vector2D(0.0, 1.0);
        Vector2D second = new Vector2D(1.0, 5.0);
        double chebychefDistance = VectorMetricProvider.CHEBYCHEV_METRIC.calculateDistance(first, second);
        assertEquals(4.0, chebychefDistance);
    }

    @Test
    void testCosineSimilarity() {
        Vector2D first = new Vector2D(-2.0, 1.0);
        Vector2D second = new Vector2D(1.0, 5.0);
        double cosineSimilarity = VectorMetricProvider.COSINE_SIMILARITY.calculateDistance(first, second);
        assertEquals(0.2631174057921088, cosineSimilarity);
    }

    @Test
    void testAngularDistance() {
        Vector2D first = new Vector2D(-2.0, 1.0);
        Vector2D second = new Vector2D(1.0, 5.0);
        double angularDistance = VectorMetricProvider.ANGULAR_DISTANCE.calculateDistance(first, second);
        assertEquals(0.584750659461432, angularDistance);
    }

    @Test
    void testMinkowskiMetricWithPLessThanOne() {
        Vector2D first = new Vector2D(0.0, 0.0);
        Vector2D second = new Vector2D(1.0, 1.0);
        Metric<Vector2D> minkowski = new MinkowskiMetric<>(0.5);
        minkowski.calculateDistance(first, second);
    }

    @Test
    void testMinkowskiMetricWithVectorsOfDifferentDimension() {
        RegularVector first = new RegularVector(1.0, 1.0, 1.0);
        RegularVector second = new RegularVector(1.0, 1.0);
        MinkowskiMetric<RegularVector> minkowski = new MinkowskiMetric<>(2);
        assertThrows(IncompatibleDimensionsException.class,
                () -> minkowski.calculateDistance(first, second));
    }

    @Test
    void testJaccardMetric() {
        Set<String> first = new HashSet<>();
        first.add("Apple");
        first.add("Pear");
        first.add("Banana");

        Set<String> second = new HashSet<>();
        second.add("Pear");
        second.add("Cucumber");
        second.add("Tomato");

        double jaccardDistance = jaccard.calculateDistance(first, second);
        assertEquals(0.8, jaccardDistance);
    }

    @Test
    void testJaccardMetricWithEmptySet() {
        Set<String> first = new HashSet<>();
        Set<String> second = new HashSet<>();
        double jaccardDistance = jaccard.calculateDistance(first, second);
        assertEquals(1.0, jaccardDistance);
    }

    @Test
    void testPairwiseDistanceCalculation() {
        List<Vector3D> vectors = new ArrayList<>();
        vectors.add(new Vector3D(0.0, 0.0, 0.0));
        vectors.add(new Vector3D(1.0, 0.0, 0.0));
        vectors.add(new Vector3D(0.0, 1.0, 0.0));
        vectors.add(new Vector3D(0.0, 0.0, 1.0));

        SymmetricMatrix actual = VectorMetricProvider.EUCLIDEAN_METRIC.calculateDistancesPairwise(vectors);
        final double sqrt2 = Math.sqrt(2);
        double[][] expected = new double[][]{{0.0}, {1.0, 0.0}, {1.0, sqrt2, 0.0}, {1.0, sqrt2, sqrt2, 0.0}};
        assertTrue(Arrays.deepEquals(expected, actual.getElements()));
    }

    @Test
    void testTanimotoCoefficient() {
        BitVector firstBitVector = new RegularBitVector(true, false, true, true, false, true);
        BitVector secondBitVector = new RegularBitVector(true, true, false, true, false, false);
        TanimotoCoefficient<BitVector> tanimotoCoefficient = new TanimotoCoefficient<>();
        double distance = tanimotoCoefficient.calculateDistance(firstBitVector, secondBitVector);
        assertEquals(0.4, distance, 1E-6);
    }
}
