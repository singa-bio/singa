package de.bioforscher.singa.javafx.voronoi.representation;

import de.bioforscher.singa.mathematics.vectors.Vector2D;

/**
 * Created by Christoph on 20/05/2017.
 */
public class Edge {

    private Vector2D lSite;
    private Vector2D rSite;

    private Vector2D va;
    private Vector2D vb;

    public Edge(Vector2D lSite, Vector2D rSite) {
        this.lSite = lSite;
        this.rSite = rSite;
    }

    public Vector2D getlSite() {
        return this.lSite;
    }

    public void setlSite(Vector2D lSite) {
        this.lSite = lSite;
    }

    public Vector2D getrSite() {
        return this.rSite;
    }

    public void setrSite(Vector2D rSite) {
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

    public void setEdgeStartPoint(Vector2D lSite, Vector2D rSite, Vector2D vertex) {
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

    public void setEdgeEndPoint(Vector2D lSite, Vector2D rSite, Vector2D vertex) {
        this.setEdgeStartPoint(rSite, lSite, vertex);
    }


}
