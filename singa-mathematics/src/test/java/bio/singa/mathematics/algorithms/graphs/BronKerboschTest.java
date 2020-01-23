package bio.singa.mathematics.algorithms.graphs;

import bio.singa.mathematics.graphs.model.GenericEdge;
import bio.singa.mathematics.graphs.model.GenericGraph;
import bio.singa.mathematics.graphs.model.GenericNode;
import bio.singa.mathematics.vectors.Vector2D;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BronKerboschTest {

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
        testGraph2.addNode(2);
        testGraph2.addNode(3);
        testGraph2.addNode(4);
        testGraph2.addNode(5);
        testGraph2.addNode(6);
        testGraph2.addEdgeBetween(1, 2);
        testGraph2.addEdgeBetween(1, 5);
        testGraph2.addEdgeBetween(2, 5);
        testGraph2.addEdgeBetween(2, 3);
        testGraph2.addEdgeBetween(3, 4);
        testGraph2.addEdgeBetween(4, 5);
        testGraph2.addEdgeBetween(6, 4);
        testGraph2.addEdgeBetween(5, 3);
    }

    @Test
    void findCliques() {
        BronKerbosch<GenericNode<Integer>, GenericEdge<Integer>, Vector2D, Integer, GenericGraph<Integer>> bronKerbosch = new BronKerbosch<>(testGraph1);
        List<Set<GenericNode<Integer>>> cliques = bronKerbosch.getCliques();
        assertEquals(cliques.get(0), Stream.of(testGraph1.getNodeWithContent(2).get(), testGraph1.getNodeWithContent(3).get()).collect(Collectors.toSet()));
        assertEquals(cliques.get(1), Stream.of(testGraph1.getNodeWithContent(1).get(), testGraph1.getNodeWithContent(2).get(), testGraph1.getNodeWithContent(5).get()).collect(Collectors.toSet()));
        assertEquals(cliques.get(2), Stream.of(testGraph1.getNodeWithContent(3).get(), testGraph1.getNodeWithContent(4).get()).collect(Collectors.toSet()));
        assertEquals(cliques.get(3), Stream.of(testGraph1.getNodeWithContent(4).get(), testGraph1.getNodeWithContent(5).get()).collect(Collectors.toSet()));
        assertEquals(cliques.get(4), Stream.of(testGraph1.getNodeWithContent(4).get(), testGraph1.getNodeWithContent(6).get()).collect(Collectors.toSet()));
    }

    @Test
    void findMaximumCliques() {
        BronKerbosch<GenericNode<Integer>, GenericEdge<Integer>, Vector2D, Integer, GenericGraph<Integer>> bronKerbosch = new BronKerbosch<>(testGraph2);
        List<Set<GenericNode<Integer>>> maximumCliques = bronKerbosch.getMaximumCliques();
        assertEquals(maximumCliques.get(0), Stream.of(testGraph2.getNodeWithContent(1).get(), testGraph2.getNodeWithContent(2).get(), testGraph2.getNodeWithContent(5).get()).collect(Collectors.toSet()));
        assertEquals(maximumCliques.get(1), Stream.of(testGraph2.getNodeWithContent(2).get(), testGraph2.getNodeWithContent(3).get(), testGraph2.getNodeWithContent(5).get()).collect(Collectors.toSet()));
        assertEquals(maximumCliques.get(2), Stream.of(testGraph2.getNodeWithContent(3).get(), testGraph2.getNodeWithContent(4).get(), testGraph2.getNodeWithContent(5).get()).collect(Collectors.toSet()));
    }
}