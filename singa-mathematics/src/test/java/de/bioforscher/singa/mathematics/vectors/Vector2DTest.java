package de.bioforscher.singa.mathematics.vectors;

import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class Vector2DTest {

    private Vector2D first;
    private Vector2D second;
    private double scalar;

    @Before
    public void initialize() {
        this.first = new Vector2D(10.0, 20.0);
        this.second = new Vector2D(15.0, 25.0);
        this.scalar = 2.0;
    }

    @Test
    public void testAddCalculation() {
        Vector2D actual = this.first.add(this.second);
        assertArrayEquals(new double[]{25.0, 45.0}, actual.getElements(), 0.0);
    }

    @Test
    public void testSubtractCalculation() {
        Vector2D actual = this.first.subtract(this.second);
        assertArrayEquals(new double[]{-5.0, -5.0}, actual.getElements(), 0.0);
    }

    @Test
    public void testMultiplicationCalculation() {
        Vector2D actual = this.first.multiply(this.second);
        assertArrayEquals(new double[]{150.0, 500.0}, actual.getElements(), 0.0);
    }

    @Test
    public void testMultiplicationWithScalarCalculation() {
        Vector2D actual = this.first.multiply(this.scalar);
        assertArrayEquals(new double[]{20.0, 40.0}, actual.getElements(), 0.0);
    }

    @Test
    public void testAdditivelyInvertCalculation() {
        Vector2D actual = this.first.additivelyInvert();
        assertArrayEquals(new double[]{-10.0, -20.0}, actual.getElements(), 0.0);
    }

    @Test
    public void testAdditivelyInvertElementCalculation() {
        Vector2D actual = this.first.invertX().invertY();
        assertArrayEquals(new double[]{-10, -20.0}, actual.getElements(), 0.0);
    }

    @Test
    public void testMagnitudeCalculation() {
        double actual = this.first.getMagnitude();
        assertEquals(10.0 * Math.sqrt(5), actual, 0.0);
    }

    @Test
    public void testDivisionWithScalarCalculation() {
        Vector2D actual = this.first.divide(this.scalar);
        assertArrayEquals(new double[]{5.0, 10.0}, actual.getElements(), 0.0);
    }

    @Test
    public void testDivisionCalculation() {
        Vector2D actual = this.first.divide(this.second);
        assertArrayEquals(new double[]{2.0 / 3.0, 0.8}, actual.getElements(), 0.0);
    }

    @Test
    public void testDotProductCalculation() {
        double actual = this.first.dotProduct(this.second);
        assertEquals(650.0, actual, 0.0);
    }

    @Test
    public void testAngleCalculation() {
        double actual = this.first.angleTo(this.second);
        assertEquals(0.07677189126977, actual, 1e-10);
    }

    @Test
    public void testMidpointCalculation() {
        Vector2D actual = this.first.getMidpointTo(this.second);
        assertArrayEquals(new double[]{12.5, 22.5}, actual.getElements(), 0.0);
    }

    @Test
    public void shouldBeNearEachOther() {
        boolean actualTrue = this.first.isNearVector(this.second, 3.0);
        boolean actualFalse = this.first.isNearVector(this.second, 2.0);
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
        assertFalse(above.canBePlacedIn(r));
        assertFalse(below.canBePlacedIn(r));
        assertFalse(left.canBePlacedIn(r));
        assertFalse(right.canBePlacedIn(r));
        assertTrue(inside.canBePlacedIn(r));
    }

    @Test
    public void testNormalizationCalculation() {
        Vector2D actual = this.first.normalize();
        assertArrayEquals(new double[]{1 / Math.sqrt(5.0), 2 / Math.sqrt(5)}, actual.getElements(), 1e-15);
        assertEquals(1.0, actual.getMagnitude(), 1e-15);
    }

}
