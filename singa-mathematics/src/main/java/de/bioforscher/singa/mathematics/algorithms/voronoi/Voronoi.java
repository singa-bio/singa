package de.bioforscher.singa.mathematics.algorithms.voronoi;

import de.bioforscher.singa.mathematics.algorithms.voronoi.model.BeachLine;
import de.bioforscher.singa.mathematics.algorithms.voronoi.model.CircleEvent;
import de.bioforscher.singa.mathematics.algorithms.voronoi.model.SiteEvent;
import de.bioforscher.singa.mathematics.algorithms.voronoi.model.VoronoiDiagram;
import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author cl
 */
public class Voronoi {

    private static final Logger logger = LoggerFactory.getLogger(BeachLine.class);
    private static final Comparator<Vector2D> topToBottom = Comparator.comparingDouble(Vector2D::getY).reversed();

    private BeachLine beachLine;
    private Deque<SiteEvent> siteEvents;

    public static VoronoiDiagram generateVoronoiDiagram(Collection<Vector2D> originalVectors, Rectangle boundingBox) {
        Voronoi voronoi = new Voronoi(originalVectors, boundingBox);
        voronoi.generateDiagram();
        return voronoi.beachLine.getDiagram();
    }

    private Voronoi(Collection<Vector2D> originalVectors, Rectangle boundingBox) {
        this.beachLine = new BeachLine(boundingBox);
        // sort the original vectors from top to bottom
        TreeSet<Vector2D> sortedCopy = new TreeSet<>(topToBottom);
        sortedCopy.addAll(originalVectors);
        // add them to the vector events
        this.siteEvents = new ArrayDeque<>();
        sortedCopy.forEach(vector -> this.siteEvents.push(new SiteEvent(vector)));
        logger.trace("Sorted original vectors in queue: {}", this.siteEvents);
    }

    private void generateDiagram() {
        // get next site Event
        int siteIdentifier = 0;
        SiteEvent siteEvent = this.siteEvents.pop();
        SiteEvent previousSite = null;
        while (true) {
            // get next circle event
            CircleEvent circleEvent = this.beachLine.getFirstCircleEvent();

            if (circleEvent != null) {
                logger.trace("Next circle event: {}", circleEvent.getEventCoordinate());
            } else {
                logger.trace("No circle event pending.");
            }

            if (siteEvent != null) {
                logger.trace("Next site event: {}", siteEvent);
            } else {
                logger.trace("No site event pending.");
            }

            // add beach section
            if (siteEvent != null && (circleEvent == null || siteEventIsBeforeCircleEvent(siteEvent, circleEvent))) {
                // only if the site is not a duplicate
                if (previousSite == null || siteEvent.getX() != previousSite.getX() || siteEvent.getY() != previousSite.getY()) {
                    // first create cell for new site
                    this.beachLine.getDiagram().createCell(siteIdentifier, siteEvent);
                    siteIdentifier++;
                    logger.trace("Processing site event: {}", siteEvent);
                    // then create a beach section for that site
                    this.beachLine.addBeachSection(siteEvent);
                    previousSite = siteEvent;
                }
                if (!this.siteEvents.isEmpty()) {
                    siteEvent = this.siteEvents.pop();
                } else {
                    siteEvent = null;
                }
            } else if (circleEvent != null) {
                logger.trace("Processing circle event: {}", circleEvent);
                this.beachLine.removeBeachSection(circleEvent.getBeachSection());
            } else {
                break;
            }

        }

        postProcess(this.beachLine.getDiagram());

    }

    private void postProcess(VoronoiDiagram diagram) {
        // wrapping-up:
        //   connect dangling edges to bounding box
        //   cut edges as per bounding box
        //   discard edges completely outside bounding box
        //   discard edges which are point-like
        diagram.clipEdges();

        //   add missing edges in order to close opened cells
        // diagram.closeCells();
        diagram.closeBorderCells();
    }

    private boolean siteEventIsBeforeCircleEvent(SiteEvent siteEvent, CircleEvent circleEvent) {
        return siteEvent.getY() < circleEvent.getEventCoordinate().getY() ||
                (siteEvent.getY() == circleEvent.getEventCoordinate().getY() && siteEvent.getX() < circleEvent.getEventCoordinate().getX());
    }

}
