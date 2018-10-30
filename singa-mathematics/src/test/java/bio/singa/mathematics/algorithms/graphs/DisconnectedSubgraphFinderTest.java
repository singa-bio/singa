package bio.singa.mathematics.algorithms.graphs;

import bio.singa.mathematics.graphs.model.Graphs;
import bio.singa.mathematics.graphs.model.UndirectedGraph;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author cl
 */
class DisconnectedSubgraphFinderTest {

    @Test
    void shouldFindSubgraphs() {
        UndirectedGraph graph = Graphs.buildCircularGraph(20);
        graph.removeNode(5);
        graph.removeNode(16);

        List<UndirectedGraph> disconnectedSubgraphs = DisconnectedSubgraphFinder.findDisconnectedSubgraphs(graph);
        assertEquals(2, disconnectedSubgraphs.size());

        UndirectedGraph first = disconnectedSubgraphs.get(0);
        assertEquals(8, first.getNodes().size());

        UndirectedGraph second = disconnectedSubgraphs.get(1);
        assertEquals(10, second.getNodes().size());
    }

}