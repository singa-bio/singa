package de.bioforscher.singa.mathematics.algorithms.voronoi.model;

import de.bioforscher.singa.mathematics.vectors.Vector2D;

/**
 * Site events originate from the source vectors of the voronoi diagram. The identifier represent the processing
 * succession (The first processed site event has id 0, the next 1, and so on).
 */
public class SiteEvent {

    /**
     * The identifier representing the processing succession.
     */
    private int identifier;

    /**
     * The original vector, called site in this context.
     */
    private Vector2D site;

    /**
     * Creates a queued {@link SiteEvent}.
     *
     * @param identifier The identifier.
     * @param site The site.
     */
    public SiteEvent(int identifier, Vector2D site) {
        this.identifier = identifier;
        this.site = site;
    }

    /**
     * Creates an unqueued {@link SiteEvent}. Initializing the identifier with -1.
     * @param site The site.
     */
    public SiteEvent(Vector2D site) {
        this.identifier = -1;
        this.site = site;
    }

    /**
     * Returns the identifier.
     * @return The identifier.
     */
    public int getIdentifier() {
        return this.identifier;
    }

    /**
     * Sets the identifier.
     * @param identifier The identifier.
     */
    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    /**
     * Returns the site.
     * @return The Site.
     */
    public Vector2D getSite() {
        return this.site;
    }

    /**
     * Returns the x-coordinate of the site.
     * @return The x-coordinate of the site.
     */
    public double getX() {
        return this.site.getX();
    }

    /**
     * Returns the y-coordinate of the site.
     * @return The y-coordinate of the site.
     */
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
