package de.bioforscher.singa.simulation.model.layer;

import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.javafx.renderer.Renderer;
import de.bioforscher.singa.javafx.renderer.graphs.GraphRenderer;
import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.topology.grids.rectangular.RectangularCoordinate;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.model.graphs.AutomatonEdge;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraphs;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import de.bioforscher.singa.simulation.model.newsections.CellRegion;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import de.bioforscher.singa.simulation.modules.model.SimulationManager;
import de.bioforscher.singa.simulation.modules.transport.VesicleDiffusion;
import javafx.application.Application;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import tec.uom.se.quantity.Quantities;

import static tec.uom.se.unit.MetricPrefix.MICRO;
import static tec.uom.se.unit.MetricPrefix.NANO;
import static tec.uom.se.unit.Units.*;

/**
 * @author cl
 */
public class VesiclePlayground extends Application implements Renderer {

    private Canvas canvas;
    private VesicleLayer layer;
    private Rectangle rectangle;
    private Simulation simulation;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        double simulationExtend = 800;
        canvas = new Canvas(simulationExtend, simulationExtend);

        BorderPane root = new BorderPane();
        root.setCenter(canvas);

        rectangle = new Rectangle(simulationExtend, simulationExtend);

        // define scales
        Environment.setSystemExtend(Quantities.getQuantity(20, MICRO(METRE)));
        Environment.setSimulationExtend(simulationExtend);
        Environment.setNodeSpacingToDiameter(Environment.getSystemExtend(), 10);
        Environment.setTimeStep(Quantities.getQuantity(1, MICRO(SECOND)));

        // create simulation
        simulation = new Simulation();

        // add vesicle transport layer
        layer = new VesicleLayer();
        VesicleDiffusion vesicleDiffusion = new VesicleDiffusion(simulation);
        layer.addVesicleModule(vesicleDiffusion);
        simulation.setVesicleLayer(layer);

        // define graphs
        AutomatonGraph graph = AutomatonGraphs.createRectangularAutomatonGraph(10, 10);
        simulation.setGraph(graph);
        simulation.initializeSpatialRepresentations();

        // define compartments
        graph.getNodes().forEach(node -> node.setCellRegion(CellRegion.CYTOSOL_A));

        // instantiate some vesicles
        for (int i = 0; i < 1; i++) {
            Vesicle vesicle = new Vesicle(String.valueOf(i),
                    new Vector2D(80, 80),
                    Quantities.getQuantity(150, NANO(METRE)),
                    CellRegion.CYTOSOL_A.getInnerSubsection());
            layer.addVesicle(vesicle);
        }

        // initialize renderer and options
        GraphRenderer<AutomatonNode, AutomatonEdge, RectangularCoordinate, AutomatonGraph> renderer = new GraphRenderer<>();
        renderer.getRenderingOptions().setDisplayingEdges(false);
        renderer.getRenderingOptions().setDisplayingNodes(false);
        renderer.setGraphicsContext(canvas.getGraphicsContext2D());

        // show
//        Scene scene = new Scene(root);
//        primaryStage.setScene(scene);
//        primaryStage.show();

        // create manager
        SimulationManager simulationManager = new SimulationManager(simulation);
        // set termination condition
        simulationManager.setSimulationTerminationToTime(Quantities.getQuantity(10, MINUTE));
        simulationManager.setUpdateEmissionToTimePassed(Quantities.getQuantity(1, SECOND));

        Thread thread = new Thread(simulationManager);
        thread.setDaemon(true);
        thread.start();

    }

    public void renderVesicles() {
        getGraphicsContext().setFill(Color.WHITE);
        fillPolygon(rectangle);
        getGraphicsContext().setFill(Color.BLACK);
        simulation.getGraph().getNodes().stream().map(AutomatonNode::getSpatialRepresentation).forEach(this::drawPolygon);
        simulation.getVesicleLayer().getVesicles().stream().map(Vesicle::getCircleRepresentation).forEach(this::drawCircle);
        for (AutomatonNode node : simulation.getGraph().getNodes()) {
            Vector2D nodePosition = node.getPosition();
            for (Vesicle vesicle : node.getAssociatedVesicles().keySet()) {
                drawStraight(nodePosition, vesicle.getPosition());
            }
        }
    }

    @Override
    public GraphicsContext getGraphicsContext() {
        return canvas.getGraphicsContext2D();
    }

    @Override
    public double getDrawingWidth() {
        return canvas.getWidth();
    }

    @Override
    public double getDrawingHeight() {
        return canvas.getHeight();
    }

}
