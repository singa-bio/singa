package de.bioforscher.javafx.renderer.graphs;

import de.bioforscher.javafx.renderer.Renderer;
import de.bioforscher.mathematics.geometry.edges.LineSegment;
import de.bioforscher.mathematics.graphs.model.Edge;
import de.bioforscher.mathematics.graphs.model.Graph;
import de.bioforscher.mathematics.graphs.model.Node;
import de.bioforscher.mathematics.vectors.Vector2D;
import javafx.animation.AnimationTimer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.GraphicsContext;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Christoph on 24/11/2016.
 */
public class GraphRenderer<NodeType extends Node<NodeType, Vector2D>, EdgeType extends Edge<NodeType>,
        GraphType extends Graph<NodeType, EdgeType>> extends AnimationTimer implements Renderer {

    private ConcurrentLinkedQueue<GraphType> graphQueue = new ConcurrentLinkedQueue<>();
    private GraphRenderOptions renderingOptions = new GraphRenderOptions();

    private DoubleProperty drawingWidth;
    private DoubleProperty drawingHeight;

    private GraphicsContext graphicsContext;

    public GraphRenderer() {
        this.drawingWidth = new SimpleDoubleProperty();
        this.drawingHeight = new SimpleDoubleProperty();
    }

    public void arrangeGraph(GraphType graph) {
        Thread graphProducer = new Thread(new GraphProducer<>(this, graph, 100));
        graphProducer.start();
        this.start();
    }

    @Override
    public void handle(long now) {
        GraphType graph;
        while ((graph = this.graphQueue.poll()) != null) {
            render(graph);
        }
    }

    public void render(GraphType graph) {
        // Background
        // getRenderingOptions().setIdentifierFont(Font.getDefault());
        getGraphicsContext().setFill(this.renderingOptions.getBackgroundColor());
        getGraphicsContext().fillRect(0, 0, getDrawingWidth(), getDrawingHeight());
        // render edges
        if (this.renderingOptions.isDisplayingEdges()) {
            graph.getEdges().forEach(this::drawEdge);
        }
        // render nodes
        if (this.renderingOptions.isDisplayingNodes()) {
            graph.getNodes().forEach(this::drawNode);
        }
    }

    protected void drawNode(NodeType node) {
        // set color and diameter
        getGraphicsContext().setFill(this.renderingOptions.getNodeColor());
        drawPoint(node.getPosition(), this.renderingOptions.getNodeDiameter());
        // draw text
        getGraphicsContext().setFill(this.renderingOptions.getIdentifierTextColor());
        drawTextCenteredOnPoint(String.valueOf(node.getIdentifier()), node.getPosition());

    }

    public ConcurrentLinkedQueue<GraphType> getGraphQueue() {
        return this.graphQueue;
    }

    protected void drawEdge(EdgeType edge) {
        // set color and width
        getGraphicsContext().setLineWidth(this.renderingOptions.getEdgeThickness());
        getGraphicsContext().setStroke(this.renderingOptions.getEdgeColor());
        // draw
        drawLineSegment(new LineSegment(edge.getSource().getPosition(), edge.getTarget().getPosition()));
    }

    public DoubleProperty drawingWidthProperty() {
        return this.drawingWidth;
    }

    public DoubleProperty drawingHeightProperty() {
        return this.drawingHeight;
    }

    @Override
    public GraphicsContext getGraphicsContext() {
        return this.graphicsContext;
    }

    public void setGraphicsContext(GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;
    }

    @Override
    public double getDrawingWidth() {
        return this.drawingWidth.get();
    }

    @Override
    public double getDrawingHeight() {
        return this.drawingHeight.get();
    }

    public GraphRenderOptions getRenderingOptions() {
        return this.renderingOptions;
    }

    public void setRenderingOptions(GraphRenderOptions renderingOptions) {
        this.renderingOptions = renderingOptions;
    }

}
