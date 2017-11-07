package de.bioforscher.singa.mathematics.geometry.faces;

import de.bioforscher.singa.mathematics.geometry.model.Polytope;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.mathematics.vectors.Vectors;

import java.util.ArrayList;

public class SimplePolygon implements Polytope<Vector2D> {

    private final Vector2D[] vertices;

    public SimplePolygon(Vector2D... vertices) {
        this.vertices = vertices;
    }

    public SimplePolygon(ArrayList<Vector2D> vertices) {
        this(vertices.toArray(new Vector2D[0]));
    }

    @Override
    public Vector2D[] getVertices() {
        return vertices;
    }

    @Override
    public Vector2D getVertex(int vertexIdentifier) {
        return vertices[vertexIdentifier];
    }

    public double getLeftMostXPosition() {
        return Vectors.getMinimalValueForIndex(Vector2D.X_INDEX, getVertices());
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

    public int getNumberOfVertices() {
        return vertices.length;
    }

    public int getNumberOfEdges() {
        return getNumberOfVertices();
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

}
