package de.bioforscher.singa.javafx.voronoi;

import de.bioforscher.singa.javafx.renderer.Renderer;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.util.*;

/**
 * @author cl
 */
public class VoronoiDiagram implements Renderer {

    private static final Comparator<Vector2D> leftToRight = Comparator.comparingDouble(Vector2D::getX);
    private static final Comparator<Vector2D> topToBottom = Comparator.comparingDouble(Vector2D::getY);

    private Canvas canvas;
    private BeachLine beachLine;
    private TreeSet<Vector2D> originalVectors;
    private Deque<Vector2D> siteEvents;

    private Vector2D site;
    private Vector2D previousSite;
    private VoronoiRBTree circle;

    public VoronoiDiagram(Collection<Vector2D> originalVectors, Canvas canvas) {
        this.canvas = canvas;
        this.beachLine = new BeachLine();

        // sort the original vectors from top to bottom
        this.originalVectors = new TreeSet<>(topToBottom);
        this.originalVectors.addAll(originalVectors);
        // add them to the vector events
        this.siteEvents = new ArrayDeque<>();
        this.originalVectors.forEach(this.siteEvents::push);

        this.site = this.siteEvents.pop();

    }

    public void nextEvent() {

        // we need to figure whether we handle a site or circle event
        // for this we find out if there is a site event and it is
        // 'earlier' than the circle event
        this.circle = this.beachLine.getFirstCircleEvent();

        // add beach section
        if (this.site != null &&
                (this.circle == null || this.site.getY() < this.circle.getY() ||
                        (this.site.getY() == this.circle.getY() && this.site.getX() < this.circle.getX()))) {
            // only if the site is not a duplicate
            if (this.previousSite == null || this.site.getX() != this.previousSite.getX() || this.site.getY() != this.previousSite.getY()) {
                // first create cell for new site
                // cells[siteid] = this.createCell(site);
                // site.voronoiId = siteid++;
                // then create a beachsection for that site
                this.beachLine.addBeachSection(this.site);
                this.previousSite = this.site;
            }
            this.site = this.siteEvents.pop();
        } else if (this.circle != null) {
            this.beachLine.removeBeachSection(this.circle.getArc());
        }

    }

    @Override
    public GraphicsContext getGraphicsContext() {
        return this.canvas.getGraphicsContext2D();
    }

    @Override
    public double getDrawingWidth() {
        return this.canvas.getWidth();
    }

    @Override
    public double getDrawingHeight() {
        return this.canvas.getHeight();
    }

}
