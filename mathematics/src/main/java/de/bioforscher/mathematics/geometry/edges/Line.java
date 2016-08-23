package de.bioforscher.mathematics.geometry.edges;

import de.bioforscher.mathematics.vectors.Vector2D;

/**
 * A line is a straight one-dimensional figure having no thickness and extending
 * infinitely in both directions. It is uniquely defined by two distinct points
 * or a single point and its slope.
 *
 * @author Christoph Leberecht
 * @version 2.0.0
 * @see <a href="https://en.wikipedia.org/wiki/Line_(geometry)">Wikipedia: Line (geometry)</a>
 */
@SuppressWarnings("SuspiciousNameCombination")
public class Line {

    private final double yIntercept;
    private final double slope;

    public Line(double yIntercept, double slope) {
        if (Double.isNaN(yIntercept)) {
            throw new IllegalArgumentException("Unable to create a new line with Double.NaN as intercept.");
        } else if (Double.isNaN(slope)) {
            throw new IllegalArgumentException("Unable to create a new line with Double.NaN as slope.");
        }
        this.yIntercept = yIntercept;
        this.slope = slope;
    }

    /**
     * Creates a new Point from a strut point and the slope;
     *
     * @param strutPoint
     * @param slope
     */
    public Line(Vector2D strutPoint, double slope) {
        if (!Double.isInfinite(slope)) {
            // classical line
            this.yIntercept = calculateYIntercept(strutPoint, slope);
            this.slope = slope;
        } else {
            // this is a vertical line
            this.yIntercept = strutPoint.getX();
            this.slope = slope;
        }
    }

    /**
     * Creates a new Line from two points p1 = (x1, y1) and p2 = (x2, y2).
     *
     * @param x1 The x-value of the first point.
     * @param y1 The y-value of the first point.
     * @param x2 The x-value of the second point.
     * @param y2 The y-value of the second point.
     */
    public Line(double x1, double y1, double x2, double y2) {
        this(new Vector2D(x1, y1), new Vector2D(x2, y2));
    }

    /**
     * Creates a new Line from two points.
     *
     * @param first The first point.
     * @param second The second point.
     */
    public Line(Vector2D first, Vector2D second) {
        if (!first.equals(second)) {
            this.slope = calculateSlope(first, second);
            this.yIntercept = calculateYIntercept(first, this.slope);
        } else {
            throw new IllegalArgumentException("Unable to create line from two identical points: " + first + " and "
                    + second + ".");
        }

    }

    /**
     * Gets the slope or gradient of the line, calculated by m = (y2 - y1)/(x2 -
     * x1).
     *
     * @return The slope.
     */
    public static double calculateSlope(Vector2D first, Vector2D second) {
        if (first.getX() == second.getX()) {
            return Double.POSITIVE_INFINITY;
        }
        return (second.getY() - first.getY()) / (second.getX() - first.getX());
    }

    /**
     * Gets the y-intercept of the equation that is defined by the two points of
     * the line segment.
     *
     * @return The y-intercept of the equation of the line segment.
     */
    public static double calculateYIntercept(Vector2D first, double slope) {
        return first.getY() - first.getX() * slope;
    }

    public double getSlope() {
        return this.slope;
    }

    /**
     * Gets the x-intercept of the line.
     *
     * @return The x-intercept of the equation of the line segment.
     */
    public double getXIntercept() {
        if (!Double.isInfinite(this.slope)) {
            // this is a classical line
            return -this.getYIntercept() / this.getSlope();
        } else {
            // this is a vertical line
            return this.yIntercept;
        }
    }

    /**
     * Gets the x-value in respect to a given y-value.
     *
     * @param y The y-value.
     * @return The x-value.
     */
    public double getXValue(double y) {
        return (y - this.getYIntercept()) / this.getSlope();
    }

    /**
     * Gets the y-intercept of the equation that is defined by the two points of
     * the line segment.
     *
     * @return The y-intercept of the equation of the line segment.
     */
    public double getYIntercept() {
        if (!Double.isInfinite(this.slope)) {
            // this is a classical line
            return this.yIntercept;
        } else {
            // this is a vertical line
            return Double.NaN;
        }
    }

    /**
     * Gets the y-value in respect to a given x-value.
     *
     * @param x The x-value.
     * @return The y-value.
     */
    public double getYValue(double x) {
        return this.slope * x + this.yIntercept;
    }

    /**
     * Returns the angle to the x-axis in radians.
     *
     * @return he angle to the x-axis in radians.
     */
    public double getAngleToXAxis() {
        return Math.atan(this.slope / 1);
    }

    public double getPerpendicularSlope() {
        return -1 / this.slope;
    }

    public Line getParallel(double distance) {
        // perpendicular angle (in rad)
        if (this.slope == 0.0) {
            // trivial case (horizontal line)
            return new Line(this.yIntercept + distance, this.slope);
        } else if (Double.isInfinite(this.slope)) {
            // trivial case (vertical line)
            return new Line(new Vector2D(this.yIntercept + distance, 0), Double.POSITIVE_INFINITY);
        } else {
            // calculate new parallel line
            return new Line(this.yIntercept + distance * Math.sqrt(1 + this.slope * this.slope), this.slope);
        }

    }

    /**
     * Gets the intercept with another line.
     *
     * @param line
     * @return
     */
    public Vector2D getInterceptWithLine(Line line) {
        final double a = this.slope;
        final double b = line.getSlope();
        final double c = this.yIntercept;
        final double d = line.getYIntercept();
        return new Vector2D((d - c) / (a - b), (a * d - b * c) / (a - b));
    }

    public Vector2D mirrorVector(Vector2D originalVector) {
        double d = (originalVector.getX() + (originalVector.getY() - this.getYIntercept())*this.getSlope())
                 / (1 + this.getSlope()*this.getSlope());
        return new Vector2D(2*d-originalVector.getX(), 2*d*this.getSlope() - originalVector.getY()+2*this.yIntercept);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(this.slope);
        result = prime * result + (int) (temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.yIntercept);
        result = prime * result + (int) (temp ^ temp >>> 32);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Line other = (Line) obj;
        if (Double.doubleToLongBits(this.slope) != Double.doubleToLongBits(other.slope)) {
            return false;
        }
        return Double.doubleToLongBits(this.yIntercept) == Double.doubleToLongBits(other.yIntercept);
    }

    @Override
    public String toString() {
        return "Line [yIntercept=" + this.yIntercept + ", slope=" + this.slope + "]";
    }

}
