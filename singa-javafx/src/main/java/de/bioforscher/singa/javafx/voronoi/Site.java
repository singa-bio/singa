package de.bioforscher.singa.javafx.voronoi;

import de.bioforscher.singa.mathematics.vectors.Vector2D;

// used both for sites and for vertices
public class Site {

    private int voronoiId;
    private Vector2D coord;

    public Site(int voronoiId, Vector2D coord) {
        this.voronoiId = voronoiId;
        this.coord = coord;
    }

    public Site(Vector2D coord) {
        this.voronoiId = -1;
        this.coord = coord;
    }

    public int getVoronoiId() {
        return this.voronoiId;
    }

    public void setVoronoiId(int voronoiId) {
        this.voronoiId = voronoiId;
    }

    public Vector2D getCoord() {
        return this.coord;
    }

    public void setCoord(Vector2D coord) {
        this.coord = coord;
    }

    public double getX() {
        return this.coord.getX();
    }

    public double getY() {
        return this.coord.getY();
    }

    @Override
    public String toString() {
        return "Site{" +
                "id=" + this.voronoiId +
                ", coord=" + this.coord +
                '}';
    }
}
