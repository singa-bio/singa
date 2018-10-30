package bio.singa.mathematics.graphs.model;

import bio.singa.mathematics.graphs.grid.GridCoordinateConverter;
import bio.singa.mathematics.vectors.Vector2D;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeneralGraphModelTest {

    @Test
    void shouldAddNode() {
        UndirectedGraph testGraph = new UndirectedGraph();
        RegularNode nodeToAdd = Nodes.createRandomlyPlacedNode(0);
        RegularNode nodeNotToAdd = Nodes.createRandomlyPlacedNode(1);
        testGraph.addNode(nodeToAdd);
        assertTrue(testGraph.containsNode(nodeToAdd));
        assertFalse(testGraph.containsEdge(nodeNotToAdd));
    }

    @Test
    void shouldConnectNodes() {
        UndirectedGraph testGraph = new UndirectedGraph();
        RegularNode source = Nodes.createRandomlyPlacedNode(0);
        RegularNode target = Nodes.createRandomlyPlacedNode(1);
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
    void shouldRemoveNode() {
        UndirectedGraph linearGraph = Graphs.buildLinearGraph(10);
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
    void shouldConvertCoordinateToIdentifier() {
        GridCoordinateConverter rgc = new GridCoordinateConverter(7, 7);
        final Vector2D coordinate = new Vector2D(5, 3);
        assertEquals(26, rgc.convert(coordinate));
        assertArrayEquals(coordinate.getElements(), rgc.convert(26).getElements());
    }

}
