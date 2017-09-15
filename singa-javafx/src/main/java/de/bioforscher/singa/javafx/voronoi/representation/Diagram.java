package de.bioforscher.singa.javafx.voronoi.representation;

import de.bioforscher.singa.javafx.voronoi.Site;
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
public class Diagram {

    private static final Logger logger = LoggerFactory.getLogger(Diagram.class);

    private Map<Integer, Cell> cells;
    private List<Edge> edges;
    private List<Vector2D> vertices;

    public Diagram() {
        this.cells = new HashMap<>();
        this.edges = new ArrayList<>();
        this.vertices = new ArrayList<>();
    }

    public List<Edge> getEdges() {
        return this.edges;
    }


    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }

    public Edge createEdge(Site lSite, Site rSite, Vector2D va, Vector2D vb) {
        Edge edge = new Edge(lSite, rSite);
        this.edges.add(edge);
        if (va != null) {
            edge.setEdgeStartPoint(lSite, rSite, va);
        }
        if (vb != null) {
            edge.setEdgeEndPoint(lSite, rSite, vb);
        }
        this.cells.get(lSite.getVoronoiId()).getHalfEdges().add(new HalfEdge(edge, lSite, rSite));
        this.cells.get(rSite.getVoronoiId()).getHalfEdges().add(new HalfEdge(edge, rSite, lSite));
        return edge;
    }

    public Edge createEdge(Site lSite, Site rSite) {
        return createEdge(lSite, rSite, null, null);
    }

    public Edge createBorderEdge(Site lSite, Vector2D va, Vector2D vb) {
        Edge edge = new Edge(lSite, null);
        edge.setVa(va);
        edge.setVb(vb);
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

    public Cell createCell(int siteId, Site site) {
        site.setVoronoiId(siteId);
        Cell cell = new Cell(site);
        this.cells.put(siteId, cell);
        return cell;
    }

    public void clipEdges(double[] bbox) {
        // connect all dangling edges to bounding box
        // or get rid of them if it can't be done
        // iterate backward so we can splice safely

        for (int iEdge = this.edges.size() - 1; iEdge >= 0; iEdge--) {
            Edge edge = this.edges.get(iEdge);
            logger.trace("Post processing edge {}, starting at {}, ending at {}", iEdge, edge.getVa(), edge.getVb());
            // edge is removed if:
            //   it is wholly outside the bounding box
            //   it is looking more like a point than a line
            if (!connectEdge(iEdge, edge, bbox) ||
                    !clipEdge(edge, bbox) ||
                    (Math.abs(edge.getVa().getX() - edge.getVb().getX()) < 1e-9 && Math.abs(edge.getVa().getY() - edge.getVb().getY()) < 1e-9)) {
                logger.trace(" Removing edge {}, starting at {}, ending at {}", iEdge, edge.getVa(), edge.getVb());
                edge.setVa(null);
                edge.setVb(null);
                edges.remove(edge);
            } else {
                logger.trace(" Post processed edge: {}, starting at {}, ending at {}", iEdge, edge.getVa(), edge.getVb());
            }
        }
    }

    public boolean connectEdge(int iEdge, Edge edge, double[] bbox) {
        // skip if end point already connected
        Vector2D vb = edge.getVb();
        if (vb != null) {
            return true;
        }

        Vector2D va = edge.getVa();
        // bbox = [x, y, width, height]
        double xl = bbox[0];
        double xr = bbox[2];
        double yt = bbox[1];
        double yb = bbox[3];
        Site lSite = edge.getlSite();
        Site rSite = edge.getrSite();
        double lx = lSite.getX();
        double ly = lSite.getY();
        double rx = rSite.getX();
        double ry = rSite.getY();
        double fx = (lx + rx) / 2;
        double fy = (ly + ry) / 2;

        // if we reach here, this means cells which use this edge will need
        // to be closed, whether because the edge was removed, or because it
        // was connected to the bounding box.
        this.cells.get(lSite.getVoronoiId()).setCloseMe(true);
        this.cells.get(rSite.getVoronoiId()).setCloseMe(true);

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
            if (fx < xl || fx >= xr) {
                // doesn't intersect with viewport
                return false;
            }
            if (lx > rx) {
                // downward
                if (va == null || va.getY() < yt) {
                    va = createVertex(fx, yt);
                } else if (va.getY() >= yb) {
                    return false;
                }
                vb = this.createVertex(fx, yb);
            } else {
                // upward
                if (va == null || va.getY() > yb) {
                    va = this.createVertex(fx, yb);
                } else if (va.getY() < yt) {
                    return false;
                }
                vb = this.createVertex(fx, yt);
            }
        } else if (fm < -1 || fm > 1) {
            // closer to vertical than horizontal, connect start point to the
            // top or bottom side of the bounding box
            if (lx > rx) {
                // downward
                if (va == null || va.getY() < yt) {
                    va = this.createVertex((yt - fb) / fm, yt);
                } else if (va.getY() >= yb) {
                    return false;
                }
                vb = this.createVertex((yb - fb) / fm, yb);
            } else {
                // upward
                if (va == null || va.getY() > yb) {
                    va = this.createVertex((yb - fb) / fm, yb);
                } else if (va.getY() < yt) {
                    return false;
                }
                vb = this.createVertex((yt - fb) / fm, yt);
            }
        } else {
            // closer to horizontal than vertical, connect start point to the
            // left or right side of the bounding box
            if (ly < ry) {
                // rightward
                if (va == null || va.getX() < xl) {
                    va = this.createVertex(xl, fm * xl + fb);
                } else if (va.getX() >= xr) {
                    return false;
                }
                vb = this.createVertex(xr, fm * xr + fb);
            } else {
                // leftward
                if (va == null || va.getX() > xr) {
                    va = this.createVertex(xr, fm * xr + fb);
                } else if (va.getX() < xl) {
                    return false;
                }
                vb = this.createVertex(xl, fm * xl + fb);
            }
        }
        // set points
        edge.setVa(va);
        edge.setVb(vb);
        logger.trace("Connected edge {} to {} and {}.", iEdge, va, vb);
        return true;
    }

    public boolean clipEdge(Edge edge, double[] bbox) {
        double xl = bbox[0];
        double xr = bbox[2];
        double yt = bbox[1];
        double yb = bbox[3];
        // could use this in renderer
        double ax = edge.getVa().getX();
        double ay = edge.getVa().getY();
        double bx = edge.getVb().getX();
        double by = edge.getVb().getY();

        double t0 = 0.0;
        double t1 = 1.0;

        double dx = bx - ax;
        double dy = by - ay;

        // left
        double q = ax - xl;
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
        q = xr - ax;
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
        q = ay - yt;
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
        q = yb - ay;
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
            edge.setVa(createVertex(ax + t0 * dx, ay + t0 * dy));
        }

        // va and/or vb were clipped, thus we will need to close
        // cells which use this edge.
        if (t0 > 0 || t1 < 1) {
            this.cells.get(edge.getlSite().getVoronoiId()).setCloseMe(true);
            this.cells.get(edge.getrSite().getVoronoiId()).setCloseMe(true);
        }
        return true;
    }

    public void closeCells(double[] bbox) {
        double xl = bbox[0];
        double xr = bbox[2];
        double yt = bbox[1];
        double yb = bbox[3];

        for (int iCell = this.cells.size() - 1; iCell >= 0; iCell--) {
            Cell cell = cells.get(iCell);
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
            List<HalfEdge> halfEdges = cell.getHalfEdges();
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
                    if (equalWithEpsilon(va.getX(), xl) && lessThanWithEpsilon(va.getY(), yb)) {
                        lastBorderSegment = equalWithEpsilon(vz.getX(), xl);
                        Vector2D vb = createVertex(xl, lastBorderSegment ? vz.getY() : yb);
                        Edge edge = createBorderEdge(cell.getSite(), va, vb);
                        iLeft++;
                        halfEdges.add(iLeft, new HalfEdge(edge, cell.getSite(), null));
                        nHalfedges++;
                        if (lastBorderSegment) {
                            break;
                        }
                        va = vb;
                    }

                    // walk rightward along bottom side
                    if (equalWithEpsilon(va.getY(), yb) && lessThanWithEpsilon(va.getX(), xr)) {
                        lastBorderSegment = equalWithEpsilon(vz.getY(), yb);
                        Vector2D vb = this.createVertex(lastBorderSegment ? vz.getX() : xr, yb);
                        Edge edge = this.createBorderEdge(cell.getSite(), va, vb);
                        iLeft++;
                        halfEdges.add(iLeft, new HalfEdge(edge, cell.getSite(), null));
                        nHalfedges++;
                        if (lastBorderSegment) {
                            break;
                        }
                        va = vb;
                    }

                    // walk upward along right side
                    if (equalWithEpsilon(va.getX(), xr) && greaterThanWithEpsilon(va.getY(), yt)) {
                        lastBorderSegment = equalWithEpsilon(vz.getX(), xr);
                        Vector2D vb = this.createVertex(xr, lastBorderSegment ? vz.getY() : yt);
                        Edge edge = this.createBorderEdge(cell.getSite(), va, vb);
                        iLeft++;
                        halfEdges.add(iLeft, new HalfEdge(edge, cell.getSite(), null));
                        nHalfedges++;
                        if (lastBorderSegment) {
                            break;
                        }
                        va = vb;
                    }

                    // walk leftward along top side
                    if (equalWithEpsilon(va.getY(), yt) && greaterThanWithEpsilon(va.getX(), xl)) {
                        lastBorderSegment = equalWithEpsilon(vz.getY(), yt);
                        Vector2D vb = this.createVertex(lastBorderSegment ? vz.getX() : xl, yt);
                        Edge edge = this.createBorderEdge(cell.getSite(), va, vb);
                        iLeft++;
                        halfEdges.add(iLeft, new HalfEdge(edge, cell.getSite(), null));
                        nHalfedges++;
                        if (lastBorderSegment) {
                            break;
                        }
                        va = vb;

                        // walk downward along left side
                        lastBorderSegment = equalWithEpsilon(vz.getX(), xl);
                        vb = this.createVertex(xl, lastBorderSegment ? vz.getY() : yb);
                        edge = this.createBorderEdge(cell.getSite(), va, vb);
                        iLeft++;
                        halfEdges.add(iLeft, new HalfEdge(edge, cell.getSite(), null));
                        nHalfedges++;
                        if (lastBorderSegment) {
                            break;
                        }
                        va = vb;

                        // walk rightward along bottom side
                        lastBorderSegment = equalWithEpsilon(vz.getY(), yb);
                        vb = this.createVertex(lastBorderSegment ? vz.getX() : xr, yb);
                        edge = this.createBorderEdge(cell.getSite(), va, vb);
                        iLeft++;
                        halfEdges.add(iLeft, new HalfEdge(edge, cell.getSite(), null));
                        nHalfedges++;
                        if (lastBorderSegment) {
                            break;
                        }
                        va = vb;

                        // walk upward along right side
                        lastBorderSegment = equalWithEpsilon(vz.getX(), xr);
                        vb = this.createVertex(xr, lastBorderSegment ? vz.getY() : yt);
                        edge = this.createBorderEdge(cell.getSite(), va, vb);
                        iLeft++;
                        halfEdges.add(iLeft, new HalfEdge(edge, cell.getSite(), null));
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
