package bio.singa.javafx.renderer.layouts.relax;

import bio.singa.javafx.renderer.graphs.GraphRenderer;
import bio.singa.mathematics.algorithms.voronoi.VoronoiRelaxation;
import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.graphs.model.Edge;
import bio.singa.mathematics.graphs.model.Graph;
import bio.singa.mathematics.graphs.model.Node;
import bio.singa.mathematics.vectors.Vector2D;

/**
 * @author cl
 */
public class RelaxationProducer<NodeType extends Node<NodeType, Vector2D, IdentifierType>, EdgeType extends Edge<NodeType>,
        IdentifierType, GraphType extends Graph<NodeType, EdgeType, IdentifierType>> implements Runnable {

    private final GraphRenderer<NodeType, EdgeType, IdentifierType, GraphType> renderer;
    private GraphType graph;
    private int totalIterations;

    public RelaxationProducer(GraphRenderer<NodeType, EdgeType, IdentifierType, GraphType> renderer, GraphType graph, int totalIterations) {
        this.renderer = renderer;
        this.graph = graph;
        this.totalIterations = totalIterations;
    }

    @Override
    public void run() {
        final Rectangle boundingBox = new Rectangle(renderer.drawingWidthProperty().doubleValue(), renderer.drawingHeightProperty().doubleValue());
        for (int i = 0; i < totalIterations; i++) {
            renderer.getGraphQueue().add(VoronoiRelaxation.relax(graph, boundingBox));
            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
