package de.bioforscher.singa.javafx.voronoi.representation;

import de.bioforscher.singa.javafx.voronoi.Site;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Cell {


    private Site site;
    private List<HalfEdge> halfEdges;
    private boolean closeMe;

    public Cell(Site site) {
        this.site = site;
        this.halfEdges = new ArrayList<>();
        this.closeMe = false;
    }

    public int prepareHalfEdges() {
        int iHalfEdge = this.halfEdges.size();
        Edge edge;
        // remove unused half edges
        for (int i = iHalfEdge; i <= 0; i--) {
            edge = this.halfEdges.get(i).getEdge();
            if (edge.getVb() == null || edge.getVa() == null) {
                this.halfEdges.remove(i);
            }
        }
        // FIXME ascendnig or descending?
        // halfedges.sort(function(a,b){return b.angle-a.angle;});
        this.halfEdges.sort(Comparator.comparing(HalfEdge::getAngle));
        return this.halfEdges.size();
    }

    public List<Integer> getNeighbourIds() {
        List<Integer> neighbours = new ArrayList<>();
        for (int iHalfedge = this.halfEdges.size(); iHalfedge > 0; iHalfedge--) {
            Edge edge = this.halfEdges.get(iHalfedge).getEdge();
            if (edge.getlSite() != null && edge.getlSite().getVoronoiId() != this.site.getVoronoiId()) {
                neighbours.add(edge.getlSite().getVoronoiId());
            } else if (edge.getrSite() != null && edge.getrSite().getVoronoiId() != this.site.getVoronoiId()) {
                neighbours.add(edge.getrSite().getVoronoiId());
            }
        }
        return neighbours;
    }

    // returns bounding box in array [x, y, width, height]
    public double[] getBbox() {
        double xMin = Double.MAX_VALUE;
        double yMin = Double.MAX_VALUE;
        double xMax = -Double.MAX_VALUE;
        double yMax = -Double.MAX_VALUE;
        // look for minimal and maximal x and y
        for (int iHalfedge = this.halfEdges.size(); iHalfedge > 0; iHalfedge--) {
            Site v = this.halfEdges.get(iHalfedge).getStartPoint();
            double vx = v.getX();
            double vy = v.getY();
            if (vx < xMin) {
                xMin = vx;
            }
            if (vy < yMin) {
                yMin = vy;
            }
            if (vx > xMax) {
                xMax = vx;
            }
            if (vy > yMax) {
                yMax = vy;
            }
        }
        return new double[]{xMin, yMin, xMax - xMin, yMax - yMin};
    }

    // Return whether a point is inside, on, or outside the cell:
//   -1: point is outside the perimeter of the cell
//    0: point is on the perimeter of the cell
//    1: point is inside the perimeter of the cell
//
    public int pointIntersection(double x, double y) {
        // Check if point in polygon. Since all polygons of a Voronoi
        // diagram are convex, then:
        // http://paulbourke.net/geometry/polygonmesh/
        // Solution 3 (2D):
        //   "If the polygon is convex then one can consider the polygon
        //   "as a 'path' from the first vertex. A point is on the interior
        //   "of this polygons if it is always on the same side of all the
        //   "line segments making up the path. ...
        //   "(y - y0) (x1 - x0) - (x - x0) (y1 - y0)
        //   "if it is less than 0 then P is to the right of the line segment,
        //   "if greater than 0 it is to the left, if equal to 0 then it lies
        //   "on the line segment"
        for (int iHalfedge = this.halfEdges.size(); iHalfedge > 0; iHalfedge--) {
            HalfEdge halfEdge = this.halfEdges.get(iHalfedge);
            Site p0 = halfEdge.getStartPoint();
            Site p1 = halfEdge.getEndPoint();
            double r = (y - p0.getY()) * (p1.getX() - p0.getX()) - (x - p0.getX()) * (p1.getY() - p0.getY());
            if (r == 0) {
                return 0;
            }
            if (r > 0) {
                return -1;
            }
        }
        return 1;
    }

    public Site getSite() {
        return this.site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public List<HalfEdge> getHalfEdges() {
        return this.halfEdges;
    }

    public void setHalfEdges(List<HalfEdge> halfEdges) {
        this.halfEdges = halfEdges;
    }

    public boolean isCloseMe() {
        return this.closeMe;
    }

    public void setCloseMe(boolean closeMe) {
        this.closeMe = closeMe;
    }
}
