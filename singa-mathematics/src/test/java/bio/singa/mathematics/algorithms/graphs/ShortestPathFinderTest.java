package bio.singa.mathematics.algorithms.graphs;

import bio.singa.mathematics.graphs.model.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Objects;

import static org.junit.Assert.assertEquals;

/**
 * @author cl
 */
public class ShortestPathFinderTest {

    private UndirectedGraph linearGraph;

    @Before
    public void initialize() {
        linearGraph = Graphs.buildLinearGraph(10);
    }

    @Test
    public void shouldFindWithPredicate() {
        RegularNode source = linearGraph.getNode(9);
        GraphPath<RegularNode, UndirectedEdge> shortestPath = ShortestPathFinder.findBasedOnPredicate(linearGraph, source, n -> n.getIdentifier() == 1);
        Objects.requireNonNull(shortestPath);
        int start = 9;
        for (RegularNode node : shortestPath.getNodes()) {
            assertEquals(node.getIdentifier().intValue(), start--);
        }
    }

    // TODO test for edges


}