package de.bioforscher.singa.mathematics.graphs;

import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.graphs.model.*;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GeneralGraphModelTest {

    private Rectangle boundingBox;

    @Before
    public void initialize() {
        this.boundingBox = new Rectangle(100, 100);
    }

    @Test
    public void shouldAddNode() {
        UndirectedGraph testGraph = new UndirectedGraph();
        RegularNode nodeToAdd = Nodes.createRandomlyPlacedNode(0, this.boundingBox);
        RegularNode nodeNotToAdd = Nodes.createRandomlyPlacedNode(1, this.boundingBox);
        testGraph.addNode(nodeToAdd);
        assertTrue(testGraph.containsNode(nodeToAdd));
        assertFalse(testGraph.containsEdge(nodeNotToAdd));
    }

    @Test
    public void shouldConnectNodes() {
        UndirectedGraph testGraph = new UndirectedGraph();
        RegularNode source = Nodes.createRandomlyPlacedNode(0, this.boundingBox);
        RegularNode target = Nodes.createRandomlyPlacedNode(1, this.boundingBox);
        testGraph.addNode(source);
        testGraph.addNode(target);
        testGraph.addEdgeBetween(0, source, target);
        UndirectedEdge edge = testGraph.getEdge(0);
        assertTrue(edge.containsNode(source));
        assertTrue(edge.containsNode(target));
        assertTrue(source.hasNeighbour(target));
        assertTrue(target.hasNeighbour(source));
    }

    @Test
    public void shouldRemoveNode() {
        UndirectedGraph linearGraph = Graphs.buildLinearGraph(10, this.boundingBox);
        int unexpectedNodeIdentifier = 5;
        RegularNode removedNode = linearGraph.removeNode(unexpectedNodeIdentifier);
        // check if node has been removed correctly
        for (RegularNode node : linearGraph.getNodes()) {
            assertNotEquals(unexpectedNodeIdentifier, (int) node.getIdentifier());
        }
        // check if edge has been removed correctly
        for (UndirectedEdge edge : linearGraph.getEdges()) {
            assertFalse(edge.containsNode(removedNode));
        }
    }

    @Test
    public void shouldConvertCoordinateToIdentifier() {
        GridCoordinateConverter rgc = new GridCoordinateConverter(7, 7);
        Assert.assertEquals(38, rgc.convert(new Vector2D(3, 5)));
        assertArrayEquals(new Vector2D(3, 5).getElements(), rgc.convert(26).getElements(), 0.0);
    }

}
