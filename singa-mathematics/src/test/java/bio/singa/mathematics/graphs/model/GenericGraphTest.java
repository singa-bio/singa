package bio.singa.mathematics.graphs.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author cl
 */
class GenericGraphTest {

    private static GenericGraph<String> genericGraph;

    @BeforeAll
    static void initializeGraph() {
        genericGraph = new GenericGraph<>();
        genericGraph.addNode("N1");
        genericGraph.addNode("N2");
        genericGraph.addNode("N3");
        genericGraph.addEdgeBetween("N1", "N3");
    }

    @Test
    void shouldAddAndConnectNodes() {
        Optional<GenericNode<String>> n1 = genericGraph.getNodeWithContent("N1");
        Optional<GenericNode<String>> n3 = genericGraph.getNodeWithContent("N3");
        // verify n1
        if (n1.isPresent()) {
            GenericNode<String> genericNode = n1.get();
            String actual = genericNode.getContent();
            assertEquals("N1", actual);
            GenericNode<String> neighbour = genericNode.getNeighbours().iterator().next();
            assertEquals("N3", neighbour.getContent());
        } else {
            fail("Node could not be retrieved from graph.");
        }
        // verify n2
        if (n3.isPresent()) {
            GenericNode<String> genericNode = n3.get();
            String actual = genericNode.getContent();
            assertEquals("N3", actual);
            GenericNode<String> neighbour = genericNode.getNeighbours().iterator().next();
            assertEquals("N1", neighbour.getContent());
        } else {
            fail("Node could not be retrieved from graph.");
        }
        // verify edge between them
        Collection<GenericEdge<String>> edges = genericGraph.getEdges();
        assertEquals(1, edges.size());
        GenericEdge<String> edge = edges.iterator().next();
        assertEquals("N1", edge.getSource().getContent());
        assertEquals("N3", edge.getTarget().getContent());
    }

    @Test
    void shouldCopyCorrectly() {
        GenericGraph<String> genericGraphCopy = genericGraph.getCopy();
        Optional<GenericNode<String>> originalOptional = genericGraph.getNodeWithContent("N1");
        Optional<GenericNode<String>> copyOptional = genericGraphCopy.getNodeWithContent("N1");

        if (originalOptional.isPresent() && copyOptional.isPresent()) {
            GenericNode<String> copy = copyOptional.get();
            GenericNode<String> original = originalOptional.get();
            assertNotSame(copy, original);
            assertEquals(copy, original);
            assertEquals(copy.getContent(), original.getContent());
        } else {
            fail("Node could not be retrieved from graph.");
        }
        // verify edge between them
        Collection<GenericEdge<String>> edges = genericGraphCopy.getEdges();
        assertEquals(1, edges.size());
        GenericEdge<String> edge = edges.iterator().next();
        assertEquals("N1", edge.getSource().getContent());
        assertEquals("N3", edge.getTarget().getContent());
    }

    @Test
    void shouldRemoveEdges() {
        GenericGraph<String> genericGraphCopy = genericGraph.getCopy();
        Optional<GenericNode<String>> n1Optional = genericGraphCopy.getNodeWithContent("N1");
        if (n1Optional.isPresent()) {
            genericGraphCopy.removeNode(n1Optional.get());
            assertEquals(0, genericGraphCopy.getEdges().size());
            Optional<GenericNode<String>> n3Optional = genericGraphCopy.getNodeWithContent("N3");
            if (n3Optional.isPresent()) {
                GenericNode<String> n3 = n3Optional.get();
                assertEquals(0, n3.getNeighbours().size());
            } else {
                fail("Node could not be retrieved from graph.");
            }
        } else {
            fail("Node could not be retrieved from graph.");
        }
    }

}