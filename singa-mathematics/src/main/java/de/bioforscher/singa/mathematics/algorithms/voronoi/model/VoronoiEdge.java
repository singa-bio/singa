package de.bioforscher.singa.mathematics.algorithms.voronoi.model;


import de.bioforscher.singa.mathematics.vectors.Vector2D;

/**
 * The edges of voronoi diagrams. A left and right side is assigned to each edge, as well as its starting and ending
 * point. The direction of  the line is considered as follows (relative to the left side):
 * <pre>
 * upward: left.x < right.x
 * downward: left.x > right.x
 * horizontal: left.x == right.x
 * upward: left.x < right.x
 * rightward: left.y < right.y
 * leftward: left.y > right.y
 * vertical: left.y == right.y
 * </pre>
 */
public class VoronoiEdge {

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
        return this.leftSite;
    }

    /**
     * Returns the site to the right of this line.
     *
     * @return The site to the right of this line.
     */
    public SiteEvent getRightSite() {
        return this.rightSite;
    }

    /**
     * Returns the point where this line starts.
     *
     * @return The point where this line starts.
     */
    public Vector2D getStartingPoint() {
        return this.startingPoint;
    }

    /**
     * Sets the point where the line starts.
     *
     * @param startingPoint The point where the line starts.
     */
    void setStartingPoint(Vector2D startingPoint) {
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
        if (this.startingPoint == null && this.endingPoint == null) {
            this.startingPoint = startingPoint;
            this.leftSite = leftSite;
            this.rightSite = rightSite;
        } else if (this.leftSite.equals(rightSite)) {
            this.endingPoint = startingPoint;
        } else {
            this.startingPoint = startingPoint;
        }
    }

    /**
     * Returns the point where the line ends.
     * @return The point where the line ends.
     */
    public Vector2D getEndingPoint() {
        return this.endingPoint;
    }

    /**
     * Sets the point where the line ends.
     * @param endingPoint The point where the line ends.
     */
    void setEndingPoint(Vector2D endingPoint) {
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
        return "VoronoiEdge{" +
                "startingPoint=" + this.startingPoint +
                ", endingPoint=" + this.endingPoint +
                '}';
    }
}
