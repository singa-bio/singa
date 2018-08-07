package bio.singa.mathematics.geometry.model;

import bio.singa.mathematics.geometry.edges.LineSegment;
import bio.singa.mathematics.geometry.edges.SimpleLineSegment;
import bio.singa.mathematics.geometry.faces.Circle;
import bio.singa.mathematics.vectors.Vector2D;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A polygon is the two dimensional representation of a polytope.
 */
public interface Polygon extends Polytope<Vector2D> {

    int OUTSIDE = -1;
    int ON_LINE = 0;
    int INSIDE = 1;

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

    /**
     * Returns an integer, describing whether a point is inside, on, or outside of the polygon:
     * <pre>
     * -1: point is outside the perimeter of the polygon
     *  0: point is on the perimeter of the polygon
     *  1: point is inside the perimeter of the polygon
     * </pre>
     *
     * @param point The point to check.
     * @return -1 if the point is outside of the perimeter of the cell, 0 if point is on the perimeter of the cell and 1
     * if the point is inside of the cell
     */
    default int evaluatePointPosition(Vector2D point) {
        for (LineSegment lineSegment : getEdges()) {
            Vector2D first = lineSegment.getEndingPoint();
            Vector2D second = lineSegment.getStartingPoint();
            double r = (point.getY() - first.getY()) * (second.getX() - first.getX()) - (point.getX() - first.getX()) * (second.getY() - first.getY());
            if (r == 0) {
                return ON_LINE;
            }
            if (r > 0) {
                return OUTSIDE;
            }
        }
        return INSIDE;
    }


    Polygon getCopy();

    void move(Vector2D targetLocation);

    void scale(double scalingFactor);

    void reduce(int times);
}
