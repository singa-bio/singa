package de.bioforscher.mathematics.geometry.faces;

import de.bioforscher.mathematics.geometry.model.HorizontalPosition;
import de.bioforscher.mathematics.geometry.model.VerticalPosition;
import de.bioforscher.mathematics.vectors.Vector2D;

public class Rectangle extends SimplePolygon {

    private static int topLeftVertexIndex = 0;
    private static int bottomRightVertexIndex = 1;

    public Rectangle(Vector2D topLeftVertex, Vector2D bottomRightVertex) {
        super(topLeftVertex, bottomRightVertex);
    }

    public Rectangle(double width, double height) {
        this(new Vector2D(0.0, height), new Vector2D(width, 0.0));
    }

    public Vector2D getTopLeftVertex() {
        return getVertex(topLeftVertexIndex);
    }

    public Vector2D getBottomRightVertex() {
        return getVertex(bottomRightVertexIndex);
    }

    public Vector2D getTopRightVertex() {
        return new Vector2D(getTopLeftVertex().getX(), getBottomRightVertex().getY());
    }

    public Vector2D getBottomLeftVertex() {
        return new Vector2D(getBottomRightVertex().getX(), getTopLeftVertex().getY());
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
        return getTopLeftVertex().distanceTo(getTopRightVertex());
    }

    @Override
    public double getHeight() {
        return getTopLeftVertex().distanceTo(getBottomLeftVertex());
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

}
