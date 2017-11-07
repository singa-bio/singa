package de.bioforscher.singa.mathematics.vectors;

import de.bioforscher.singa.mathematics.matrices.Matrix;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class ScalarTest {

    Scalar firstScalar;
    Scalar secondScalar;

    @Before
    public void initialize() {
        firstScalar = new Scalar(4.0);
        secondScalar = new Scalar(7.0);
    }

    @Test
    public void testAddCalculation() {
        Scalar actual = firstScalar.add(secondScalar);
        assertEquals(11.0, actual.getValue(), 0.0);
    }

    @Test
    public void testSubtractCalculation() {
        Scalar actual = firstScalar.subtract(secondScalar);
        assertEquals(-3.0, actual.getValue(), 0.0);
    }

    @Test
    public void testAdditivelyInvertCalculation() {
        Scalar actual = firstScalar.additivelyInvert();
        assertEquals(-4.0, actual.getValue(), 0.0);
    }

    @Test
    public void testAdditivelyInvertElementCalculation() {
        Scalar actual = secondScalar.additiveleyInvertElement(0);
        assertEquals(-7.0, actual.getValue(), 0.0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldNotInvert() {
        Scalar actual = secondScalar.additiveleyInvertElement(1);
        assertNull(actual);
    }

    @Test
    public void testMultiplicationCalculation() {
        Scalar actual = firstScalar.multiply(secondScalar);
        assertEquals(28.0, actual.getValue(), 0.0);
    }

    @Test
    public void testDivisionCalculation() {
        Scalar actual = firstScalar.divide(secondScalar);
        assertEquals(4.0 / 7.0, actual.getValue(), 0.0);
    }

    @Test
    public void testNormalizationCalculation() {
        Scalar actual = firstScalar.normalize();
        assertEquals(4.0, actual.getValue(), 0.0);
    }

    @Test
    public void testDotProductCalculation() {
        double actual = firstScalar.dotProduct(secondScalar);
        assertEquals(28.0, actual, 0.0);
    }

    @Test
    public void testDyadicProductCalculation() {
        Matrix actual = firstScalar.dyadicProduct(secondScalar);
        assertTrue(Arrays.deepEquals(new double[][]{{28}}, actual.getElements()));
    }

    @Test
    public void testMagnitudeCalculation() {
        double actual = firstScalar.getMagnitude();
        assertEquals(4.0, actual, 0.0);
    }

    @Test
    public void testDistanceCalculation() {
        double actual = firstScalar.distanceTo(secondScalar);
        assertEquals(3.0, actual, 0.0);
    }

    @Test
    public void testDimensionString() {
        String actual = firstScalar.getDimensionAsString();
        assertEquals("1D", actual);
    }

}
