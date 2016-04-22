package de.bioforscher.mathematics.geometry.faces;

import de.bioforscher.mathematics.geometry.edges.Line;
import de.bioforscher.mathematics.geometry.edges.LineSegment;
import de.bioforscher.mathematics.vectors.Vector2D;

public class Circle {

    private Vector2D midpoint;
    private double radius;

    public Vector2D getMidpoint() {
        return midpoint;
    }

    public void setMidpoint(Vector2D midpoint) {
        this.midpoint = midpoint;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    /**
     * Circumscribed circle from 3 points (e.g. triangle)
     *
     * @param a
     * @param b
     * @param c
     */
    public Circle(Vector2D a, Vector2D b, Vector2D c) {

        LineSegment abLine = new LineSegment(a, b);
        Line abBisect = abLine.calculatePerpendicularBisector();

        LineSegment acLine = new LineSegment(a, c);
        Line acBisect = acLine.calculatePerpendicularBisector();

        this.midpoint = abBisect.getInterceptWithLine(acBisect);

        this.radius = this.midpoint.distanceTo(a);

    }

}
