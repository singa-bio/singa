package bio.singa.mathematics.geometry.faces;

import bio.singa.mathematics.geometry.edges.LineSegment;
import bio.singa.mathematics.geometry.edges.SimpleLineSegment;
import bio.singa.mathematics.geometry.model.HorizontalPosition;
import bio.singa.mathematics.geometry.model.VerticalPosition;
import bio.singa.mathematics.vectors.Vector2D;

public class Rectangle extends VertexPolygon {

    private static final int topLeftVertexIndex = 0;
    private static final int topRightVertexIndex = 1;
    private static final int bottomRightVertexIndex = 2;
    private static final int bottomLeftVertexIndex = 3;

    public Rectangle(Vector2D topLeftVertex, Vector2D bottomRightVertex) {
        super(topLeftVertex, new Vector2D(bottomRightVertex.getX(), topLeftVertex.getY()), bottomRightVertex, new Vector2D(topLeftVertex.getX(), bottomRightVertex.getY()));
    }

    public Rectangle(double width, double height) {
        this(new Vector2D(0.0, 0.0), new Vector2D(width, height));
    }

    public LineSegment getTopEdge() {
        return new SimpleLineSegment(getTopLeftVertex(), getTopRightVertex());
    }

    public LineSegment getBottomEdge() {
        return new SimpleLineSegment(getBottomLeftVertex(), getBottomRightVertex());
    }

    public LineSegment getRightEdge() {
        return new SimpleLineSegment(getTopRightVertex(), getBottomRightVertex());
    }

    public LineSegment getLeftEdge() {
        return new SimpleLineSegment(getTopLeftVertex(), getBottomLeftVertex());
    }

    public Vector2D getTopLeftVertex() {
        return getVertex(topLeftVertexIndex);
    }

    public Vector2D getBottomRightVertex() {
        return getVertex(bottomRightVertexIndex);
    }

    public Vector2D getTopRightVertex() {
        return getVertex(topRightVertexIndex);
    }

    public Vector2D getBottomLeftVertex() {
        return getVertex(bottomLeftVertexIndex);
    }

    public Vector2D getVertex(HorizontalPosition horizontalPosition, VerticalPosition verticalPosition) {
        if (horizontalPosition == HorizontalPosition.Left) {
            if (verticalPosition == VerticalPosition.Top) {
                return getTopLeftVertex();
            } else {
                return getBottomLeftVertex();
            }
        } else {
            if (verticalPosition == VerticalPosition.Top) {
                return getTopRightVertex();
            } else {
                return getBottomRightVertex();
            }
        }
    }

    @Override
    public double getWidth() {
        return getTopRightVertex().getX() - getTopLeftVertex().getX();
    }

    @Override
    public double getHeight() {
        return getBottomLeftVertex().getY() - getTopLeftVertex().getY();
    }

    @Override
    public double getLeftMostXPosition() {
        return getTopLeftVertex().getX();
    }

    @Override
    public double getRightMostXPosition() {
        return getBottomRightVertex().getX();
    }

    @Override
    public double getTopMostYPosition() {
        return getTopLeftVertex().getY();
    }

    @Override
    public double getBottomMostYPosition() {
        return getBottomRightVertex().getY();
    }

    @Override
    public double getPerimeter() {
        return 2 * (getWidth() + getHeight());
    }

    @Override
    public double getArea() {
        return getWidth() * getHeight();
    }

    public Vector2D getCentre() {
        Vector2D topLeftVertex = getTopLeftVertex();
        Vector2D bottomRightVertex = getBottomRightVertex();
        return new Vector2D((topLeftVertex.getX() + bottomRightVertex.getX()) / 2.0, (topLeftVertex.getY() + bottomRightVertex.getY()) / 2.0);
    }

}
