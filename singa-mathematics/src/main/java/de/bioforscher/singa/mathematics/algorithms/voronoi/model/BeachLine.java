package de.bioforscher.singa.mathematics.algorithms.voronoi.model;

import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.TreeSet;


public class BeachLine {

    private static final Logger logger = LoggerFactory.getLogger(BeachLine.class);
    private static final double epsilon = 1E-9;

    private BeachSection beachline;
    private TreeSet<CircleEvent> circleEvents;

    private VoronoiDiagram diagram;

    public BeachLine(Rectangle boundingBox) {
        this.diagram = new VoronoiDiagram(boundingBox);
        this.beachline = new BeachSection();
        this.circleEvents = new TreeSet<>(CircleEvent.topToBottom);
    }

    public void addBeachSection(SiteEvent site) {

        logger.debug("Adding beach section for site {}.", site);

        double x = site.getX();
        double directrix = site.getY();

        BeachSection node = this.beachline.getRoot();

        BeachSection lArc = null;
        BeachSection rArc = null;

        double dxl;
        double dxr;

        // determine left and right beach sections which will surround the newly created beach sections
        while (node != null) {
            logger.trace("Calculating left break point.");
            dxl = leftBreakPoint(node, directrix) - x;
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
            logger.trace("Calculating break points again.");
        }

        BeachSection newArc = new BeachSection(site);
        this.beachline.insertSuccessor(lArc, newArc);

        // case

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
            this.beachline.insertSuccessor(newArc, rArc);

            // create VoronoiEdge
            VoronoiEdge edge = this.diagram.createEdge(lArc.getSite(), newArc.getSite());
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
            newArc.setEdge(this.diagram.createEdge(lArc.getSite(), newArc.getSite()));
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
            double ax = lSite.getX();
            double ay = lSite.getY();
            double bx = site.getX() - ax;
            double by = site.getY() - ay;
            SiteEvent rSite = rArc.getSite();
            double cx = rSite.getX() - ax;
            double cy = rSite.getY() - ay;
            double d = 2 * (bx * cy - by * cx);
            double hb = bx * bx + by * by;
            double hc = cx * cx + cy * cy;
            Vector2D vertex = diagram.createVertex((cy * hb - by * hc) / d + ax, (bx * hc - cx * hb) / d + ay);

            // one transition disappear
            rArc.getEdge().setEdgeStartPoint(lSite, rSite, vertex);

            // two new transitions appear at the new vertex location
            newArc.setEdge(this.diagram.createEdge(lSite, site, null, vertex));
            rArc.setEdge(this.diagram.createEdge(site, rSite, null, vertex));

            // check whether the left and right beach sections are collapsing
            // and if so create circle events, to handle the point of collapse.
            attachCircleEvent(lArc);
            attachCircleEvent(rArc);
        }
    }

    public void removeBeachSection(BeachSection beachSection) {

        CircleEvent circle = beachSection.getCircleEvent();
        double x = circle.getEventCoordinate().getX();
        double y = circle.getyCenter();
        Vector2D vertex = diagram.createVertex(x, y);
        BeachSection previous = beachSection.getPrevious();
        BeachSection next = beachSection.getNext();

        LinkedList<BeachSection> disappearingTransitions = new LinkedList<>();
        disappearingTransitions.push(beachSection);

        // remove collapsed beach section from beach line
        logger.trace("Beach section {} collapsed, removing it.", beachSection);
        detachBeachsection(beachSection);

        // there could be more than one empty arc at the deletion point, this
        // happens when more than two edges are linked by the same vertex,
        // so we will collect all those edges by looking up both sides of
        // the deletion point.
        // by the way, there is *always* a predecessor/successor to any collapsed
        // beach section, it's just impossible to have a collapsing first/last
        // beach sections on the beachline, since they obviously are unconstrained
        // on their left/right side.

        // look left
        BeachSection lArc = previous;
        while (lArc.getCircleEvent() != null &&
                Math.abs(x - lArc.getCircleEvent().getEventCoordinate().getX()) < 1e-9 &&
                Math.abs(y - lArc.getCircleEvent().getyCenter()) < 1e-9) {
            logger.trace("Found beach section to the left - detaching {}.", lArc);
            previous = lArc.getPrevious();
            disappearingTransitions.push(lArc);
            detachBeachsection(lArc);
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
                Math.abs(y - rArc.getCircleEvent().getyCenter()) < 1e-9) {
            logger.trace("Found beach section to the left - detaching {}.", rArc);
            next = rArc.getNext();
            disappearingTransitions.offer(rArc);
            detachBeachsection(rArc);
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
            rArc.getEdge().setEdgeStartPoint(lArc.getSite(), rArc.getSite(), vertex);
        }

        // create a new edge as we have now a new transition between
        // two beach sections which were previously not adjacent.
        // since this edge appears as a new vertex is defined, the vertex
        // actually define an end point of the edge (relative to the site
        // on the left)
        lArc = disappearingTransitions.getFirst();
        rArc = disappearingTransitions.getLast();
        rArc.setEdge(this.diagram.createEdge(lArc.getSite(), rArc.getSite(), null, vertex));

        // create circle events if any for beach sections left in the beachline
        // adjacent to collapsed sections
        attachCircleEvent(lArc);
        attachCircleEvent(rArc);
    }

    public void detachBeachsection(BeachSection beachSection) {
        detachCircleEvent(beachSection);
        this.beachline.removeNode(beachSection);
    }

    private double leftBreakPoint(BeachSection node, double directrix) {
        logger.trace("Calculating break point for node "+node+" and directrix "+directrix);
        SiteEvent site = node.getSite();
        double rfocx = site.getX();
        double rfocy = site.getY();
        // distance or delta to the directrix
        double pby2 = rfocy - directrix;
        // focus is on the directrix
        if (pby2 == 0.0) {
            return rfocx;
        }

        // handle previous  beach line section
        BeachSection previousSection = node.getPrevious();
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

        double hl = lfocx - rfocx;
        double aby2 = 1 / pby2 - 1 / plby2;
        double b = hl / plby2;

        if (aby2 != 0.0) {
            return (-b + Math.sqrt(b * b - 2 * aby2 * (hl * hl / (-2 * plby2) - lfocy + plby2 / 2 + rfocy - pby2 / 2))) / aby2 + rfocx;
        }
        // both parabolas have same distance to directrix, thus break point is midway
        return (rfocx + lfocx) / 2;
    }

    private double rightBreakPoint(BeachSection node, double directrix) {
        BeachSection rArc = node.getNext();
        if (rArc != null) {
            return leftBreakPoint(rArc, directrix);
        }
        SiteEvent site = node.getSite();
        double result = site.getY() == directrix ? site.getX() : Double.POSITIVE_INFINITY;
        logger.trace("No beach section to the right.", result);
        return result;
    }

    private void detachCircleEvent(BeachSection arc) {
        CircleEvent circleEvent = arc.getCircleEvent();
        if (circleEvent != null) {
            this.circleEvents.remove(circleEvent);
            arc.setCircleEvent(null);
        }
    }

    private void attachCircleEvent(BeachSection arc) {
        BeachSection lArc = arc.getPrevious();
        BeachSection rArc = arc.getNext();

        if (lArc == null || rArc == null) {
            return;
        }

        SiteEvent lSite = lArc.getSite();
        SiteEvent cSite = arc.getSite();
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
        circleEvent.setBeachSection(arc);
        circleEvent.setSite(cSite);
        circleEvent.setEventCoordinate(new Vector2D(x + bx, ycenter + Math.sqrt(x * x + y * y)));
        circleEvent.setyCenter(ycenter);
        arc.setCircleEvent(circleEvent);

        // add newly created circle event
        this.circleEvents.add(circleEvent);
    }

    public CircleEvent getFirstCircleEvent() {
        if (this.circleEvents.isEmpty()) {
            return null;
        } else {
            return this.circleEvents.first();
        }
    }

    public VoronoiDiagram getDiagram() {
        return this.diagram;
    }

    public void setDiagram(VoronoiDiagram diagram) {
        this.diagram = diagram;
    }
}
