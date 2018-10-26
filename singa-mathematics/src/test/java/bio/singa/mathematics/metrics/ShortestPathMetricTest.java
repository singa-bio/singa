package bio.singa.mathematics.metrics;

import bio.singa.mathematics.graphs.model.Graphs;
import bio.singa.mathematics.graphs.model.Node;
import bio.singa.mathematics.graphs.model.UndirectedGraph;
import bio.singa.mathematics.metrics.implementations.ShortestPathMetric;
import bio.singa.mathematics.metrics.model.Metric;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShortestPathMetricTest {

    private UndirectedGraph linearGraph;
    private UndirectedGraph circularGraph;
    private UndirectedGraph treeGraph;

    @BeforeEach
    void initObjects() {
        linearGraph = Graphs.buildLinearGraph(10);
        circularGraph = Graphs.buildCircularGraph(10);
        treeGraph = Graphs.buildTreeGraph(4);
    }

    @Test
    void testFirstEqualsSecondTrivialCase() {
        Metric<Node<?, ?, ?>> shortestPath = new ShortestPathMetric(linearGraph);
        double distance = shortestPath.calculateDistance(linearGraph.getNode(2), linearGraph.getNode(2));
        assertEquals(0.0, distance);
    }

    @Test
    void testLinearGraph() {
        Metric<Node<?, ?, ?>> shortestPath = new ShortestPathMetric(linearGraph);
        double distance = shortestPath.calculateDistance(linearGraph.getNode(0), linearGraph.getNode(7));
        assertEquals(7.0, distance);
    }

    @Test
    void testCircularGraph() {
        Metric<Node<?, ?, ?>> shortestPath = new ShortestPathMetric(circularGraph);
        double distance = shortestPath.calculateDistance(circularGraph.getNode(0), circularGraph.getNode(7));
        assertEquals(3.0, distance);
    }

    @Test
    void testTreeGraph() {
        Metric<Node<?, ?, ?>> shortestPath = new ShortestPathMetric(treeGraph);
        double distance = shortestPath.calculateDistance(treeGraph.getNode(0), treeGraph.getNode(7));
        assertEquals(4.0, distance);
    }

}
