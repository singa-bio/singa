package bio.singa.mathematics.vectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author fk
 */
class VectorsTest {

    private RegularVector vector;

    @BeforeEach
    void setUp() {
        double[] doubles = {0.5, 0.6, 0.7, 0.8};
        vector = new RegularVector(doubles);
    }

    @Test
    void getAverage() {
        assertEquals(0.65, Vectors.getAverage(vector));
    }

    @Test
    void getMedian() {
        assertEquals((0.6 + 0.7) / 2, Vectors.getMedian(vector));
        double[] doubles = {0.5, 0.6, 0.7, 0.8, 0.9};
        RegularVector oddVector = new RegularVector(doubles);
        assertEquals(0.7, Vectors.getMedian(oddVector));
    }

    @Test
    void getStandardDeviation() {
        assertEquals(0.12909, Vectors.getStandardDeviation(vector), 1E-4);
    }

    @Test
    void getVariance() {
        assertEquals(0.01666, Vectors.getVariance(vector), 1E-4);
    }

    @Test
    void getIndexOfMinimalElement() {
        assertEquals(0, Vectors.getIndexWithMinimalElement(vector));
    }

    @Test
    void getIndexOfAbsoluteMinimalElement() {
        assertEquals(0, Vectors.getIndexWithAbsoluteMinimalElement(vector));
    }

    @Test
    void getIndexOfMaximalElement() {
        assertEquals(3, Vectors.getIndexWithMaximalElement(vector));
    }

    @Test
    void getIndexOfAbsoluteMaximalElement() {
        assertEquals(3, Vectors.getIndexWithAbsoluteMaximalElement(vector));
    }
}