package bio.singa.mathematics.algorithms.voronoi.model;

import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.vectors.Vector2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.TreeSet;


public class BeachLine {

    /**
     * The logger for the beach line.
     */
    private static final Logger logger = LoggerFactory.getLogger(BeachLine.class);

    /**
     * The cutoff for numerical precision.
     */
    private static final double epsilon = 1E-9;

    /**
     * The beach line.
     */
    private BeachSection beachline;

    /**
     * The circle events that are being processed. Automatically sorting by processing order (next to be processed to
     * the top).
     */
    private TreeSet<CircleEvent> circleEvents;

    /**
     * The Voronoi diagram.
     */
    private VoronoiDiagram diagram;

    /**
     * Creates a new beach line for a given bounding box.
     *
     * @param boundingBox The bounding box.
     */
    public BeachLine(Rectangle boundingBox) {
        diagram = new VoronoiDiagram(boundingBox);
        beachline = new BeachSection();
        // top to bottom
        circleEvents = new TreeSet<>(Comparator.comparingDouble((CircleEvent circle) -> circle.getEventCoordinate().getY()));
    }

    /**
     * For a given site event, add a beach section to the beach line.
     *
     * @param site The site event.
     */
    public void addBeachSection(SiteEvent site) {

        logger.debug("Adding beach section for site {}.", site);

        double x = site.getX();
        double directrix = site.getY();

        BeachSection node = beachline.getRoot();

        BeachSection lArc = null;
        BeachSection rArc = null;

        double dxl;
        double dxr;

        // determine left and right beach sections which will surround the newly created beach sections
        while (node != null) {
            logger.trace("Calculating left break point.");
            dxl = calculateLeftBreakPoint(node, directrix) - x;
            logger.trace("Left break point is {}.", dxl);
            if (dxl > epsilon) {
                // before the left edge of the beach section
                node = node.getLeft();
            } else {
                logger.trace("Calculating right break point.");
                dxr = x - rightBreakPoint(node, directrix);
                logger.trace("Right break point is {}.", dxr);
                if (dxr > epsilon) {
                    // after the right edge of the beach section
                    if (node.getRight() == null) {
                        lArc = node;
                        break;
                    }
                    node = node.getRight();
                } else {
                    if (dxl > -epsilon) {
                        // exactly on the left edge of the beach section
                        lArc = node.getPrevious();
                        rArc = node;
                    } else if (dxr > -epsilon) {
                        // exactly on the right edge of the beach section
                        lArc = node;
                        rArc = node.getNext();
                    } else {
                        // in the middle of the beach section
                        lArc = node;
                        rArc = node;
                    }
                    break;
                }
            }
        }

        BeachSection newArc = new BeachSection(site);
        beachline.insertSuccessor(lArc, newArc);

        // [null,null]
        // least likely case: new beach section is the first beach section on the beachline.
        // This case means:
        //   no new transition appears
        //   no collapsing beach section
        //   new beach section become root of the RB-tree
        if (lArc == null && rArc == null) {
            return;
        }

        // [lArc,rArc] where lArc == rArc
        // most likely case: new beach section split an existing beach section.
        // This case means:
        //   one new transition appears
        //   the left and right beach section might be collapsing as a result
        //   two new nodes added to the RB-tree
        if (lArc == rArc) {
            // invalidate circle event of the split beach section
            detachCircleEvent(lArc);

            // split beach section into two separate sections
            rArc = new BeachSection(lArc.getSite());
            beachline.insertSuccessor(newArc, rArc);

            // create edge
            VoronoiEdge edge = diagram.createEdge(lArc.getSite(), newArc.getSite());
            newArc.setEdge(edge);
            rArc.setEdge(edge);

            // check whether the left and right beach sections are collapsing and if so create circle events
            attachCircleEvent(lArc);
            attachCircleEvent(rArc);
            return;
        }

        // [lArc,null]
        // even less likely case: new beach section is the *last* beach section on the beachline -- this can happen
        // *only* if *all* the previous beach sections currently on the beachline share the same y value as
        // the new beach section.
        // This case means:
        //   one new transition appears
        //   no collapsing beach section as a result
        //   new beach section become right-most node of the RB-tree
        if (lArc != null && rArc == null) {
            newArc.setEdge(diagram.createEdge(lArc.getSite(), newArc.getSite()));
            return;
        }

        // [null,rArc]
        // cannot happen since sites are processed from top to bottom and left to right

        // [lArc,rArc] where lArc != rArc
        // somewhat less likely case: new beach section falls *exactly* in between two existing beach sections
        // This case means:
        //   one transition disappears
        //   two new transitions appear
        //   the left and right beach section might be collapsing as a result
        //   only one new node added to the RB-tree
        if (lArc != null) {
            // invalidate circle events of left and right sites
            detachCircleEvent(lArc);
            detachCircleEvent(rArc);

            // an existing transition disappears, meaning a vertex is defined at
            // the disappearance point.
            // since the disappearance is caused by the new beach section, the
            // vertex is at the center of the circumscribed circle of the left,
            // new and right beach sections.
            // http://mathforum.org/library/drmath/view/55002.html
            SiteEvent lSite = lArc.getSite();
            final double ax = lSite.getX();
            final double ay = lSite.getY();
            final double bx = site.getX() - ax;
            final double by = site.getY() - ay;
            SiteEvent rSite = rArc.getSite();
            final double cx = rSite.getX() - ax;
            final double cy = rSite.getY() - ay;
            final double d = 2.0 * (bx * cy - by * cx);
            final double hb = bx * bx + by * by;
            final double hc = cx * cx + cy * cy;
            Vector2D vertex = diagram.createVertex((cy * hb - by * hc) / d + ax, (bx * hc - cx * hb) / d + ay);

            // one transition disappear
            rArc.getEdge().setStartingPoint(lSite, rSite, vertex);

            // two new transitions appear at the new vertex location
            newArc.setEdge(diagram.createEdge(lSite, site, null, vertex));
            rArc.setEdge(diagram.createEdge(site, rSite, null, vertex));

            // check whether the left and right beach sections are collapsing
            // and if so create circle events, to handle the point of collapse.
            attachCircleEvent(lArc);
            attachCircleEvent(rArc);
        }
    }

