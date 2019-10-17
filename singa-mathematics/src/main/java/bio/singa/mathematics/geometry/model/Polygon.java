package bio.singa.mathematics.geometry.model;

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

    /**
     * Returns the intersection and the nodes, which connecting edge was intersected.
     *
     * @param lineSegment The line segment.
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
