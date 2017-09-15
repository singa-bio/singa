package de.bioforscher.singa.mathematics.algorithms.graphs;

import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.graphs.model.Graphs;
import de.bioforscher.singa.mathematics.graphs.model.RegularNode;
import de.bioforscher.singa.mathematics.graphs.model.UndirectedGraph;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.Objects;

import static org.junit.Assert.assertEquals;

/**
 * @author cl
 */
public class ShortestPathFinderTest {

    private UndirectedGraph linearGraph;

    private final Rectangle boundingBox = new Rectangle(new Vector2D(0, 100), new Vector2D(100, 0));

    @Before
    public void initialize() {
        this.linearGraph = Graphs.buildLinearGraph(10, this.boundingBox);
    }

    @Test
    public void shouldFindWithPredicate() {
        RegularNode source = this.linearGraph.getNode(9);
        LinkedList<RegularNode> shortestPath = ShortestPathFinder.findBasedOnPredicate(source, n -> n.getIdentifier() == 1);
        Objects.requireNonNull(shortestPath);
        int start = 9;
        for (RegularNode node: shortestPath) {
            assertEquals(node.getIdentifier().intValue(), start--);
        }
    }

}