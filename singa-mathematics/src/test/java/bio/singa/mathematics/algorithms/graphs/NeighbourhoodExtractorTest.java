package bio.singa.mathematics.algorithms.graphs;

import bio.singa.mathematics.graphs.model.Graphs;
import bio.singa.mathematics.graphs.model.RegularNode;
import bio.singa.mathematics.graphs.model.UndirectedGraph;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;

import static bio.singa.mathematics.GraphAssertion.assertGraphContainsNodes;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author cl
 */
class NeighbourhoodExtractorTest {


    @Test
    void shouldExtractNeighborhood() {
        UndirectedGraph undirectedGraph = Graphs.buildLinearGraph(10);
        RegularNode node = undirectedGraph.getNode(4);
        UndirectedGraph neighborhood = NeighbourhoodExtractor.extractNeighborhood(undirectedGraph, node, 2);
        assertGraphContainsNodes(neighborhood, 2, 3, 4, 5, 6);
    }

    @Test
    void shouldExtractShell() {
        UndirectedGraph undirectedGraph = Graphs.buildTreeGraph(4);
        RegularNode node = undirectedGraph.getNode(4);
        List<RegularNode> shellNodes = NeighbourhoodExtractor.extractShell(undirectedGraph, node, 2);
        Iterator<RegularNode> iterator = shellNodes.iterator();
        assertEquals(2, (int) iterator.next().getIdentifier());
        assertEquals(5, (int) iterator.next().getIdentifier());
    }


}