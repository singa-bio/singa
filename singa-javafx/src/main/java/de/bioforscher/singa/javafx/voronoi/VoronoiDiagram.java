package de.bioforscher.singa.javafx.voronoi;

import de.bioforscher.singa.javafx.renderer.Renderer;
import de.bioforscher.singa.javafx.voronoi.representation.Diagram;
import de.bioforscher.singa.javafx.voronoi.representation.Edge;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author cl
 */
public class VoronoiDiagram implements Renderer {

    private static final Logger logger = LoggerFactory.getLogger(BeachLine.class);
    public static final Comparator<Vector2D> topToBottom = Comparator.comparingDouble(Vector2D::getY).reversed();

    private Canvas canvas;
    private BeachLine beachLine;
    private TreeSet<Vector2D> originalVectors;
    private Deque<Site> siteEvents;

    private Site previousSite;

    int siteId = 0;

    public VoronoiDiagram(Collection<Vector2D> originalVectors, Canvas canvas) {
        this.canvas = canvas;
        this.beachLine = new BeachLine();

        // sort the original vectors from top to bottom
        this.originalVectors = new TreeSet<>(topToBottom);
        this.originalVectors.addAll(originalVectors);
        // add them to the vector events
        this.siteEvents = new ArrayDeque<>();
        this.originalVectors.forEach(vector -> this.siteEvents.push(new Site(vector)));
        logger.trace("Sorted original vectors in queue: {}", this.siteEvents);
    }

    public void perform() {
        // get next site Event
        Site siteEvent = this.siteEvents.pop();
        while (!this.siteEvents.isEmpty()) {
            // get next circle event
            CircleEvent circleEvent = this.beachLine.getFirstCircleEvent();
            if (circleEvent != null) {
                logger.trace("Next circle event: {}", circleEvent.getEventCoordinate());
            } else {
                logger.trace("No circle event pending.");
            }
            logger.trace("Next site event: {}", siteEvent);

            // add beach section
            if (siteEvent != null && (circleEvent == null || siteEventIsBeforeCircleEvent(siteEvent, circleEvent))) {
                // only if the site is not a duplicate
                if (this.previousSite == null || siteEvent.getX() != this.previousSite.getX() || siteEvent.getY() != this.previousSite.getY()) {
                    // first create cell for new site
                    this.beachLine.getDiagram().createCell(this.siteId, siteEvent);
                    this.siteId++;
                    logger.trace("Processing site event: {}", siteEvent);
                    // then create a beach section for that site
                    this.beachLine.addBeachSection(siteEvent);
                    this.previousSite = siteEvent;
                }
                siteEvent = this.siteEvents.pop();
            } else if (circleEvent != null) {
                logger.trace("Processing circle event: {}", circleEvent);
                this.beachLine.removeBeachSection(circleEvent.getArc());
            }

        }

        Diagram diagram = this.beachLine.getDiagram();
        logger.trace("Before clean up the following vertices and edges have been created.");
        for (Vector2D vector : diagram.getVertices()) {
            logger.trace("{}", vector);
            getGraphicsContext().setFill(Color.TOMATO);
            drawPoint(vector);
        }
        for (Edge edge : diagram.getEdges()) {
            logger.trace("{}", edge);
        }

        // bbox = [x, y, width, height]
        double[] bbox = new double[] {0, 0, getDrawingWidth(), getDrawingHeight()};

        // wrapping-up:
        //   connect dangling edges to bounding box
        //   cut edges as per bounding box
        //   discard edges completely outside bounding box
        //   discard edges which are point-like
        diagram.clipEdges(bbox);

        //   add missing edges in order to close opened cells
        diagram.closeCells(bbox);

    }

    private boolean siteEventIsBeforeCircleEvent(Site siteEvent, CircleEvent circleEvent) {
        return siteEvent.getY() < circleEvent.getEventCoordinate().getY() ||
                (siteEvent.getY() == circleEvent.getEventCoordinate().getY() && siteEvent.getX() < circleEvent.getEventCoordinate().getX());
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
