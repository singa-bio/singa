package de.bioforscher.singa.javafx.voronoi.representation;


import de.bioforscher.singa.javafx.voronoi.Site;

public class Edge {

    private Site lSite;
    private Site rSite;

    private Site va;
    private Site vb;

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

    public Site getVa() {
        return this.va;
    }

    public void setVa(Site va) {
        this.va = va;
    }

    public Site getVb() {
        return this.vb;
    }

    public void setVb(Site vb) {
        this.vb = vb;
    }

    public void setEdgeStartPoint(Site lSite, Site rSite, Site vertex) {
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

    public void setEdgeEndPoint(Site lSite, Site rSite, Site vertex) {
        this.setEdgeStartPoint(rSite, lSite, vertex);
    }


    @Override
    public String toString() {
        return "Edge{" +
                "lSite=" + this.lSite +
                ", rSite=" + this.rSite +
                ", va=" + this.va +
                ", vb=" + this.vb +
                '}';
    }
}
