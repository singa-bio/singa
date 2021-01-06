package bio.singa.mathematics.algorithms.voronoi.model;

import bio.singa.mathematics.vectors.Vector2D;

import java.util.StringJoiner;

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
        identifier = -1;
        this.site = site;
    }

    /**
     * Returns the identifier.
     * @return The identifier.
     */
    public int getIdentifier() {
        return identifier;
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
        return site;
    }

    /**
     * Returns the x-coordinate of the site.
     * @return The x-coordinate of the site.
     */
    public double getX() {
        return site.getX();
    }

    /**
     * Returns the y-coordinate of the site.
     * @return The y-coordinate of the site.
     */
    public double getY() {
        return site.getY();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SiteEvent.class.getSimpleName() + "[", "]")
                .add("identifier=" + identifier)
                .add("site=" + site)
                .toString();
    }

}
