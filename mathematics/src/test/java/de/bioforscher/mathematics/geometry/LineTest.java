package de.bioforscher.mathematics.geometry;

import de.bioforscher.mathematics.geometry.edges.Line;
import de.bioforscher.mathematics.vectors.Vector2D;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class LineTest {

    /**
     * tests for illegal argument exception if both given points are identical
     */
    @Test(expected = IllegalArgumentException.class)
    public void shouldNotCreateFromIdenticalPoints() {
        Line line = new Line(new Vector2D(1.0, 1.0), new Vector2D(1.0, 1.0));
        assertNull(line);
    }

    /**
     * tests for illegal argument exception if slope is Double.NaN
     */
    @Test(expected = IllegalArgumentException.class)
    public void shouldNotCreateForInvalidSlope() {
        Line line = new Line(0.0, Math.log(3*-1));
        assertNull(line);
    }

    /**
     * tests for illegal argument exception if y-intercept is Double.NaN
     */
    @Test(expected = IllegalArgumentException.class)
    public void shouldNotCreateForInvalidYIntercept() {
        Line line = new Line(Math.sqrt(3-6), 0.5);
        assertNull(line);
    }

    /**
     * should create vertical line
     */
    @Test
    public void shouldCreateVerticalLine() {
        Line line = new Line(new Vector2D(13.0 , 4.0), Double.POSITIVE_INFINITY);
        assertEquals(13.0, line.getXIntercept(), 0.0);
        assertEquals(Double.NaN, line.getYIntercept(), 0.0);
    }

    /**
     * should calculate slope
     */
    @Test
    public void shouldCalculateSlope() {
        double slope = Line.calculateSlope(new Vector2D(7, 22), new Vector2D(12, 14));
        assertEquals(-8.0/5.0, slope, 0.0);
    }

    /**
     * should calculate x-intercept
     */
    @Test
    public void shouldCalculateXIntercept() {
        Line line = new Line(new Vector2D(1.0 ,13.0), new Vector2D(-3.0, -4.0));
        assertEquals(-35.0/17.0, line.getXIntercept(), 0.0);
    }

    /**
     * should calculate y-intercept
     */
    @Test
    public void shouldCalculateYIntercept() {
        double yIntercept = Line.calculateYIntercept(new Vector2D(7, 22), 4.0/7.0);
        assertEquals(18.0, yIntercept, 0.0);
    }

    /**
     * should calculate x-value from y-value
     */
    @Test
    public void shouldCalculateXValue() {
        Line line = new Line(new Vector2D(43.0 ,7.0), new Vector2D(21.0, -4.0));
        assertEquals(33.0, line.getXValue(2.0), 0.0);
    }

    /**
     * should calculate y-value from x-value
     */
    @Test
    public void shouldCalculateYValue() {
        Line line = new Line(new Vector2D(-4.0 ,37.0), new Vector2D(12.0, -4.0));
        assertEquals(141.0/16.0, line.getYValue(7.0), 0.0);
    }

    /**
     * should calculate angle to x-axis
     */
    @Test
    public void shouldAngleToXAxis() {
        Line line = new Line(new Vector2D(23.0 ,9.0), new Vector2D(57.0, 2.0));
        System.out.println(Math.toDegrees(line.getAngleToXAxis()));
    }


}
