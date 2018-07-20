package de.bioforscher.singa.mathematics.algorithms.voronoi.model;

import de.bioforscher.singa.mathematics.graphs.trees.RedBlackNode;

/**
 * The beach section is an implementation of a {@link RedBlackNode}. Sorting itself to efficiently store neighbouring
 * beach sections.
 */
public class BeachSection extends RedBlackNode<BeachSection> {

    /**
     * The site event.
     */
    private SiteEvent site;

    /**
     * The circle event.
     */
    private CircleEvent circleEvent;

    private VoronoiEdge edge;

    public BeachSection() {
    }

    public BeachSection(SiteEvent site) {
        this.site = site;
    }

    public SiteEvent getSite() {
        return site;
    }

    public void setSite(SiteEvent site) {
        this.site = site;
    }

    public CircleEvent getCircleEvent() {
        return circleEvent;
    }

    public void setCircleEvent(CircleEvent circleEvent) {
        this.circleEvent = circleEvent;
    }

    public VoronoiEdge getEdge() {
        return edge;
    }

    public void setEdge(VoronoiEdge edge) {
        this.edge = edge;
    }

}
