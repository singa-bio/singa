package bio.singa.simulation.model.modules.displacement;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.chemistry.features.diffusivity.Diffusivity;
import bio.singa.chemistry.features.permeability.MembranePermeability;
import bio.singa.features.model.Evidence;
import bio.singa.features.parameters.Environment;
import bio.singa.features.units.UnitRegistry;
import bio.singa.javafx.renderer.Renderer;
import bio.singa.mathematics.geometry.edges.SimpleLineSegment;
import bio.singa.mathematics.geometry.faces.Circle;
import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.geometry.model.Polygon;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.displacement.implementations.VesicleDiffusion;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.simulation.Simulation;
import bio.singa.simulation.renderer.AutomatonGraphRenderer;
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

import static bio.singa.chemistry.features.permeability.MembranePermeability.CENTIMETRE_PER_SECOND;
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
    private static final ChemicalEntity water = new SmallMolecule.Builder("water")
            .name("water")
            .assignFeature(new MembranePermeability(Quantities.getQuantity(0.1, CENTIMETRE_PER_SECOND), Evidence.MANUALLY_ANNOTATED))
            .assignFeature(new Diffusivity(10, Evidence.MANUALLY_ANNOTATED))
            .build();
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
        UnitRegistry.setTime(Quantities.getQuantity(1, MICRO(SECOND)));

        simulation = new Simulation();

        Vesicle vesicle = new Vesicle("0",
                new Vector2D(220, 220),
                Quantities.getQuantity(ThreadLocalRandom.current().nextDouble(100, 200), NANO(METRE))
                        .to(UnitRegistry.getSpaceUnit()));

        vesicle.getConcentrationContainer().set(CellTopology.INNER, water, 50.0);

        // add vesicle transport layer
        layer = new VesicleLayer(simulation);
        layer.addVesicle(vesicle);
        simulation.setVesicleLayer(layer);

        // define graphs
        AutomatonGraph graph = AutomatonGraphs.createRectangularAutomatonGraph(10, 10);
        simulation.setGraph(graph);

        for (AutomatonNode node : graph.getNodes()) {
            node.setCellRegion(CellRegion.CYTOSOL_A);
            node.getConcentrationContainer().set(CellTopology.INNER, water, 40.0);
        }


        // add diffusion
//        MembraneDiffusion.inSimulation(simulation)
//                .cargo(water)
//                .build();
//
//        Diffusion.inSimulation(simulation)
//                .onlyFor(water)
//                .build();

        VesicleDiffusion vesicleDiffusion = new VesicleDiffusion();
        vesicleDiffusion.setSimulation(simulation);
        simulation.getModules().add(vesicleDiffusion);

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
            }
        };
        animationTimer.start();

    }

    public void renderVesicles() {
        getGraphicsContext().setFill(Color.WHITE);
        fillPolygon(rectangle);
        getGraphicsContext().setFill(Color.BLACK);
        renderer.rescaleColors(simulation.getGraph());
        for (AutomatonNode node : simulation.getGraph().getNodes()) {
            Polygon polygon = node.getSpatialRepresentation();
            getGraphicsContext().setFill(renderer.getBioRenderingOptions().getColorForUpdatable(node));
            fillPolygon(polygon);
            strokePolygon(polygon);
        }
        for (Vesicle vesicle : simulation.getVesicleLayer().getVesicles()) {
            Circle circle = vesicle.getCircleRepresentation();
            getGraphicsContext().setFill(renderer.getBioRenderingOptions().getColorForUpdatable(vesicle));
            fillCircle(circle);
            strokeCircle(circle);
            for (AutomatonNode node : vesicle.getAssociatedNodes().keySet()) {
                strokeLineSegment(new SimpleLineSegment(vesicle.getCurrentPosition(), node.getPosition()));
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
