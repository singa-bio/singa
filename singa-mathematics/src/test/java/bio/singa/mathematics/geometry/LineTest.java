package bio.singa.mathematics.geometry;

import bio.singa.mathematics.geometry.edges.Line;
import bio.singa.mathematics.vectors.Vector2D;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LineTest {

    /**
     * tests for illegal argument exception if both given points are identical
     */
    @Test
    @DisplayName("illegal argument exception if both given points are identical")
    void shouldNotCreateFromIdenticalPoints() {
        assertThrows(IllegalArgumentException.class,
                () -> new Line(new Vector2D(1.0, 1.0), new Vector2D(1.0, 1.0)));
    }

    /**
     * tests for illegal argument exception if slope is Double.NaN
     */
    @Test
    void shouldNotCreateForInvalidSlope() {
        assertThrows(IllegalArgumentException.class,
                () -> new Line(0.0, Math.log(3 * -1)));
    }

    /**
     * tests for illegal argument exception if y-intercept is Double.NaN
     */
    @Test
    void shouldNotCreateForInvalidYIntercept() {
        assertThrows(IllegalArgumentException.class,
                () -> new Line(Math.sqrt(3 - 6), 0.5));
    }

    /**
     * should create vertical line
     */
    @Test
    void shouldCreateVerticalLine() {
        Line line = new Line(new Vector2D(13.0, 4.0), Double.POSITIVE_INFINITY);
        assertEquals(13.0, line.getXIntercept());
        assertEquals(Double.NaN, line.getYIntercept());
    }

    /**
     * should calculate slope
     */
    @Test
    void shouldCalculateSlope() {
        // normal
        double slope = Line.calculateSlope(new Vector2D(7, 22), new Vector2D(12, 14));
        assertEquals(-8.0 / 5.0, slope);
        // vertical
        double vertical = Line.calculateSlope(new Vector2D(7, 22), new Vector2D(7, 14));
        assertTrue(Double.isInfinite(vertical));
        // horizontal
        double horizontal = Line.calculateSlope(new Vector2D(5, 22), new Vector2D(12, 22));
        assertEquals(0.0, horizontal);
    }

    /**
     * should calculate y-intercept
     */
    @Test
    void shouldCalculateYIntercept() {
        // normal
        double yIntercept = Line.calculateYIntercept(new Vector2D(7, 22), 4.0 / 7.0);
        assertEquals(18.0, yIntercept);
        // vertical
        double vertical = Line.calculateYIntercept(new Vector2D(7, 22), Double.POSITIVE_INFINITY);
        assertTrue(Double.isInfinite(vertical));
        // horizontal
        double horizontal = Line.calculateYIntercept(new Vector2D(7, 22), 0.0);
        assertEquals(22.0, horizontal);
    }

    /**
     * should calculate x-intercept
     */
    @Test
    void shouldCalculateXIntercept() {
        // normal
        Line line = new Line(new Vector2D(1.0, 13.0), new Vector2D(-3.0, -4.0));
        assertEquals(-35.0 / 17.0, line.getXIntercept());
        // vertical
        Line vertical = new Line(new Vector2D(7, 22), new Vector2D(7, 14));
        assertEquals(7, vertical.getXIntercept());
        // horizontal
        Line horizontal = new Line(new Vector2D(5, 22), new Vector2D(12, 22));
        assertTrue(Double.isInfinite(horizontal.getXIntercept()));
    }

    /**
     * should calculate x-value from y-value
     */
    @Test
    void shouldCalculateXValue() {
        // normal
        Line line = new Line(new Vector2D(43.0, 7.0), new Vector2D(21.0, -4.0));
        assertEquals(33.0, line.getXValue(2.0));
        // vertical
        Line vertical = new Line(new Vector2D(7, 22), new Vector2D(7, 14));
        assertEquals(7.0, vertical.getXValue(2.0));
        // horizontal
        Line horizontal = new Line(new Vector2D(5, 22), new Vector2D(12, 22));
        assertTrue(Double.isInfinite(horizontal.getXValue(21)));
    }

    /**
     * should calculate y-value from x-value
     */
    @Test
    void shouldCalculateYValue() {
        Line line = new Line(new Vector2D(-4.0, 37.0), new Vector2D(12.0, -4.0));
        assertEquals(141.0 / 16.0, line.getYValue(7.0));
    }

    /**
     * should calculate angle to x-axis
     */
    @Test
    void shouldAngleToXAxis() {
        Line line = new Line(new Vector2D(23.0, 9.0), new Vector2D(57.0, 2.0));
        assertEquals(-11.633633998940436, Math.toDegrees(line.getAngleToXAxis()));
    }


}
