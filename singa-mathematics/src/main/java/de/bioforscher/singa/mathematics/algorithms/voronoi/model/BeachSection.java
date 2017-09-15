package de.bioforscher.singa.mathematics.algorithms.voronoi.model;

import de.bioforscher.singa.mathematics.graphs.trees.RedBlackNode;

/**
 * Some kind of RB Tree
 */
public class BeachSection extends RedBlackNode<BeachSection> {

    private SiteEvent site;
    private CircleEvent circleEvent;

    private VoronoiEdge edge;

    public BeachSection() {
    }

    public BeachSection(SiteEvent site) {
        this.site = site;
    }

    public SiteEvent getSite() {
        return this.site;
    }

    public void setSite(SiteEvent site) {
        this.site = site;
    }

    public CircleEvent getCircleEvent() {
        return this.circleEvent;
    }

    public void setCircleEvent(CircleEvent circleEvent) {
        this.circleEvent = circleEvent;
    }

    public VoronoiEdge getEdge() {
        return this.edge;
    }

    public void setEdge(VoronoiEdge edge) {
        this.edge = edge;
    }

}
