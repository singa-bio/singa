package de.bioforscher.singa.mathematics.intervals;

import org.junit.BeforeClass;
import org.junit.Test;

import static de.bioforscher.singa.mathematics.NumberConceptAssertion.assertIntervalEquals;

/**
 * @author cl
 */
public class IntervalTest {

    private static Interval i1;
    private static Interval i2;

    @BeforeClass
    public static void setup() {
        i1 = new Interval(-16.0, 21.3);
        i2 = new Interval(5.2, 77.9);
    }

    @Test
    public void testAdd() throws Exception {
        Interval expected = new Interval(-10.8, 99.2);
        Interval actual = i1.add(i2);
        assertIntervalEquals(expected, actual, 0.0);
    }

    @Test
    public void testAdditivelyInvert() throws Exception {
        Interval expected = new Interval(-5.2, -77.9);
        Interval actual = i2.additivelyInvert();
        assertIntervalEquals(expected, actual, 0.0);
    }

    @Test
    public void testMultiply() throws Exception {
        Interval expected = new Interval(-1246.4, 1659.27);
        Interval actual = i1.multiply(i2);
        assertIntervalEquals(expected, actual, 1e-10);
    }

    @Test
    public void testSubtract() throws Exception {
        Interval expected = new Interval(-21.2, -56.6);
        Interval actual = i1.subtract(i2);
        assertIntervalEquals(expected, actual, 1e-10);
    }

}