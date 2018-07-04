package de.bioforscher.singa.mathematics.geometry.model;

import de.bioforscher.singa.mathematics.geometry.edges.LineSegment;
import de.bioforscher.singa.mathematics.geometry.edges.SimpleLineSegment;
import de.bioforscher.singa.mathematics.geometry.faces.Circle;
import de.bioforscher.singa.mathematics.vectors.Vector2D;

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
        Vector2D[] vertices = getVertices();
        if (vertices.length < 2) {
            return segments;
        }
        for (int i = 1; i < vertices.length; i++) {
            segments.add(new SimpleLineSegment(vertices[i - 1],vertices[i]));
        }
        segments.add(new SimpleLineSegment(vertices[vertices.length-1],vertices[0]));
        return segments;
    }

    default Set<Vector2D> getIntersections(Circle circle) {
        Set<Vector2D> intersections = new HashSet<>();
        for (LineSegment lineSegment : getEdges()) {
            intersections.addAll(lineSegment.intersectionsWith(circle));
        }
        return intersections;
    }

    default Set<Vector2D> getIntersections(LineSegment lineSegment) {
        Set<Vector2D> intersections = new HashSet<>();
        for (LineSegment polygonSegment : getEdges()) {
            intersections.addAll(polygonSegment.intersectionsWith(lineSegment));
        }
        return intersections;
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
        // Since all polygons of a Voronoi diagram are convex, the following solution applies:
        // http://paulbourke.net/geometry/polygonmesh/
        // Solution 3 (2D):
        //   "If the polygon is convex then one can consider the polygon
        //   "as a 'path' from the first vertex. A point is on the interior
        //   "of this polygons if it is always on the same side of all the
        //   "line segments making up the path. ...
        //   "(y - y0) (x1 - x0) - (x - x0) (y1 - y0)
        //   "if it is less than 0 then P is to the right of the line segment,
        //   "if greater than 0 it is to the left, if equal to 0 then it lies
        //   "on the line segment"
        for (LineSegment lineSegment : getEdges()) {
            // FIXME this relies on the ordering of the line segments
            // voronoi segemnts are ordered in reverse
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


}
