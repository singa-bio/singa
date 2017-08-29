package de.bioforscher.singa.mathematics.algorithms.graphs;

import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.graphs.model.Graphs;
import de.bioforscher.singa.mathematics.graphs.model.RegularNode;
import de.bioforscher.singa.mathematics.graphs.model.UndirectedGraph;
import org.junit.Test;

/**
 * @author cl
 */
public class NeighbourhoodExtractorTest {

    private static Rectangle rectangle = new Rectangle(500, 500);

    @Test
    public void shouldExtractNeighborhood() {
        UndirectedGraph undirectedGraph = Graphs.buildLinearGraph(10, rectangle);
        RegularNode node = undirectedGraph.getNode(4);

        NeighbourhoodExtractor.extractNeighborhood(undirectedGraph, node, 3);

    }

}