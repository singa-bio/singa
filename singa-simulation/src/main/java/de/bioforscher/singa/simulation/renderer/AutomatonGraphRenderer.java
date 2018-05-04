package de.bioforscher.singa.simulation.renderer;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.core.events.UpdateEventListener;
import de.bioforscher.singa.javafx.renderer.colors.ColorScale;
import de.bioforscher.singa.javafx.renderer.graphs.GraphRenderOptions;
import de.bioforscher.singa.javafx.renderer.graphs.GraphRenderer;
import de.bioforscher.singa.mathematics.algorithms.voronoi.VoronoiGenerator;
import de.bioforscher.singa.mathematics.algorithms.voronoi.model.VoronoiCell;
import de.bioforscher.singa.mathematics.algorithms.voronoi.model.VoronoiDiagram;
import de.bioforscher.singa.mathematics.geometry.edges.LineSegment;
import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.topology.grids.rectangular.RectangularCoordinate;
import de.bioforscher.singa.simulation.events.GraphUpdatedEvent;
import de.bioforscher.singa.simulation.model.graphs.AutomatonEdge;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import javafx.scene.paint.Color;

import java.util.HashMap;

import static de.bioforscher.singa.simulation.model.compartments.CellSectionState.MEMBRANE;

public class AutomatonGraphRenderer extends GraphRenderer<AutomatonNode, AutomatonEdge, RectangularCoordinate, AutomatonGraph>
        implements UpdateEventListener<GraphUpdatedEvent> {

    private BioGraphRenderOptions bioRenderingOptions;
    private AutomatonGraph graph;

    public AutomatonGraphRenderer(AutomatonGraph graph) {
        GraphRenderOptions options = new GraphRenderOptions();
        setRenderingOptions(options);
        bioRenderingOptions = new BioGraphRenderOptions();
        this.graph = graph;
        renderVoronoi(true);
    }

    public BioGraphRenderOptions getBioRenderingOptions() {
        return bioRenderingOptions;
    }

    @Override
    protected void drawNode(AutomatonNode node) {
        // decide on style
        switch (bioRenderingOptions.getRenderingMode()) {
            case ENTITY_BASED: {
                getGraphicsContext().setFill(bioRenderingOptions.getNodeColor(node));
                break;
            }
            case STATE_BASED: {
                switch (node.getState()) {
                    case AQUEOUS: {
                        getGraphicsContext().setFill(Color.CADETBLUE);
                        break;
                    }
                    case CYTOSOL: {
                        getGraphicsContext().setFill(Color.CORAL);
                        break;
                    }
                    case MEMBRANE: {
                        getGraphicsContext().setFill(Color.BURLYWOOD);
                        break;
                    }
                }
            }
        }
        drawPoint(node.getPosition(), getRenderingOptions().getNodeDiameter());
        // circle point if node is observed
        if (node.isObserved()) {
            getGraphicsContext().setStroke(Color.BLUEVIOLET);
            circlePoint(node.getPosition(), getRenderingOptions().getNodeDiameter());
        }
    }

    @Override
    protected void drawEdge(AutomatonEdge edge) {
        // set width
        getGraphicsContext().setLineWidth(getRenderingOptions().getEdgeThickness());
        LineSegment connectingSegment = new LineSegment(edge.getSource().getPosition(), edge.getTarget().getPosition());
        // decide on style
        if (edge.getSource().getState() != MEMBRANE || edge.getTarget().getState() != MEMBRANE) {
            // connection not between membrane nodes
            getGraphicsContext().setStroke(bioRenderingOptions.getEdgeColor(edge));
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

    public void renderVoronoi(boolean flag) {
        if (flag) {
            setRenderBefore(graph -> {
                // generate node map for cell identification
                HashMap<Integer, AutomatonNode> nodeMap = new HashMap<>();
                int identifier = 0;

                ChemicalEntity highlightEntity = bioRenderingOptions.getNodeHighlightEntity();
                if (highlightEntity != null) {

                    double min = Double.MAX_VALUE;
                    double max = -Double.MAX_VALUE;

                    for (AutomatonNode nodeType : graph.getNodes()) {
                        nodeMap.put(identifier, nodeType);
                        identifier++;
                        double concentration = nodeType.getConcentration(highlightEntity).getValue().doubleValue();
                        if (concentration > max) {
                            max = concentration;
                        } else if (concentration < min) {
                            min = concentration;
                        }
                    }

                    ColorScale nodeColorScale = bioRenderingOptions.getNodeColorScale();
                    nodeColorScale.setMaximalValue(max);
                    nodeColorScale.setMinimalValue(min);

                    final VoronoiDiagram diagram = VoronoiGenerator.generateVoronoiDiagram(nodeMap, new Rectangle(getDrawingWidth(), getDrawingHeight()));
                    for (VoronoiCell voronoiCell : diagram.getCells()) {
                        int nodeIdentifier = voronoiCell.getSite().getIdentifier();
                        AutomatonNode automatonNode = nodeMap.get(nodeIdentifier);
                        getGraphicsContext().setFill(bioRenderingOptions.getNodeColor(automatonNode));
                        fillPolygon(voronoiCell);
                    }
                }

                return null;
            });
        }
    }

    @Override
    public void onEventReceived(GraphUpdatedEvent event) {
        getGraphQueue().add(event.getGraph());
    }

}
