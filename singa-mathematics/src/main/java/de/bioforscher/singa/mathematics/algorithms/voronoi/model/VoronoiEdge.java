package de.bioforscher.singa.mathematics.algorithms.voronoi.model;


import de.bioforscher.singa.mathematics.vectors.Vector2D;

public class VoronoiEdge {

    private SiteEvent leftSite;
    private SiteEvent rightSite;

    private Vector2D startingPoint;
    private Vector2D endingPoint;

    public VoronoiEdge(SiteEvent leftSite, SiteEvent rightSite) {
        this.leftSite = leftSite;
        this.rightSite = rightSite;
    }

    public SiteEvent getLeftSite() {
        return this.leftSite;
    }

    public void setLeftSite(SiteEvent leftSite) {
        this.leftSite = leftSite;
    }

    public SiteEvent getRightSite() {
        return this.rightSite;
    }

    public void setRightSite(SiteEvent rightSite) {
        this.rightSite = rightSite;
    }

    public Vector2D getStartingPoint() {
        return this.startingPoint;
    }

    public void setStartingPoint(Vector2D startingPoint) {
        this.startingPoint = startingPoint;
    }

    public Vector2D getEndingPoint() {
        return this.endingPoint;
    }

    public void setEndingPoint(Vector2D endingPoint) {
        this.endingPoint = endingPoint;
    }

    public void setEdgeStartPoint(SiteEvent lSite, SiteEvent rSite, Vector2D vertex) {
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

    public void setEdgeEndPoint(SiteEvent lSite, SiteEvent rSite, Vector2D vertex) {
        this.setEdgeStartPoint(rSite, lSite, vertex);
    }

    @Override
    public String toString() {
        return "VoronoiEdge{" +
                "startingPoint=" + this.startingPoint +
                ", endingPoint=" + this.endingPoint +
                '}';
    }
}