    /**
     * Removes a beach section from the beach line if it has collapsed.
     *
     * @param beachSection The beach section to remove.
     */
    public void removeBeachSection(BeachSection beachSection) {
        logger.trace("Beach section {} collapsed, removing it.", beachSection);
        CircleEvent circle = beachSection.getCircleEvent();
        final double x = circle.getEventCoordinate().getX();
        final double y = circle.getYCenter();
        Vector2D vertex = diagram.createVertex(x, y);
        BeachSection previous = beachSection.getPrevious();
        BeachSection next = beachSection.getNext();

        LinkedList<BeachSection> disappearingTransitions = new LinkedList<>();
        disappearingTransitions.push(beachSection);

        // remove collapsed beach section from beach line
        detachBeachSection(beachSection);

        // there could be more than one empty arc at the deletion point, this
        // happens when more than two edges are linked by the same vertex,
        // so we will collect all those edges by looking up both sides of
        // the deletion point.
        // by the way, there is *always* a predecessor/successor to any collapsed
        // beach section, it's just impossible to have a collapsing first/last
        // beach sections on the beach line, since they obviously are unconstrained
        // on their left/right side.

        // look left
        BeachSection lArc = previous;
        while (lArc.getCircleEvent() != null &&
                Math.abs(x - lArc.getCircleEvent().getEventCoordinate().getX()) < 1e-9 &&
                Math.abs(y - lArc.getCircleEvent().getYCenter()) < 1e-9) {
            logger.trace("Found beach section to the left - detaching {}.", lArc);
            previous = lArc.getPrevious();
            disappearingTransitions.push(lArc);
            detachBeachSection(lArc);
            lArc = previous;
        }

        // even though it is not disappearing, I will also add the beach section
        // immediately to the left of the left-most collapsed beach section, for
        // convenience, since we need to refer to it later as this beach section
        // is the 'left' site of an edge for which a start point is set.
        disappearingTransitions.push(lArc);
        detachCircleEvent(lArc);

        // look right
        BeachSection rArc = next;
        while (rArc.getCircleEvent() != null &&
                Math.abs(x - rArc.getCircleEvent().getEventCoordinate().getX()) < 1e-9 &&
                Math.abs(y - rArc.getCircleEvent().getYCenter()) < 1e-9) {
            logger.trace("Found beach section to the left - detaching {}.", rArc);
            next = rArc.getNext();
            disappearingTransitions.offer(rArc);
            detachBeachSection(rArc);
            rArc = next;
        }

        // we also have to add the beach section immediately to the right of the
        // right-most collapsed beach section, since there is also a disappearing
        // transition representing an edge's start point on its left.
        disappearingTransitions.offer(rArc);
        detachCircleEvent(rArc);

        // walk through all the disappearing transitions between beach sections and
        // set the start point of their (implied) edge.
        int nArcs = disappearingTransitions.size();
        for (int iArc = 1; iArc < nArcs; iArc++) {
            logger.trace("Removing transition {}", iArc);
            rArc = disappearingTransitions.get(iArc);
            lArc = disappearingTransitions.get(iArc - 1);
            rArc.getEdge().setStartingPoint(lArc.getSite(), rArc.getSite(), vertex);
        }

        // create a new edge as we have now a new transition between
        // two beach sections which were previously not adjacent.
        // since this edge appears as a new vertex is defined, the vertex
        // actually define an end point of the edge (relative to the site
        // on the left)
        lArc = disappearingTransitions.getFirst();
        rArc = disappearingTransitions.getLast();
        rArc.setEdge(diagram.createEdge(lArc.getSite(), rArc.getSite(), null, vertex));

        // create circle events if any for beach sections left in the beachline
        // adjacent to collapsed sections
        attachCircleEvent(lArc);
        attachCircleEvent(rArc);
    }

