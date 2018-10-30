package bio.singa.mathematics.algorithms.graphs;

import bio.singa.mathematics.graphs.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author cl
 */
class ShortestPathFinderTest {

    private UndirectedGraph linearGraph;

    @BeforeEach
    void initialize() {
        linearGraph = Graphs.buildLinearGraph(10);
    }

    @Test
    void shouldFindWithPredicate() {
        RegularNode source = linearGraph.getNode(9);
        GraphPath<RegularNode, UndirectedEdge> shortestPath = ShortestPathFinder.findBasedOnPredicate(linearGraph, source, n -> n.getIdentifier() == 1);
        Objects.requireNonNull(shortestPath);
        int start = 9;
        for (RegularNode node : shortestPath.getNodes()) {
            assertEquals(node.getIdentifier().intValue(), start--);
        }
    }

}