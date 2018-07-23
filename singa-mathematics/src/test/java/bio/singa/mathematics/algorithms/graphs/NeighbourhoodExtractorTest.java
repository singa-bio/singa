package bio.singa.mathematics.algorithms.graphs;

import bio.singa.mathematics.graphs.model.Graphs;
import bio.singa.mathematics.graphs.model.RegularNode;
import bio.singa.mathematics.graphs.model.UndirectedGraph;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;

import static bio.singa.mathematics.GraphAssertion.assertGraphContainsNodes;
import static org.junit.Assert.assertTrue;

/**
 * @author cl
 */
public class NeighbourhoodExtractorTest {


    @Test
    public void shouldExtractNeighborhood() {
        UndirectedGraph undirectedGraph = Graphs.buildLinearGraph(10);
        RegularNode node = undirectedGraph.getNode(4);
        UndirectedGraph neighborhood = NeighbourhoodExtractor.extractNeighborhood(undirectedGraph, node, 2);
        assertGraphContainsNodes(neighborhood, 2, 3, 4, 5, 6);
    }

    @Test
    public void shouldExtractShell() {
        UndirectedGraph undirectedGraph = Graphs.buildTreeGraph(4);
        RegularNode node = undirectedGraph.getNode(4);
        List<RegularNode> shellNodes = NeighbourhoodExtractor.extractShell(undirectedGraph, node, 2);
        Iterator<RegularNode> iterator = shellNodes.iterator();
        assertTrue(iterator.next().getIdentifier().equals(2));
        assertTrue(iterator.next().getIdentifier().equals(5));
    }


}