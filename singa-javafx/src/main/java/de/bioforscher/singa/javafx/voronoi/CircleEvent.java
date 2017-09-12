package de.bioforscher.singa.javafx.voronoi;

import de.bioforscher.singa.mathematics.vectors.Vector2D;

import java.util.Comparator;

/**
 * @author cl
 */
public class CircleEvent {

    public static final Comparator<CircleEvent> topToBottom = Comparator.comparingDouble((CircleEvent circle) -> circle.getEventCoordinate().getY());

    // arc in beach line
    private VoronoiNode arc;
    // site creating this event
    private Site site;
    // the sorting in the event calling routine
    private Vector2D eventCoordinate;
    // the centre of the circle
    private double yCenter;

    public VoronoiNode getArc() {
        return arc;
    }

    public void setArc(VoronoiNode arc) {
        this.arc = arc;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public Vector2D getEventCoordinate() {
        return eventCoordinate;
    }

    public void setEventCoordinate(Vector2D eventCoordinate) {
        this.eventCoordinate = eventCoordinate;
    }

    public double getyCenter() {
        return yCenter;
    }

    public void setyCenter(double yCenter) {
        this.yCenter = yCenter;
    }
}
