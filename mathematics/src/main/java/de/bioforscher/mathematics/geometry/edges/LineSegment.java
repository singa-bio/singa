package de.bioforscher.mathematics.geometry.edges;

import de.bioforscher.mathematics.vectors.Vector2D;

/**
 * A line segment is a part of a line that is bounded by two distinct points and
 * "contains" every point on the line between and including its end points.
 *
 * @author Christoph Leberecht
 * @version 2.0.0
 */
public class LineSegment extends Line {

    private final Vector2D startingPoint;
    private final Vector2D endingPoint;

    public LineSegment(double x1, double y1, double x2, double y2) {
        this(new Vector2D(x1, y2), new Vector2D(x2, y2));
    }

    public LineSegment(Vector2D start, Vector2D end) {
        super(start, end);
        this.startingPoint = start;
        this.endingPoint = end;
    }

    public Vector2D getStartingPoint() {
        return this.startingPoint;
    }

    public Vector2D getEndingPoint() {
        return this.endingPoint;
    }

    /**
     * Gets the x-value in respect to a given y-value. If the line segment does
     * not contain a point with this y-value this method returns
     * {@code Double.NaN}.
     *
     * @param y The y-value.
     * @return The x-value.
     */
    @Override
    public double getXValue(double y) {
        if (y > getStartingPoint().getY() && y < getEndingPoint().getY()
                || y > getEndingPoint().getY() && y < getStartingPoint().getY()) {
            return super.getXValue(y);
        } else {
            return Double.NaN;
        }
    }

    /**
     * Gets the y-intercept of the line segment. If the line segment does not
     * intercept the x-axis this method returns {@code Double.NaN}.
     *
     * @return The y-intercept of the line segment.
     */
    @Override
    public double getXIntercept() {
        if (getStartingPoint().getX() < 0 ^ getEndingPoint().getX() < 0) {
            return super.getXIntercept();
        } else {
            return Double.NaN;
        }
    }

    /**
     * Gets the y-value in respect to a given x-value. If the line segment does
     * not contain a point with this x-value this method returns
     * {@code Double.NaN}.
     *
     * @param x The x-value.
     * @return The y-value.
     */
    @Override
    public double getYValue(double x) {
        if (x > getStartingPoint().getX() && x < getEndingPoint().getX()
                || x > getEndingPoint().getX() && x < getStartingPoint().getX()) {
            return super.getYValue(x);
        } else {
            return Double.NaN;
        }
    }

    /**
     * Gets the x-intercept of the line segment. If the line segment does not
     * intercept the y-axis this method returns {@code Double.NaN}.
     *
     * @return The x-intercept of the line segment.
     */
    @Override
    public double getYIntercept() {
        if (getStartingPoint().getY() < 0 ^ getEndingPoint().getY() < 0) {
            return super.getYIntercept();
        } else {
            return Double.NaN;
        }
    }

    public Line calculatePerpendicularBisector() {
        Vector2D midAB = getStartingPoint().getMidpointTo(getEndingPoint());
        double slope = -1 / this.getSlope();
        double yIntercept = midAB.getY() - slope * midAB.getX();
        return new Line(midAB, new Vector2D(0, yIntercept));
    }

}
