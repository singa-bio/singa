package bio.singa.mathematics.algorithms.graphs;

import bio.singa.mathematics.graphs.model.GenericGraph;
import bio.singa.mathematics.graphs.model.GenericNode;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BronKerboschTest {

    @Test
    void findMaximumClique() {
        GenericGraph<Integer> testGraph = new GenericGraph<>();
        testGraph.addNode(1);
        testGraph.addNode(2);
        testGraph.addNode(3);
        testGraph.addNode(4);
        testGraph.addNode(5);
        testGraph.addNode(6);
        testGraph.addEdgeBetween(1, 2);
        testGraph.addEdgeBetween(1, 5);
        testGraph.addEdgeBetween(2, 5);
        testGraph.addEdgeBetween(2, 3);
        testGraph.addEdgeBetween(3, 4);
        testGraph.addEdgeBetween(4, 5);
        testGraph.addEdgeBetween(6, 4);


        List<Set<GenericNode<Integer>>> maximumCliques = new BronKerbosch<>(testGraph).findCliques();
        assertEquals(maximumCliques.get(0), Stream.of(testGraph.getNodeWithContent(2).get(), testGraph.getNodeWithContent(3).get()).collect(Collectors.toSet()));
        assertEquals(maximumCliques.get(1), Stream.of(testGraph.getNodeWithContent(1).get(), testGraph.getNodeWithContent(2).get(), testGraph.getNodeWithContent(5).get()).collect(Collectors.toSet()));
        assertEquals(maximumCliques.get(2), Stream.of(testGraph.getNodeWithContent(3).get(), testGraph.getNodeWithContent(4).get()).collect(Collectors.toSet()));
        assertEquals(maximumCliques.get(3), Stream.of(testGraph.getNodeWithContent(4).get(), testGraph.getNodeWithContent(5).get()).collect(Collectors.toSet()));
        assertEquals(maximumCliques.get(4), Stream.of(testGraph.getNodeWithContent(4).get(), testGraph.getNodeWithContent(6).get()).collect(Collectors.toSet()));
    }
}