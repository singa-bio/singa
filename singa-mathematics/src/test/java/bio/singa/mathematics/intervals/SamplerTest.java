package bio.singa.mathematics.intervals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author cl
 */
class SamplerTest {

    @Test
    @DisplayName("multiplicative sampling")
    void testMultiplicativeSampling() {
        List<Double> firstSamples = Sampler.sampleMultiplicative(1, 10, 10);
        for (int i = 1; i <= 10; i++) {
            assertEquals(firstSamples.get(i-1).doubleValue(), i);
        }
    }

    @Test
    @DisplayName("exponential sampling")
    void testExponentialSampling() {
        List<Double> secondSamples = Sampler.sampleExponentially(-3, 2, 1);
        assertEquals(secondSamples.get(0).doubleValue(), 0.001);
        assertEquals(secondSamples.get(1).doubleValue(), 0.01);
        assertEquals(secondSamples.get(2).doubleValue(), 0.1);
        assertEquals(secondSamples.get(3).doubleValue(), 1.0);
        assertEquals(secondSamples.get(4).doubleValue(), 10.0);
        assertEquals(secondSamples.get(5).doubleValue(), 100.0);
    }

}