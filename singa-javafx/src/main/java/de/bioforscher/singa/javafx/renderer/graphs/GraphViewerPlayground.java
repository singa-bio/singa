package de.bioforscher.singa.javafx.renderer.graphs;

import de.bioforscher.singa.mathematics.algorithms.graphs.DisconnectedSubgraphFinder;
import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.graphs.model.UndirectedGraph;
import de.bioforscher.singa.mathematics.graphs.util.GraphFactory;
import javafx.application.Application;

import java.util.List;

/**
 * @author fk
 */
public class GraphViewerPlayground {

    public static void main(String[] args) {
        UndirectedGraph graph = GraphFactory.buildRandomGraph(100, 0.01, new Rectangle(400, 400));
        graph.removeNode(5);
        graph.removeNode(16);

        List<UndirectedGraph> disconnectedSubgraphs = DisconnectedSubgraphFinder.findDisconnectedSubgraphs(graph);

        System.out.println(disconnectedSubgraphs);

        GraphDisplayApplication.graph = graph;
        Application.launch(GraphDisplayApplication.class);
    }

}
