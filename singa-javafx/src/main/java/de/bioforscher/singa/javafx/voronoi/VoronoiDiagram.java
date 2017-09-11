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

    private static final Comparator<Vector2D> leftToRight = Comparator.comparingDouble(Vector2D::getX);
    private static final Comparator<Vector2D> topToBottom = Comparator.comparingDouble(Vector2D::getY).reversed();

    private Canvas canvas;
    private BeachLine beachLine;
    private TreeSet<Vector2D> originalVectors;
    private Deque<Site> siteEvents;

    private Site site;
    private Site previousSite;
    private VoronoiRBTree circle;

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

        this.site = this.siteEvents.pop();


    }

    public void nextEvent() {
        while (!this.siteEvents.isEmpty()) {

            // we need to figure whether we handle a site or circle event
            // for this we find out if there is a site event and it is
            // 'earlier' than the circle event
            this.circle = this.beachLine.getFirstCircleEvent();
            if (this.circle != null) {
                System.out.println("Next circle event: " + new Vector2D(this.circle.getX(), this.circle.getY()));
            } else {
                System.out.println("No circle event pending.");
            }
            System.out.println("Next site event: " + this.site);

            // add beach section
            if (this.site != null &&
                    (this.circle == null || this.site.getY() < this.circle.getY() ||
                            (this.site.getY() == this.circle.getY() && this.site.getX() < this.circle.getX()))) {
                // only if the site is not a duplicate
                if (this.previousSite == null || this.site.getX() != this.previousSite.getX() || this.site.getY() != this.previousSite.getY()) {
                    // first create cell for new site
                    this.beachLine.getDiagram().createCell(this.siteId, this.site);
                    this.siteId++;
                    System.out.println(" -> handling site event");
                    // then create a beach section for that site
                    this.beachLine.addBeachSection(this.site);
                    this.previousSite = this.site;
                }
                this.site = this.siteEvents.pop();
            } else if (this.circle != null) {
                System.out.println(" -> handling circle event");
                this.beachLine.removeBeachSection(this.circle.getArc());
            }

            List<Vector2D> vertices = this.beachLine.getDiagram().getVertices();
            List<Edge> edges = this.beachLine.getDiagram().getEdges();
            System.out.println();
            System.out.println("Current vertices:");
            vertices.forEach(System.out::println);
            System.out.println("Current edges:");
            edges.forEach(System.out::println);
            System.out.println("---------------------");
            getGraphicsContext().setStroke(Color.TOMATO);
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
