package de.bioforscher.singa.mathematics.algorithms.graphs;

import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.graphs.model.RegularNode;
import de.bioforscher.singa.mathematics.graphs.model.UndirectedGraph;
import de.bioforscher.singa.mathematics.graphs.util.GraphFactory;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

/**
 * @author cl
 */
public class ShortestPathFinderTest {

    private UndirectedGraph linearGraph;
    private UndirectedGraph circularGraph;
    private UndirectedGraph treeGraph;

    private final Rectangle boundingBox = new Rectangle(new Vector2D(0, 100), new Vector2D(100, 0));

    @Before
    public void initObjects() {
        this.linearGraph = GraphFactory.buildLinearGraph(10, this.boundingBox);
        this.circularGraph = GraphFactory.buildCircularGraph(10, this.boundingBox);
        this.treeGraph = GraphFactory.buildTreeGraph(4, this.boundingBox);
    }

    @Test
    public void shouldFindWithPredicate() {
        RegularNode source = this.linearGraph.getNode(9);
        LinkedList<RegularNode> basedOnPredicate = ShortestPathFinder.findBasedOnPredicate(source, n -> n.getIdentifier() == 1);
        System.out.println(basedOnPredicate);
    }

    // @Test
    //public void shouldTrack

}