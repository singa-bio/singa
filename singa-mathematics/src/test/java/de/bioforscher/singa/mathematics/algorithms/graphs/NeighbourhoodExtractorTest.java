package de.bioforscher.singa.mathematics.algorithms.graphs;

import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.graphs.model.Graphs;
import de.bioforscher.singa.mathematics.graphs.model.RegularNode;
import de.bioforscher.singa.mathematics.graphs.model.UndirectedGraph;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;

import static de.bioforscher.singa.mathematics.GraphAssertion.assertGraphContainsNodes;
import static org.junit.Assert.assertTrue;

/**
 * @author cl
 */
public class NeighbourhoodExtractorTest {

    private static Rectangle rectangle = new Rectangle(500, 500);

    @Test
    public void shouldExtractNeighborhood() {
        UndirectedGraph undirectedGraph = Graphs.buildLinearGraph(10, rectangle);
        RegularNode node = undirectedGraph.getNode(4);
        UndirectedGraph neighborhood = NeighbourhoodExtractor.extractNeighborhood(undirectedGraph, node, 2);
        assertGraphContainsNodes(neighborhood, 2, 3, 4, 5, 6);
    }

    @Test
    public void shouldExtractShell() {
        UndirectedGraph undirectedGraph = Graphs.buildTreeGraph(4, rectangle);
        RegularNode node = undirectedGraph.getNode(4);
        List<RegularNode> shellNodes = NeighbourhoodExtractor.extractShell(undirectedGraph, node, 2);
        Iterator<RegularNode> iterator = shellNodes.iterator();
        assertTrue(iterator.next().getIdentifier().equals(2));
        assertTrue(iterator.next().getIdentifier().equals(5));
    }


}