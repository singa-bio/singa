package bio.singa.mathematics.geometry.faces;

import bio.singa.mathematics.geometry.model.Polygon;
import bio.singa.mathematics.vectors.Vector2D;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author cl
 */
class PolygonsTest {

    private static Polygon pentagon;
    private static Polygon concavePentagon;
    private static Polygon complexPentagon;


    @BeforeAll
    static void initialize() {
        // test case creation https://www.mathsisfun.com/geometry/polygons-interactive.html
        Vector2D p1 = new Vector2D(5.1, 5.8);
        Vector2D p2 = new Vector2D(7.0, 2.8);
        Vector2D p3 = new Vector2D(4.8, 0.1);
        Vector2D p4 = new Vector2D(1.5, 1.4);
        // irregular pentagon
        Vector2D p5irregular = new Vector2D(2.3, 4.6);
        // concave pentagon
        Vector2D p5concave = new Vector2D(4.5, 2.9);
        // complex pentagon
        Vector2D p5complex = new Vector2D(7.7, 4.7);
        pentagon = new ComplexPolygon(p1, p2, p3, p4, p5irregular);
        concavePentagon = new ComplexPolygon(p1, p2, p3, p4, p5concave);
        complexPentagon = new ComplexPolygon(p1, p2, p3, p4, p5complex);
    }

    @Test
    @DisplayName("evaluate point position - irregular (normal) polygon")
    void testPointInsidePolygon1() {
        assertTrue(Polygons.isInside(pentagon, new Vector2D(4.0, 3.0)));
        assertFalse(Polygons.isInside(pentagon, new Vector2D(3.0, 6.0)));
    }

    @Test
    @DisplayName("evaluate point position - concave polygon")
    void testPointInsidePolygon2() {
        assertTrue(Polygons.isInside(concavePentagon, new Vector2D(4.0, 2.0)));
        assertFalse(Polygons.isInside(concavePentagon, new Vector2D(4.0, 4.0)));
    }

    @Test
    @DisplayName("evaluate point position - complex polygon")
    void testPointInsidePolygon3() {
        assertTrue(Polygons.isInside(complexPentagon, new Vector2D(6.0, 3.0)));
        assertFalse(Polygons.isInside(complexPentagon, new Vector2D(6.0, 4.0)));
        assertTrue(Polygons.isInside(complexPentagon, new Vector2D(6.0, 5.0)));
    }

}