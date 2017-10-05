package de.bioforscher.singa.mathematics.algorithms.voronoi.model;

import de.bioforscher.singa.mathematics.vectors.Vector2D;

/**
 * Each edge can be represented by two directed opposed half edges. Each edge holds a reference to its original site
 * event its corresponding edge and its angle.
 */
public class VoronoiHalfEdge {

    /**
     * The original site event, this half edge belongs to.
     */
    private final SiteEvent site;

    /**
     * The "non-half" version of this edge.
     */
    private final VoronoiEdge edge;

    /**
     * The angle in relation to the starting point of the edge.
     */
    private final double angle;

    /**
     * Creates a half edge.
     *
     * @param edge The the edge this half edge is part of.
     * @param lSite The site to the left of this edge.
     * @param rSite The right site of this edge.
     */
    VoronoiHalfEdge(VoronoiEdge edge, SiteEvent lSite, SiteEvent rSite) {
        this.site = lSite;
        this.edge = edge;
        // 'angle' is a value to be used for properly sorting the
        // halfsegments counterclockwise. By convention, we will
        // use the angle of the line defined by the 'site to the left'
        // to the 'site to the right'.
        // However, border edges have no 'site to the right': thus we
        // use the angle of line perpendicular to the halfsegment (the
        // edge should have both end points defined in such case.)
        if (rSite != null) {
            this.angle = Math.atan2(rSite.getY() - lSite.getY(), rSite.getX() - lSite.getX());
        } else {
            Vector2D startingPoint = edge.getStartingPoint();
            Vector2D endingPoint = edge.getEndingPoint();
            if (edge.getLeftSite().equals(lSite)) {
                this.angle = Math.atan2(endingPoint.getX() - startingPoint.getX(), startingPoint.getY() - endingPoint.getY());
            } else {
                this.angle = Math.atan2(startingPoint.getX() - endingPoint.getX(), endingPoint.getY() - startingPoint.getY());
            }
        }
    }

    /**
     * Returns the point where this edge starts.
     *
     * @return The point where this edge starts.
     */
    public Vector2D getStartPoint() {
        if (this.edge.getLeftSite().equals(this.site)) {
            return this.edge.getStartingPoint();
        } else {
            return this.edge.getEndingPoint();
        }
    }

    /**
     * Returns the point where this edge ends.
     *
     * @return The point where this edge ends.
     */
    public Vector2D getEndPoint() {
        if (this.edge.getLeftSite().equals(this.site)) {
            return this.edge.getEndingPoint();
        } else {
            return this.edge.getStartingPoint();
        }
    }

    /**
     * Returns the site associated to this half edge.
     *
     * @return The site associated to this half edge.
     */
    public SiteEvent getSite() {
        return this.site;
    }

    /**
     * The full edge corresponding to this edge.
     *
     * @return The full edge corresponding to this edge.
     */
    public VoronoiEdge getEdge() {
        return this.edge;
    }

    /**
     * Returns the angle in relation to the starting point of the edge.
     *
     * @return The angle in relation to the starting point of the edge.
     */
    public double getAngle() {
        return this.angle;
    }



}
