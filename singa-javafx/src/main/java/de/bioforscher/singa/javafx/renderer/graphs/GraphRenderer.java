package de.bioforscher.singa.javafx.renderer.graphs;

import de.bioforscher.singa.javafx.renderer.Renderer;
import de.bioforscher.singa.mathematics.algorithms.voronoi.VoronoiGenerator;
import de.bioforscher.singa.mathematics.algorithms.voronoi.VoronoiRelaxation;
import de.bioforscher.singa.mathematics.algorithms.voronoi.model.VoronoiDiagram;
import de.bioforscher.singa.mathematics.geometry.edges.SimpleLineSegment;
import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.graphs.model.Edge;
import de.bioforscher.singa.mathematics.graphs.model.Graph;
import de.bioforscher.singa.mathematics.graphs.model.Node;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import javafx.animation.AnimationTimer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;
import java.util.stream.Collectors;

import static de.bioforscher.singa.javafx.renderer.graphs.GraphRenderer.RenderingMode.FORCE_DIRECTED;

/**
 * @author cl
 */
public class GraphRenderer<NodeType extends Node<NodeType, Vector2D, IdentifierType>, EdgeType extends Edge<NodeType>,
        IdentifierType, GraphType extends Graph<NodeType, EdgeType, IdentifierType>> extends AnimationTimer implements Renderer {

    private final ConcurrentLinkedQueue<GraphType> graphQueue = new ConcurrentLinkedQueue<>();
    private GraphRenderOptions renderingOptions = new GraphRenderOptions();
    private Function<GraphType, Void> renderBeforeFunction;
    private Function<GraphType, Void> renderAfterFunction;
    private GraphicsContext graphicsContext;

    private final DoubleProperty drawingWidth;
    private final DoubleProperty drawingHeight;
    private StringProperty renderingMode;

    public GraphRenderer() {
        drawingWidth = new SimpleDoubleProperty();
        drawingHeight = new SimpleDoubleProperty();
        renderingMode = new SimpleStringProperty(FORCE_DIRECTED.name());
    }

    public void arrangeGraph(GraphType graph) {
        Thread graphProducer = new Thread(new GraphProducer<>(this, graph, 100));
        graphProducer.start();
        start();
    }

    public void arrangeOnce(GraphType graph) {
        GraphDrawingTool<NodeType, EdgeType, IdentifierType, GraphType> gdt = new GraphDrawingTool<>(graph,
                drawingWidthProperty(), drawingHeightProperty(), 100);
        render(gdt.arrangeGraph(80));
    }

    public void relaxGraph(GraphType graph) {
        Thread graphProducer = new Thread(new RelaxationProducer<>(this, graph, 100));
        graphProducer.start();
        start();
    }

    public void relaxOnce(GraphType graph) {
        final Rectangle boundingBox = new Rectangle(drawingWidthProperty().doubleValue(), drawingHeightProperty().doubleValue());
        render(VoronoiRelaxation.relax(graph, boundingBox));
    }

    @Override
    public void handle(long now) {
        GraphType graph;
        while ((graph = graphQueue.poll()) != null) {
            render(graph);
        }
    }

    public void render(GraphType graph) {
        fillBackground();
        if (renderBeforeFunction != null) {
            renderBeforeFunction.apply(graph);
        }
        // render edges
        if (renderingOptions.isDisplayingEdges()) {
            graph.getEdges().forEach(this::drawEdge);
        }
        // render nodes
        if (renderingOptions.isDisplayingNodes()) {
            graph.getNodes().forEach(this::drawNode);
        }
        if (renderAfterFunction != null) {
            renderAfterFunction.apply(graph);
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
        fillPoint(node.getPosition(), renderingOptions.getNodeDiameter());
        // draw text
        getGraphicsContext().setFill(renderingOptions.getIdentifierTextColor());
        if (renderingOptions.isDisplayingIdentifierText()) {
            strokeTextCenteredOnPoint(String.valueOf(node.getIdentifier()), node.getPosition());
        }
    }

    public ConcurrentLinkedQueue<GraphType> getGraphQueue() {
        return graphQueue;
    }

    protected void drawEdge(EdgeType edge) {
        // set color and width
        getGraphicsContext().setLineWidth(renderingOptions.getEdgeThickness());
        getGraphicsContext().setStroke(renderingOptions.getEdgeColor());
        // draw
        strokeLineSegment(new SimpleLineSegment(edge.getSource().getPosition(), edge.getTarget().getPosition()));
    }

    public void renderVoronoi(boolean flag) {
        if (flag) {
            renderBeforeFunction = graph -> {
                List<Vector2D> sites = graph.getNodes().stream().map(Node::getPosition).collect(Collectors.toList());
                final VoronoiDiagram voronoiDiagram = VoronoiGenerator.generateVoronoiDiagram(sites, new Rectangle(drawingWidth.doubleValue(), drawingHeight.doubleValue()));
                drawDiagram(voronoiDiagram);
                return null;
            };
        } else {
            renderBeforeFunction = null;
        }
    }

    public void drawDiagram(VoronoiDiagram diagram) {
        getGraphicsContext().setStroke(Color.SEAGREEN);
        getGraphicsContext().setLineWidth(4);
        diagram.getEdges().forEach(edge -> strokeStraight(edge.getStartingPoint(), edge.getEndingPoint()));
        getGraphicsContext().setLineWidth(6);
        getGraphicsContext().setFill(Color.SEAGREEN.darker());
        diagram.getVertices().forEach(this::fillPoint);
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

    public String getRenderingMode() {
        return renderingMode.get();
    }

    public StringProperty renderingModeProperty() {
        return renderingMode;
    }

    public void setRenderingMode(String renderingMode) {
        this.renderingMode.set(renderingMode);
    }

    public enum RenderingMode {
        FORCE_DIRECTED("Force directed"), LLOYDS_RELAXATION("Lloyd's relaxation");

        private final String dispayText;

        RenderingMode(String dispayText) {
            this.dispayText = dispayText;
        }

        public String getDispayText() {
            return dispayText;
        }

    }

}
