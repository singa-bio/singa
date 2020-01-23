package bio.singa.mathematics.algorithms.graphs;

import bio.singa.mathematics.graphs.model.GenericEdge;
import bio.singa.mathematics.graphs.model.GenericGraph;
import bio.singa.mathematics.graphs.model.GenericNode;
import bio.singa.mathematics.vectors.Vector2D;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SmallestSetOfSmallestRingsFinderTest {

    @Test
    void findSmallestRings() {
        GenericGraph<Integer> testGraph1 = new GenericGraph<>();
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

        SmallestSetOfSmallestRingsFinder<GenericNode<Integer>, GenericEdge<Integer>, Vector2D, Integer, GenericGraph<Integer>> ringFinder = new SmallestSetOfSmallestRingsFinder<>(testGraph1);
        assertEquals(2, ringFinder.getRings().size());
        assertEquals(3, ringFinder.getRings().get(0).size());
        assertEquals(4, ringFinder.getRings().get(1).size());
    }
}