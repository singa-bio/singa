package de.bioforscher.singa.mathematics.metrics;

import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.graphs.model.Graphs;
import de.bioforscher.singa.mathematics.graphs.model.Node;
import de.bioforscher.singa.mathematics.graphs.model.UndirectedGraph;
import de.bioforscher.singa.mathematics.metrics.implementations.ShortestPathMetric;
import de.bioforscher.singa.mathematics.metrics.model.Metric;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ShortestPathMetricTest {

    private UndirectedGraph linearGraph;
    private UndirectedGraph circularGraph;
    private UndirectedGraph treeGraph;

    private final Rectangle boundingBox = new Rectangle(new Vector2D(0, 100), new Vector2D(100, 0));

    @Before
    public void initObjects() {
        linearGraph = Graphs.buildLinearGraph(10, boundingBox);
        circularGraph = Graphs.buildCircularGraph(10, boundingBox);
        treeGraph = Graphs.buildTreeGraph(4, boundingBox);
    }

    @Test
    public void testFirstEqualsSecondTrivialCase() {
        Metric<Node<?, ?, ?>> shortestPath = new ShortestPathMetric(linearGraph);
        double distance = shortestPath.calculateDistance(linearGraph.getNode(2), linearGraph.getNode(2));
        assertEquals(0.0, distance, 0.0);
    }

    @Test
    public void testLinearGraph() {
        Metric<Node<?, ?, ?>> shortestPath = new ShortestPathMetric(linearGraph);
        double distance = shortestPath.calculateDistance(linearGraph.getNode(0), linearGraph.getNode(7));
        assertEquals(7.0, distance, 0.0);
    }

    @Test
    public void testCircularGraph() {
        Metric<Node<?, ?, ?>> shortestPath = new ShortestPathMetric(circularGraph);
        double distance = shortestPath.calculateDistance(circularGraph.getNode(0), circularGraph.getNode(7));
        assertEquals(3.0, distance, 0.0);
    }

    @Test
    public void testTreeGraph() {
        Metric<Node<?, ?, ?>> shortestPath = new ShortestPathMetric(treeGraph);
        double distance = shortestPath.calculateDistance(treeGraph.getNode(0), treeGraph.getNode(7));
        assertEquals(4.0, distance, 0.0);
    }

    // TODO degenerate case
    // TODO first and second in different subgraphs
}
