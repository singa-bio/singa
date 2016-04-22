package de.bioforscher.mathematics.metrics;

import de.bioforscher.mathematics.geometry.faces.Rectangle;
import de.bioforscher.mathematics.graphs.model.Node;
import de.bioforscher.mathematics.graphs.model.UndirectedGraph;
import de.bioforscher.mathematics.graphs.util.GraphFactory;
import de.bioforscher.mathematics.metrics.implementations.ShortestPathMetric;
import de.bioforscher.mathematics.metrics.model.Metric;
import de.bioforscher.mathematics.vectors.Vector2D;
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
        this.linearGraph = GraphFactory.buildLinearGraph(10, this.boundingBox);
        this.circularGraph = GraphFactory.buildCircularGraph(10, this.boundingBox);
        this.treeGraph = GraphFactory.buildTreeGraph(4, this.boundingBox);
    }

    @Test
    public void testFirstEqualsSecondTrivialCase() {
        Metric<Node<?, ?>> shortestPath = new ShortestPathMetric(this.linearGraph);
        double distance = shortestPath.calculateDistance(this.linearGraph.getNode(2), this.linearGraph.getNode(2));
        assertEquals(0.0, distance, 0.0);
    }

    @Test
    public void testLinearGraph() {
        Metric<Node<?, ?>> shortestPath = new ShortestPathMetric(this.linearGraph);
        double distance = shortestPath.calculateDistance(this.linearGraph.getNode(0), this.linearGraph.getNode(7));
        assertEquals(7.0, distance, 0.0);
    }

    @Test
    public void testCircularGraph() {
        Metric<Node<?, ?>> shortestPath = new ShortestPathMetric(this.circularGraph);
        double distance = shortestPath.calculateDistance(this.circularGraph.getNode(0), this.circularGraph.getNode(7));
        assertEquals(3.0, distance, 0.0);
    }

    @Test
    public void testTreeGraph() {
        Metric<Node<?, ?>> shortestPath = new ShortestPathMetric(this.treeGraph);
        double distance = shortestPath.calculateDistance(this.treeGraph.getNode(0), this.treeGraph.getNode(7));
        assertEquals(4.0, distance, 0.0);
    }

    // TODO degenerate case
    // TODO first and second in different subgraphs
}
