package de.bioforscher.singa.javafx.renderer.graphs;

import de.bioforscher.singa.javafx.renderer.Renderer;
import de.bioforscher.singa.mathematics.geometry.edges.LineSegment;
import de.bioforscher.singa.mathematics.graphs.model.Edge;
import de.bioforscher.singa.mathematics.graphs.model.Graph;
import de.bioforscher.singa.mathematics.graphs.model.Node;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import javafx.animation.AnimationTimer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.GraphicsContext;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;

/**
 * @author cl
 */
public class GraphRenderer<NodeType extends Node<NodeType, Vector2D, IdentifierType>, EdgeType extends Edge<NodeType>,
        IdentifierType, GraphType extends Graph<NodeType, EdgeType, IdentifierType>> extends AnimationTimer implements Renderer {

    private final ConcurrentLinkedQueue<GraphType> graphQueue = new ConcurrentLinkedQueue<>();
    private final DoubleProperty drawingWidth;
    private final DoubleProperty drawingHeight;
    private GraphRenderOptions renderingOptions = new GraphRenderOptions();
    private Function<GraphType, Void> renderBeforeFunction;
    private Function<GraphType, Void> renderAfterFunction;
    private GraphicsContext graphicsContext;

    public GraphRenderer() {
        drawingWidth = new SimpleDoubleProperty();
        drawingHeight = new SimpleDoubleProperty();
    }

    public void arrangeGraph(GraphType graph) {
        Thread graphProducer = new Thread(new GraphProducer<>(this, graph, 100));
        graphProducer.start();
        start();
    }

    @Override
    public void handle(long now) {
        GraphType graph;
        while ((graph = graphQueue.poll()) != null) {
            fillBackground();
            if (renderBeforeFunction != null) {
                renderBeforeFunction.apply(graph);
            }
            render(graph);
            if (renderAfterFunction != null) {
                renderAfterFunction.apply(graph);
            }
        }
    }

    public void render(GraphType graph) {
        // render edges
        if (renderingOptions.isDisplayingEdges()) {
            graph.getEdges().forEach(this::drawEdge);
        }
        // render nodes
        if (renderingOptions.isDisplayingNodes()) {
            graph.getNodes().forEach(this::drawNode);
        }
    }

    public void fillBackground() {
        // background
        getGraphicsContext().setFill(renderingOptions.getBackgroundColor());
        getGraphicsContext().fillRect(0, 0, getDrawingWidth(), getDrawingHeight());
    }

    public void setRenderAfter(Function<GraphType, Void> renderAfterFunction) {
        this.renderAfterFunction = renderAfterFunction;
    }

    public void setRenderBefore(Function<GraphType, Void> renderBeforeFunction) {
        this.renderBeforeFunction = renderBeforeFunction;
    }

    protected void drawNode(NodeType node) {
        // set color and diameter
        getGraphicsContext().setFill(renderingOptions.getNodeColor());
        drawPoint(node.getPosition(), renderingOptions.getNodeDiameter());
        // draw text
        getGraphicsContext().setFill(renderingOptions.getIdentifierTextColor());
        drawTextCenteredOnPoint(String.valueOf(node.getIdentifier()), node.getPosition());

    }

    public ConcurrentLinkedQueue<GraphType> getGraphQueue() {
        return graphQueue;
    }

    protected void drawEdge(EdgeType edge) {
        // set color and width
        getGraphicsContext().setLineWidth(renderingOptions.getEdgeThickness());
        getGraphicsContext().setStroke(renderingOptions.getEdgeColor());
        // draw
        drawLineSegment(new LineSegment(edge.getSource().getPosition(), edge.getTarget().getPosition()));
    }

    public DoubleProperty drawingWidthProperty() {
        return drawingWidth;
    }

    public DoubleProperty drawingHeightProperty() {
        return drawingHeight;
    }

    @Override
    public GraphicsContext getGraphicsContext() {
        return graphicsContext;
    }

    public void setGraphicsContext(GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;
    }

    @Override
    public double getDrawingWidth() {
        return drawingWidth.get();
    }

    @Override
    public double getDrawingHeight() {
        return drawingHeight.get();
    }

    public GraphRenderOptions getRenderingOptions() {
        return renderingOptions;
    }

    public void setRenderingOptions(GraphRenderOptions renderingOptions) {
        this.renderingOptions = renderingOptions;
    }

}
