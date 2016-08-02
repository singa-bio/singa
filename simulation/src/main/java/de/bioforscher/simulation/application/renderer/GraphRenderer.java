package de.bioforscher.simulation.application.renderer;

import de.bioforscher.core.events.UpdateEventListener;
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

public class GraphRenderer extends AnimationTimer implements UpdateEventListener<GraphUpdatedEvent> {

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
                this.graphicsContext.strokeLine(ge.x1 + nodeDiameter / 2, ge.y1 + nodeDiameter / 2,
                        ge.x2 + nodeDiameter / 2, ge.y2 + nodeDiameter / 2);
            }
        }

        // Render graph edges
        if (this.renderingOptions.isRenderEdges()) {
            // Set width
            this.graphicsContext.setLineWidth(this.renderingOptions.getStanderdEdgeWidth());
            // Draw the edges
            for (BioEdge e : g.getEdges()) {
                this.graphicsContext.setStroke(this.bioRenderingOptions.getEdgeColor(e));
                this.graphicsContext.strokeLine(e.getSource().getPosition().getX() + nodeDiameter / 2,
                        e.getSource().getPosition().getY() + nodeDiameter / 2,
                        e.getTarget().getPosition().getX() + nodeDiameter / 2,
                        e.getTarget().getPosition().getY() + nodeDiameter / 2);
            }
        }

        // Nodes
        if (this.renderingOptions.isRenderNodes()) {

            for (BioNode n : g.getNodes()) {
                // Set options
                this.graphicsContext.setFill(this.bioRenderingOptions.getNodeColor(n));
                // Draw the node
                this.graphicsContext.fillOval(n.getPosition().getX(), n.getPosition().getY(), nodeDiameter,
                        nodeDiameter);
                if (n.isObserved()) {
                    this.graphicsContext.setStroke(Color.CADETBLUE);
                    this.graphicsContext.strokeOval(n.getPosition().getX(), n.getPosition().getY(), nodeDiameter,
                            nodeDiameter);
                }

            }
        }

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
