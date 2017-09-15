package de.bioforscher.singa.mathematics.algorithms.voronoi.model;

import de.bioforscher.singa.mathematics.vectors.Vector2D;

public class VoronoiHalfEdge {

    private SiteEvent site;
    private VoronoiEdge edge;
    private double angle;

    public VoronoiHalfEdge(VoronoiEdge edge, SiteEvent lSite, SiteEvent rSite) {
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
            this.angle = Math.atan2(rSite.getY()-lSite.getY(), rSite.getX()-lSite.getX());
        } else {
            Vector2D va = edge.getStartingPoint();
            Vector2D vb = edge.getEndingPoint();
            if (edge.getLeftSite().equals(lSite)) {
                this.angle = Math.atan2(vb.getX()-va.getX(), va.getY()-vb.getY());
            } else {
                this.angle = Math.atan2(va.getX()-vb.getX(), vb.getY()-va.getY());
            }
        }
    }

    public Vector2D getStartPoint() {
        if (this.edge.getLeftSite().equals(this.site)) {
            return this.edge.getStartingPoint();
        } else {
            return this.edge.getEndingPoint();
        }
    }

    public Vector2D getEndPoint() {
        if (this.edge.getLeftSite().equals(this.site)) {
            return this.edge.getEndingPoint();
        } else {
            return this.edge.getStartingPoint();
        }
    }

    public SiteEvent getSite() {
        return this.site;
    }

    public void setSite(SiteEvent site) {
        this.site = site;
    }

    public VoronoiEdge getEdge() {
        return this.edge;
    }

    public void setEdge(VoronoiEdge edge) {
        this.edge = edge;
    }

    public double getAngle() {
        return this.angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }
}
