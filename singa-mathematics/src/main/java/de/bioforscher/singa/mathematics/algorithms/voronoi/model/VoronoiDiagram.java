package de.bioforscher.singa.mathematics.algorithms.voronoi.model;

import de.bioforscher.singa.mathematics.algorithms.voronoi.Voronoi;
import de.bioforscher.singa.mathematics.geometry.edges.Line;
import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Contains all information about the voronoi diagram created by {@link Voronoi#generateVoronoiDiagram(Collection,
 * Rectangle)}. Vertices are created for every point two edges meet and for intersections with the bounding box. Edges
 * are splitting cells that are associated to each of the processed vectors.
 */
public class VoronoiDiagram {

    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(VoronoiDiagram.class);

    /**
     * The cells mapped by the identifier of the processed site.
     */
    private Map<Integer, VoronoiCell> cells;

    /**
     * The edges splitting cells.
     */
    private List<VoronoiEdge> edges;

    /**
     * All intersections of edges and bounding box.
     */
    private List<Vector2D> vertices;

    /**
     * Left border of the bounding box
     */
    private double leftBorder;

    /**
     * Right border of the bounding box
     */
    private double rightBorder;

    /**
     * Top border of the bounding box.
     */
    private double topBorder;

    /**
     * Bottom border of the bounding box.
     */
    private double bottomBorder;

    Line top = new Line(0, 0, 500, 0);
    Line bottom = new Line(0, 500, 500, 500);
    Line left = new Line(0, 0, 0, 500);
    Line right = new Line(500, 0, 500, 500);

    /**
     * Creates a new Voronoi diagram with the bounding box.
     *
     * @param boundingBox The bounding box surrounding the voronoi diagram.
     */
    VoronoiDiagram(Rectangle boundingBox) {
        this.cells = new HashMap<>();
        this.edges = new ArrayList<>();
        this.vertices = new ArrayList<>();

        this.leftBorder = boundingBox.getLeftMostXPosition();
        this.rightBorder = boundingBox.getRightMostXPosition();
        this.topBorder = boundingBox.getBottomMostYPosition();
        this.bottomBorder = boundingBox.getTopMostYPosition();
    }

    /**
     * Gets all edges of the voronoi diagram.
     *
     * @return
     */
    public List<VoronoiEdge> getEdges() {
        return this.edges;
    }

    /**
     * Creates a new edge, adds it to the diagram and returns it.
     *
     * @param leftSite The site event on the left of this edge.
     * @param rightSite The site event on the right of this edge.
     * @param startingPoint The starting point.
     * @param endingPoint The ending point.
     * @return The edge.
     */
    VoronoiEdge createEdge(SiteEvent leftSite, SiteEvent rightSite, Vector2D startingPoint, Vector2D endingPoint) {
        VoronoiEdge edge = new VoronoiEdge(leftSite, rightSite);
        this.edges.add(edge);
        if (startingPoint != null) {
            edge.setStartingPoint(leftSite, rightSite, startingPoint);
        }
        if (endingPoint != null) {
            edge.setEndingPoint(leftSite, rightSite, endingPoint);
        }
        this.cells.get(leftSite.getIdentifier()).getHalfEdges().add(new VoronoiHalfEdge(edge, leftSite, rightSite));
        this.cells.get(rightSite.getIdentifier()).getHalfEdges().add(new VoronoiHalfEdge(edge, rightSite, leftSite));
        return edge;
    }

    /**
     * Creates a new edge, adds it to the diagram and returns it.
     *
     * @param leftSite The site event on the left of this edge.
     * @param rightSite The site event on the right of this edge.
     * @return The edge.
     */
    VoronoiEdge createEdge(SiteEvent leftSite, SiteEvent rightSite) {
        return createEdge(leftSite, rightSite, null, null);
    }

    /**
     * Creates a new edge associated with the border of the diagram, adds it to the diagram and returns it.
     *
     * @param leftSite The site event on the left of this edge.
     * @param startingPoint The starting point.
     * @param endingPoint The ending point.
     * @return The edge.
     */
    private VoronoiEdge createBorderEdge(SiteEvent leftSite, Vector2D startingPoint, Vector2D endingPoint) {
        VoronoiEdge edge = new VoronoiEdge(leftSite, null);
        edge.setStartingPoint(startingPoint);
        edge.setEndingPoint(endingPoint);
        this.edges.add(edge);
        return edge;
    }

