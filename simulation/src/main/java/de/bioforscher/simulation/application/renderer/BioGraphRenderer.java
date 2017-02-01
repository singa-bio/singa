package de.bioforscher.simulation.application.renderer;

import de.bioforscher.core.events.UpdateEventListener;
import de.bioforscher.javafx.renderer.Renderer;
import de.bioforscher.javafx.renderer.graphs.GraphRenderOptions;
import de.bioforscher.javafx.renderer.graphs.GraphRenderer;
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

import static de.bioforscher.simulation.model.NodeState.CELL_MEMBRANE;

public class BioGraphRenderer extends GraphRenderer<BioNode, BioEdge, AutomatonGraph> implements UpdateEventListener<GraphUpdatedEvent> {

    private BioGraphRenderOptions bioRenderingOptions;

    public BioGraphRenderer() {
        GraphRenderOptions options = new GraphRenderOptions();
        this.setRenderingOptions(options);
        this.bioRenderingOptions = new BioGraphRenderOptions();
    }

    public BioGraphRenderOptions getBioRenderingOptions() {
        return this.bioRenderingOptions;
    }

    @Override
    protected void drawNode(BioNode node) {
        // decide on style
        if (!this.bioRenderingOptions.isColoringByEntity()) {
            switch (node.getState()) {
                case AQUEOUS: {
                    getGraphicsContext().setFill(Color.CADETBLUE);
                    break;
                }
                case CYTOSOL: {
                    getGraphicsContext().setFill(Color.LIGHTGREEN);
                    break;
                }
                case CELL_MEMBRANE: {
                    getGraphicsContext().setFill(Color.BURLYWOOD);
                    break;
                }
            }
        } else {
            getGraphicsContext().setFill(this.bioRenderingOptions.getNodeColor(node));
        }
        drawPoint(node.getPosition(), getRenderingOptions().getNodeDiameter());

        // circle point if node is observed
        if (node.isObserved()) {
            getGraphicsContext().setStroke(Color.BLUEVIOLET);
            circlePoint(node.getPosition(), getRenderingOptions().getNodeDiameter());
        }
    }

    @Override
    protected void drawEdge(BioEdge edge) {
        // set width
        getGraphicsContext().setLineWidth(getRenderingOptions().getEdgeThickness());
        LineSegment connectingSegment = new LineSegment(edge.getSource().getPosition(), edge.getTarget().getPosition());
        // decide on style
        if (edge.getSource().getState() != CELL_MEMBRANE || edge.getTarget().getState() != CELL_MEMBRANE) {
            // connection not between membrane nodes
            getGraphicsContext().setStroke(this.bioRenderingOptions.getEdgeColor(edge));
            drawLineSegment(connectingSegment);
        } else {
            // connection between membrane nodes
            getGraphicsContext().setStroke(Color.BURLYWOOD);
            // draw upper parallel
            LineSegment upperParallelSegment = connectingSegment.getParallelSegment(getRenderingOptions().getNodeDiameter()/2.0);
            drawLineSegment(upperParallelSegment);
            // draw lower parallel
            LineSegment lowerParallelSegment = connectingSegment.getParallelSegment(-getRenderingOptions().getNodeDiameter()/2.0);
            drawLineSegment(lowerParallelSegment);
        }
    }

    @Override
    public void onEventReceived(GraphUpdatedEvent event) {
        getGraphQueue().add(event.getGraph());
    }

}
