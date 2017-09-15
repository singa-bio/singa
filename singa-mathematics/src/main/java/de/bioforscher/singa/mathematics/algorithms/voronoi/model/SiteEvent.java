package de.bioforscher.singa.mathematics.algorithms.voronoi.model;

import de.bioforscher.singa.mathematics.vectors.Vector2D;

// used both for sites and for vertices
public class SiteEvent {

    private int identifier;
    private Vector2D site;

    public SiteEvent(int identifier, Vector2D site) {
        this.identifier = identifier;
        this.site = site;
    }

    public SiteEvent(Vector2D site) {
        this.identifier = -1;
        this.site = site;
    }

    public int getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public Vector2D getSite() {
        return this.site;
    }

    public void setSite(Vector2D site) {
        this.site = site;
    }

    public double getX() {
        return this.site.getX();
    }

    public double getY() {
        return this.site.getY();
    }

    @Override
    public String toString() {
        return "SiteEvent{" +
                "id=" + this.identifier +
                ", site=" + this.site +
                '}';
    }
}
