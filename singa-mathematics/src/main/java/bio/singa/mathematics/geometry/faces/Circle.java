package bio.singa.mathematics.geometry.faces;

import bio.singa.mathematics.vectors.Vector2D;

import java.util.HashSet;
import java.util.Set;

public class Circle {

    private Vector2D midpoint;
    private double radius;

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


    public Set<Double> getXValue(double yValue) {
        Set<Double> values = new HashSet<>();
        double sqrt = Math.sqrt(-Math.pow(midpoint.getY(), 2) + 2 * midpoint.getY() * yValue + Math.pow(radius, 2) - Math.pow(yValue, 2));
        values.add(midpoint.getX() + sqrt);
        values.add(midpoint.getX() - sqrt);
        return values;
    }

    public Set<Double> getYValue(double xValue) {
        Set<Double> values = new HashSet<>();
        double sqrt = Math.sqrt(-Math.pow(midpoint.getX(), 2) + 2 * midpoint.getX() * xValue + Math.pow(radius, 2) - Math.pow(xValue, 2));
        values.add(midpoint.getY() + sqrt);
        values.add(midpoint.getY() - sqrt);
        return values;
    }

    public double getCircumference() {
        return Circles.circumference(radius);
    }

    public double getArea() {
        return Circles.area(radius);
    }

}
