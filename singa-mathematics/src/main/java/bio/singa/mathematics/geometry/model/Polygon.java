package bio.singa.mathematics.geometry.model;

import bio.singa.mathematics.geometry.edges.LineSegment;
import bio.singa.mathematics.geometry.edges.SimpleLineSegment;
import bio.singa.mathematics.geometry.faces.Circle;
import bio.singa.mathematics.geometry.faces.Polygons;
import bio.singa.mathematics.vectors.Vector2D;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A polygon is the two dimensional representation of a polytope.
 */
public interface Polygon extends Polytope<Vector2D> {

    default List<LineSegment> getEdges() {
        List<LineSegment> segments = new ArrayList<>();
        List<Vector2D> vertices = getVertices();
        if (vertices.size() < 2) {
            return segments;
        }
        for (int i = 1; i < vertices.size(); i++) {
            segments.add(new SimpleLineSegment(vertices.get(i - 1), vertices.get(i)));
        }
        segments.add(new SimpleLineSegment(vertices.get(vertices.size() - 1), vertices.get(0)));
        return segments;
    }

    default Set<Vector2D> getIntersections(Circle circle) {
        Set<Vector2D> intersections = new HashSet<>();
        for (LineSegment lineSegment : getEdges()) {
            intersections.addAll(lineSegment.getIntersectionWith(circle));
        }
        return intersections;
    }

    default Set<Vector2D> getIntersections(LineSegment lineSegment) {
        Set<Vector2D> intersections = new HashSet<>();
        for (LineSegment polygonSegment : getEdges()) {
            polygonSegment.getIntersectionWith(lineSegment).ifPresent(intersections::add);
        }
        return intersections;
    }

    default Vector2D getCentroid() {
        List<Vector2D> vertices = getVertices();
        int vectorCount = vertices.size();
        double[] sum = new double[2];
        for (Vector2D vector : vertices) {
            sum[0] += vector.getX();
            sum[1] += vector.getY();
        }
        return new Vector2D(sum[0] / vectorCount, sum[1] / vectorCount);
    }

    default boolean isInside(Vector2D point) {
        return Polygons.isInside(this, point);
    }

    Polygon getCopy();

    void move(Vector2D targetLocation);

    void scale(double scalingFactor);

    Set<Vector2D> reduce(int times);
}