    /**
     * Returns all vertices of the diagram.
     *
     * @return All vertices of the diagram.
     */
    public List<Vector2D> getVertices() {
        return this.vertices;
    }

    /**
     * Creates a new vertex, adds it to the diagram and returns it.
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @return The vertex.
     */
    Vector2D createVertex(double x, double y) {
        return createVertex(new Vector2D(x, y));
    }

    /**
     * Creates a new vertex, adds it to the diagram and returns it.
     *
     * @param vertex The vertex.
     * @return The vertex.
     */
    Vector2D createVertex(Vector2D vertex) {
        this.vertices.add(vertex);
        return vertex;
    }


    /**
     * Creates a new cell, adds it to the diagram and returns it.
     *
     * @param siteIdentifier The identifier of the site.
     * @param site The site.
     * @return The cell.
     */
    public VoronoiCell createCell(int siteIdentifier, SiteEvent site) {
        site.setIdentifier(siteIdentifier);
        VoronoiCell cell = new VoronoiCell(site);
        this.cells.put(siteIdentifier, cell);
        return cell;
    }

    /**
     * Returns all cells.
     *
     * @return All cells.
     */
    public Collection<VoronoiCell> getCells() {
        return this.cells.values();
    }

    /**
     * Clips all edges sticking out of the bounding box.
     */
    public void clipEdges() {
        // connect all dangling edges to bounding box
        // or get rid of them if it can't be done
        // iterate backward so we can splice safely
        for (int iEdge = this.edges.size() - 1; iEdge >= 0; iEdge--) {
            VoronoiEdge edge = this.edges.get(iEdge);
            logger.trace("Post processing edge {}, starting at {}, ending at {}", iEdge, edge.getStartingPoint(), edge.getEndingPoint());
            // edge is removed if:
            //   it is wholly outside the bounding box
            //   it is looking more like a point than a line
            if (!connectEdge(iEdge, edge) ||
                    !clipEdge(edge) ||
                    (Math.abs(edge.getStartingPoint().getX() - edge.getEndingPoint().getX()) < 1e-9 && Math.abs(edge.getStartingPoint().getY() - edge.getEndingPoint().getY()) < 1e-9)) {
                logger.trace(" Removing edge {}, starting at {}, ending at {}", iEdge, edge.getStartingPoint(), edge.getEndingPoint());
                edges.remove(edge);
            } else {
                logger.trace(" Post processed edge: {}, starting at {}, ending at {}", iEdge, edge.getStartingPoint(), edge.getEndingPoint());
            }
        }
    }

    /**
     * Connects the edge if it has no associated ending point.
     *
     * @param iEdge The identifier of the edge.
     * @param edge The edge.
     * @return True if the edge was connected, false if nothing could be done.
     */
    private boolean connectEdge(int iEdge, VoronoiEdge edge) {
        // skip if end point already connected
        Vector2D vb = edge.getEndingPoint();
        if (vb != null) {
            return true;
        }

        Vector2D va = edge.getStartingPoint();
        SiteEvent lSite = edge.getLeftSite();
        SiteEvent rSite = edge.getRightSite();
        double lx = lSite.getX();
        double ly = lSite.getY();
        double rx = rSite.getX();
        double ry = rSite.getY();
        double fx = (lx + rx) / 2;
        double fy = (ly + ry) / 2;

        // if we reach here, this means cells which use this edge will need
        // to be closed, whether because the edge was removed, or because it
        // was connected to the bounding box.
        this.cells.get(lSite.getIdentifier()).setClosed(false);
        this.cells.get(rSite.getIdentifier()).setClosed(false);

        // get the line equation of the bisector if line is not vertical
        double fm = 0.0;
        double fb = 0.0;

        if (ry != ly) {
            fm = (lx - rx) / (ry - ly);
            fb = fy - fm * fx;
        }

        // depending on the direction, find the best side of the
        // bounding box to use to determine a reasonable start point
        if (Double.isInfinite(fm)) {
            // special case: vertical line
            if (fx < leftBorder || fx >= rightBorder) {
                // doesn't intersect with viewport
                return false;
            }
            if (lx > rx) {
                // downward
                if (va == null || va.getY() < topBorder) {
                    va = createVertex(fx, topBorder);
                } else if (va.getY() >= bottomBorder) {
                    return false;
                }
                vb = this.createVertex(fx, bottomBorder);
            } else {
                // upward
                if (va == null || va.getY() > bottomBorder) {
                    va = this.createVertex(fx, bottomBorder);
                } else if (va.getY() < topBorder) {
                    return false;
                }
                vb = this.createVertex(fx, topBorder);
            }
        } else if (fm < -1 || fm > 1) {
            // closer to vertical than horizontal, connect start point to the
            // top or bottom side of the bounding box
            if (lx > rx) {
                // downward
                if (va == null || va.getY() < topBorder) {
                    va = this.createVertex((topBorder - fb) / fm, topBorder);
                } else if (va.getY() >= bottomBorder) {
                    return false;
                }
                vb = this.createVertex((bottomBorder - fb) / fm, bottomBorder);
            } else {
                // upward
                if (va == null || va.getY() > bottomBorder) {
                    va = this.createVertex((bottomBorder - fb) / fm, bottomBorder);
                } else if (va.getY() < topBorder) {
                    return false;
                }
                vb = this.createVertex((topBorder - fb) / fm, topBorder);
            }
        } else {
            // closer to horizontal than vertical, connect start point to the
            // left or right side of the bounding box
            if (ly < ry) {
                // rightward
                if (va == null || va.getX() < leftBorder) {
                    va = this.createVertex(leftBorder, fm * leftBorder + fb);
                } else if (va.getX() >= rightBorder) {
                    return false;
                }
                vb = this.createVertex(rightBorder, fm * rightBorder + fb);
            } else {
                // leftward
                if (va == null || va.getX() > rightBorder) {
                    va = this.createVertex(rightBorder, fm * rightBorder + fb);
                } else if (va.getX() < leftBorder) {
                    return false;
                }
                vb = this.createVertex(leftBorder, fm * leftBorder + fb);
            }
        }
        // set points
        edge.setStartingPoint(va);
        edge.setEndingPoint(vb);
        logger.trace("Connected edge {} to {} and {}.", iEdge, va, vb);
        return true;
    }

