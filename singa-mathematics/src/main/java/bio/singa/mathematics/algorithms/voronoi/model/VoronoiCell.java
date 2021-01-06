package bio.singa.mathematics.algorithms.voronoi.model;

import bio.singa.mathematics.geometry.edges.LineSegment;
import bio.singa.mathematics.geometry.model.Polygon;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.mathematics.vectors.Vectors2D;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A cell of the voronoi diagram. Containing its bounding half edges and the corresponding site.
 */
public class VoronoiCell implements Polygon {

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

    public List<Vector2D> determineDanglingStubs() {
        List<Vector2D> stubs = new ArrayList<>();
        Vector2D firstDanglingStub;
        Vector2D secondDanglingStub;
        for (int i = 0; i < halfEdges.size(); i++) {
            firstDanglingStub = halfEdges.get(i).getEndPoint();
            secondDanglingStub = halfEdges.get((i + 1) % halfEdges.size()).getStartPoint();
            if (Math.abs(firstDanglingStub.getX() - secondDanglingStub.getX()) >= 1e-9 || Math.abs(firstDanglingStub.getY() - secondDanglingStub.getY()) >= 1e-9) {
                stubs.add(firstDanglingStub);
                stubs.add(secondDanglingStub);
            }
        }
        return stubs;
    }

    /**
     * Returns true, if this cell is closed and false otherwise.
     *
     * @return true, if this cell is closed and false otherwise.
     */
    public boolean isClosed() {
        return closed;
    }

    /**
     * Sets whether this cell is considered as closed or not.
     *
     * @param closed true, if this cell is closed and false otherwise.
     */
    void setClosed(boolean closed) {
        this.closed = closed;
    }

    public boolean isOpen() {
        return !closed;
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
        return new Vector2D(x / m, y / m);
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
    public List<Vector2D> getVertices() {
        List<Vector2D> vertices = new ArrayList<>();
        for (VoronoiHalfEdge halfEdge : halfEdges) {
            vertices.add(halfEdge.getStartPoint());
        }
        return vertices;
    }

    public void cleanEdges() {
        // segments might have been initialized out of order
        // case 1: start and end points are assigned with different clockwise orders
        // case 2: the start and end points dont match up when iterated sequentially
        // move segments to a pool to draw from
        List<VoronoiHalfEdge> drawPool = new ArrayList<>(halfEdges);
        // new solution pool wth the sorted and potentially flipped edges
        Deque<VoronoiHalfEdge> solutionPool = new ArrayDeque<>();
        // get and remove initial edge from pool
        ListIterator<VoronoiHalfEdge> drawIterator = drawPool.listIterator();
        VoronoiHalfEdge firstEdge = drawIterator.next();
        // determine if its directionality is correct
        // FIXME seems to not work correctly
        if (!Vectors2D.isOnTheLeft(getSite().getSite(), firstEdge.getStartPoint(), firstEdge.getEndPoint())) {
            // swap sides
            firstEdge = firstEdge.invert();
        }
        //add to solutions
        drawIterator.remove();
        solutionPool.offer(firstEdge);

        // draw from original edges
        while (!drawPool.isEmpty()) {
            VoronoiHalfEdge first = solutionPool.peekLast();
            Vector2D endPoint = Objects.requireNonNull(first).getEndPoint();
            drawIterator = drawPool.listIterator();
            int previousSolutionSize = solutionPool.size();
            while (drawIterator.hasNext()) {
                // find adjacent edge
                VoronoiHalfEdge drawEdge = drawIterator.next();
                if (drawEdge.getStartPoint().equals(endPoint)) {
                    // all fine
                    drawIterator.remove();
                    solutionPool.offer(drawEdge);
                    break;
                }
                if (drawEdge.getEndPoint().equals(endPoint)) {
                    // swap sides
                    drawEdge = drawEdge.invert();
                    drawIterator.remove();
                    solutionPool.offer(drawEdge);
                    break;
                }
            }
            if (solutionPool.size() == previousSolutionSize) {
                // non of the segments matched the endpoint of the previous segments
                // unable to sort edges, if there is still unclosed sections
                System.out.println("unable to close cell wit id "+getSite().getIdentifier());
                return;
            }

        }
        // set new edges
        halfEdges = new ArrayList<>(solutionPool);
    }

    @Override
    public Vector2D getVertex(int vertexIdentifier) {
        return halfEdges.get(vertexIdentifier).getStartPoint();
    }

    @Override
    public List<LineSegment> getEdges() {
        return halfEdges.stream()
                .map(VoronoiHalfEdge::getEdge)
                .collect(Collectors.toList());
    }



    @Override
    public void move(Vector2D targetLocation) {

    }

    @Override
    public void scale(double scalingFactor) {

    }

    @Override
    public Set<Vector2D> reduce(int times) {
        return null;
    }

    @Override
    public Polygon getCopy() {
        return null;
    }

    @Override
    public int getNumberOfVertices() {
        return halfEdges.size();
    }
}
