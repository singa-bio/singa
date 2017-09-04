package de.bioforscher.singa.mathematics.geometry.edges;

import de.bioforscher.singa.mathematics.metrics.model.VectorMetricProvider;
import de.bioforscher.singa.mathematics.vectors.Vector2D;

/**
 * A line segment is a part of a line that is bounded by two distinct points and
 * "contains" every point on the line between and including its end points.
 *
 * @author cl
 */
public class LineSegment extends Line {

    private final Vector2D startingPoint;
    private final Vector2D endingPoint;

    /**
     * Creates a new LineSegment between the starting point p1 = (x1, y1) and ending point p2 = (x2, y2).
     *
     * @param x1 The x-value of the first point.
     * @param y1 The y-value of the first point.
     * @param x2 The x-value of the second point.
     * @param y2 The y-value of the second point.
     */
    public LineSegment(double x1, double y1, double x2, double y2) {
        this(new Vector2D(x1, y1), new Vector2D(x2, y2));
    }

    /**
     * Creates a new LineSegment between the start and ending point.
     *
     * @param start The starting point.
     * @param end The ending point.
     */
    public LineSegment(Vector2D start, Vector2D end) {
        super(start, end);
        this.startingPoint = start;
        this.endingPoint = end;
    }

    /**
     * Returns the starting point.
     * @return The starting point.
     */
    public Vector2D getStartingPoint() {
        return this.startingPoint;
    }

    /**
     * Returns the ending point.
     * @return The ending point.
     */
    public Vector2D getEndingPoint() {
        return this.endingPoint;
    }

    /**
     * Gets the x-value in respect to a given y-value. If the line segment does
     * not contain a point with this y-value this method returns {@link Double#NaN}.
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
     * intercept the x-axis this method returns {@link Double#NaN}.
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
     * not contain a point with this x-value this method returns {@link Double#NaN}.
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
     * intercept the y-axis this method returns {@link Double#NaN}.
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

    /**
     * Returns a new LineSegment parallel to this one, separated by the given
     * ({@link VectorMetricProvider#EUCLIDEAN_METRIC Euclidean}-)distance.
     * Negative distances return lines below, respectively left of this line and positive distances vice versa.
     * @param distance The offset distance of the new parallel line.
     * @return A new line parallel to this one.
     */
    public LineSegment getParallelSegment(double distance) {
        if (isHorizontal()) {
            return new LineSegment(this.startingPoint.getX(), this.startingPoint.getY() + distance,
                    this.endingPoint.getX(), this.endingPoint.getY()+distance);
        } else if (isVertical()) {
            return new LineSegment(this.startingPoint.getX() + distance, this.startingPoint.getY(),
                    this.endingPoint.getX() + distance, this.endingPoint.getY());
        } else {
            Line parallel = getParallel(distance);
            Line perpendicularStart = new Line(this.startingPoint, parallel.getPerpendicularSlope());
            Line perpendicularEnd = new Line(this.endingPoint, parallel.getPerpendicularSlope());
            return new LineSegment(parallel.getInterceptWithLine(perpendicularStart),
                    parallel.getInterceptWithLine(perpendicularEnd));
        }
    }

    /**
     * Creates a new line using the attributes of this line segment.
     * @return A new line using the attributes of this line segment.
     */
    public Line getLineRepresentation() {
        return new Line(super.getYIntercept(), getSlope());
    }

    /**
     * Returns a new Line that passes through the middle of this segment with a perpendicular slope.
     * @return A new perpendicular bisecting Line.
     * @see <a href="https://en.wikipedia.org/wiki/Bisection#Line_segment_bisector">Wikipedia: Bisection</a>
     */
    public Line calculatePerpendicularBisector() {
        Vector2D midAB = getStartingPoint().getMidpointTo(getEndingPoint());
        double slope = getPerpendicularSlope();
        double yIntercept = midAB.getY() - slope * midAB.getX();
        return new Line(midAB, new Vector2D(0, yIntercept));
    }

    /**
     * Returns the length of this segment (i.e. the euclidean distance between start and endpoint).
     * @return The length.
     */
    public double getLength() {
        return VectorMetricProvider.EUCLIDEAN_METRIC.calculateDistance(this.startingPoint, this.endingPoint);
    }

}
