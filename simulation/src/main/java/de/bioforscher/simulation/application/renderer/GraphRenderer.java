package de.bioforscher.simulation.application.renderer;

import de.bioforscher.mathematics.geometry.faces.Rectangle;
import de.bioforscher.mathematics.graphs.model.RegularNode;
import de.bioforscher.mathematics.graphs.model.UndirectedEdge;
import de.bioforscher.mathematics.graphs.model.UndirectedGraph;
import de.bioforscher.mathematics.graphs.voronoi.VoronoiFaceEdge;
import de.bioforscher.mathematics.graphs.voronoi.VoronoiFactory;
import de.bioforscher.mathematics.vectors.Vector2D;
import de.bioforscher.simulation.model.AutomatonGraph;
import de.bioforscher.simulation.model.BioEdge;
import de.bioforscher.simulation.model.BioNode;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;

public class GraphRenderer {

    private GraphRenderOptions renderingOptions;
    private BioGraphRenderOptions bioRenderingOptions;
    private VoronoiFactory vonoroiFactory;

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

    public void draw(UndirectedGraph graph) {

        // node diameter is needed everywhere
        final double nodeDiameter = this.renderingOptions.getStandardNodeDiameter();

        // Background
        this.graphicsContext.setFill(this.renderingOptions.getBackgroundColor());
        this.graphicsContext.fillRect(0, 0, this.canvas.getWidth(), this.canvas.getHeight());

        // Render Vonoroi Edges
        if (this.renderingOptions.isRenderVoronoi()) {
            // Generate Voronoi edges
            List<VoronoiFaceEdge> edges = this.vonoroiFactory.generateVonoroi(graph,
                    new Rectangle(new Vector2D(0, 800), new Vector2D(800, 0)));
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
            // Set options
            this.graphicsContext.setStroke(this.renderingOptions.getStandardEdgeColor());
            this.graphicsContext.setLineWidth(this.renderingOptions.getStanderdEdgeWidth());
            // Draw the edges
            for (UndirectedEdge e : graph.getEdges()) {
                this.graphicsContext.strokeLine(e.getSource().getPosition().getX() + nodeDiameter / 2,
                        e.getSource().getPosition().getY() + nodeDiameter / 2,
                        e.getTarget().getPosition().getX() + nodeDiameter / 2,
                        e.getTarget().getPosition().getY() + nodeDiameter / 2);
            }
        }

        // Nodes
        if (this.renderingOptions.isRenderNodes()) {
            // Set options
            this.graphicsContext.setFill(this.renderingOptions.getStandardNodeColor());
            // Draw the nodes
            for (RegularNode n : graph.getNodes()) {
                this.graphicsContext.fillOval(n.getPosition().getX(), n.getPosition().getY(), nodeDiameter,
                        nodeDiameter);
            }
        }

    }

    public void drawBio(AutomatonGraph g) {

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

}