    /**
     * Clips the edge if it sticks out of the bounding box.
     *
     * @param edge The Edge.
     * @return True if the edge was clipped, false if nothing was done.
     */
    private boolean clipEdge(VoronoiEdge edge) {

        double ax = edge.getStartingPoint().getX();
        double ay = edge.getStartingPoint().getY();
        double bx = edge.getEndingPoint().getX();
        double by = edge.getEndingPoint().getY();

        double t0 = 0.0;
        double t1 = 1.0;

        double dx = bx - ax;
        double dy = by - ay;

        // left
        double q = ax - leftBorder;
        if (dx == 0.0 && q < 0) {
            return false;
        }
        double r = -q / dx;
        if (dx < 0) {
            if (r < t0) {
                return false;
            }
            if (r < t1) {
                t1 = r;
            }
        } else if (dx > 0) {
            if (r > t1) {
                return false;
            }
            if (r > t0) {
                t0 = r;
            }
        }

        // right
        q = rightBorder - ax;
        if (dx == 0 && q < 0) {
            return false;
        }
        r = q / dx;
        if (dx < 0) {
            if (r > t1) {
                return false;
            }
            if (r > t0) {
                t0 = r;
            }
        } else if (dx > 0) {
            if (r < t0) {
                return false;
            }
            if (r < t1) {
                t1 = r;
            }
        }

        // top
        q = ay - topBorder;
        if (dy == 0 && q < 0) {
            return false;
        }
        r = -q / dy;
        if (dy < 0) {
            if (r < t0) {
                return false;
            }
            if (r < t1) {
                t1 = r;
            }
        } else if (dy > 0) {
            if (r > t1) {
                return false;
            }
            if (r > t0) {
                t0 = r;
            }
        }

        // bottom
        q = bottomBorder - ay;
        if (dy == 0 && q < 0) {
            return false;
        }
        r = q / dy;
        if (dy < 0) {
            if (r > t1) {
                return false;
            }
            if (r > t0) {
                t0 = r;
            }
        } else if (dy > 0) {
            if (r < t0) {
                return false;
            }
            if (r < t1) {
                t1 = r;
            }
        }

        // if we reach this point, Voronoi edge is within bbox

        // we need to create a new vertex rather
        // than modifying the existing one, since the existing
        // one is likely shared with at least another edge
        if (t0 > 0) {
            edge.setStartingPoint(createVertex(ax + t0 * dx, ay + t0 * dy));
        }

        // va and/or vb were clipped, thus we will need to close
        // cells which use this edge.
        if (t0 > 0 || t1 < 1) {
            this.cells.get(edge.getLeftSite().getIdentifier()).setClosed(false);
            this.cells.get(edge.getRightSite().getIdentifier()).setClosed(false);
        }

        return true;
    }

