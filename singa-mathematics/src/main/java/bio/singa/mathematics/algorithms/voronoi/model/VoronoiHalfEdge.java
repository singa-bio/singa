package bio.singa.mathematics.algorithms.voronoi.model;

import bio.singa.mathematics.vectors.Vector2D;

import java.util.StringJoiner;

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
        site = lSite;
        this.edge = edge;
        // 'angle' is a value to be used for properly sorting the
        // halfsegments counterclockwise. By convention, we will
        // use the angle of the line defined by the 'site to the left'
        // to the 'site to the right'.
        // However, border edges have no 'site to the right': thus we
        // use the angle of line perpendicular to the halfsegment (the
        // edge should have both end points defined in such case.)
        if (rSite != null) {
            angle = Math.atan2(rSite.getY() - lSite.getY(), rSite.getX() - lSite.getX());
        } else {
            Vector2D startingPoint = edge.getStartingPoint();
            Vector2D endingPoint = edge.getEndingPoint();
            if (edge.getLeftSite().equals(lSite)) {
                angle = Math.atan2(endingPoint.getX() - startingPoint.getX(), startingPoint.getY() - endingPoint.getY());
            } else {
                angle = Math.atan2(startingPoint.getX() - endingPoint.getX(), endingPoint.getY() - startingPoint.getY());
            }
        }
    }

    public VoronoiHalfEdge invert() {
        if (edge.getLeftSite() != null && edge.getRightSite() != null) {
            return new VoronoiHalfEdge(edge, edge.getLeftSite(), edge.getRightSite());
        }
        if (edge.getRightSite() == null) {
            Vector2D previousStart = edge.getStartingPoint();
            Vector2D previousEnd = edge.getEndingPoint();
            edge.setStartingPoint(previousEnd);
            edge.setEndingPoint(previousStart);
            return new VoronoiHalfEdge(edge, edge.getLeftSite(), edge.getRightSite());
        }
        throw new IllegalArgumentException("Cannot create half edge from edge with no set sides.");
    }

    /**
     * Returns the point where this edge starts.
     *
     * @return The point where this edge starts.
     */
    public Vector2D getStartPoint() {
        if (edge.getLeftSite().equals(site)) {
            return edge.getStartingPoint();
        }
        return edge.getEndingPoint();
    }

    /**
     * Returns the point where this edge ends.
     *
     * @return The point where this edge ends.
     */
    public Vector2D getEndPoint() {
        if (edge.getLeftSite().equals(site)) {
            return edge.getEndingPoint();
        }
        return edge.getStartingPoint();
    }

    /**
     * Returns the site associated to this half edge.
     *
     * @return The site associated to this half edge.
     */
    public SiteEvent getSite() {
        return site;
    }

    /**
     * The full edge corresponding to this edge.
     *
     * @return The full edge corresponding to this edge.
     */
    public VoronoiEdge getEdge() {
        return edge;
    }

    /**
     * Returns the angle in relation to the starting point of the edge.
     *
     * @return The angle in relation to the starting point of the edge.
     */
    public double getAngle() {
        return angle;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", VoronoiHalfEdge.class.getSimpleName() + "[", "]")
                .add("site=" + site)
                .add("start=" + getStartPoint())
                .add("end=" + getEndPoint())
                .toString();
    }
}
