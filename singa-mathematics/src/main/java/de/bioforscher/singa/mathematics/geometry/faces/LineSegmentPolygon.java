package de.bioforscher.singa.mathematics.geometry.faces;

import de.bioforscher.singa.mathematics.geometry.edges.LineSegment;
import de.bioforscher.singa.mathematics.geometry.edges.SimpleLineSegment;
import de.bioforscher.singa.mathematics.geometry.model.Polygon;
import de.bioforscher.singa.mathematics.vectors.Vector2D;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class LineSegmentPolygon implements Polygon {

    private List<LineSegment> edges;

    public LineSegmentPolygon(List<LineSegment> edges) {
        this.edges = edges;
    }

    @Override
    public Vector2D[] getVertices() {
        Vector2D[] vertices = new Vector2D[edges.size()];
        for (int i = 0; i < edges.size(); i++) {
            vertices[i] = edges.get(i).getStartingPoint();
        }
        return vertices;
    }

    @Override
    public Vector2D getVertex(int vertexIdentifier) {
        return edges.get(vertexIdentifier).getStartingPoint();
    }

    @Override
    public List<LineSegment> getEdges() {
        return edges;
    }

    public Vector2D getCentroid() {
        Vector2D[] vertices = getVertices();
        int vectorCount = vertices.length;
        double[] sum = new double[2];
        for (Vector2D vector : vertices) {
            sum[0] += vector.getX();
            sum[1] += vector.getY();
        }
        return new Vector2D(sum[0] / vectorCount, sum[1] / vectorCount);
    }

    public void scale(double scalingFactor) {
        List<LineSegment> scaledLineSegments = new ArrayList<>();
        for (LineSegment lineSegment : edges) {
            scaledLineSegments.add(new SimpleLineSegment(lineSegment.getStartingPoint().multiply(scalingFactor), lineSegment.getEndingPoint().multiply(scalingFactor)));
        }
        edges = scaledLineSegments;
    }

    public void rotate(double angle) {
        List<LineSegment> scaledLineSegments = new ArrayList<>();
        Vector2D centre = getCentroid();
        for (LineSegment lineSegment : edges) {
            scaledLineSegments.add(new SimpleLineSegment(lineSegment.getStartingPoint().rotate(centre, angle), lineSegment.getEndingPoint().rotate(centre, angle)));
        }
        edges = scaledLineSegments;
    }

    public void reduce(int iterations) {
        for (int i = 0; i < iterations; i++) {
            reduce();
        }
    }

    private void reduce() {
        List<LineSegment> reducedLineSegments = new ArrayList<>();
        for (int i = 1; i < edges.size() - 1; i += 2) {
            Vector2D previous = edges.get(i - 1).getStartingPoint();
            Vector2D current = edges.get(i).getEndingPoint();
            if (!previous.equals(current)) {
                SimpleLineSegment lineSegment = new SimpleLineSegment(previous, current);
                reducedLineSegments.add(lineSegment);
            }
        }
        edges = reducedLineSegments;
        // take care first and last are connected
        LineSegment first = reducedLineSegments.get(0);
        LineSegment last = reducedLineSegments.get(reducedLineSegments.size() - 1);
        edges.add(new SimpleLineSegment(last.getEndingPoint(), first.getStartingPoint()));
    }

    public void moveCentreTo(Vector2D newCentre) {
        Vector2D displacement = newCentre.subtract(getCentroid());
        List<LineSegment> movedLineSegments = new ArrayList<>();
        for (LineSegment lineSegment : edges) {
            movedLineSegments.add(new SimpleLineSegment(lineSegment.getStartingPoint().add(displacement), lineSegment.getEndingPoint().add(displacement)));
        }
        edges = movedLineSegments;
    }

}
