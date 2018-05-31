package de.bioforscher.singa.simulation.model.layer;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.entities.SmallMolecule;
import de.bioforscher.singa.chemistry.descriptive.features.diffusivity.Diffusivity;
import de.bioforscher.singa.chemistry.descriptive.features.permeability.MembranePermeability;
import de.bioforscher.singa.features.model.FeatureOrigin;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.javafx.renderer.Renderer;
import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.geometry.model.Polygon;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraphs;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import de.bioforscher.singa.simulation.model.newsections.CellRegion;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import de.bioforscher.singa.simulation.modules.transport.MembraneDiffusion;
import de.bioforscher.singa.simulation.modules.transport.VesicleDiffusion;
import de.bioforscher.singa.simulation.renderer.AutomatonGraphRenderer;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

import javax.measure.quantity.Length;
import java.util.concurrent.ThreadLocalRandom;

import static de.bioforscher.singa.chemistry.descriptive.features.permeability.MembranePermeability.CENTIMETRE_PER_SECOND;
import static de.bioforscher.singa.simulation.model.newsections.CellTopology.INNER;
import static tec.uom.se.unit.MetricPrefix.MICRO;
import static tec.uom.se.unit.MetricPrefix.NANO;
import static tec.uom.se.unit.Units.METRE;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
public class VesiclePlayground extends Application implements Renderer {

    private Canvas canvas;
    private VesicleLayer layer;
    private Rectangle rectangle;
    private Simulation simulation;
    private static final ChemicalEntity water = SmallMolecule.create("water").build();
    private AutomatonGraphRenderer renderer;


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

        ComparableQuantity<Length> systemExtend = Quantities.getQuantity(20, MICRO(METRE));
        Environment.setSystemExtend(systemExtend);
        Environment.setSimulationExtend(500);
        Environment.setNodeSpacingToDiameter(systemExtend, 10);
        Environment.setTimeStep(Quantities.getQuantity(1, MICRO(SECOND)));

        simulation = new Simulation();

        Vesicle vesicle = new Vesicle("0",
                new Vector2D(250, 250),
                Quantities.getQuantity(ThreadLocalRandom.current()
                        .nextDouble(100, 200), NANO(METRE))
                        .to(Environment.getNodeDistance().getUnit()));

        vesicle.getConcentrationContainer().set(INNER, water, 50.0);

        // add vesicle transport layer
        layer = new VesicleLayer();
        layer.addVesicle(vesicle);
        simulation.setVesicleLayer(layer);

        // define graphs
        AutomatonGraph graph = AutomatonGraphs.createRectangularAutomatonGraph(10, 10);
        simulation.setGraph(graph);

        for (AutomatonNode node : graph.getNodes()) {
            node.setCellRegion(CellRegion.CYTOSOL_A);
            node.getConcentrationContainer().set(INNER, water, 40.0);
        }

        // setup species
        SmallMolecule water = new SmallMolecule.Builder("water")
                .name("water")
                .assignFeature(new MembranePermeability(Quantities.getQuantity(1, CENTIMETRE_PER_SECOND), FeatureOrigin.MANUALLY_ANNOTATED))
                .assignFeature(new Diffusivity(10, FeatureOrigin.MANUALLY_ANNOTATED))
                .build();

        // add diffusion
        MembraneDiffusion.inSimulation(simulation)
                .cargo(water)
                .build();

//        FreeDiffusion.inSimulation(simulation)
//                .onlyFor(water)
//                .build();

        VesicleDiffusion vesicleDiffusion = new VesicleDiffusion(simulation);
        layer.addVesicleModule(vesicleDiffusion);

        simulation.initializeSpatialRepresentations();

        // initialize renderer and options
        renderer = new AutomatonGraphRenderer();
        renderer.getRenderingOptions().setDisplayingEdges(false);
        renderer.getRenderingOptions().setDisplayingNodes(false);
        renderer.getBioRenderingOptions().setNodeHighlightEntity(water);
        renderer.setGraphicsContext(canvas.getGraphicsContext2D());

        // show
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                simulation.nextEpoch();
                renderVesicles();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    // bs
                }
            }
        };
        animationTimer.start();


//        // create manager
//        SimulationManager simulationManager = new SimulationManager(simulation);
//        // set termination condition
//        simulationManager.setSimulationTerminationToTime(Quantities.getQuantity(10, MINUTE));
//        simulationManager.setUpdateEmissionToTimePassed(Quantities.getQuantity(1, SECOND));
//
//        Thread thread = new Thread(simulationManager);
//        thread.setDaemon(true);
//        thread.start();

    }

    public void renderVesicles() {
        getGraphicsContext().setFill(Color.WHITE);
        fillPolygon(rectangle);
        getGraphicsContext().setFill(Color.BLACK);
        for (AutomatonNode node : simulation.getGraph().getNodes()) {
            Polygon spatialRepresentation = node.getSpatialRepresentation();
            getGraphicsContext().setFill(renderer.getBioRenderingOptions().getNodeColor(node));
            fillPolygon(spatialRepresentation);
            drawPolygon(spatialRepresentation);
        }
        simulation.getVesicleLayer().getVesicles().stream().map(Vesicle::getCircleRepresentation).forEach(this::drawCircle);
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
