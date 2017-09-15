package de.bioforscher.singa.mathematics.algorithms.voronoi.model;

import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Christoph on 20/05/2017.
 */
public class VoronoiDiagram {

    private static final Logger logger = LoggerFactory.getLogger(VoronoiDiagram.class);

    private Map<Integer, VoronoiCell> cells;
    private List<VoronoiEdge> edges;
    private List<Vector2D> vertices;

    private double leftBorder;
    private double rightBorder;
    private double topBorder;
    private double bottomBorder;

    public VoronoiDiagram(Rectangle boundingBox) {
        this.cells = new HashMap<>();
        this.edges = new ArrayList<>();
        this.vertices = new ArrayList<>();

        this.leftBorder = boundingBox.getLeftMostXPosition();
        this.rightBorder = boundingBox.getRightMostXPosition();
        this.topBorder = boundingBox.getBottomMostYPosition();
        this.bottomBorder = boundingBox.getTopMostYPosition();
    }

    public List<VoronoiEdge> getEdges() {
        return this.edges;
    }


    public void setEdges(List<VoronoiEdge> edges) {
        this.edges = edges;
    }

    public VoronoiEdge createEdge(SiteEvent lSite, SiteEvent rSite, Vector2D va, Vector2D vb) {
        VoronoiEdge edge = new VoronoiEdge(lSite, rSite);
        this.edges.add(edge);
        if (va != null) {
            edge.setEdgeStartPoint(lSite, rSite, va);
        }
        if (vb != null) {
            edge.setEdgeEndPoint(lSite, rSite, vb);
        }
        this.cells.get(lSite.getIdentifier()).getHalfEdges().add(new VoronoiHalfEdge(edge, lSite, rSite));
        this.cells.get(rSite.getIdentifier()).getHalfEdges().add(new VoronoiHalfEdge(edge, rSite, lSite));
        return edge;
    }

    public VoronoiEdge createEdge(SiteEvent lSite, SiteEvent rSite) {
        return createEdge(lSite, rSite, null, null);
    }

    public VoronoiEdge createBorderEdge(SiteEvent lSite, Vector2D va, Vector2D vb) {
        VoronoiEdge edge = new VoronoiEdge(lSite, null);
        edge.setStartingPoint(va);
        edge.setEndingPoint(vb);
        this.edges.add(edge);
        return edge;
    }

    public List<Vector2D> getVertices() {
        return this.vertices;
    }

    public void setVertices(List<Vector2D> vertices) {
        this.vertices = vertices;
    }

    public Vector2D createVertex(double x, double y) {
        Vector2D vertex = new Vector2D(x, y);
        this.vertices.add(vertex);
        return vertex;
    }

    public VoronoiCell createCell(int siteId, SiteEvent site) {
        site.setIdentifier(siteId);
        VoronoiCell cell = new VoronoiCell(site);
        this.cells.put(siteId, cell);
        return cell;
    }

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
                edge.setStartingPoint(null);
                edge.setEndingPoint(null);
                edges.remove(edge);
            } else {
                logger.trace(" Post processed edge: {}, starting at {}, ending at {}", iEdge, edge.getStartingPoint(), edge.getEndingPoint());
            }
        }
    }

    public boolean connectEdge(int iEdge, VoronoiEdge edge) {
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
        this.cells.get(lSite.getIdentifier()).setCloseMe(true);
        this.cells.get(rSite.getIdentifier()).setCloseMe(true);

        // get the line equation of the bisector if line is not vertical
        double fm = 0.0;
        double fb = 0.0;

        if (ry != ly) {
            fm = (lx - rx) / (ry - ly);
            fb = fy - fm * fx;
        }

        // remember, direction of line (relative to left site):
        // upward: left.x < right.x
        // downward: left.x > right.x
        // horizontal: left.x == right.x
        // upward: left.x < right.x
        // rightward: left.y < right.y
        // leftward: left.y > right.y
        // vertical: left.y == right.y

        // depending on the direction, find the best side of the
        // bounding box to use to determine a reasonable start point

        // rhill 2013-12-02:
        // While at it, since we have the values which define the line,
        // clip the end of va if it is outside the bbox.
        // https://github.com/gorhill/Javascript-Voronoi/issues/15
        // TODO: Do all the clipping here rather than rely on Liang-Barsky
        // which does not do well sometimes due to loss of arithmetic
        // precision. The code here doesn't degrade if one of the vertex is
        // at a huge distance.

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

    public boolean clipEdge(VoronoiEdge edge) {
        // could use this in renderer
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

        // if t0 > 0, va needs to change
        // rhill 2011-06-03: we need to create a new vertex rather
        // than modifying the existing one, since the existing
        // one is likely shared with at least another edge
        if (t0 > 0) {
            edge.setStartingPoint(createVertex(ax + t0 * dx, ay + t0 * dy));
        }

        // va and/or vb were clipped, thus we will need to close
        // cells which use this edge.
        if (t0 > 0 || t1 < 1) {
            this.cells.get(edge.getLeftSite().getIdentifier()).setCloseMe(true);
            this.cells.get(edge.getRightSite().getIdentifier()).setCloseMe(true);
        }
        return true;
    }

    public void closeCells() {
        for (int iCell = this.cells.size() - 1; iCell >= 0; iCell--) {
            VoronoiCell cell = cells.get(iCell);
            boolean lastBorderSegment = false;
            // prune, order halfedges counterclockwise, then add missing ones
            // required to close cells
            if (cell.prepareHalfEdges() != 0) {
                continue;
            }
            if (!cell.isCloseMe()) {
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
                Vector2D vz = halfEdges.get((iLeft + 1) % nHalfedges).getEndPoint();
                // if end point is not equal to start point, we need to add the missing
                // halfedge(s) up to vz
                if (Math.abs(va.getX() - vz.getX()) >= 1e-9 || Math.abs(va.getY() - vz.getY()) >= 1e-9) {
                    // rhill 2013-12-02:
                    // "Holes" in the halfedges are not necessarily always adjacent.
                    // https://github.com/gorhill/Javascript-Voronoi/issues/16

                    // find entry point:
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

    private static boolean equalWithEpsilon(double a, double b) {
        return Math.abs(a - b) < 1e-9;
    }

    private static boolean greaterThanWithEpsilon(double a, double b) {
        return a - b > 1e-9;
    }

    private static boolean greaterThanOrEqualWithEpsilon(double a, double b) {
        return b - a < 1e-9;
    }

    private static boolean lessThanWithEpsilon(double a, double b) {
        return b - a > 1e-9;
    }

    private static boolean lessThanOrEqualWithEpsilon(double a, double b) {
        return a - b < 1e-9;
    }

}
