package bio.singa.mathematics.algorithms.voronoi.model;


import bio.singa.mathematics.geometry.edges.LineSegment;
import bio.singa.mathematics.vectors.Vector2D;

import java.util.StringJoiner;

/**
 * The edges of voronoi diagrams. A left and right side is assigned to each edge, as well as its starting and ending
 * point. The direction of  the line is considered as follows (relative to the left side):
 * <pre>
 * upward: left.x lt right.x
 * downward: left.x gt right.x
 * horizontal: left.x == right.x
 * upward: left.x lt right.x
 * rightward: left.y lt right.y
 * leftward: left.y gt right.y
 * vertical: left.y == right.y
 * </pre>
 */
public class VoronoiEdge implements LineSegment {

    /**
     * The site to the left of this line.
     */
    private SiteEvent leftSite;

    /**
     * The site to the right of this line.
     */
    private SiteEvent rightSite;

    /**
     * The starting point of this line.
     */
    private Vector2D startingPoint;

    /**
     * The ending point of this line.
     */
    private Vector2D endingPoint;

    /**
     * Creates a new voronoi edge.
     *
     * @param leftSite The site to the left of this line.
     * @param rightSite The site to the right of this line.
     */
    VoronoiEdge(SiteEvent leftSite, SiteEvent rightSite) {
        this.leftSite = leftSite;
        this.rightSite = rightSite;
    }

    /**
     * Returns the site to the left of this line.
     *
     * @return The site to the left of this line.
     */
    public SiteEvent getLeftSite() {
        return leftSite;
    }

    public void setLeftSite(SiteEvent leftSite) {
        this.leftSite = leftSite;
    }

    public void setRightSite(SiteEvent rightSite) {
        this.rightSite = rightSite;
    }

    /**
     * Returns the site to the right of this line.
     *
     * @return The site to the right of this line.
     */
    public SiteEvent getRightSite() {
        return rightSite;
    }

    /**
     * Returns the point where this line starts.
     *
     * @return The point where this line starts.
     */
    public Vector2D getStartingPoint() {
        return startingPoint;
    }

    /**
     * Sets the point where the line starts.
     *
     * @param startingPoint The point where the line starts.
     */
    public void setStartingPoint(Vector2D startingPoint) {
        this.startingPoint = startingPoint;
    }

    /**
     * Sets the point where the line starts.
     *
     * @param leftSite The site event left of this line.
     * @param rightSite The site event right of this line.
     * @param startingPoint The starting point.
     */
    void setStartingPoint(SiteEvent leftSite, SiteEvent rightSite, Vector2D startingPoint) {
        if (this.startingPoint == null && endingPoint == null) {
            this.startingPoint = startingPoint;
            this.leftSite = leftSite;
            this.rightSite = rightSite;
        } else if (this.leftSite.equals(rightSite)) {
            endingPoint = startingPoint;
        } else {
            this.startingPoint = startingPoint;
        }
    }

    /**
     * Returns the point where the line ends.
     * @return The point where the line ends.
     */
    public Vector2D getEndingPoint() {
        return endingPoint;
    }

    /**
     * Sets the point where the line ends.
     * @param endingPoint The point where the line ends.
     */
    public void setEndingPoint(Vector2D endingPoint) {
        this.endingPoint = endingPoint;
    }

    /**
     * Sets the point where the line ends.
     *
     * @param leftSite The site event left of this line.
     * @param rightSite The site event right of this line.
     * @param endingPoint The ending point.
     */
    void setEndingPoint(SiteEvent leftSite, SiteEvent rightSite, Vector2D endingPoint) {
        setStartingPoint(rightSite, leftSite, endingPoint);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", VoronoiEdge.class.getSimpleName() + "[", "]")
                .add("startingPoint=" + startingPoint)
                .add("endingPoint=" + endingPoint)
                .toString();
    }

}
