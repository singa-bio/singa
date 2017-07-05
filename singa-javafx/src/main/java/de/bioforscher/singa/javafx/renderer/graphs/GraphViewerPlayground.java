package de.bioforscher.singa.javafx.renderer.graphs;

import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.graphs.model.Graph;
import de.bioforscher.singa.mathematics.graphs.util.GraphFactory;
import javafx.application.Application;

/**
 * @author fk
 */
public class GraphViewerPlayground {
    public static void main(String[] args) {
        Graph graph = GraphFactory.buildGridGraph(2, 3, new Rectangle(200, 200), false);
        GraphDisplayApplication.graph = graph;
        Application.launch(GraphDisplayApplication.class);
    }
}
