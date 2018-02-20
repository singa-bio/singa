package de.bioforscher.singa.mathematics.algorithms.graphs;

import de.bioforscher.singa.mathematics.graphs.model.GraphPath;
import de.bioforscher.singa.mathematics.graphs.model.Graphs;
import de.bioforscher.singa.mathematics.graphs.model.RegularNode;
import de.bioforscher.singa.mathematics.graphs.model.UndirectedGraph;
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
        GraphPath<RegularNode> shortestPath = ShortestPathFinder.findBasedOnPredicate(source, n -> n.getIdentifier() == 1);
        Objects.requireNonNull(shortestPath);
        int start = 9;
        for (RegularNode node : shortestPath) {
            assertEquals(node.getIdentifier().intValue(), start--);
        }
    }

}