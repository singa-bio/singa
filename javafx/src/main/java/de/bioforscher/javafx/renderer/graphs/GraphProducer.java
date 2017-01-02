package de.bioforscher.javafx.renderer.graphs;

import de.bioforscher.mathematics.graphs.model.Edge;
import de.bioforscher.mathematics.graphs.model.Graph;
import de.bioforscher.mathematics.graphs.model.Node;
import de.bioforscher.mathematics.vectors.Vector2D;

/**
 * Created by Christoph on 24/11/2016.
 */
public class GraphProducer<NodeType extends Node<NodeType, Vector2D>, EdgeType extends Edge<NodeType>,
        GraphType extends Graph<NodeType, EdgeType>> implements Runnable {

    private final GraphRenderer<NodeType, EdgeType, GraphType> renderer;
    private GraphType graph;
    private int totalIterations;

    public GraphProducer(GraphRenderer<NodeType, EdgeType, GraphType> renderer, GraphType graph, int iterations) {
        this.renderer = renderer;
        this.graph = graph;
        this.totalIterations = iterations;
    }

    @Override
    public void run() {
        GraphDrawingTool<NodeType, EdgeType, GraphType> gdt = new GraphDrawingTool<>(this.graph,
                this.renderer.drawingWidthProperty(), this.renderer.drawingHeightProperty(), 100);
        for (int i = 0; i < this.totalIterations; i++) {
            this.renderer.getGraphQueue().add(gdt.arrangeGraph(i));
            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
