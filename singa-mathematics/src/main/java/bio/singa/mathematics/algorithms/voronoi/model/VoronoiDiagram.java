package bio.singa.mathematics.algorithms.voronoi.model;

import bio.singa.core.utility.Pair;
import bio.singa.mathematics.algorithms.voronoi.VoronoiGenerator;
import bio.singa.mathematics.geometry.edges.Line;
import bio.singa.mathematics.geometry.edges.LineRay;
import bio.singa.mathematics.geometry.edges.LineSegment;
import bio.singa.mathematics.geometry.edges.SimpleLineSegment;
import bio.singa.mathematics.geometry.model.Polygon;
import bio.singa.mathematics.vectors.Vector2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static bio.singa.mathematics.metrics.model.VectorMetricProvider.EUCLIDEAN_METRIC;

/**
 * Contains all information about the voronoi diagram created by {@link VoronoiGenerator#generateVoronoiDiagram(Collection,
 * Polygon)}. Vertices are created for every point two edges meet and for intersections with the bounding box. Edges
 * are splitting cells that are associated to each of the processed vectors.
 */
public class VoronoiDiagram {

    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(VoronoiDiagram.class);
    /**
     * The bounding box.
     */
    private final Polygon boundingBox;
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

    /**
     * Creates a new Voronoi diagram with the bounding box.
     *
     * @param boundingBox The bounding box surrounding the voronoi diagram.
     */
    VoronoiDiagram(Polygon boundingBox) {
        cells = new HashMap<>();
        edges = new ArrayList<>();
        vertices = new ArrayList<>();
//        Rectangle minimalBoundingBox = Polygons.getMinimalBoundingBox(boundingBox);
        this.boundingBox = boundingBox;
//        leftBorder = minimalBoundingBox.getLeftMostXPosition();
//        rightBorder = minimalBoundingBox.getRightMostXPosition();
//        bottomBorder = minimalBoundingBox.getBottomMostYPosition();
//        topBorder = minimalBoundingBox.getTopMostYPosition();
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

    public List<Vector2D> getSites() {
        return cells.values().stream().map(cell -> cell.getSite().getSite()).collect(Collectors.toList());
    }

    /**
     * Returns the edges of the Voronoi diagram.
     *
     * @return The edges of the Voronoi diagram.
     */
    public List<VoronoiEdge> getEdges() {
        return edges;
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
        edges.add(edge);
        if (startingPoint != null) {
            edge.setStartingPoint(leftSite, rightSite, startingPoint);
        }
        if (endingPoint != null) {
            edge.setEndingPoint(leftSite, rightSite, endingPoint);
        }
        cells.get(leftSite.getIdentifier()).getHalfEdges().add(new VoronoiHalfEdge(edge, leftSite, rightSite));
        cells.get(rightSite.getIdentifier()).getHalfEdges().add(new VoronoiHalfEdge(edge, rightSite, leftSite));
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
        edges.add(edge);
        return edge;
    }

    /**
     * Returns all vertices of the diagram.
     *
     * @return All vertices of the diagram.
     */
    public List<Vector2D> getVertices() {
        return vertices;
    }

    /**
     * Creates a new vertex, adds it to the diagram and returns it.
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @return The vertex.
     */
    Vector2D addVertex(double x, double y) {
        return addVertex(new Vector2D(x, y));
    }

    /**
     * Creates a new vertex, adds it to the diagram and returns it.
     *
     * @param vertex The vertex.
     * @return The vertex.
     */
    Vector2D addVertex(Vector2D vertex) {
        if (vertices.contains(vertex)) {
            return vertex;
        }
        vertices.add(vertex);
        return vertex;
    }

    /**
     * Creates a new cell, adds it to the diagram and returns it.
     *
     * @param siteIdentifier The identifier of the site.
     * @param site The site.
     * @return The cell.
     */
    public VoronoiCell addCell(int siteIdentifier, SiteEvent site) {
        site.setIdentifier(siteIdentifier);
        VoronoiCell cell = new VoronoiCell(site);
        cells.put(siteIdentifier, cell);
        return cell;
    }

    /**
     * Returns all cells.
     *
     * @return All cells.
     */
    public Collection<VoronoiCell> getCells() {
        return cells.values();
    }

    public Polygon getBoundingBox() {
        return boundingBox;
    }

    /**
     * Clips all edges sticking out of the bounding box.
     */
    public void clipEdges() {
        // connect all dangling edges to bounding box
        // or get rid of them if it can't be done
        // iterate backward so we can splice safely
        for (int iEdge = edges.size() - 1; iEdge >= 0; iEdge--) {
            VoronoiEdge edge = edges.get(iEdge);
            logger.trace("Post processing edge {}, starting at {}, ending at {}", iEdge, edge.getStartingPoint(), edge.getEndingPoint());
            // edge is removed if:
            //   it is wholly outside the bounding box
            //   it is looking more like a point than a line
            if (!connectEdge(edge) ||
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

    /**
     * Connects the edge if it has no associated ending point.
     *
     * @param edge The edge.
     * @return True if the edge was connected, false if nothing could be done.
     */
    private boolean connectEdge(VoronoiEdge edge) {
        // skip if end point already connected
        Vector2D startingPoint = edge.getStartingPoint();
        Vector2D endingPoint = edge.getEndingPoint();

        // [null, null]
        // both start and end are not set
        if (startingPoint == null && endingPoint == null) {
            Vector2D leftSite = edge.getLeftSite().getSite();
            Vector2D rightSite = edge.getRightSite().getSite();

            SimpleLineSegment lineSegment = new SimpleLineSegment(leftSite, rightSite);
            Line perpendicularBisector = lineSegment.getPerpendicularBisector();

            Set<Vector2D> intersections = boundingBox.getIntersections(perpendicularBisector);

            if (intersections.size() == 2) {
                Iterator<Vector2D> iterator = intersections.iterator();
                Vector2D first = addVertex(iterator.next());
                Vector2D second = addVertex(iterator.next());
                edge.setStartingPoint(first);
                edge.setEndingPoint(second);
                // mark cells to be closed
                markCellsOpen(edge);
                return true;
            }

            // line is beyond repair
            return false;
        }

        // [start, end]
        // both start and end are set
        if (endingPoint != null) {

            // check if any point is inside
            boolean startIsInside = boundingBox.containsVector(startingPoint);
            boolean endIsInside = boundingBox.containsVector(endingPoint);
            // get intersections with the line segemnt representing the edge
            LineSegment lineSegment = new SimpleLineSegment(startingPoint, endingPoint);
            Set<Vector2D> intersections = boundingBox.getIntersections(lineSegment);

            // #intersections == 0
            // no intersections and both points outside, drop edge
            if (intersections.size() < 1 && !startIsInside && !endIsInside) {
                return false;
            }

            // #intersections == 1
            // determine which side is inside the polygon and modify the other
            if (intersections.size() == 1) {
                Vector2D updatedVertex = addVertex(intersections.iterator().next());
                if (startIsInside) {
                    edge.setEndingPoint(updatedVertex);
                }
                if (endIsInside) {
                    edge.setStartingPoint(updatedVertex);
                }
                // mark cells to be closed
                markCellsOpen(edge);
                return true;
            }

            // #intersections > 1
            // might be the case if there are complex polygons or start and end are placed outside of the bounding box
            if (intersections.size() > 1) {
                System.out.println("+");
                // determine closest points
                List<Vector2D> intersectionList = new ArrayList<>(intersections);
                Map.Entry<Vector2D, Double> startClosest = EUCLIDEAN_METRIC.calculateClosestDistance(intersectionList, startingPoint);
                Vector2D updatedStart = addVertex(startClosest.getKey());
                edge.setStartingPoint(updatedStart);
                Map.Entry<Vector2D, Double> endClosest = EUCLIDEAN_METRIC.calculateClosestDistance(intersectionList, endingPoint);
                Vector2D updatedEnd = addVertex(endClosest.getKey());
                edge.setStartingPoint(updatedEnd);
                // mark cells to be closed
                markCellsOpen(edge);
                return true;
            }

            // edge is totally contained in the bounding box
            return true;

        }

        // calculate new ending point
        SiteEvent leftSite = edge.getLeftSite();
        SiteEvent rightSite = edge.getRightSite();

        // if we reach here, this means cells which use this edge will need
        // to be closed, whether because the edge was removed, or because it
        // was connected to the bounding box.
        markCellsOpen(edge);

        // if the end point is null and starting point is not in the box
        if (!boundingBox.containsVector(startingPoint)) {
            return false;
        }

        // calculate midpoint between the two adjacent sites
        Vector2D midpoint = leftSite.getSite().getMidpointTo(rightSite.getSite());
        // ray one direction
        Vector2D firstIntersection = determineIntersectionWithBoundingBox(midpoint, startingPoint)
                .orElseThrow(() -> new IllegalStateException("Unable to determine intersection"));
        // ray other direction
        Vector2D secondIntersection = determineIntersectionWithBoundingBox(startingPoint, midpoint)
                .orElseThrow(() -> new IllegalStateException("Unable to determine intersection"));

        // use the shortest intersection as end point
        if (isOnTheLeft(leftSite.getSite(), startingPoint, firstIntersection)) {
            endingPoint = addVertex(firstIntersection);
        } else {
            endingPoint = addVertex(secondIntersection);
        }

        // set point
        edge.setEndingPoint(endingPoint);
        logger.trace("Connected dangling edge starting at {} to {}.", startingPoint, endingPoint);
        return true;
    }

    /**
     * Mark both cells adjacent to an edge to be closed later.
     *
     * @param edge The edge with adjacent cells.
     */
    private void markCellsOpen(VoronoiEdge edge) {
        SiteEvent leftSite = edge.getLeftSite();
        SiteEvent rightSite = edge.getRightSite();
        cells.get(leftSite.getIdentifier()).setClosed(false);
        cells.get(rightSite.getIdentifier()).setClosed(false);
    }

    /**
     * Determines, which side of a line a point is on. If d &lt; 0 then the point lies on one side of the line,
     * and if d &gt; 0 then it lies on the other side. If d = 0 then the point lies exactly on the line.
     *
     * @param point The point in question.
     * @param start Starting point of the line.
     * @param end Another point on the line.
     * @return The resulting value.
     * @see <a href="https://math.stackexchange.com/a/274728">Stack exchange post</a>
     */
    private double determineSide(Vector2D point, Vector2D start, Vector2D end) {
        // TODO maybe move to general class
        // (x−x1)(y2−y1)−(y−y1)(x2−x1)
        return (point.getX() - start.getX()) * (end.getY() - start.getY()) - (point.getY() - start.getY()) * (end.getX() - start.getX());
    }

    private boolean isOnTheLeft(Vector2D point, Vector2D start, Vector2D end) {
        return determineSide(point, start, end) > 0;
    }

    private Optional<Vector2D> determineIntersectionWithBoundingBox(Vector2D startingPoint, Vector2D midpoint) {
        LineRay lineRay = new LineRay(midpoint, startingPoint);
        Set<Vector2D> intersections = boundingBox.getIntersections(lineRay);
        // FIXME in complex polygons there might be more than 1 intersection
        if (intersections.size() == 1) {
            return Optional.of(intersections.iterator().next());
        } else {
            return Optional.empty();
        }
    }

    /**
     * Closes all cells that have not been closed until now.
     */
    public void closeCells() {

        for (VoronoiCell cell : cells.values()) {
            // prune, order half edges counterclockwise, then add missing ones
            // required to close cells
            if (cell.prepareHalfEdges() == 0) {
                continue;
            }
            if (cell.isClosed()) {
                continue;
            }
            // find first 'unclosed' point.
            // an 'unclosed' point will be the end point of a halfedge which
            // does not match the start point of the following halfedge
            List<VoronoiHalfEdge> halfEdges = cell.getHalfEdges();

            // prepare a copy for the final check



            // find open sections
            List<Pair<Vector2D>> danglingStubs = cell.determineDanglingStubs();
            // no open sections
            if (danglingStubs.isEmpty()) {
                continue;
            }

            if (danglingStubs.size() == 1) {
                Pair<Vector2D> stubPair = danglingStubs.get(0);
                Vector2D firstDanglingStub = stubPair.getFirst();
                Vector2D secondDanglingStub = stubPair.getSecond();

                // check which line segments the relevant points lie on
                List<LineSegment> boundingBoxEdges = boundingBox.getEdges();
                LineSegment firstSegment = getCorrespondingSegment(firstDanglingStub, boundingBoxEdges);
                LineSegment secondSegment = getCorrespondingSegment(secondDanglingStub, boundingBoxEdges);

                SiteEvent site = cell.getSite();
                if (firstSegment.equals(secondSegment)) {
                    // trivial case
                    // stubs on same segment, just add the line between both points
                    VoronoiEdge edge = createBorderEdge(site, firstDanglingStub, secondDanglingStub);
                    halfEdges.add(new VoronoiHalfEdge(edge, site, null));
                } else {
                    // stubs on different segments, traverse path to the other one
                    // get initial direction by checking which edge keeps the point on the left side
                    // by contract, the right side of border edges should be empty
                    Vector2D siteVector = site.getSite();
                    Vector2D firstVertex;
                    Vector2D lastVertex;
                    // determine in which direction to extend the cell
                    // midpoint of the segment should be closer to to the cell's site
                    double startingDistance = getMidpointDistance(siteVector, firstDanglingStub, firstSegment.getStartingPoint());
                    double endingDistance = getMidpointDistance(siteVector, firstDanglingStub, firstSegment.getEndingPoint());
                    if (startingDistance < endingDistance) {
                        firstVertex = firstSegment.getStartingPoint();
                        lastVertex = firstSegment.getEndingPoint();
                    } else {
                        firstVertex = firstSegment.getEndingPoint();
                        lastVertex = firstSegment.getStartingPoint();
                    }
                    // add initial line segment
                    addVertex(firstVertex);
                    VoronoiEdge initialEdge = createBorderEdge(site, firstDanglingStub, firstVertex);
                    halfEdges.add(new VoronoiHalfEdge(initialEdge, site, null));
                    // then, traverse vertices in the given order
                    List<Vector2D> traverseVertices = boundingBox.traverseVertices(firstVertex, lastVertex);
                    Vector2D previousVertex = null;
                    for (Vector2D currentVertex : traverseVertices) {
                        if (previousVertex != null) {
                            // and add line segment by line segment
                            SimpleLineSegment currentSegment = new SimpleLineSegment(previousVertex, currentVertex);
                            if (currentSegment.isCongruent(secondSegment)) {
                                // target segment reached
                                addVertex(previousVertex);
                                VoronoiEdge edge = createBorderEdge(site, previousVertex, secondDanglingStub);
                                halfEdges.add(new VoronoiHalfEdge(edge, site, null));
                                break;
                            }
                            addVertex(previousVertex);
                            VoronoiEdge edge = createBorderEdge(site, previousVertex, currentVertex);
                            halfEdges.add(new VoronoiHalfEdge(edge, site, null));
                        }
                        previousVertex = currentVertex;
                    }

                }
            }

            if (danglingStubs.size() > 1) {

                // TODO implement
                // close boundary see:
                //  points.add(new Vector2D(100.0, 200.0));
                //  points.add(new Vector2D(150.0, 200.0));
                //  points.add(new Vector2D(200.0, 200.0));
                System.out.println("more than one stub");
            }
            cell.setClosed(true);
        }
    }

    public LineSegment getCorrespondingSegment(Vector2D firstDanglingStub, List<LineSegment> boundingBoxEdges) {
        return boundingBoxEdges.stream()
                .filter(lineSegment -> lineSegment.isAboutOnLine(firstDanglingStub))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("unable to get polygon segment"));
    }

    private double getMidpointDistance(Vector2D site, Vector2D start, Vector2D end) {
        return new SimpleLineSegment(start, end).distanceTo(site);
    }

}
