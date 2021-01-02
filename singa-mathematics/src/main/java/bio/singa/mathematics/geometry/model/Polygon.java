package bio.singa.mathematics.geometry.model;

import bio.singa.mathematics.geometry.edges.Line;
import bio.singa.mathematics.geometry.edges.LineRay;
import bio.singa.mathematics.geometry.edges.LineSegment;
import bio.singa.mathematics.geometry.edges.SimpleLineSegment;
import bio.singa.mathematics.geometry.faces.Circle;
import bio.singa.mathematics.geometry.faces.Polygons;
import bio.singa.mathematics.graphs.model.RegularNode;
import bio.singa.mathematics.graphs.model.UndirectedGraph;
import bio.singa.mathematics.vectors.Vector2D;

import java.util.*;

/**
 * A polygon is the two dimensional representation of a polytope.
 */
public interface Polygon extends Polytope<Vector2D> {

    default List<LineSegment> getEdges() {
        List<LineSegment> segments = new ArrayList<>();
        List<Vector2D> vertices = getVertices();
        if (vertices.size() < 2) {
            return segments;
        }
        for (int i = 1; i < vertices.size(); i++) {
            segments.add(new SimpleLineSegment(vertices.get(i - 1), vertices.get(i)));
        }
        segments.add(new SimpleLineSegment(vertices.get(vertices.size() - 1), vertices.get(0)));
        return segments;
    }

    default List<Vector2D> traverseVertices(Vector2D firstVector, Vector2D lastVector) {
        List<Vector2D> vertices = getVertices();
        List<Vector2D> sortedVertices = new ArrayList<>();
//        sortedVertices.add(firstVector);
        int indexFirst = vertices.indexOf(firstVector);
        int indexLast = vertices.indexOf(lastVector);
        if ((indexFirst +1) % vertices.size() == (indexLast +1) % vertices.size() + 1) {
            ListIterator<Vector2D> iterator = vertices.listIterator(indexFirst);
            while (sortedVertices.size() != vertices.size()) {
                if (!iterator.hasNext()) {
                    iterator = vertices.listIterator();
                }
                Vector2D vector = iterator.next();
                sortedVertices.add(vector);
                if (vector.equals(lastVector)) {
                    break;
                }
            }
        } else {
            ListIterator<Vector2D> iterator = vertices.listIterator((indexFirst + 1) % vertices.size());
            while (sortedVertices.size() != vertices.size()) {
                if (!iterator.hasPrevious()) {
                    iterator = vertices.listIterator(vertices.size());
                }
                Vector2D vector = iterator.previous();
                sortedVertices.add(vector);
                if (vector.equals(lastVector)) {
                    break;
                }
            }
        }
        return sortedVertices;
    }

    default List<Vector2D> getVertices(Vector2D startingWith) {
        List<Vector2D> vertices = getVertices();
        if (!vertices.contains(startingWith)) {
            return Collections.emptyList();
        }
        List<Vector2D> sortedVertices = new ArrayList<>();
        sortedVertices.add(startingWith);
        int index = vertices.indexOf(startingWith);
        Iterator<Vector2D> iterator = vertices.listIterator(index + 1 % vertices.size());
        while (iterator.hasNext()) {
            Vector2D vector = iterator.next();
            if (vector.equals(startingWith)) {
                break;
            }
            sortedVertices.add(vector);
            if (!iterator.hasNext()) {
                iterator = vertices.listIterator();
            }
        }
        return sortedVertices;
    }

    default List<LineSegment> getEdges(LineSegment startingWith) {
        List<LineSegment> edges = getEdges();
        if (!edges.contains(startingWith)) {
            return Collections.emptyList();
        }
        List<LineSegment> sortedEdges = new ArrayList<>();
        sortedEdges.add(startingWith);
        int index = edges.indexOf(startingWith);
        Iterator<LineSegment> iterator = edges.listIterator(index + 1 % edges.size());
        while (sortedEdges.size() != edges.size()) {
            if (!iterator.hasNext()) {
                iterator = edges.listIterator();
            }
            LineSegment lineSegment = iterator.next();
            if (lineSegment.equals(startingWith)) {
                break;
            }
            sortedEdges.add(lineSegment);
        }
        return sortedEdges;
    }

    default Set<Vector2D> getIntersections(Circle circle) {
        Set<Vector2D> intersections = new HashSet<>();
        for (LineSegment lineSegment : getEdges()) {
            intersections.addAll(lineSegment.getIntersectionWith(circle));
        }
        return intersections;
    }

