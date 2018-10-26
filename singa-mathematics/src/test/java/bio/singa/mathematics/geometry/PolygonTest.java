package bio.singa.mathematics.geometry;

import bio.singa.mathematics.geometry.faces.VertexPolygon;
import bio.singa.mathematics.vectors.Vector2D;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PolygonTest {

    private static VertexPolygon simplePolygon;

    @BeforeAll
    static void initialize() {
        List<Vector2D> vectors = new ArrayList<>();
        vectors.add(new Vector2D(4.0, 10.0));
        vectors.add(new Vector2D(9.0, 7.0));
        vectors.add(new Vector2D(11.0, 2.0));
        vectors.add(new Vector2D(-2.0, 2.0));
        simplePolygon = new VertexPolygon(vectors.toArray(new Vector2D[0]));
    }

    @Test
    void testExtremePositions() {
        assertEquals(-2.0, simplePolygon.getLeftMostXPosition());
        assertEquals(11.0, simplePolygon.getRightMostXPosition());
        assertEquals(10.0, simplePolygon.getTopMostYPosition());
        assertEquals(2.0, simplePolygon.getBottomMostYPosition());
    }

    @Test
    void testExtent() {
        assertEquals(13.0, simplePolygon.getWidth());
        assertEquals(8.0, simplePolygon.getHeight());
    }

    @Test
    void testArea() {
        assertEquals(61.5, simplePolygon.getArea());
    }

    @Test
    void testPerimeter() {
        assertEquals(34.21611670197981, simplePolygon.getPerimeter());
    }

}
