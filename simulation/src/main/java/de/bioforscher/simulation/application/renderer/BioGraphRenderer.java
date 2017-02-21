package de.bioforscher.simulation.application.renderer;

import de.bioforscher.core.events.UpdateEventListener;
import de.bioforscher.javafx.renderer.graphs.GraphRenderOptions;
import de.bioforscher.javafx.renderer.graphs.GraphRenderer;
import de.bioforscher.mathematics.geometry.edges.LineSegment;
import de.bioforscher.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.simulation.model.graphs.BioEdge;
import de.bioforscher.simulation.model.graphs.BioNode;
import de.bioforscher.simulation.events.GraphUpdatedEvent;
import javafx.scene.paint.Color;

import static de.bioforscher.simulation.model.compartments.NodeState.CELL_MEMBRANE;

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
    public void render(AutomatonGraph graph) {
        // Background
        // getRenderingOptions().setIdentifierFont(Font.getDefault());
        getGraphicsContext().setFill(getRenderingOptions().getBackgroundColor());
        getGraphicsContext().fillRect(0, 0, getDrawingWidth(), getDrawingHeight());
        // render edges
        if (getRenderingOptions().isDisplayingEdges()) {
            graph.getEdges().forEach(this::drawEdge);
        }
        // render nodes
        if (getRenderingOptions().isDisplayingNodes()) {
            graph.getNodes().forEach(this::drawNode);
        }
    }

    @Override
    protected void drawNode(BioNode node) {
        // decide on style
        if (!this.bioRenderingOptions.isColoringByEntity() && !this.bioRenderingOptions.isColoringByCompartment()) {
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
        } else if (this.bioRenderingOptions.isColoringByCompartment()) {
            if (node.getContainingCompartment().equals("default")) {
                getGraphicsContext().setFill(Color.CADETBLUE);
            } else {
                getGraphicsContext().setFill(Color.BURLYWOOD);
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
            LineSegment upperParallelSegment = connectingSegment.getParallelSegment(getRenderingOptions().getNodeDiameter() / 2.0);
            drawLineSegment(upperParallelSegment);
            // draw lower parallel
            LineSegment lowerParallelSegment = connectingSegment.getParallelSegment(-getRenderingOptions().getNodeDiameter() / 2.0);
            drawLineSegment(lowerParallelSegment);
        }
    }

    @Override
    public void onEventReceived(GraphUpdatedEvent event) {
        getGraphQueue().add(event.getGraph());
    }

}
