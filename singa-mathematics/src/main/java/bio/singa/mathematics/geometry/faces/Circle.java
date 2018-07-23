package bio.singa.mathematics.geometry.faces;

import bio.singa.mathematics.geometry.edges.Line;
import bio.singa.mathematics.geometry.edges.SimpleLineSegment;
import bio.singa.mathematics.metrics.model.VectorMetricProvider;
import bio.singa.mathematics.vectors.Vector2D;

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
        Line abBisect = abLine.getPerpendicularBisector();

        SimpleLineSegment acLine = new SimpleLineSegment(firstPoint, thirdPoint);
        Line acBisect = acLine.getPerpendicularBisector();

        midpoint = abBisect.getIntersectWithLine(acBisect);

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

    /**
     * Returns the angle between two vectors that are projected on the circle circumference.
     *
     * @param first The first vector.
     * @param second The second vector.
     * @return The angle in radians.
     */
    public double getCentralAngleBetween(Vector2D first, Vector2D second) {
        // https://math.stackexchange.com/questions/185829/how-do-you-find-an-angle-between-two-points-on-the-edge-of-a-circle
        double rSq = radius * radius * 2.0;
        double c = VectorMetricProvider.EUCLIDEAN_METRIC.calculateDistance(first, second);
        // in rad for angle multiply by (180/Math.PI)
        return Math.acos((rSq - c * c)/rSq);
    }

    public double getArcLengthBetween(Vector2D first, Vector2D second) {
        return Math.abs(getCentralAngleBetween(first, second) * radius);
    }

    public double getCircumference() {
        return 2.0 * radius * Math.PI;
    }

}
