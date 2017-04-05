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
        this.firstScalar = new Scalar(4.0);
        this.secondScalar = new Scalar(7.0);
    }

    @Test
    public void testAddCalculation() {
        Scalar actual = this.firstScalar.add(this.secondScalar);
        assertEquals(11.0, actual.getValue(), 0.0);
    }

    @Test
    public void testSubtractCalculation() {
        Scalar actual = this.firstScalar.subtract(this.secondScalar);
        assertEquals(-3.0, actual.getValue(), 0.0);
    }

    @Test
    public void testAdditivelyInvertCalculation() {
        Scalar actual = this.firstScalar.additivelyInvert();
        assertEquals(-4.0, actual.getValue(), 0.0);
    }

    @Test
    public void testAdditivelyInvertElementCalculation() {
        Scalar actual = this.secondScalar.additiveleyInvertElement(0);
        assertEquals(-7.0, actual.getValue(), 0.0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldNotInvert() {
        Scalar actual = this.secondScalar.additiveleyInvertElement(1);
        assertNull(actual);
    }

    @Test
    public void testMultiplicationCalculation() {
        Scalar actual = this.firstScalar.multiply(this.secondScalar);
        assertEquals(28.0, actual.getValue(), 0.0);
    }

    @Test
    public void testDivisionCalculation() {
        Scalar actual = this.firstScalar.divide(this.secondScalar);
        assertEquals(4.0 / 7.0, actual.getValue(), 0.0);
    }

    @Test
    public void testNormalizationCalculation() {
        Scalar actual = this.firstScalar.normalize();
        assertEquals(4.0, actual.getValue(), 0.0);
    }

    @Test
    public void testDotProductCalculation() {
        double actual = this.firstScalar.dotProduct(this.secondScalar);
        assertEquals(28.0, actual, 0.0);
    }

    @Test
    public void testDyadicProductCalculation() {
        Matrix actual = this.firstScalar.dyadicProduct(this.secondScalar);
        assertTrue(Arrays.deepEquals(new double[][]{{28}}, actual.getElements()));
    }

    @Test
    public void testMagnitudeCalculation() {
        double actual = this.firstScalar.getMagnitude();
        assertEquals(4.0, actual, 0.0);
    }

    @Test
    public void testDistanceCalculation() {
        double actual = this.firstScalar.distanceTo(this.secondScalar);
        assertEquals(3.0, actual, 0.0);
    }

    @Test
    public void testDimensionString() {
        String actual = this.firstScalar.getDimensionAsString();
        assertEquals("1D", actual);
    }

}
