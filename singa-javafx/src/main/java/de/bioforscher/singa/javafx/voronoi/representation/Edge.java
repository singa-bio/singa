package de.bioforscher.singa.javafx.voronoi.representation;


import de.bioforscher.singa.javafx.voronoi.Site;
import de.bioforscher.singa.mathematics.vectors.Vector2D;

public class Edge {

    private Site lSite;
    private Site rSite;

    private Vector2D va;
    private Vector2D vb;

    public Edge(Site lSite, Site rSite) {
        this.lSite = lSite;
        this.rSite = rSite;
    }

    public Site getlSite() {
        return this.lSite;
    }

    public void setlSite(Site lSite) {
        this.lSite = lSite;
    }

    public Site getrSite() {
        return this.rSite;
    }

    public void setrSite(Site rSite) {
        this.rSite = rSite;
    }

    public Vector2D getVa() {
        return this.va;
    }

    public void setVa(Vector2D va) {
        this.va = va;
    }

    public Vector2D getVb() {
        return this.vb;
    }

    public void setVb(Vector2D vb) {
        this.vb = vb;
    }

    public void setEdgeStartPoint(Site lSite, Site rSite, Vector2D vertex) {
        if (this.va == null && this.vb == null) {
            this.va = vertex;
            this.lSite = lSite;
            this.rSite = rSite;
        } else if (this.lSite.equals(rSite)) {
            this.vb = vertex;
        } else {
            this.va = vertex;
        }
    }

    public void setEdgeEndPoint(Site lSite, Site rSite, Vector2D vertex) {
        this.setEdgeStartPoint(rSite, lSite, vertex);
    }

    @Override
    public String toString() {
        return "Edge{" +
                "va=" + this.va +
                ", vb=" + this.vb +
                '}';
    }
}
