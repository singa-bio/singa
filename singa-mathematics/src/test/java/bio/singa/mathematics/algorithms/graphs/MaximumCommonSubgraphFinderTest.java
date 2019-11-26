package bio.singa.mathematics.algorithms.graphs;

import bio.singa.mathematics.graphs.model.GenericGraph;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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
        testGraph1.addEdgeBetween(1, 2);
        testGraph1.addEdgeBetween(1, 5);
        testGraph1.addEdgeBetween(2, 5);
        testGraph1.addEdgeBetween(2, 3);
        testGraph1.addEdgeBetween(3, 4);
        testGraph1.addEdgeBetween(4, 5);
        testGraph1.addEdgeBetween(6, 4);

        testGraph2 = new GenericGraph<>();
        testGraph2.addNode(1);
        testGraph2.addNode(3);
        testGraph2.addNode(5);
        testGraph2.addEdgeBetween(1, 5);
        testGraph2.addEdgeBetween(1, 3);
        testGraph2.addEdgeBetween(5, 3);
    }

    @Test
    void findMaximumCommonSubgraph() {
        new MaximumCommonSubgraphFinder<>(testGraph1,testGraph2);
    }
}