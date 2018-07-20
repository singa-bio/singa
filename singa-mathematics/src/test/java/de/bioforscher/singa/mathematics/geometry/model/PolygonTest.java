package de.bioforscher.singa.mathematics.geometry.model;

import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * @author cl
 */
public class PolygonTest {

    @Test
    public void evaluatePointPosition() {
        Rectangle rectangle = new Rectangle(new Vector2D(0,0), new Vector2D(200, 200));
        Vector2D inside = new Vector2D(10,10);
        Vector2D onLine = new Vector2D(100, 200);
        Vector2D outside = new Vector2D(300, 300);
        assertEquals(rectangle.evaluatePointPosition(inside), Rectangle.INSIDE);
        assertEquals(rectangle.evaluatePointPosition(onLine), Rectangle.ON_LINE);
        assertEquals(rectangle.evaluatePointPosition(outside), Rectangle.OUTSIDE);
    }
}