package bio.singa.mathematics.intervals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static bio.singa.mathematics.NumberConceptAssertion.assertIntervalEquals;

/**
 * @author cl
 */
class IntervalTest {

    private static DoubleInterval i1;
    private static DoubleInterval i2;

    @BeforeAll
    static void initialize() {
        i1 = new DoubleInterval(-16.0, 21.3);
        i2 = new DoubleInterval(5.2, 77.9);
    }

    @Test
    void testAdd() {
        DoubleInterval expected = new DoubleInterval(-10.8, 99.2);
        DoubleInterval actual = i1.add(i2);
        assertIntervalEquals(expected, actual, 0.0);
    }

    @Test
    void testAdditivelyInvert() {
        DoubleInterval expected = new DoubleInterval(-5.2, -77.9);
        DoubleInterval actual = i2.additivelyInvert();
        assertIntervalEquals(expected, actual, 0.0);
    }

    @Test
    void testMultiply() {
        DoubleInterval expected = new DoubleInterval(-1246.4, 1659.27);
        DoubleInterval actual = i1.multiply(i2);
        assertIntervalEquals(expected, actual, 1e-10);
    }

    @Test
    void testSubtract() {
        DoubleInterval expected = new DoubleInterval(-21.2, -56.6);
        DoubleInterval actual = i1.subtract(i2);
        assertIntervalEquals(expected, actual, 1e-10);
    }

}