    /**
     * Calculates the intersection (break point) to the first beach section left of the given beach section.
     *
     * @param beachSection The beach section.
     * @param directrix The y position of the directrix.
     * @return The break point (the x position on the directrix).
     */
    private double calculateLeftBreakPoint(BeachSection beachSection, double directrix) {
        logger.trace("Calculating break point for node " + beachSection + " and directrix " + directrix);
        // get focus
        double rightFocusX = beachSection.getSite().getX();
        double rightFocusY = beachSection.getSite().getY();
        // distance or delta to the directrix
        double pby2 = rightFocusY - directrix;
        // focus is on the directrix
        if (pby2 == 0.0) {
            return rightFocusX;
        }

        // handle previous beach line section
        BeachSection previousSection = beachSection.getPrevious();
        if (previousSection == null) {
            logger.trace("No beach section to the left.");
            return Double.NEGATIVE_INFINITY;
        }
        SiteEvent leftSite = previousSection.getSite();
        double lfocx = leftSite.getX();
        double lfocy = leftSite.getY();
        double plby2 = lfocy - directrix;

        if (plby2 == 0.0) {
            return lfocx;
        }

        double hl = lfocx - rightFocusX;
        double aby2 = 1 / pby2 - 1 / plby2;
        double b = hl / plby2;

        if (aby2 != 0.0) {
            return (-b + Math.sqrt(b * b - 2 * aby2 * (hl * hl / (-2 * plby2) - lfocy + plby2 / 2 + rightFocusY - pby2 / 2))) / aby2 + rightFocusX;
        }
        // both parabolas have same distance to directrix, thus break point is midway
        return (rightFocusX + lfocx) / 2;
    }

