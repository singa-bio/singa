package de.bioforscher.singa.javafx.voronoi.representation;

import de.bioforscher.singa.javafx.voronoi.Site;

public class HalfEdge {

    private Site site;
    private Edge edge;
    private double angle;

    public HalfEdge(Edge edge, Site lSite, Site rSite) {
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
            Site va = edge.getVa();
            Site vb = edge.getVb();
            if (edge.getlSite().equals(lSite)) {
                this.angle = Math.atan2(vb.getX()-va.getX(), va.getY()-vb.getY());
            } else {
                this.angle = Math.atan2(va.getX()-vb.getX(), vb.getY()-va.getY());
            }
        }
    }

    public Site getStartPoint() {
        if (this.edge.getlSite().equals(this.site)) {
            return this.edge.getVa();
        } else {
            return this.edge.getVb();
        }
    }

    public Site getEndPoint() {
        if (this.edge.getlSite().equals(this.site)) {
            return this.edge.getVb();
        } else {
            return this.edge.getVa();
        }
    }

    public Site getSite() {
        return this.site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public Edge getEdge() {
        return this.edge;
    }

    public void setEdge(Edge edge) {
        this.edge = edge;
    }

    public double getAngle() {
        return this.angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }
}
