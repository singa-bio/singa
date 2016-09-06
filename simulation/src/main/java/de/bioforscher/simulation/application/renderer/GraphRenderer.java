package de.bioforscher.simulation.application.renderer;

import de.bioforscher.core.events.UpdateEventListener;
import de.bioforscher.javafx.renderer.Renderer;
import de.bioforscher.mathematics.geometry.edges.LineSegment;
import de.bioforscher.mathematics.geometry.faces.Rectangle;
import de.bioforscher.mathematics.graphs.voronoi.VoronoiFaceEdge;
import de.bioforscher.mathematics.graphs.voronoi.VoronoiFactory;
import de.bioforscher.simulation.model.AutomatonGraph;
import de.bioforscher.simulation.model.BioEdge;
import de.bioforscher.simulation.model.BioNode;
import de.bioforscher.simulation.model.GraphUpdatedEvent;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import static de.bioforscher.simulation.model.NodeState.AQUEOUS;
import static de.bioforscher.simulation.model.NodeState.CELL_MEMBRANE;

public class GraphRenderer extends AnimationTimer implements Renderer, UpdateEventListener<GraphUpdatedEvent> {

    private GraphRenderOptions renderingOptions;
    private BioGraphRenderOptions bioRenderingOptions;
    private VoronoiFactory vonoroiFactory;

    private ConcurrentLinkedQueue<AutomatonGraph> graphQueue = new ConcurrentLinkedQueue<>();

    private Canvas canvas;
    private GraphicsContext graphicsContext;

    public GraphRenderer(Canvas canvas) {
        this.canvas = canvas;
        this.graphicsContext = canvas.getGraphicsContext2D();
        this.renderingOptions = new GraphRenderOptions();
        this.bioRenderingOptions = new BioGraphRenderOptions();
        this.vonoroiFactory = new VoronoiFactory();
    }

    public GraphRenderOptions getOptions() {
        return this.renderingOptions;
    }

    public void setOptions(GraphRenderOptions options) {
        this.renderingOptions = options;
    }

    public BioGraphRenderOptions getBioRenderingOptions() {
        return this.bioRenderingOptions;
    }

    public void setBioRenderingOptions(BioGraphRenderOptions bioRenderingOptions) {
        this.bioRenderingOptions = bioRenderingOptions;
    }

    public void render(AutomatonGraph g) {

        // node diameter is needed everywhere
        double nodeDiameter = this.renderingOptions.getStandardNodeDiameter();

        // Background
        this.graphicsContext.setFill(this.renderingOptions.getBackgroundColor());
        this.graphicsContext.fillRect(0, 0, this.canvas.getWidth(), this.canvas.getHeight());

        // Render Vonoroi edges
        if (this.renderingOptions.isRenderVoronoi()) {
            // Generate Voronoi edges
            List<VoronoiFaceEdge> edges = this.vonoroiFactory.generateVonoroi(g,
                    new Rectangle(this.canvas.getWidth(), this.canvas.getHeight()));
            // Set options
            this.graphicsContext.setStroke(Color.LIGHTGREEN);
            this.graphicsContext.setLineWidth(this.renderingOptions.getStanderdEdgeWidth());
            // Draw the edges
            for (VoronoiFaceEdge ge : edges) {
                this.graphicsContext.strokeLine(ge.x1 - nodeDiameter / 2, ge.y1 - nodeDiameter / 2,
                        ge.x2 - nodeDiameter / 2, ge.y2 - nodeDiameter / 2);
            }
        }

        // edges
        if (this.renderingOptions.isRenderEdges()) {
            g.getEdges().forEach(this::drawEdge);
        }

        // nodes
        if (this.renderingOptions.isRenderNodes()) {
            g.getNodes().forEach(this::drawNode);
        }

    }

    private void drawNode(BioNode node) {

        double x = node.getPosition().getX();
        double y = node.getPosition().getY();
        double diameter = this.renderingOptions.getStandardNodeDiameter();

        // decide on style
        if (node.getState() == AQUEOUS) {
            this.graphicsContext.setFill(Color.AQUAMARINE);
            this.graphicsContext.fillOval(x- diameter / 2, y- diameter / 2, diameter, diameter);
        } else if (node.getState() == CELL_MEMBRANE) {
            this.graphicsContext.setFill(Color.BURLYWOOD);
            this.graphicsContext.fillOval(x- diameter / 2, y- diameter / 2, diameter, diameter);
        } else {
            this.graphicsContext.setFill(this.bioRenderingOptions.getNodeColor(node));
            this.graphicsContext.fillOval(x- diameter / 2, y- diameter / 2, diameter, diameter);
        }

        if (node.isObserved()) {
            this.graphicsContext.setStroke(Color.CADETBLUE);
            this.graphicsContext.strokeOval(x- diameter / 2, y- diameter / 2, diameter, diameter);
        }
    }

    private void drawEdge(BioEdge edge) {
        // Set width
        this.graphicsContext.setLineWidth(this.renderingOptions.getStanderdEdgeWidth());
        double diameter = this.renderingOptions.getStandardNodeDiameter();
        LineSegment connectingSegment = new LineSegment(edge.getSource().getPosition(), edge.getTarget().getPosition());
        // decide on style
        if (edge.getSource().getState() != CELL_MEMBRANE || edge.getTarget().getState() != CELL_MEMBRANE) {
            // connection not between membrane nodes
            this.graphicsContext.setStroke(this.bioRenderingOptions.getEdgeColor(edge));
            drawLineSegment(connectingSegment);
        } else {
            // connection between membrane nodes
            this.graphicsContext.setStroke(Color.BURLYWOOD);
            // draw upper parallel
            LineSegment upperParallelSegment = connectingSegment.getParallelSegment(diameter/2.0);
            drawLineSegment(upperParallelSegment);
            // draw lower parallel
            LineSegment lowerParallelSegment = connectingSegment.getParallelSegment(-diameter/2.0);
            drawLineSegment(lowerParallelSegment);
        }
    }

    @Override
    public GraphicsContext getGraphicsContext() {
        return this.graphicsContext;
    }

    @Override
    public Canvas getCanvas() {
        return this.canvas;
    }


    public ConcurrentLinkedQueue<AutomatonGraph> getGraphQueue() {
        return this.graphQueue;
    }

    @Override
    public void onEventReceived(GraphUpdatedEvent event) {
        this.graphQueue.add(event.getGraph());
    }

    @Override
    public void handle(long now) {
        AutomatonGraph g;
        while ((g = this.graphQueue.poll()) != null) {
            render(g);
        }
    }
}
