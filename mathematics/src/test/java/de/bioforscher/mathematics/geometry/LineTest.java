package de.bioforscher.mathematics.geometry;

import de.bioforscher.mathematics.geometry.edges.Line;
import de.bioforscher.mathematics.vectors.Vector2D;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class LineTest {

    private Line line;

    @Before
    public void initialize() {
        this.line = new Line(-3, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldInitializeIncorrectly() {
        Line lineFromPoints = new Line(new Vector2D(1.0, 1.0), new Vector2D(1.0, 1.0));
        assertNull(lineFromPoints);
    }

    @Test
    public void testXValueCalculation() {
        assertEquals(2.0, this.line.getXValue(1), 0.0);
    }

    @Test
    public void testYValueCalculation() {
        assertEquals(-1.0, this.line.getYValue(1), 0.0);
    }

    @Test
    public void testXInterceptCalculation() {
        assertEquals(1.5, this.line.getXIntercept(), 0.0);
    }

    @Test
    public void testYInterceptCalculation() {
        Line lineFromPointAndSlope = new Line(new Vector2D(1.0, 1.0), 1);
        assertEquals(0, lineFromPointAndSlope.getYIntercept(), 0.0);
    }

    @Test
    public void testSlopeCalculation() {
        Line lineFromPoints = new Line(1.0, 1.0, 4.0, 2.0);
        assertEquals(1.0 / 3.0, lineFromPoints.getSlope(), 0.0);
    }

}
