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
        this.vector = new RegularVector(doubles);
    }

    @Test
    public void getAverage() throws Exception {
        assertEquals(0.65, Vectors.getAverage(this.vector), 0.0);
    }

    @Test
    public void getStandardDeviation() throws Exception {
        assertEquals(0.12909, Vectors.getStandardDeviation(this.vector), 1E-4);
    }

    @Test
    public void getVariance() throws Exception {
        assertEquals(0.01666, Vectors.getVariance(this.vector), 1E-4);
    }
}