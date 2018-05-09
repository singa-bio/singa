package de.bioforscher.singa.mathematics.algorithms.voronoi;

import de.bioforscher.singa.mathematics.algorithms.voronoi.model.*;
import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.graphs.model.Node;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * A Voronoi diagram is a partitioning of a plane into regions based on distance to points in a specific subset of the
 * plane. That set of points (called sites) is specified beforehand, and for each site there is a corresponding region
 * consisting of all points closer to that seed than to any other. These regions are called {@link VoronoiCell}s.
 * The implementation is heavily based on the javascript implementation of Raymond Hill based on the sweep line
 * algorithm by Steven Fortune (Fortune's algorithm).
 * <pre>
 * Fortune, Steven. "A sweepline algorithm for Voronoi diagrams."
 * Algorithmica 2.1-4 (1987): 153.
 * </pre>
 *
 * @author cl
 * @see <a href="https://en.wikipedia.org/wiki/Fortune%27s_algorithm">Wikipedia: Fortune's algorithm</a>
 * @see <a href="http://www.raymondhill.net/voronoi/rhill-voronoi.html">Javascript implementation of Raymonnd Hill</a>
 * @see <a href="https://link.springer.com/article/10.1007%2FBF01840357">A sweepline algorithm for Voronoi diagrams.</a>
 */
public class VoronoiGenerator {

    /**
     * The logger
     */
    private static final Logger logger = LoggerFactory.getLogger(VoronoiGenerator.class);

    /**
     * The beach line containing {@link BeachSection}s.
     */
    private BeachLine beachLine;

    /**
     * The original site events.
     */
    private Deque<SiteEvent> siteEvents;

    /**
     * Generates and returns the Voronoi diagram.
     *
     * @param sites The sites.
     * @param boundingBox The bounding box.
     * @return The Voronoi diagram.
     */
    public static VoronoiDiagram generateVoronoiDiagram(Collection<Vector2D> sites, Rectangle boundingBox) {
        VoronoiGenerator voronoi = new VoronoiGenerator(sites, boundingBox);
        voronoi.generateDiagram();
        return voronoi.beachLine.getDiagram();
    }


    public static <IdentifierType, NodeType extends Node<NodeType, Vector2D, IdentifierType>> VoronoiDiagram generateVoronoiDiagram(Map<Integer, NodeType> nodeMap, Rectangle boundingBox) {
        VoronoiGenerator voronoi = new VoronoiGenerator(nodeMap, boundingBox);
        voronoi.generateDiagram();
        return voronoi.beachLine.getDiagram();
    }

    /**
     * Creates a new Voronoi generator.
     *
     * @param sites The sites.
     * @param boundingBox The bounding box.
     */
    private VoronoiGenerator(Collection<Vector2D> sites, Rectangle boundingBox) {
        // initialize beach line with bounding box
        beachLine = new BeachLine(boundingBox);
        // sort the original vectors from top to bottom
        TreeSet<Vector2D> sortedCopy = new TreeSet<>(Comparator.comparingDouble(Vector2D::getY).thenComparing(Vector2D::getX).reversed());
        sortedCopy.addAll(sites);
        // add them as site events to the deque
        siteEvents = new ArrayDeque<>();
        sortedCopy.forEach(vector -> siteEvents.push(new SiteEvent(vector)));
        logger.trace("Sorted sites in queue: {}", siteEvents);
    }

    private <IdentifierType, NodeType extends Node<NodeType, Vector2D, IdentifierType>> VoronoiGenerator(Map<Integer, NodeType> nodeMap, Rectangle boundingBox) {
        // initialize beach line with bounding box
        beachLine = new BeachLine(boundingBox);
        // sort the original vectors from top to bottom
        Comparator<AbstractMap.Entry<Integer,NodeType>> comparator = Comparator.comparing(nodeType -> nodeType.getValue().getPosition().getY());
        TreeSet<AbstractMap.Entry<Integer,NodeType>> sortedCopy = new TreeSet<>(comparator.reversed());
        sortedCopy.addAll(nodeMap.entrySet());
        // add them as site events to the deque
        siteEvents = new ArrayDeque<>();
        sortedCopy.forEach(entry -> siteEvents.push(new SiteEvent(entry.getKey(), entry.getValue().getPosition())));
        logger.trace("Sorted sites in queue: {}", siteEvents);
    }

    /**
     * Triggers generation of of the Voronoi diagram using the current sites.
     */
    private void generateDiagram() {
        // set first site and previous site
        int siteIdentifier = 0;
        SiteEvent siteEvent = siteEvents.pop();
        SiteEvent previousSite = null;

        // loop breaks if neither circle nor site events remain
        while (true) {
            // get next circle event
            CircleEvent circleEvent = beachLine.getFirstCircleEvent();
            // determine next event
            if (siteEvent != null && (circleEvent == null || siteEventIsBeforeCircleEvent(siteEvent, circleEvent))) {
                // only if the site is not a duplicate
                if (previousSite == null || siteEvent.getX() != previousSite.getX() || siteEvent.getY() != previousSite.getY()) {
                    logger.trace("Processing site event: {}", siteEvent);
                    // first create cell for new site
                    if (siteEvent.getIdentifier() == -1) {
                        beachLine.getDiagram().createCell(siteIdentifier, siteEvent);
                        siteIdentifier++;
                    } else {
                        beachLine.getDiagram().createCell(siteEvent.getIdentifier(), siteEvent);
                    }
                    // then create a beach section for that site
                    beachLine.addBeachSection(siteEvent);
                    previousSite = siteEvent;
                }
                // get next site event
                if (!siteEvents.isEmpty()) {
                    siteEvent = siteEvents.pop();
                } else {
                    siteEvent = null;
                }
            } else if (circleEvent != null) {
                logger.trace("Processing circle event: {}", circleEvent);
                beachLine.removeBeachSection(circleEvent.getBeachSection());
            } else {
                break;
            }

        }
        postProcess();
    }

    /**
     * Wrapping up the diagram. Connect dangling edges to bounding box, discard edges completely outside bounding box,
     * discard edges which are point-like, and add missing edges in order to close opened cells.
     */
    private void postProcess() {
        beachLine.getDiagram().clipEdges();
        beachLine.getDiagram().closeCells();
    }

    /**
     * Returns true if the site event needs to be processed before the circle event.
     *
     * @param siteEvent The site event.
     * @param circleEvent The circle event.
     * @return True if the site event needs to be processed before the circle event.
     */
    private boolean siteEventIsBeforeCircleEvent(SiteEvent siteEvent, CircleEvent circleEvent) {
        return siteEvent.getY() < circleEvent.getEventCoordinate().getY() ||
                (siteEvent.getY() == circleEvent.getEventCoordinate().getY() && siteEvent.getX() < circleEvent.getEventCoordinate().getX());
    }

}
