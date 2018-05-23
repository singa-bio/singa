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

}
