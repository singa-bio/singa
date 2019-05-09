package bio.singa.mathematics.geometry.faces;

import bio.singa.mathematics.vectors.Vector2D;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author cl
 */
class CirclesTest {

    private static Circle circle;
    private static Vector2D first;
    private static Vector2D second;

    @BeforeAll
    static void initialize() {
        circle = new Circle(new Vector2D(5.0, 5.0), 2);
        first = new Vector2D(7.0, 5.0);
        second = new Vector2D(5.0, 7.0);
    }

    @Test
    @DisplayName("circle calculations - circumference")
    void circumference() {
        double circumference = Circles.circumference(circle.getRadius());
        assertEquals(4 * Math.PI, circumference, 1e-12);
    }

    @Test
    @DisplayName("circle calculations - area")
    void area() {
        double area = Circles.area(circle.getRadius());
        assertEquals(4 * Math.PI, area, 1e-12);
    }

    @Test
    @DisplayName("circle calculations - central angle")
    void centralAngle() {
        double centralAngle = Circles.centralAngle(circle, first, second);
        assertEquals(Math.PI / 2.0, centralAngle, 1e-12);
    }

    @Test
    @DisplayName("circle calculations - arc length")
    void arcLength() {
        double arcLength = Circles.arcLength(circle, first, second);
        assertEquals(Math.PI, arcLength, 1e-12);
    }

    @Test
    @DisplayName("circle calculations - point sampling")
    void samplePoints() {
        Circle circle = new Circle(new Vector2D(0, 0), 1);
        List<Vector2D> samplePoints = Circles.samplePoints(circle, 4);
        assertEquals(4, samplePoints.size());
        assertEquals(new Vector2D(1.0, 0.0).getX(), samplePoints.get(0).getX(), 1e-8);
        assertEquals(new Vector2D(1.0, 0.0).getY(), samplePoints.get(0).getY(), 1e-8);
        assertEquals(new Vector2D(0.0, 1.0).getX(), samplePoints.get(1).getX(), 1e-8);
        assertEquals(new Vector2D(0.0, 1.0).getY(), samplePoints.get(1).getY(), 1e-8);
        assertEquals(new Vector2D(-1.0, 0.0).getX(), samplePoints.get(2).getX(), 1e-8);
        assertEquals(new Vector2D(-1.0, 0.0).getY(), samplePoints.get(2).getY(), 1e-8);
        assertEquals(new Vector2D(0.0, -1.0).getX(), samplePoints.get(3).getX(), 1e-8);
        assertEquals(new Vector2D(0.0, -1.0).getY(), samplePoints.get(3).getY(), 1e-8);
    }
}