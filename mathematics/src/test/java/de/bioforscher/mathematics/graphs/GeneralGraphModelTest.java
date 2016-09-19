package de.bioforscher.mathematics.graphs;

import de.bioforscher.mathematics.geometry.faces.Rectangle;
import de.bioforscher.mathematics.graphs.model.RegularNode;
import de.bioforscher.mathematics.graphs.model.UndirectedEdge;
import de.bioforscher.mathematics.graphs.model.UndirectedGraph;
import de.bioforscher.mathematics.graphs.util.GraphFactory;
import de.bioforscher.mathematics.graphs.util.NodeFactory;
import de.bioforscher.mathematics.graphs.util.RectangularGridCoordinateConverter;
import de.bioforscher.mathematics.vectors.Vector2D;
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
        RegularNode nodeToAdd = NodeFactory.createRandomlyPlacedNode(0, this.boundingBox);
        RegularNode nodeNotToAdd = NodeFactory.createRandomlyPlacedNode(1, this.boundingBox);
        testGraph.addNode(nodeToAdd);
        assertTrue(testGraph.containsNode(nodeToAdd));
        assertFalse(testGraph.containsEdge(nodeNotToAdd));
    }

    @Test
    public void shouldConnectNodes() {
        UndirectedGraph testGraph = new UndirectedGraph();
        RegularNode source = NodeFactory.createRandomlyPlacedNode(0, this.boundingBox);
        RegularNode target = NodeFactory.createRandomlyPlacedNode(1, this.boundingBox);
        testGraph.addNode(source);
        testGraph.addNode(target);
        testGraph.connect(0, source, target);
        UndirectedEdge edge = testGraph.getEdge(0);
        assertTrue(edge.containsNode(source));
        assertTrue(edge.containsNode(target));
        assertTrue(source.hasNeighbour(target));
        assertTrue(target.hasNeighbour(source));
    }

    @Test
    public void shouldRemoveNode() {
        UndirectedGraph linearGraph = GraphFactory.buildLinearGraph(10, this.boundingBox);
        int unexpectedNodeIdentifier = 5;
        linearGraph.removeNode(unexpectedNodeIdentifier);
        // check if node has been removed correctly
        for (RegularNode node : linearGraph.getNodes()) {
            assertNotEquals(unexpectedNodeIdentifier, node.getIdentifier());
        }
        // check if edge has been removed correctly
        for (UndirectedEdge edge : linearGraph.getEdges()) {
            assertFalse(edge.containsNode(unexpectedNodeIdentifier));
        }
    }

    @Test
    public void shouldConvertCoordinateToIdentifier() {
        RectangularGridCoordinateConverter rgc = new RectangularGridCoordinateConverter(7, 7);
        assertEquals(38, rgc.convert(new Vector2D(3, 5)));
        assertArrayEquals(new Vector2D(3, 5).getElements(), rgc.convert(26).getElements(), 0.0);
    }

}
