package de.bioforscher.singa.mathematics.algorithms.voronoi.model;

import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.geometry.model.Polygon;
import de.bioforscher.singa.mathematics.vectors.Vector2D;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

/**
 * A cell of the voronoi diagram. Containing its bounding half edges and the corresponding site.
 */
public class VoronoiCell implements Polygon {

    public static int OUTSIDE = -1;
    public static int ON_LINE = 0;
    public static int INSIDE = 1;

    /**
     * The original site of this cell.
     */
    private SiteEvent site;

    /**
     * The bounding edges.
     */
    private List<VoronoiHalfEdge> halfEdges;

    /**
     * A flag that remembers whether this cell can be closed.
     */
    private boolean closed;

    /**
     * Creates a new cell for the given site.
     *
     * @param site The original site.
     */
    VoronoiCell(SiteEvent site) {
        this.site = site;
        halfEdges = new ArrayList<>();
        closed = true;
    }

    /**
     * Initializes the half edges of this cell.
     *
     * @return The number of half edges bounding this cell.
     */
    int prepareHalfEdges() {
        // remove unused half edges
        ListIterator<VoronoiHalfEdge> halfEdgeIterator = halfEdges.listIterator();
        while (halfEdgeIterator.hasNext()) {
            VoronoiEdge edge = halfEdgeIterator.next().getEdge();
            if (edge.getEndingPoint() == null || edge.getStartingPoint() == null) {
                halfEdgeIterator.remove();
            }
        }
        // sort them by angle
        halfEdges.sort(Comparator.comparing(VoronoiHalfEdge::getAngle).reversed());
        // return the number of remaining edges
        return halfEdges.size();
    }

    /**
     * Returns the identifiers of the neighbouring cells.
     *
     * @return The Identifiers of the neighbouring cells.
     */
    public List<Integer> getNeighbourIds() {
        List<Integer> neighbours = new ArrayList<>();
        // get references from the neighbouring cells.
        for (VoronoiHalfEdge halfEdge : halfEdges) {
            VoronoiEdge edge = halfEdge.getEdge();
            if (edge.getLeftSite() != null && edge.getLeftSite().getIdentifier() != site.getIdentifier()) {
                neighbours.add(edge.getLeftSite().getIdentifier());
            } else if (edge.getRightSite() != null && edge.getRightSite().getIdentifier() != site.getIdentifier()) {
                neighbours.add(edge.getRightSite().getIdentifier());
            }
        }
        return neighbours;
    }

    /**
     * Returns the minimal bounding box that contains all sites.
     *
     * @return The minimal bounding box that contains all sites.
     */
    public Rectangle getMinimalBoundingBox() {
        double xMin = Double.MAX_VALUE;
        double yMin = Double.MAX_VALUE;
        double xMax = -Double.MAX_VALUE;
        double yMax = -Double.MAX_VALUE;
        // look for minimal and maximal x and y
        for (VoronoiHalfEdge halfEdge : halfEdges) {
            Vector2D startPoint = halfEdge.getStartPoint();
            double currentX = startPoint.getX();
            double currentY = startPoint.getY();
            if (currentX < xMin) {
                xMin = currentX;
            }
            if (currentY < yMin) {
                yMin = currentY;
            }
            if (currentX > xMax) {
                xMax = currentX;
            }
            if (currentY > yMax) {
                yMax = currentY;
            }
        }
        return new Rectangle(new Vector2D(xMin, yMax - yMin), new Vector2D(xMax - xMin, yMin));
    }

    /**
     * Returns an integer, describing whether a point is inside, on, or outside of the cell:
     * <pre>
     * -1: point is outside the perimeter of the cell
     *  0: point is on the perimeter of the cell
     *  1: point is inside the perimeter of the cell
     * </pre>
     *
     * @param point The point to check.
     * @return -1 if the point is outside of the perimeter of the cell, 0 if point is on the perimeter of the cell and 1
     * if the point is inside of the cell
     */
    public int evaluatePointPosition(Vector2D point) {
        // Since all polygons of a Voronoi diagram are convex, the following solution applies:
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
        for (VoronoiHalfEdge halfEdge : halfEdges) {
            Vector2D p0 = halfEdge.getStartPoint();
            Vector2D p1 = halfEdge.getEndPoint();
            double r = (point.getY() - p0.getY()) * (p1.getX() - p0.getX()) - (point.getX() - p0.getX()) * (p1.getY() - p0.getY());
            if (r == 0) {
                return ON_LINE;
            }
            if (r > 0) {
                return OUTSIDE;
            }
        }
        return INSIDE;
    }

    /**
     * Returns the original site of this cell.
     *
     * @return The original site of this cell.
     */
    public SiteEvent getSite() {
        return site;
    }

    /**
     * Returns all half edges enclosing this cell.
     *
     * @return All half edges enclosing this cell.
     */
    public List<VoronoiHalfEdge> getHalfEdges() {
        return halfEdges;
    }

    /**
     * Returns true, if this cell is closed and false otherwise.
     *
     * @return true, if this cell is closed and false otherwise.
     */
    public boolean isClosed() {
        return closed;
    }

    public boolean isOpen() {
        return !closed;
    }

    /**
     * Sets whether this cell is considered as closed or not.
     *
     * @param closed true, if this cell is closed and false otherwise.
     */
    void setClosed(boolean closed) {
        this.closed = closed;
    }

    public Vector2D getCentroid() {
        double x = 0.0;
        double y = 0.0;
        for (VoronoiHalfEdge halfEdge : halfEdges) {
            final Vector2D startPoint = halfEdge.getStartPoint();
            final Vector2D endPoint = halfEdge.getEndPoint();
            final double v = startPoint.getX() * endPoint.getY() - endPoint.getX() * startPoint.getY();
            x += (startPoint.getX() + endPoint.getX()) * v;
            y += (startPoint.getY() + endPoint.getY()) * v;
        }
        double m = getArea() * 6.0;
        return new Vector2D(x/m, y/m);
    }

    public double getArea() {
        double area = 0.0;
        for (VoronoiHalfEdge halfEdge : halfEdges) {
            final Vector2D startPoint = halfEdge.getStartPoint();
            final Vector2D endPoint = halfEdge.getEndPoint();
            area += startPoint.getX() * endPoint.getY();
            area -= startPoint.getY() * endPoint.getX();
        }
        return area / 2.0;
    }

    @Override
    public Vector2D[] getVertices() {
        Vector2D[] vertices = new Vector2D[halfEdges.size()];
        for (int index = 0; index < halfEdges.size(); index++) {
            vertices[index] = halfEdges.get(index).getStartPoint();
        }
        return vertices;
    }

    @Override
    public Vector2D getVertex(int vertexIdentifier) {
        return halfEdges.get(vertexIdentifier).getStartPoint();
    }

    @Override
    public int getNumberOfVertices() {
        return halfEdges.size();
    }
}
