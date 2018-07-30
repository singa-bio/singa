package bio.singa.mathematics.geometry;

import bio.singa.mathematics.geometry.faces.VertexPolygon;
import bio.singa.mathematics.vectors.Vector2D;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PolygonTest {

    private VertexPolygon simplePolygon;

    @Before
    public void initialize() {
        List<Vector2D> vectors = new ArrayList<>();
        vectors.add(new Vector2D(4.0, 10.0));
        vectors.add(new Vector2D(9.0, 7.0));
        vectors.add(new Vector2D(11.0, 2.0));
        vectors.add(new Vector2D(-2.0, 2.0));
        simplePolygon = new VertexPolygon(vectors.toArray(new Vector2D[0]));
    }

    @Test
    public void testExtremePositions() {
        assertEquals(-2.0, simplePolygon.getLeftMostXPosition(), 0.0);
        assertEquals(11.0, simplePolygon.getRightMostXPosition(), 0.0);
        assertEquals(10.0, simplePolygon.getTopMostYPosition(), 0.0);
        assertEquals(2.0, simplePolygon.getBottomMostYPosition(), 0.0);
    }

    @Test
    public void testExtent() {
        assertEquals(13.0, simplePolygon.getWidth(), 0.0);
        assertEquals(8.0, simplePolygon.getHeight(), 0.0);
    }

    @Test
    public void testArea() {
        assertEquals(61.5, simplePolygon.getArea(), 0.0);
    }

    @Test
    public void testPerimeter() {
        assertEquals(34.21611670197981, simplePolygon.getPerimeter(), 0.0);
    }

}
