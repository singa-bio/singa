package bio.singa.simulation.renderer;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.core.events.UpdateEventListener;
import bio.singa.javafx.renderer.colors.ColorScale;
import bio.singa.javafx.renderer.graphs.GraphRenderOptions;
import bio.singa.javafx.renderer.graphs.GraphRenderer;
import bio.singa.mathematics.algorithms.voronoi.VoronoiGenerator;
import bio.singa.mathematics.algorithms.voronoi.model.VoronoiCell;
import bio.singa.mathematics.algorithms.voronoi.model.VoronoiDiagram;
import bio.singa.mathematics.geometry.edges.SimpleLineSegment;
import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.topology.grids.rectangular.RectangularCoordinate;
import bio.singa.simulation.events.GraphUpdatedEvent;
import bio.singa.simulation.model.graphs.AutomatonEdge;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.sections.CellSubsection;
import javafx.scene.paint.Color;

import java.util.HashMap;

public class AutomatonGraphRenderer extends GraphRenderer<AutomatonNode, AutomatonEdge, RectangularCoordinate, AutomatonGraph>
        implements UpdateEventListener<GraphUpdatedEvent> {

    private BioGraphRenderOptions bioRenderingOptions;
    private AutomatonGraph graph;

    public AutomatonGraphRenderer(AutomatonGraph graph) {
        GraphRenderOptions options = new GraphRenderOptions();
        setRenderingOptions(options);
        bioRenderingOptions = new BioGraphRenderOptions();
        this.graph = graph;
        // renderVoronoi(true);
    }

    public AutomatonGraphRenderer() {
        this(null);
    }

    public BioGraphRenderOptions getBioRenderingOptions() {
        return bioRenderingOptions;
    }

    @Override
    public void render(AutomatonGraph graph) {
        rescaleColors(graph);
        super.render(graph);
    }

    @Override
    protected void drawNode(AutomatonNode node) {
        // decide on style
        switch (bioRenderingOptions.getRenderingMode()) {
            case ENTITY_BASED: {
                getGraphicsContext().setFill(bioRenderingOptions.getColorForUpdatable(node));
                break;
            }
            case STATE_BASED: {
                // FIXME currently not working
//                switch (node.getState()) {
//                    case AQUEOUS: {
//                        getGraphicsContext().setFill(Color.CADETBLUE);
//                        break;
//                    }
//                    case CYTOSOL: {
//                        getGraphicsContext().setFill(Color.CORAL);
//                        break;
//                    }
//                    case MEMBRANE: {
//                        getGraphicsContext().setFill(Color.BURLYWOOD);
//                        break;
//                    }
//                }
            }
        }
        fillPoint(node.getPosition(), getRenderingOptions().getNodeDiameter());
        // circle point if node is observed
        if (node.isObserved()) {
            getGraphicsContext().setStroke(Color.BLUEVIOLET);
            strokeCircle(node.getPosition(), getRenderingOptions().getNodeDiameter());
        }
    }

    @Override
    protected void drawEdge(AutomatonEdge edge) {
        // set width
        getGraphicsContext().setLineWidth(getRenderingOptions().getEdgeThickness());
        SimpleLineSegment connectingSegment = new SimpleLineSegment(edge.getSource().getPosition(), edge.getTarget().getPosition());
        // decide on style
        // FIXME currently not working
//         if (edge.getSource().getState() != MEMBRANE || edge.getTarget().getState() != MEMBRANE) {
            // connection not between membrane nodes
            getGraphicsContext().setStroke(bioRenderingOptions.getEdgeColor(edge));
            strokeLineSegment(connectingSegment);
//        } else {
//            // connection between membrane nodes
//            getGraphicsContext().setStroke(Color.BURLYWOOD);
//            // draw upper parallel
//            SimpleLineSegment upperParallelSegment = connectingSegment.getParallelSegment(getRenderingOptions().getNodeDiameter() / 2.0);
//            strokeLineSegment(upperParallelSegment);
//            // draw lower parallel
//            SimpleLineSegment lowerParallelSegment = connectingSegment.getParallelSegment(-getRenderingOptions().getNodeDiameter() / 2.0);
//            strokeLineSegment(lowerParallelSegment);
//        }
    }

    public void renderVoronoi(boolean flag) {
        if (flag) {
            setRenderBefore(graph -> {
                // generate node map for cell identification
                HashMap<Integer, AutomatonNode> nodeMap = new HashMap<>();
                int identifier = 0;

                ChemicalEntity highlightEntity = bioRenderingOptions.getNodeHighlightEntity();
                if (highlightEntity != null) {



                    final VoronoiDiagram diagram = VoronoiGenerator.generateVoronoiDiagram(nodeMap, new Rectangle(getDrawingWidth(), getDrawingHeight()));
                    for (VoronoiCell voronoiCell : diagram.getCells()) {
                        int nodeIdentifier = voronoiCell.getSite().getIdentifier();
                        AutomatonNode automatonNode = nodeMap.get(nodeIdentifier);
                        getGraphicsContext().setFill(bioRenderingOptions.getColorForUpdatable(automatonNode));
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

    public void rescaleColors(AutomatonGraph graph) {
        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;
        for (AutomatonNode node : graph.getNodes()) {
            // FIXME current workaround for rendering multiple concentrations of one node
            CellSubsection firstSubsection = node.getCellRegion().getSubsections().iterator().next();
            double concentration = node.getConcentrationContainer().get(firstSubsection, bioRenderingOptions.getNodeHighlightEntity()).getValue().doubleValue();
            if (concentration > max) {
                max = concentration;
            } else if (concentration < min) {
                min = concentration;
            }
        }
        ColorScale nodeColorScale = bioRenderingOptions.getNodeColorScale();
        nodeColorScale.setMaximalValue(max);
        nodeColorScale.setMinimalValue(min);
    }

}
