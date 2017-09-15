package de.bioforscher.singa.mathematics.algorithms.voronoi.model;


import de.bioforscher.singa.mathematics.vectors.Vector2D;

/**
 * The edge in the voronoi diagram. It contains a left and right side. as well as its starting and ending point. The
 * direction of  the line is considered as follows (relative to the left side):
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
     * @param leftSite The site to the left of this line.
     * @param rightSite The site to the right of this line.
     */
    VoronoiEdge(SiteEvent leftSite, SiteEvent rightSite) {
        this.leftSite = leftSite;
        this.rightSite = rightSite;
    }

    /**
     * Returns the site to the left of this line.
     * @return The site to the left of this line.
     */
    public SiteEvent getLeftSite() {
        return this.leftSite;
    }

    /**
     * Returns the site to the right of this line.
     * @return The site to the right of this line.
     */
    public SiteEvent getRightSite() {
        return this.rightSite;
    }

    /**
     * Returns the point where this line starts.
     * @return The point where this line starts.
     */
    public Vector2D getStartingPoint() {
        return this.startingPoint;
    }

    void setStartingPoint(Vector2D startingPoint) {
        this.startingPoint = startingPoint;
    }

    void setStartingPoint(SiteEvent lSite, SiteEvent rSite, Vector2D vertex) {
        if (this.startingPoint == null && this.endingPoint == null) {
            this.startingPoint = vertex;
            this.leftSite = lSite;
            this.rightSite = rSite;
        } else if (this.leftSite.equals(rSite)) {
            this.endingPoint = vertex;
        } else {
            this.startingPoint = vertex;
        }
    }

    public Vector2D getEndingPoint() {
        return this.endingPoint;
    }

    void setEndingPoint(Vector2D endingPoint) {
        this.endingPoint = endingPoint;
    }

    void setEndingPoint(SiteEvent lSite, SiteEvent rSite, Vector2D vertex) {
        setStartingPoint(rSite, lSite, vertex);
    }

    @Override
    public String toString() {
        return "VoronoiEdge{" +
                "startingPoint=" + this.startingPoint +
                ", endingPoint=" + this.endingPoint +
                '}';
    }
}