    /**
     * Closes all cells that have not been closed until now.
     */
    public void closeCells() {
        boolean lastBorderSegment;
        for (VoronoiCell cell : cells.values()) {
            // prune, order halfedges counterclockwise, then add missing ones
            // required to close cells
            if (cell.prepareHalfEdges() != 0) {
                continue;
            }
            if (cell.isClosed()) {
                continue;
            }
            // find first 'unclosed' point.
            // an 'unclosed' point will be the end point of a halfedge which
            // does not match the start point of the following halfedge
            List<VoronoiHalfEdge> halfEdges = cell.getHalfEdges();
            int nHalfedges = halfEdges.size();

            int iLeft = 0;
            while (iLeft < nHalfedges) {
                Vector2D va = halfEdges.get(iLeft).getEndPoint();
                Vector2D vz = halfEdges.get((iLeft + 1) % nHalfedges).getStartPoint();
                // if end point is not equal to start point, we need to add the missing
                // halfedge(s) up to vz
                if (Math.abs(va.getX() - vz.getX()) >= 1e-9 || Math.abs(va.getY() - vz.getY()) >= 1e-9) {

                    // walk downward along left side
                    if (equalWithEpsilon(va.getX(), leftBorder) && lessThanWithEpsilon(va.getY(), bottomBorder)) {
                        lastBorderSegment = equalWithEpsilon(vz.getX(), leftBorder);
                        Vector2D vb = createVertex(leftBorder, lastBorderSegment ? vz.getY() : bottomBorder);
                        VoronoiEdge edge = createBorderEdge(cell.getSite(), va, vb);
                        iLeft++;
                        halfEdges.add(iLeft, new VoronoiHalfEdge(edge, cell.getSite(), null));
                        nHalfedges++;
                        if (lastBorderSegment) {
                            break;
                        }
                        va = vb;
                    }

                    // walk rightward along bottom side
                    if (equalWithEpsilon(va.getY(), bottomBorder) && lessThanWithEpsilon(va.getX(), rightBorder)) {
                        lastBorderSegment = equalWithEpsilon(vz.getY(), bottomBorder);
                        Vector2D vb = this.createVertex(lastBorderSegment ? vz.getX() : rightBorder, bottomBorder);
                        VoronoiEdge edge = this.createBorderEdge(cell.getSite(), va, vb);
                        iLeft++;
                        halfEdges.add(iLeft, new VoronoiHalfEdge(edge, cell.getSite(), null));
                        nHalfedges++;
                        if (lastBorderSegment) {
                            break;
                        }
                        va = vb;
                    }

                    // walk upward along right side
                    if (equalWithEpsilon(va.getX(), rightBorder) && greaterThanWithEpsilon(va.getY(), topBorder)) {
                        lastBorderSegment = equalWithEpsilon(vz.getX(), rightBorder);
                        Vector2D vb = this.createVertex(rightBorder, lastBorderSegment ? vz.getY() : topBorder);
                        VoronoiEdge edge = this.createBorderEdge(cell.getSite(), va, vb);
                        iLeft++;
                        halfEdges.add(iLeft, new VoronoiHalfEdge(edge, cell.getSite(), null));
                        nHalfedges++;
                        if (lastBorderSegment) {
                            break;
                        }
                        va = vb;
                    }

                    // walk leftward along top side
                    if (equalWithEpsilon(va.getY(), topBorder) && greaterThanWithEpsilon(va.getX(), leftBorder)) {
                        lastBorderSegment = equalWithEpsilon(vz.getY(), topBorder);
                        Vector2D vb = this.createVertex(lastBorderSegment ? vz.getX() : leftBorder, topBorder);
                        VoronoiEdge edge = this.createBorderEdge(cell.getSite(), va, vb);
                        iLeft++;
                        halfEdges.add(iLeft, new VoronoiHalfEdge(edge, cell.getSite(), null));
                        nHalfedges++;
                        if (lastBorderSegment) {
                            break;
                        }
                        va = vb;

                        // walk downward along left side
                        lastBorderSegment = equalWithEpsilon(vz.getX(), leftBorder);
                        vb = this.createVertex(leftBorder, lastBorderSegment ? vz.getY() : bottomBorder);
                        edge = this.createBorderEdge(cell.getSite(), va, vb);
                        iLeft++;
                        halfEdges.add(iLeft, new VoronoiHalfEdge(edge, cell.getSite(), null));
                        nHalfedges++;
                        if (lastBorderSegment) {
                            break;
                        }
                        va = vb;

                        // walk rightward along bottom side
                        lastBorderSegment = equalWithEpsilon(vz.getY(), bottomBorder);
                        vb = this.createVertex(lastBorderSegment ? vz.getX() : rightBorder, bottomBorder);
                        edge = this.createBorderEdge(cell.getSite(), va, vb);
                        iLeft++;
                        halfEdges.add(iLeft, new VoronoiHalfEdge(edge, cell.getSite(), null));
                        nHalfedges++;
                        if (lastBorderSegment) {
                            break;
                        }
                        va = vb;

                        // walk upward along right side
                        lastBorderSegment = equalWithEpsilon(vz.getX(), rightBorder);
                        vb = this.createVertex(rightBorder, lastBorderSegment ? vz.getY() : topBorder);
                        edge = this.createBorderEdge(cell.getSite(), va, vb);
                        iLeft++;
                        halfEdges.add(iLeft, new VoronoiHalfEdge(edge, cell.getSite(), null));
                        nHalfedges++;
                        if (lastBorderSegment) {
                            break;
                        }

                    }

                }

            }

        }
    }


