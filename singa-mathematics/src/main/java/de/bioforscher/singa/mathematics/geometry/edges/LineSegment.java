package de.bioforscher.singa.mathematics.geometry.edges;

import de.bioforscher.singa.mathematics.geometry.faces.Circle;
import de.bioforscher.singa.mathematics.vectors.Vector2D;

import java.util.HashSet;
import java.util.Set;

/**
 * @author cl
 */
public interface LineSegment {

    /**
     * Returns the starting point.
     *
     * @return The starting point.
     */
    Vector2D getStartingPoint();

    /**
     * Returns the ending point.
     *
     * @return The ending point.
     */
    Vector2D getEndingPoint();

    /**
     * Sets the point where the line starts.
     *
     * @param startingPoint The point where the line starts.
     */
    void setStartingPoint(Vector2D startingPoint);

    /**
     * Sets the point where the line ends.
     *
     * @param endingPoint The point where the line ends.
     */
    void setEndingPoint(Vector2D endingPoint);

    default double getLength() {
        return getStartingPoint().distanceTo(getEndingPoint());
    }

    default boolean isOnLine(Vector2D vector) {
        return Math.min(getStartingPoint().getX(), getEndingPoint().getX()) <= vector.getX()
                && Math.max(getStartingPoint().getX(), getEndingPoint().getX()) >= vector.getX()
                && Math.min(getStartingPoint().getY(), getEndingPoint().getY()) <= vector.getY()
                && Math.max(getStartingPoint().getY(), getEndingPoint().getY()) >= vector.getY();

    }

    default Set<Vector2D> intersectionsWith(Circle circle) {
        Set<Vector2D> intersections = new HashSet<>();
        // see http://mathworld.wolfram.com/Circle-LineIntersection.html
        // transform line points, such that circle is at origin to origin
        Vector2D end = getEndingPoint().subtract(circle.getMidpoint());
        Vector2D start = getStartingPoint().subtract(circle.getMidpoint());
        double dx = end.getX() - start.getX();
        double dy = end.getY() - start.getY();
        double drSquared = Math.pow(dx, 2) + Math.pow(dy, 2);
        double d = start.getX() * end.getY() - end.getX() * start.getY();
        double discriminant = Math.pow(circle.getRadius(), 2) * drSquared - Math.pow(d, 2);
        if (discriminant >= 0) {
            double discriminantRt = Math.sqrt(discriminant);
            // two intersections
            // x positions
            double termX1 = d * dy;
            double termX2 = Math.copySign(1.0, dy) * dx * discriminantRt;
            double xPlus = (termX1 + termX2) / drSquared;
            double xMinus = (termX1 - termX2) / drSquared;
            // y positions
            double termY1 = -d * dx;
            double termY2 = Math.abs(dy) * discriminantRt;
            double yPlus = (termY1 + termY2) / drSquared;
            double yMinus = (termY1 - termY2) / drSquared;
            // add to intersections
            Vector2D first = new Vector2D(xPlus, yPlus).add(circle.getMidpoint());
            Vector2D second = new Vector2D(xMinus, yMinus).add(circle.getMidpoint());

            if (isOnLine(first)) {
                intersections.add(first);
            }
            if (isOnLine(second)) {
                intersections.add(second);
            }
        }
        return intersections;
    }


}
