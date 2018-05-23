package de.bioforscher.singa.mathematics.geometry.faces;

import de.bioforscher.singa.mathematics.geometry.edges.Line;
import de.bioforscher.singa.mathematics.geometry.edges.SimpleLineSegment;
import de.bioforscher.singa.mathematics.vectors.Vector2D;

public class Circle {

    private Vector2D midpoint;
    private double radius;

    /**
     * Circumscribed circle from 3 points (e.g. triangle)
     *
     * @param firstPoint The first point of the triangle.
     * @param secondPoint The second point of the triangle.
     * @param thirdPoint The third point of the triangle.
     */
    public Circle(Vector2D firstPoint, Vector2D secondPoint, Vector2D thirdPoint) {

        SimpleLineSegment abLine = new SimpleLineSegment(firstPoint, secondPoint);
        Line abBisect = abLine.calculatePerpendicularBisector();

        SimpleLineSegment acLine = new SimpleLineSegment(firstPoint, thirdPoint);
        Line acBisect = acLine.calculatePerpendicularBisector();

        midpoint = abBisect.getInterceptWithLine(acBisect);

        radius = midpoint.distanceTo(firstPoint);

    }

    public Circle(Vector2D midpoint, double radius) {
        this.midpoint = midpoint;
        this.radius = radius;
    }

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

    public double arcLengthBetweenPoints(Vector2D first, Vector2D second) {
        final double rsq = 2.0 * radius * radius;
        // double angle = Math.acos((rsq - Math.pow(first.getX() - second.getX(), 2) + Math.pow(first.getY() - second.getY(), 2)) / rsq);
        double angle = first.angleTo(second);
        return angle*Math.PI*radius;
    }

    public double getCircumference() {
        return 2.0 * radius * Math.PI;
    }

}
