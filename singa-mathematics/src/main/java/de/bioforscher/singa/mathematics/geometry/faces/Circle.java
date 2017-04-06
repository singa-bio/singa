package de.bioforscher.singa.mathematics.geometry.faces;

import de.bioforscher.singa.mathematics.geometry.edges.Line;
import de.bioforscher.singa.mathematics.geometry.edges.LineSegment;
import de.bioforscher.singa.mathematics.vectors.Vector2D;

public class Circle {

    private Vector2D midpoint;
    private double radius;

    public Vector2D getMidpoint() {
        return this.midpoint;
    }

    public void setMidpoint(Vector2D midpoint) {
        this.midpoint = midpoint;
    }

    public double getRadius() {
        return this.radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    /**
     * Circumscribed circle from 3 points (e.g. triangle)
     *
     * @param firstPoint The first point of the triangle.
     * @param secondPoint The second point of the triangle.
     * @param thirdPoint The third point of the triangle.
     */
    public Circle(Vector2D firstPoint, Vector2D secondPoint, Vector2D thirdPoint) {

        LineSegment abLine = new LineSegment(firstPoint, secondPoint);
        Line abBisect = abLine.calculatePerpendicularBisector();

        LineSegment acLine = new LineSegment(firstPoint, thirdPoint);
        Line acBisect = acLine.calculatePerpendicularBisector();

        this.midpoint = abBisect.getInterceptWithLine(acBisect);

        this.radius = this.midpoint.distanceTo(firstPoint);

    }

}
