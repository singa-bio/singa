package bio.singa.mathematics.algorithms.voronoi.model;

import bio.singa.mathematics.vectors.Vector2D;

/**
 * A circle event happens when a parabolic arc shrinks to a point and disappears from the beach line. Let bj be the
 * disappearing arc and bi and bk are two neighboring arcs before bj disappears. These arcs are then defined by three
 * different points pi, pj and pk. At the moment when bj disappears, all three arcs pass through a common point q.
 * The distance between q and the scanning line is the same as the distance from q to all three points pi, pj and pk.
 * These three points define a circle, with its center at point q which represents the Voronoi point. The lowest point
 * of this circle touches the scanning line and represents the circle event.
 *
 * @author cl
 * @see <a href="http://old.cescg.org/CESCG99/RCuk/circle.htm">Circle events explained</a>
 */
public class CircleEvent {

    /**
     * The referenced beach section
     */
    private BeachSection beachSection;

    /**
     * The referenced site.
     */
    private SiteEvent site;

    /**
     * The event coordinate (determining que position)
     */
    private Vector2D eventCoordinate;

    /**
     * The centre of the circle
     */
    private double yCenter;

    /**
     * Returns the referenced beach section.
     * @return The referenced beach section.
     */
    public BeachSection getBeachSection() {
        return beachSection;
    }

    /**
     * Sets the beach section.
     * @param beachSection The beach section.
     */
    public void setBeachSection(BeachSection beachSection) {
        this.beachSection = beachSection;
    }

    /**
     * Returns the referenced site.
     * @return The referenced site.
     */
    public SiteEvent getSite() {
        return site;
    }

    /**
     * Sets the site.
     * @param site The site.
     */
    public void setSite(SiteEvent site) {
        this.site = site;
    }

    /**
     * Returns the event coordinate.
     * @return The event coordinate.
     */
    public Vector2D getEventCoordinate() {
        return eventCoordinate;
    }

    /**
     * Sets the event coordinate.
     * @param eventCoordinate The event coordinate.
     */
    public void setEventCoordinate(Vector2D eventCoordinate) {
        this.eventCoordinate = eventCoordinate;
    }

    /**
     * Returns the y position of the centre of the circle.
     * @return The y position of the centre of the circle.
     */
    public double getYCenter() {
        return yCenter;
    }

    /**
     * Sets the y position of the centre of the circle.
     * @param yCenter The y position of the centre of the circle.
     */
    public void setYCenter(double yCenter) {
        this.yCenter = yCenter;
    }
}
