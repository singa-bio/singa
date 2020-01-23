package bio.singa.mathematics.graphs.model;


import bio.singa.core.utility.Pair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraphsTest {

    private static UndirectedGraph graph1;
    private static UndirectedGraph graph2;

    @BeforeAll
    static void setUp() {
        graph1 = Graphs.buildLinearGraph(3);
        graph2 = Graphs.buildLinearGraph(3);
    }

    @Test
    void modularProduct() {
        GenericGraph<Pair<RegularNode>> modularProduct = Graphs.modularProduct(graph1, graph2);
        GenericNode<Pair<RegularNode>> a1b1 = modularProduct.getNodeWithContent(new Pair<>(graph1.getNode(0), graph2.getNode(0))).get();
        GenericNode<Pair<RegularNode>> a1b2 = modularProduct.getNodeWithContent(new Pair<>(graph1.getNode(0), graph2.getNode(1))).get();
        GenericNode<Pair<RegularNode>> a1b3 = modularProduct.getNodeWithContent(new Pair<>(graph1.getNode(0), graph2.getNode(2))).get();
        GenericNode<Pair<RegularNode>> a2b1 = modularProduct.getNodeWithContent(new Pair<>(graph1.getNode(1), graph2.getNode(0))).get();
        GenericNode<Pair<RegularNode>> a2b2 = modularProduct.getNodeWithContent(new Pair<>(graph1.getNode(1), graph2.getNode(1))).get();
        GenericNode<Pair<RegularNode>> a2b3 = modularProduct.getNodeWithContent(new Pair<>(graph1.getNode(1), graph2.getNode(2))).get();
        GenericNode<Pair<RegularNode>> a3b1 = modularProduct.getNodeWithContent(new Pair<>(graph1.getNode(2), graph2.getNode(0))).get();
        GenericNode<Pair<RegularNode>> a3b2 = modularProduct.getNodeWithContent(new Pair<>(graph1.getNode(2), graph2.getNode(1))).get();
        GenericNode<Pair<RegularNode>> a3b3 = modularProduct.getNodeWithContent(new Pair<>(graph1.getNode(2), graph2.getNode(2))).get();

        // check pairing
        assertTrue(modularProduct.getEdgeBetween(a1b1, a2b2).isPresent());
        assertTrue(modularProduct.getEdgeBetween(a1b1, a3b3).isPresent());
        assertTrue(modularProduct.getEdgeBetween(a1b2, a2b1).isPresent());
        assertTrue(modularProduct.getEdgeBetween(a1b2, a2b3).isPresent());
        assertTrue(modularProduct.getEdgeBetween(a1b3, a2b2).isPresent());
        assertTrue(modularProduct.getEdgeBetween(a2b1, a1b2).isPresent());
        assertTrue(modularProduct.getEdgeBetween(a2b1, a3b2).isPresent());
        assertTrue(modularProduct.getEdgeBetween(a2b2, a1b1).isPresent());
        assertTrue(modularProduct.getEdgeBetween(a3b1, a2b2).isPresent());
        assertTrue(modularProduct.getEdgeBetween(a3b3, a2b2).isPresent());

        // check no pairing
        assertFalse(modularProduct.getEdgeBetween(a1b1, a1b2).isPresent());
        assertFalse(modularProduct.getEdgeBetween(a1b1, a1b3).isPresent());
        assertFalse(modularProduct.getEdgeBetween(a1b3, a2b3).isPresent());
        assertFalse(modularProduct.getEdgeBetween(a2b3, a3b3).isPresent());
        assertFalse(modularProduct.getEdgeBetween(a3b3, a3b2).isPresent());
        assertFalse(modularProduct.getEdgeBetween(a3b1, a2b1).isPresent());
        assertFalse(modularProduct.getEdgeBetween(a2b1, a1b1).isPresent());
    }

    @Test
    void modularProductWithNodeCondition() {
        GenericGraph<Pair<RegularNode>> modularProduct = Graphs.modularProduct(graph1, graph2, (n1, n2) -> !(n1.getIdentifier() == 0 && n2.getIdentifier() == 0), (e1, e2) -> true);
        assertFalse(modularProduct.getNodeWithContent(new Pair<>(graph1.getNode(0), graph2.getNode(0))).isPresent());
        GenericNode<Pair<RegularNode>> a1b2 = modularProduct.getNodeWithContent(new Pair<>(graph1.getNode(0), graph2.getNode(1))).get();
        GenericNode<Pair<RegularNode>> a1b3 = modularProduct.getNodeWithContent(new Pair<>(graph1.getNode(0), graph2.getNode(2))).get();
        GenericNode<Pair<RegularNode>> a2b1 = modularProduct.getNodeWithContent(new Pair<>(graph1.getNode(1), graph2.getNode(0))).get();
        GenericNode<Pair<RegularNode>> a2b2 = modularProduct.getNodeWithContent(new Pair<>(graph1.getNode(1), graph2.getNode(1))).get();
        GenericNode<Pair<RegularNode>> a2b3 = modularProduct.getNodeWithContent(new Pair<>(graph1.getNode(1), graph2.getNode(2))).get();
        GenericNode<Pair<RegularNode>> a3b1 = modularProduct.getNodeWithContent(new Pair<>(graph1.getNode(2), graph2.getNode(0))).get();
        GenericNode<Pair<RegularNode>> a3b2 = modularProduct.getNodeWithContent(new Pair<>(graph1.getNode(2), graph2.getNode(1))).get();
        GenericNode<Pair<RegularNode>> a3b3 = modularProduct.getNodeWithContent(new Pair<>(graph1.getNode(2), graph2.getNode(2))).get();

        // check pairing
        assertTrue(modularProduct.getEdgeBetween(a1b2, a2b1).isPresent());
        assertTrue(modularProduct.getEdgeBetween(a1b2, a2b3).isPresent());
        assertTrue(modularProduct.getEdgeBetween(a1b3, a2b2).isPresent());
        assertTrue(modularProduct.getEdgeBetween(a2b1, a1b2).isPresent());
        assertTrue(modularProduct.getEdgeBetween(a2b1, a3b2).isPresent());
        assertTrue(modularProduct.getEdgeBetween(a3b1, a2b2).isPresent());
        assertTrue(modularProduct.getEdgeBetween(a3b3, a2b2).isPresent());

        // check no pairing
        assertFalse(modularProduct.getEdgeBetween(a1b3, a2b3).isPresent());
        assertFalse(modularProduct.getEdgeBetween(a2b3, a3b3).isPresent());
        assertFalse(modularProduct.getEdgeBetween(a3b3, a3b2).isPresent());
        assertFalse(modularProduct.getEdgeBetween(a3b1, a2b1).isPresent());
    }

    @Test
    void modularProductWithEdgeCondition() {
        GenericGraph<Pair<RegularNode>> modularProduct = Graphs.modularProduct(graph1, graph2, (n1, n2) -> true, (e1, e2) -> !(e1.getIdentifier() == 0 && e2.getIdentifier() == 0));
        GenericNode<Pair<RegularNode>> a1b1 = modularProduct.getNodeWithContent(new Pair<>(graph1.getNode(0), graph2.getNode(0))).get();
        GenericNode<Pair<RegularNode>> a1b2 = modularProduct.getNodeWithContent(new Pair<>(graph1.getNode(0), graph2.getNode(1))).get();
        GenericNode<Pair<RegularNode>> a1b3 = modularProduct.getNodeWithContent(new Pair<>(graph1.getNode(0), graph2.getNode(2))).get();
        GenericNode<Pair<RegularNode>> a2b1 = modularProduct.getNodeWithContent(new Pair<>(graph1.getNode(1), graph2.getNode(0))).get();
        GenericNode<Pair<RegularNode>> a2b2 = modularProduct.getNodeWithContent(new Pair<>(graph1.getNode(1), graph2.getNode(1))).get();
        GenericNode<Pair<RegularNode>> a2b3 = modularProduct.getNodeWithContent(new Pair<>(graph1.getNode(1), graph2.getNode(2))).get();
        GenericNode<Pair<RegularNode>> a3b1 = modularProduct.getNodeWithContent(new Pair<>(graph1.getNode(2), graph2.getNode(0))).get();
        GenericNode<Pair<RegularNode>> a3b2 = modularProduct.getNodeWithContent(new Pair<>(graph1.getNode(2), graph2.getNode(1))).get();
        GenericNode<Pair<RegularNode>> a3b3 = modularProduct.getNodeWithContent(new Pair<>(graph1.getNode(2), graph2.getNode(2))).get();

        // check pairing
        assertFalse(modularProduct.getEdgeBetween(a1b1, a2b2).isPresent());
        assertTrue(modularProduct.getEdgeBetween(a1b1, a3b3).isPresent());
        assertFalse(modularProduct.getEdgeBetween(a1b2, a2b1).isPresent());
        assertTrue(modularProduct.getEdgeBetween(a1b2, a2b3).isPresent());
        assertTrue(modularProduct.getEdgeBetween(a1b3, a2b2).isPresent());
        assertFalse(modularProduct.getEdgeBetween(a2b1, a1b2).isPresent());
        assertTrue(modularProduct.getEdgeBetween(a2b1, a3b2).isPresent());
        assertFalse(modularProduct.getEdgeBetween(a2b2, a1b1).isPresent());
        assertTrue(modularProduct.getEdgeBetween(a3b1, a2b2).isPresent());
        assertTrue(modularProduct.getEdgeBetween(a3b3, a2b2).isPresent());

        // check no pairing
        assertFalse(modularProduct.getEdgeBetween(a1b1, a1b2).isPresent());
        assertFalse(modularProduct.getEdgeBetween(a1b1, a1b3).isPresent());
        assertFalse(modularProduct.getEdgeBetween(a1b3, a2b3).isPresent());
        assertFalse(modularProduct.getEdgeBetween(a2b3, a3b3).isPresent());
        assertFalse(modularProduct.getEdgeBetween(a3b3, a3b2).isPresent());
        assertFalse(modularProduct.getEdgeBetween(a3b1, a2b1).isPresent());
        assertFalse(modularProduct.getEdgeBetween(a2b1, a1b1).isPresent());
    }
}