    default Set<Vector2D> getIntersections(LineSegment lineSegment) {
        Set<Vector2D> intersections = new HashSet<>();
        for (LineSegment polygonSegment : getEdges()) {
            polygonSegment.getIntersectionWith(lineSegment).ifPresent(intersections::add);
        }
        return intersections;
    }

    default Set<Vector2D> getIntersections(Line line) {
        Set<Vector2D> intersections = new HashSet<>();
        for (LineSegment polygonSegment : getEdges()) {
            Optional<Vector2D> intersection = ((SimpleLineSegment) polygonSegment).getIntersectionWith(line);
            intersection.ifPresent(intersections::add);
        }
        return intersections;
    }

    default Set<Vector2D> getIntersections(LineRay ray) {
        Set<Vector2D> intersections = new HashSet<>();
        for (LineSegment polygonSegment : getEdges()) {
            Optional<Vector2D> intersection = ((SimpleLineSegment) polygonSegment).getIntersectionWith(ray);
            intersection.ifPresent(intersections::add);
        }
        return intersections;
    }

    /**
     * Returns the line segment which the given point is on.
     *
     * @return
     */
    default Optional<LineSegment> getEdgeWith(Vector2D point) {
        return getEdges().stream()
                .filter(lineSegment -> lineSegment.isAboutOnLine(point))
                .findAny();
    }

    /**
     * Returns the intersection and the nodes, which connecting edge was intersected.
     *
     * @param lineSegment The line segment.
     * @return An intersection fragment containing the point of intersection as well as the start and endpoint of the
     * edge that was intersected.
     */
    default List<IntersectionFragment> getIntersectionFragments(LineSegment lineSegment) {
        List<IntersectionFragment> intersections = new ArrayList<>();
        for (LineSegment polygonSegment : getEdges()) {
            Optional<Vector2D> intersection = polygonSegment.getIntersectionWith(lineSegment);
            intersection.ifPresent(vector2D -> intersections.add(new IntersectionFragment(vector2D, polygonSegment.getStartingPoint(), polygonSegment.getEndingPoint())));
        }
        return intersections;
    }

    default UndirectedGraph toGraph() {
        UndirectedGraph graph = new UndirectedGraph();
        for (LineSegment lineSegment : getEdges()) {
            Vector2D startingPoint = lineSegment.getStartingPoint();
            Vector2D endingPoint = lineSegment.getEndingPoint();
            // get source node
            RegularNode startingNode = graph.getNode(node -> node.getPosition().equals(startingPoint))
                    // add if it is not already present
                    .orElseGet(() -> {
                        RegularNode node = new RegularNode(graph.nextNodeIdentifier(), startingPoint);
                        graph.addNode(node);
                        return node;
                    });
            // get target node
            RegularNode endingNode = graph.getNode(node -> node.getPosition().equals(endingPoint))
                    // add if it is not already present
                    .orElseGet(() -> {
                        RegularNode node = new RegularNode(graph.nextNodeIdentifier(), endingPoint);
                        graph.addNode(node);
                        return node;
                    });
            // add edge between nodes
            graph.addEdgeBetween(startingNode, endingNode);
        }
        return graph;
    }

    default Vector2D getCentroid() {
        List<Vector2D> vertices = getVertices();
        int vectorCount = vertices.size();
        double[] sum = new double[2];
        for (Vector2D vector : vertices) {
            sum[0] += vector.getX();
            sum[1] += vector.getY();
        }
        return new Vector2D(sum[0] / vectorCount, sum[1] / vectorCount);
    }

    default double getMaximalY() {
        return getVertices().stream()
                .mapToDouble(Vector2D::getY)
                .max().getAsDouble();
    }

    default boolean containsVector(Vector2D point) {
        return Polygons.containsVector(this, point);
    }

    Polygon getCopy();

    void move(Vector2D targetLocation);

    void scale(double scalingFactor);

    Set<Vector2D> reduce(int times);

    class IntersectionFragment {

        private final Vector2D intersection;
        private final Vector2D intersectedSource;
        private final Vector2D intersectedTarget;

        public IntersectionFragment(Vector2D intersection, Vector2D intersectedSource, Vector2D intersectedTarget) {
            this.intersection = intersection;
            this.intersectedSource = intersectedSource;
            this.intersectedTarget = intersectedTarget;
        }

        public Vector2D getIntersection() {
            return intersection;
        }

        public Vector2D getIntersectedStart() {
            return intersectedSource;
        }

        public Vector2D getIntersectedEnd() {
            return intersectedTarget;
        }

    }
}
