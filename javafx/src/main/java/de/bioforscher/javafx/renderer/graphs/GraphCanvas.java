package de.bioforscher.javafx.renderer.graphs;

import de.bioforscher.mathematics.graphs.model.Edge;
import de.bioforscher.mathematics.graphs.model.Graph;
import de.bioforscher.mathematics.graphs.model.Node;
import de.bioforscher.mathematics.vectors.Vector2D;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;

/**
 * Created by Christoph on 24/11/2016.
 */
public class GraphCanvas<NodeType extends Node<NodeType, Vector2D>> extends Canvas {

    private GraphRenderer<NodeType> renderer;

    public GraphCanvas(Graph<NodeType, Edge<NodeType>> graph) {
        this.renderer = new GraphRenderer<>(graph, this);
    }

    public void renderGraph() {
        this.renderer.render();
    }

    public void arrage(ActionEvent event) {
        this.renderer.arrangeGraph(event);
    }

}
