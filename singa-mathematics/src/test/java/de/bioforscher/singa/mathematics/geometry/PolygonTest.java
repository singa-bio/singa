package de.bioforscher.singa.mathematics.geometry;

import de.bioforscher.singa.mathematics.geometry.faces.SimplePolygon;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PolygonTest {

    private SimplePolygon simplePolygon;

    @Before
    public void initialize() {
        List<Vector2D> vectors = new ArrayList<>();
        vectors.add(new Vector2D(4.0, 10.0));
        vectors.add(new Vector2D(9.0, 7.0));
        vectors.add(new Vector2D(11.0, 2.0));
        vectors.add(new Vector2D(-2.0, 2.0));
        this.simplePolygon = new SimplePolygon(vectors.toArray(new Vector2D[0]));
    }

    @Test
    public void testExtremePositions() {
        assertEquals(-2.0, this.simplePolygon.getLeftMostXPosition(), 0.0);
        assertEquals(11.0, this.simplePolygon.getRightMostXPosition(), 0.0);
        assertEquals(10.0, this.simplePolygon.getTopMostYPosition(), 0.0);
        assertEquals(2.0, this.simplePolygon.getBottomMostYPosition(), 0.0);
    }

    @Test
    public void testExtent() {
        assertEquals(13.0, this.simplePolygon.getWidth(), 0.0);
        assertEquals(8.0, this.simplePolygon.getHeight(), 0.0);
    }

    @Test
    public void testArea() {
        assertEquals(61.5, this.simplePolygon.getArea(), 0.0);
    }

    @Test
    public void testPerimeter() {
        assertEquals(34.2161167019798, this.simplePolygon.getPerimeter(), 0.0);
    }

}