    public void closeBorderCells() {


        for (VoronoiCell cell : cells.values()) {
            // prepare all half edges
            cell.prepareHalfEdges();
            // if
            if (cell.isOpen()) {
                final ListIterator<VoronoiHalfEdge> halfEdgeIterator = cell.getHalfEdges().listIterator();
                while (halfEdgeIterator.hasNext()) {
                    VoronoiHalfEdge current = halfEdgeIterator.next();
                    Vector2D startPoint = current.getStartPoint();
                    Vector2D endPoint = current.getEndPoint();

                    // check start
                    Vector2D newStart = getIntersectionWithBorder(startPoint, endPoint);
                    if (newStart != null) {
                        current.setStartPoint(newStart);

                        // add vertex
                        // createVertex(newStart);
                        // add edge
                        // replace half edge
                    }

                    // check end
                    Vector2D newEnd = getIntersectionWithBorder(endPoint,startPoint);
                    if (newEnd != null) {
                        current.setEndPoint(endPoint);
                        // add vertex
                        // createVertex(newEnd);
                        // add edge
                        // replace half edge
                    }

                }
            }

        }
    }

    /**
     * Returns the intersection with a border, if there is no intersection {@code null} is returned.
     * @param pointToCheck The point that is checked for misplacement.
     * @param fixedPoint The point that is used as a fixed reference.
     * @return Ihe intersection with a border, if there is no intersection {@code null} is returned.
     */
    public Vector2D getIntersectionWithBorder(Vector2D pointToCheck, Vector2D fixedPoint) {
        if (pointToCheck.getX() < leftBorder) {
            // calculate intersection with left border
            Line edgeLine = new Line(fixedPoint, pointToCheck);
            return left.getInterceptWithLine(edgeLine);
        }
        if (pointToCheck.getX() > rightBorder) {
            // calculate intersection with right border
            Line edgeLine = new Line(fixedPoint, pointToCheck);
            return right.getInterceptWithLine(edgeLine);
        }
        if (pointToCheck.getY() < topBorder) {
            // calculate intersection with top border
            Line edgeLine = new Line(fixedPoint, pointToCheck);
            return top.getInterceptWithLine(edgeLine);
        }
        if (pointToCheck.getY() > bottomBorder) {
            // calculate intersection with bottom border
            Line edgeLine = new Line(fixedPoint, pointToCheck);
            return bottom.getInterceptWithLine(edgeLine);
        }
        return null;
    }

    /**
     * Returns true if a is equal to b withing range of epsilon.
     *
     * @param a The first value.
     * @param b The second value.
     * @return true if a is equal to b withing range of epsilon.
     */
    private static boolean equalWithEpsilon(double a, double b) {
        return Math.abs(a - b) < 1e-9;
    }

    /**
     * Returns true if a is greater than b withing range of epsilon.
     *
     * @param a The first value.
     * @param b The second value.
     * @return true if a is greater than b withing range of epsilon.
     */
    private static boolean greaterThanWithEpsilon(double a, double b) {
        return a - b > 1e-9;
    }

    /**
     * Returns true if a is less than b withing range of epsilon.
     *
     * @param a The first value.
     * @param b The second value.
     * @return true if a is less than b withing range of epsilon.
     */
    private static boolean lessThanWithEpsilon(double a, double b) {
        return b - a > 1e-9;
    }

}
