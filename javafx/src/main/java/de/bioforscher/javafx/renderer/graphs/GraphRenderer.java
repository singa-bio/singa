package de.bioforscher.javafx.renderer.graphs;

import de.bioforscher.javafx.renderer.Renderer;
import de.bioforscher.mathematics.geometry.edges.LineSegment;
import de.bioforscher.mathematics.graphs.model.Edge;
import de.bioforscher.mathematics.graphs.model.Graph;
import de.bioforscher.mathematics.graphs.model.Node;
import de.bioforscher.mathematics.vectors.Vector2D;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Christoph on 24/11/2016.
 */
public class GraphRenderer<NodeType extends Node<NodeType, Vector2D>> extends AnimationTimer implements Renderer {

    private Graph<NodeType, Edge<NodeType>> graph;
    private GraphCanvas canvas;
    private ConcurrentLinkedQueue<Graph> graphQueue = new ConcurrentLinkedQueue<>();
    private GraphRenderOptions renderingOptions;

    public GraphRenderer(Graph<NodeType, Edge<NodeType>> graph, GraphCanvas canvas) {
        this.graph = graph;
        this.canvas = canvas;
        this.renderingOptions = new GraphRenderOptions();
    }

    public Graph getGraph() {
        return this.graph;
    }

    public void setGraph(Graph<NodeType, Edge<NodeType>> graph) {
        this.graph = graph;
    }

    @Override
    public Canvas getCanvas() {
        return this.canvas;
    }

    public void arrangeGraph(ActionEvent event) {
        Thread graphProducer = new Thread(new GraphProducer(this.graphQueue, this.graph, this.canvas, 100));
        graphProducer.start();
        this.start();
    }

    @Override
    public void handle(long now) {
        Graph g;
        while ((g = this.graphQueue.poll()) != null) {
            render(g);
        }
    }

    public void render() {
        render(this.graph);
    }

    public void render(Graph<NodeType, Edge<NodeType>> g) {

        // node diameter is needed everywhere
        double nodeDiameter = this.renderingOptions.getStandardNodeDiameter();

        // Background
        getGraphicsContext().setFill(this.renderingOptions.getBackgroundColor());
        getGraphicsContext().fillRect(0, 0, this.canvas.getWidth(), this.canvas.getHeight());

        // render edges
        if (this.renderingOptions.isRenderEdges()) {
            g.getEdges().forEach(this::drawEdge);
        }
        // render nodes
        if (this.renderingOptions.isRenderNodes()) {
            g.getNodes().forEach(this::drawNode);
        }
    }

    private void drawNode(NodeType node) {
        double diameter = this.renderingOptions.getStandardNodeDiameter();
        getGraphicsContext().setFill(this.renderingOptions.getStandardNodeColor());
        drawPoint(node.getPosition(), diameter);

        getGraphicsContext().setFont(Font.font("Verdana", FontWeight.LIGHT, 15));
        getGraphicsContext().setFill(this.renderingOptions.getStandardEdgeColor());
        getGraphicsContext().fillText(String.valueOf(node.getIdentifier()), node.getPosition().getX(), node.getPosition().getY());
    }

    private void drawEdge(Edge<NodeType> edge) {
        // set width
        getGraphicsContext().setLineWidth(this.renderingOptions.getStanderdEdgeWidth());
        double diameter = this.renderingOptions.getStandardNodeDiameter();
        LineSegment connectingSegment = new LineSegment(edge.getSource().getPosition(), edge.getTarget().getPosition());
        // decide on style
        getGraphicsContext().setStroke(this.renderingOptions.getStandardEdgeColor());
        drawLineSegment(connectingSegment);
    }

}
