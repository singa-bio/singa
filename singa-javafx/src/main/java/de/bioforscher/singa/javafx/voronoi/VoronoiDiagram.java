package de.bioforscher.singa.javafx.voronoi;

import de.bioforscher.singa.javafx.renderer.Renderer;
import de.bioforscher.singa.javafx.voronoi.representation.Edge;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.*;

/**
 * @author cl
 */
public class VoronoiDiagram implements Renderer {

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
    }

    public void perform() {
        // get next site Event
        Site siteEvent = this.siteEvents.pop();
        while (!this.siteEvents.isEmpty()) {
            // TODO circle events after site events have been cleared are skipped
            // get next circle event
            CircleEvent circleEvent = this.beachLine.getFirstCircleEvent();
            if (circleEvent != null) {
                System.out.println("Next circle event: " + circleEvent.getEventCoordinate());
            } else {
                System.out.println("No circle event pending.");
            }
            System.out.println("Next site event: " + siteEvent);

            // add beach section
            if (siteEvent != null && (circleEvent == null || siteEventIsBeforeCircleEvent(siteEvent, circleEvent))) {
                // only if the site is not a duplicate
                if (this.previousSite == null || siteEvent.getX() != this.previousSite.getX() || siteEvent.getY() != this.previousSite.getY()) {
                    // first create cell for new site
                    this.beachLine.getDiagram().createCell(this.siteId, siteEvent);
                    this.siteId++;
                    System.out.println(" -> handling site event");
                    // then create a beach section for that site
                    this.beachLine.addBeachSection(siteEvent);
                    this.previousSite = siteEvent;
                }
                siteEvent = this.siteEvents.pop();
            } else if (circleEvent != null) {
                System.out.println(" -> handling circle event");
                this.beachLine.removeBeachSection(circleEvent.getArc());
            }

            List<Vector2D> vertices = this.beachLine.getDiagram().getVertices();
            List<Edge> edges = this.beachLine.getDiagram().getEdges();
            System.out.println();
            System.out.println("Current vertices:");
            vertices.forEach(System.out::println);
            System.out.println("Current edges:");
            edges.forEach(System.out::println);
            System.out.println("---------------------");
        }

        getGraphicsContext().setFill(Color.TOMATO);
        this.beachLine.getDiagram().getEdges().forEach( edge -> {
            if (edge.getVa() != null) {
                System.out.println(edge.getVa());
                drawPoint(edge.getVa());
            }
            if (edge.getVb() != null) {
                System.out.println(edge.getVb());
                drawPoint(edge.getVb());
            }
        });
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
