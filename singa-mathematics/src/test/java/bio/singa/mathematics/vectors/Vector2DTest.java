package bio.singa.mathematics.vectors;

import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.geometry.model.Polygon;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class Vector2DTest {

    private Vector2D first;
    private Vector2D second;
    private double scalar;

    @Before
    public void initialize() {
        first = new Vector2D(10.0, 20.0);
        second = new Vector2D(15.0, 25.0);
        scalar = 2.0;
    }

    @Test
    public void testAddCalculation() {
        Vector2D actual = first.add(second);
        assertArrayEquals(new double[]{25.0, 45.0}, actual.getElements(), 0.0);
    }

    @Test
    public void testSubtractCalculation() {
        Vector2D actual = first.subtract(second);
        assertArrayEquals(new double[]{-5.0, -5.0}, actual.getElements(), 0.0);
    }

    @Test
    public void testMultiplicationCalculation() {
        Vector2D actual = first.multiply(second);
        assertArrayEquals(new double[]{150.0, 500.0}, actual.getElements(), 0.0);
    }

    @Test
    public void testMultiplicationWithScalarCalculation() {
        Vector2D actual = first.multiply(scalar);
        assertArrayEquals(new double[]{20.0, 40.0}, actual.getElements(), 0.0);
    }

    @Test
    public void testAdditivelyInvertCalculation() {
        Vector2D actual = first.additivelyInvert();
        assertArrayEquals(new double[]{-10.0, -20.0}, actual.getElements(), 0.0);
    }

    @Test
    public void testAdditivelyInvertElementCalculation() {
        Vector2D actual = first.invertX().invertY();
        assertArrayEquals(new double[]{-10, -20.0}, actual.getElements(), 0.0);
    }

    @Test
    public void testMagnitudeCalculation() {
        double actual = first.getMagnitude();
        assertEquals(10.0 * Math.sqrt(5), actual, 0.0);
    }

    @Test
    public void testDivisionWithScalarCalculation() {
        Vector2D actual = first.divide(scalar);
        assertArrayEquals(new double[]{5.0, 10.0}, actual.getElements(), 0.0);
    }

    @Test
    public void testDivisionCalculation() {
        Vector2D actual = first.divide(second);
        assertArrayEquals(new double[]{2.0 / 3.0, 0.8}, actual.getElements(), 0.0);
    }

    @Test
    public void testDotProductCalculation() {
        double actual = first.dotProduct(second);
        assertEquals(650.0, actual, 0.0);
    }

    @Test
    public void testAngleCalculation() {
        double actual = first.angleTo(second);
        assertEquals(0.07677189126977, actual, 1e-10);
    }

    @Test
    public void testMidpointCalculation() {
        Vector2D actual = first.getMidpointTo(second);
        assertArrayEquals(new double[]{12.5, 22.5}, actual.getElements(), 0.0);
    }

    @Test
    public void shouldBeNearEachOther() {
        boolean actualTrue = first.isNearVector(second, 3.0);
        boolean actualFalse = first.isNearVector(second, 2.0);
        assertTrue(actualTrue);
        assertFalse(actualFalse);
    }

    @Test
    public void shouldBeInRectangle() {
        Rectangle r = new Rectangle(new Vector2D(0.0, 10.0), new Vector2D(10.0, 0.0));
        Vector2D above = new Vector2D(5.0, 11.0);
        Vector2D below = new Vector2D(5.0, -1.0);
        Vector2D left = new Vector2D(-1.0, 5.0);
        Vector2D right = new Vector2D(11.0, 5.0);
        Vector2D inside = new Vector2D(5.0, 5.0);
        assertNotEquals(r.evaluatePointPosition(above), Polygon.INSIDE);
        assertNotEquals(r.evaluatePointPosition(below), Polygon.INSIDE);
        assertNotEquals(r.evaluatePointPosition(left), Polygon.INSIDE);
        assertNotEquals(r.evaluatePointPosition(right), Polygon.INSIDE);
        assertEquals(r.evaluatePointPosition(inside), Polygon.INSIDE);
    }

    @Test
    public void testNormalizationCalculation() {
        Vector2D actual = first.normalize();
        assertArrayEquals(new double[]{1 / Math.sqrt(5.0), 2 / Math.sqrt(5)}, actual.getElements(), 1e-15);
        assertEquals(1.0, actual.getMagnitude(), 1e-15);
    }

}
