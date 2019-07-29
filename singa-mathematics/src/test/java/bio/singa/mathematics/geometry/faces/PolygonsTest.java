package bio.singa.mathematics.geometry.faces;

import bio.singa.core.utility.Pair;
import bio.singa.mathematics.geometry.edges.LineSegment;
import bio.singa.mathematics.geometry.edges.SimpleLineSegment;
import bio.singa.mathematics.geometry.model.Polygon;
import bio.singa.mathematics.vectors.Vector2D;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

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
        assertTrue(Polygons.containsVector(pentagon, new Vector2D(4.0, 3.0)));
        assertFalse(Polygons.containsVector(pentagon, new Vector2D(3.0, 6.0)));
    }

    @Test
    @DisplayName("evaluate point position - concave polygon")
    void testPointInsidePolygon2() {
        assertTrue(Polygons.containsVector(concavePentagon, new Vector2D(4.0, 2.0)));
        assertFalse(Polygons.containsVector(concavePentagon, new Vector2D(4.0, 4.0)));
    }

    @Test
    @DisplayName("evaluate point position - complex polygon")
    void testPointInsidePolygon3() {
        assertTrue(Polygons.containsVector(complexPentagon, new Vector2D(6.0, 3.0)));
        assertFalse(Polygons.containsVector(complexPentagon, new Vector2D(6.0, 4.0)));
        assertTrue(Polygons.containsVector(complexPentagon, new Vector2D(6.0, 5.0)));
    }

    @Test
    void getTouchingLineSegments() {

        Polygon first = new Rectangle(new Vector2D(100, 100), new Vector2D(200, 200));
        Polygon second = new Rectangle(new Vector2D(200, 150), new Vector2D(300, 200));

        Map<Pair<LineSegment>, LineSegment> touchingLineSegments = Polygons.getTouchingLineSegments(first, second);
        Map.Entry<Pair<LineSegment>, LineSegment> entry = touchingLineSegments.entrySet().iterator().next();

        assertTrue(new SimpleLineSegment(new Vector2D(200.0, 100.0), new Vector2D(200.0, 200.0)).isCongruentTo(entry.getKey().getFirst()));
        assertTrue(new SimpleLineSegment(new Vector2D(200.0, 200.0), new Vector2D(200.0, 150.0)).isCongruentTo(entry.getKey().getSecond()));
        assertTrue(new SimpleLineSegment(new Vector2D(200.0, 200.0), new Vector2D(200.0, 150.0)).isCongruentTo(entry.getValue()));
    }

    @Test
    void getTouchingLineSegmentsTrivial() {

        Polygon first = new Rectangle(new Vector2D(100, 100), new Vector2D(200, 200));
        Polygon second = new Rectangle(new Vector2D(200, 100), new Vector2D(300, 200));

        Map<Pair<LineSegment>, LineSegment> touchingLineSegments = Polygons.getTouchingLineSegments(first, second);
        Map.Entry<Pair<LineSegment>, LineSegment> entry = touchingLineSegments.entrySet().iterator().next();

        assertTrue(new SimpleLineSegment(new Vector2D(200.0, 100.0), new Vector2D(200.0, 200.0)).isCongruentTo(entry.getKey().getFirst()));
        assertTrue(new SimpleLineSegment(new Vector2D(200.0, 200.0), new Vector2D(200.0, 100.0)).isCongruentTo(entry.getKey().getSecond()));
        assertTrue(new SimpleLineSegment(new Vector2D(200.0, 200.0), new Vector2D(200.0, 100.0)).isCongruentTo(entry.getValue()));
    }

    @Test
    void getTouchingLineSegmentsOverlapping() {

        Polygon first = new Rectangle(new Vector2D(100, 100), new Vector2D(200, 200));
        Polygon second = new Rectangle(new Vector2D(200, 150), new Vector2D(300, 250));

        Map<Pair<LineSegment>, LineSegment> touchingLineSegments = Polygons.getTouchingLineSegments(first, second);
        Map.Entry<Pair<LineSegment>, LineSegment> entry = touchingLineSegments.entrySet().iterator().next();
        System.out.println(entry.getKey().getSecond());

        assertTrue(new SimpleLineSegment(new Vector2D(200.0, 100.0), new Vector2D(200.0, 200.0)).isCongruentTo(entry.getKey().getFirst()));
        assertTrue(new SimpleLineSegment(new Vector2D(200.0, 250.0), new Vector2D(200.0, 150.0)).isCongruentTo(entry.getKey().getSecond()));
        assertTrue(new SimpleLineSegment(new Vector2D(200.0, 200.0), new Vector2D(200.0, 150.0)).isCongruentTo(entry.getValue()));

    }

}