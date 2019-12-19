package bio.singa.mathematics.algorithms.graphs;

import bio.singa.mathematics.graphs.model.GenericEdge;
import bio.singa.mathematics.graphs.model.GenericGraph;
import bio.singa.mathematics.graphs.model.GenericNode;
import bio.singa.mathematics.vectors.Vector2D;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MaximumCommonSubgraphFinderTest {

    private static GenericGraph<Integer> testGraph1;
    private static GenericGraph<Integer> testGraph2;

    @BeforeAll
    static void setUp() {
        testGraph1 = new GenericGraph<>();
        testGraph1.addNode(1);
        testGraph1.addNode(2);
        testGraph1.addNode(3);
        testGraph1.addNode(4);
        testGraph1.addNode(5);
        testGraph1.addNode(6);
        testGraph1.addNode(7);
        testGraph1.addEdgeBetween(1, 2);
        testGraph1.addEdgeBetween(1, 5);
        testGraph1.addEdgeBetween(2, 5);
        testGraph1.addEdgeBetween(2, 3);
        testGraph1.addEdgeBetween(3, 4);
        testGraph1.addEdgeBetween(4, 5);
        testGraph1.addEdgeBetween(6, 4);
        testGraph1.addEdgeBetween(7, 1);

        testGraph2 = new GenericGraph<>();
        testGraph2.addNode(1);
        testGraph2.addNode(2);
        testGraph2.addNode(3);
        testGraph2.addNode(4);
        testGraph2.addNode(5);
        testGraph2.addNode(6);
        testGraph2.addNode(7);
        testGraph2.addEdgeBetween(1, 2);
        testGraph2.addEdgeBetween(1, 5);
        testGraph2.addEdgeBetween(2, 5);
        testGraph2.addEdgeBetween(2, 3);
        testGraph2.addEdgeBetween(3, 4);
        testGraph2.addEdgeBetween(4, 5);
        testGraph2.addEdgeBetween(6, 4);
        testGraph2.addEdgeBetween(7, 1);
        testGraph2.addEdgeBetween(7, 2);
        testGraph2.addEdgeBetween(5, 6);
    }

    @Test
    void findMaximumCommonSubgraph() {
        MaximumCommonSubgraphFinder<GenericNode<Integer>, GenericEdge<Integer>, Vector2D, Integer, GenericGraph<Integer>>
                mcs = new MaximumCommonSubgraphFinder<>(testGraph1, testGraph2, (n1, n2) -> true, (e1, e2) -> true);
        List<GenericGraph<Integer>> cliqueGraphs = mcs.getMaximumCliquesAsGraphs();
        assertEquals(2, cliqueGraphs.size());
    }
}