package de.bioforscher.singa.mathematics.algorithms.voronoi.model;

import de.bioforscher.singa.mathematics.vectors.Vector2D;

import java.util.Comparator;

/**
 * @author cl
 */
public class CircleEvent {

    public static final Comparator<CircleEvent> topToBottom = Comparator.comparingDouble((CircleEvent circle) -> circle.getEventCoordinate().getY());

    // beachSection in beach line
    private BeachSection beachSection;
    // site creating this event
    private SiteEvent site;
    // the sorting in the event calling routine
    private Vector2D eventCoordinate;
    // the centre of the circle
    private double yCenter;

    public BeachSection getBeachSection() {
        return beachSection;
    }

    public void setBeachSection(BeachSection beachSection) {
        this.beachSection = beachSection;
    }

    public SiteEvent getSite() {
        return site;
    }

    public void setSite(SiteEvent site) {
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
