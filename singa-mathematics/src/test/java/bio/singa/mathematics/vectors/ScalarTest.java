package bio.singa.mathematics.vectors;

import bio.singa.mathematics.matrices.Matrix;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ScalarTest {

    private Scalar firstScalar;
    private Scalar secondScalar;

    @BeforeEach
    void initialize() {
        firstScalar = new Scalar(4.0);
        secondScalar = new Scalar(7.0);
    }

    @Test
    void testAddCalculation() {
        Scalar actual = firstScalar.add(secondScalar);
        assertEquals(11.0, actual.getValue());
    }

    @Test
    void testSubtractCalculation() {
        Scalar actual = firstScalar.subtract(secondScalar);
        assertEquals(-3.0, actual.getValue());
    }

    @Test
    void testAdditivelyInvertCalculation() {
        Scalar actual = firstScalar.additivelyInvert();
        assertEquals(-4.0, actual.getValue());
    }

    @Test
    void testAdditivelyInvertElementCalculation() {
        Scalar actual = secondScalar.additiveleyInvertElement(0);
        assertEquals(-7.0, actual.getValue());
    }

    @Test
    void shouldNotInvert() {
        assertThrows(IndexOutOfBoundsException.class,
                () -> secondScalar.additiveleyInvertElement(1));
    }

    @Test
    void testMultiplicationCalculation() {
        Scalar actual = firstScalar.multiply(secondScalar);
        assertEquals(28.0, actual.getValue());
    }

    @Test
    void testDivisionCalculation() {
        Scalar actual = firstScalar.divide(secondScalar);
        assertEquals(4.0 / 7.0, actual.getValue());
    }

    @Test
    void testNormalizationCalculation() {
        Scalar actual = firstScalar.normalize();
        assertEquals(4.0, actual.getValue());
    }

    @Test
    void testDotProductCalculation() {
        double actual = firstScalar.dotProduct(secondScalar);
        assertEquals(28.0, actual);
    }

    @Test
    void testDyadicProductCalculation() {
        Matrix actual = firstScalar.dyadicProduct(secondScalar);
        assertTrue(Arrays.deepEquals(new double[][]{{28}}, actual.getElements()));
    }

    @Test
    void testMagnitudeCalculation() {
        double actual = firstScalar.getMagnitude();
        assertEquals(4.0, actual);
    }

    @Test
    void testDistanceCalculation() {
        double actual = firstScalar.distanceTo(secondScalar);
        assertEquals(3.0, actual);
    }

    @Test
    void testDimensionString() {
        String actual = firstScalar.getDimensionAsString();
        assertEquals("1D", actual);
    }

}
