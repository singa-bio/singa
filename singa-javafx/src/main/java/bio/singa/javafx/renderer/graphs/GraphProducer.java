package bio.singa.javafx.renderer.graphs;

import bio.singa.mathematics.graphs.model.Edge;
import bio.singa.mathematics.graphs.model.Graph;
import bio.singa.mathematics.graphs.model.Node;
import bio.singa.mathematics.vectors.Vector2D;

/**
 * @author cl
 */
public class GraphProducer<NodeType extends Node<NodeType, Vector2D, IdentifierType>, EdgeType extends Edge<NodeType>,
        IdentifierType, GraphType extends Graph<NodeType, EdgeType, IdentifierType>> implements Runnable {

    private final GraphRenderer<NodeType, EdgeType, IdentifierType, GraphType> renderer;
    private final GraphType graph;
    private final int totalIterations;

    public GraphProducer(GraphRenderer<NodeType, EdgeType, IdentifierType, GraphType> renderer, GraphType graph, int totalIterations) {
        this.renderer = renderer;
        this.graph = graph;
        this.totalIterations = totalIterations;
    }

    @Override
    public void run() {
        ForceDirectedGraphLayout<NodeType, EdgeType, IdentifierType, GraphType> gdt = new ForceDirectedGraphLayout<>(graph,
                renderer.drawingWidthProperty(), renderer.drawingHeightProperty(), 100);
        for (int i = 0; i < totalIterations; i++) {
            renderer.getGraphQueue().add(gdt.arrangeGraph(i));
            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
