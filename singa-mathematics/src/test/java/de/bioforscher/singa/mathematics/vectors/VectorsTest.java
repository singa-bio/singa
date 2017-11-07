package de.bioforscher.singa.mathematics.vectors;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author fk
 */
public class VectorsTest {

    private RegularVector vector;

    @Before
    public void setUp() {
        double[] doubles = {0.5, 0.6, 0.7, 0.8};
        vector = new RegularVector(doubles);
    }

    @Test
    public void getAverage() throws Exception {
        assertEquals(0.65, Vectors.getAverage(vector), 0.0);
    }

    @Test
    public void getMedian() throws Exception {
        assertEquals((0.6 + 0.7) / 2, Vectors.getMedian(vector), 0.0);
        double[] doubles = {0.5, 0.6, 0.7, 0.8, 0.9};
        RegularVector oddVector = new RegularVector(doubles);
        assertEquals(0.7, Vectors.getMedian(oddVector), 0.0);
    }

    @Test
    public void getStandardDeviation() throws Exception {
        assertEquals(0.12909, Vectors.getStandardDeviation(vector), 1E-4);
    }

    @Test
    public void getVariance() throws Exception {
        assertEquals(0.01666, Vectors.getVariance(vector), 1E-4);
    }

    @Test
    public void getIndexOfMinimalElement() {
        assertEquals(0, Vectors.getIndexWithMinimalElement(vector));
    }

    @Test
    public void getIndexOfAbsoluteMinimalElement() {
        assertEquals(0, Vectors.getIndexWithAbsoluteMinimalElement(vector));
    }

    @Test
    public void getIndexOfMaximalElement() {
        assertEquals(3, Vectors.getIndexWithMaximalElement(vector));
    }

    @Test
    public void getIndexOfAbsoluteMaximalElement() {
        assertEquals(3, Vectors.getIndexWithAbsoluteMaximalElement(vector));
    }
}