    /**
     * Calculates the intersection (break point) to the first beach section right of the given beach section.
     *
     * @param beachSection The beach section.
     * @param directrix The y position of the directrix.
     * @return The break point (the x position on the directrix).
     */
    private double rightBreakPoint(BeachSection beachSection, double directrix) {
        BeachSection rArc = beachSection.getNext();
        if (rArc != null) {
            return calculateLeftBreakPoint(rArc, directrix);
        }
        SiteEvent site = beachSection.getSite();
        double result = site.getY() == directrix ? site.getX() : Double.POSITIVE_INFINITY;
        logger.trace("No beach section to the right.", result);
        return result;
    }

    /**
     * Detaches a beach section from the beach line.
     *
     * @param beachSection The beach section.
     */
    private void detachBeachSection(BeachSection beachSection) {
        detachCircleEvent(beachSection);
        beachline.removeNode(beachSection);
    }

    /**
     * Removes referenced circle event from collapsing beach section.
     *
     * @param beachSection The collapsing beach section.
     */
    private void detachCircleEvent(BeachSection beachSection) {
        CircleEvent circleEvent = beachSection.getCircleEvent();
        if (circleEvent != null) {
            circleEvents.remove(circleEvent);
            beachSection.setCircleEvent(null);
        }
    }

    /**
     * Creates and attaches a new circle event for the beach section.
     *
     * @param beachSection The beach section.
     */
    private void attachCircleEvent(BeachSection beachSection) {
        BeachSection lArc = beachSection.getPrevious();
        BeachSection rArc = beachSection.getNext();

        if (lArc == null || rArc == null) {
            return;
        }

        SiteEvent lSite = lArc.getSite();
        SiteEvent cSite = beachSection.getSite();
        SiteEvent rSite = rArc.getSite();

        // If site of left beachsection is same as site of
        // right beachsection, there can't be convergence
        if (lSite == rSite) {
            return;
        }

        // Find the circumscribed circle for the three sites associated
        // with the beachsection triplet.
        // http://mathforum.org/library/drmath/view/55002.html
        // Except that I bring the origin at cSite to simplify calculations.
        // The bottom-most part of the circumcircle is our Fortune 'circle
        // event', and its center is a vertex potentially part of the final
        // Voronoi diagram.
        double bx = cSite.getX();
        double by = cSite.getY();
        double ax = lSite.getX() - bx;
        double ay = lSite.getY() - by;
        double cx = rSite.getX() - bx;
        double cy = rSite.getY() - by;

        // If points l->c->r are clockwise, then center beach section does not
        // collapse, hence it can't end up as a vertex (we reuse 'd' here, which
        // sign is reverse of the orientation, hence we reverse the test.
        // http://en.wikipedia.org/wiki/Curve_orientation#Orientation_of_a_simple_polygon
        // rhill 2011-05-21: Nasty finite precision error which caused circumcircle() to
        // return infinites: 1e-12 seems to fix the problem.
        double d = 2 * (ax * cy - ay * cx);
        if (d >= -2e-12) {
            return;
        }

        double ha = ax * ax + ay * ay;
        double hc = cx * cx + cy * cy;
        double x = (cy * ha - ay * hc) / d;
        double y = (ax * hc - cx * ha) / d;
        double ycenter = y + by;

        CircleEvent circleEvent = new CircleEvent();
        circleEvent.setBeachSection(beachSection);
        circleEvent.setSite(cSite);
        circleEvent.setEventCoordinate(new Vector2D(x + bx, ycenter + Math.sqrt(x * x + y * y)));
        circleEvent.setYCenter(ycenter);
        beachSection.setCircleEvent(circleEvent);

        // add newly created circle event
        circleEvents.add(circleEvent);
    }

    /**
     * Gets the next circle event that needs to be processed.
     *
     * @return The next circle event or null if no circle event exists.
     */
    public CircleEvent getFirstCircleEvent() {
        if (circleEvents.isEmpty()) {
            return null;
        } else {
            return circleEvents.first();
        }
    }

    /**
     * Returns the Voronoi diagram.
     * @return The Voronoi diagram.
     */
    public VoronoiDiagram getDiagram() {
        return diagram;
    }

}
