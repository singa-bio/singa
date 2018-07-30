package bio.singa.mathematics.geometry.faces;

import bio.singa.mathematics.geometry.model.Polygon;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.mathematics.vectors.Vectors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class VertexPolygon implements Polygon {

    private Comparator<Vector2D> conterClockwise = Comparator.comparing(k -> k, this::compare);

    private List<Vector2D> vertices;
    private Vector2D centre;

    public VertexPolygon(Vector2D... vertices) {
        this(Arrays.asList(vertices));
    }

    public VertexPolygon(List<Vector2D> vertices) {
        this.vertices = new ArrayList<>(vertices);
        centre = getCentroid();
        this.vertices.sort(conterClockwise);
    }

    public VertexPolygon() {
        vertices = new ArrayList<>();
    }

    public VertexPolygon(VertexPolygon polygon) {
        vertices = new ArrayList<>(polygon.vertices);
        centre = polygon.centre;
    }

    public int compare(Vector2D first, Vector2D second) {
        if (first.getX() - centre.getX() >= 0 && second.getX() - centre.getX() < 0) {
            return 1;
        }
        if (first.getX() - centre.getX() < 0 && second.getX() - centre.getX() >= 0) {
            return -1;
        }
        if (first.getX() - centre.getX() == 0 && second.getX() - centre.getX() == 0) {
            if (first.getY() - centre.getY() >= 0 || second.getY() - centre.getY() >= 0) {
                if (first.getY() > second.getY()) {
                    return 1;
                } else {
                    return -1;
                }
            } else {
                if (second.getY() > first.getY()) {
                    return 1;
                } else {
                    return -1;
                }
            }
        }

        // compute the cross product of vectors (centre -> first) getX() (centre -> second)
        double det = (first.getX() - centre.getX()) * (second.getY() - centre.getY()) - (second.getX() - centre.getX()) * (first.getY() - centre.getY());
        if (det < 0) {
            return 1;
        }
        if (det > 0) {
            return -1;
        }

        // points first firstnd second firstre on the sfirstme line from the centre
        // check which point is closer to the centre
        double d1 = (first.getX() - centre.getX()) * (first.getX() - centre.getX()) + (first.getY() - centre.getY()) * (first.getY() - centre.getY());
        double d2 = (second.getX() - centre.getX()) * (second.getX() - centre.getX()) + (second.getY() - centre.getY()) * (second.getY() - centre.getY());
        if (d1 > d2) {
            return 1;
        } else {
            return -1;
        }

    }

    @Override
    public List<Vector2D> getVertices() {
        return vertices;
    }

    @Override
    public Vector2D getVertex(int vertexIdentifier) {
        return vertices.get(vertexIdentifier);
    }

    public void addVertex(Vector2D vertex) {
        vertices.add(vertex);
        centre = getCentroid();
        vertices.sort(conterClockwise);
    }

    public double getLeftMostXPosition() {
        return Vectors.getMinimalValueForIndex(Vector2D.X_INDEX, vertices);
    }

    public double getRightMostXPosition() {
        return Vectors.getMaximalValueForIndex(Vector2D.X_INDEX, getVertices());
    }

    public double getBottomMostYPosition() {
        return Vectors.getMinimalValueForIndex(Vector2D.Y_INDEX, getVertices());
    }

    public double getTopMostYPosition() {
        return Vectors.getMaximalValueForIndex(Vector2D.Y_INDEX, getVertices());
    }

    public double getWidth() {
        return Math.abs(getRightMostXPosition() - getLeftMostXPosition());
    }

    public double getHeight() {
        return Math.abs(getTopMostYPosition() - getBottomMostYPosition());
    }

    public void scale(double scalingFactor) {
        List<Vector2D> scaledVectors = new ArrayList<>();
        for (Vector2D vertex : vertices) {
            scaledVectors.add(vertex.multiply(scalingFactor));
        }
        vertices = scaledVectors;
    }

    public void rotate(double angle) {
        List<Vector2D> rotatedVectors = new ArrayList<>();
        Vector2D centre = getCentroid();
        for (Vector2D vertex : vertices) {
            rotatedVectors.add(vertex.rotate(centre, angle));
        }
        vertices = rotatedVectors;
    }

    public void reduce(int iterations) {
        for (int i = 0; i < iterations; i++) {
            reduce();
        }
    }

    private void reduce() {
        List<Vector2D> reducedVectors = new ArrayList<>();
        for (int index = 0; index < vertices.size(); index += 2) {
            reducedVectors.add(vertices.get(index));
        }
        vertices = reducedVectors;
    }

    public void moveCentreTo(Vector2D newCentre) {
        Vector2D displacement = newCentre.subtract(getCentroid());
        List<Vector2D> reducedVectors = new ArrayList<>();
        for (Vector2D vertex : vertices) {
            reducedVectors.add(vertex.add(displacement));
        }
        vertices = reducedVectors;
        centre = newCentre;
    }


    public double getArea() {
        double sum = 0;
        for (int index = 0; index < getNumberOfVertices(); index++) {
            final int offsetIndex = (index + 1) % getNumberOfVertices();
            final double m1 = getVertex(index).getX() * getVertex(offsetIndex).getY();
            final double m2 = getVertex(offsetIndex).getX() * getVertex(index).getY();
            sum += m1 - m2;
        }
        return Math.abs(sum / 2.0);
    }

    public double getPerimeter() {
        double sum = 0;
        for (int index = 0; index < getNumberOfVertices(); index++) {
            final int offsetIndex = (index + 1) % getNumberOfVertices();
            final double length = getVertex(index).distanceTo(getVertex(offsetIndex));
            sum += length;
        }
        return sum;
    }

    @Override
    public Polygon getCopy() {
        return new VertexPolygon(this);
    }
}
