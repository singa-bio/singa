package de.bioforscher.singa.simulation.model.layer;

import de.bioforscher.singa.features.parameters.EnvironmentalParameters;
import de.bioforscher.singa.javafx.renderer.Renderer;
import de.bioforscher.singa.javafx.renderer.graphs.GraphDrawingTool;
import de.bioforscher.singa.javafx.renderer.graphs.GraphRenderer;
import de.bioforscher.singa.mathematics.algorithms.voronoi.VoronoiGenerator;
import de.bioforscher.singa.mathematics.algorithms.voronoi.model.VoronoiCell;
import de.bioforscher.singa.mathematics.algorithms.voronoi.model.VoronoiDiagram;
import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.graphs.model.Graphs;
import de.bioforscher.singa.mathematics.graphs.model.Node;
import de.bioforscher.singa.mathematics.topology.grids.rectangular.RectangularCoordinate;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.mathematics.vectors.Vectors;
import de.bioforscher.singa.simulation.model.compartments.EnclosedCompartment;
import de.bioforscher.singa.simulation.model.graphs.AutomatonEdge;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraphs;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import tec.uom.se.quantity.Quantities;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

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
    private AutomatonGraph graph;
    private AnimationTimer timer;
    private VoronoiDiagram voronoiDiagram;


    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {

        canvas = new Canvas(500, 500);

        BorderPane root = new BorderPane();
        root.setCenter(canvas);

        Rectangle rectangle = new Rectangle(500, 500);
        Rectangle vesicleRegion = new Rectangle(new Vector2D(120, 120), new Vector2D(380, 380));
        layer = new VesicleLayer(rectangle);

        graph = AutomatonGraphs.useStructureFrom(Graphs.buildGridGraph(10, 10, rectangle));

        GraphDrawingTool<AutomatonNode, AutomatonEdge, RectangularCoordinate, AutomatonGraph> gdt = new GraphDrawingTool<>(graph,
                new SimpleDoubleProperty(500), new SimpleDoubleProperty(500), 100);
        for (int i = 0; i < 20; i++) {
            gdt.arrangeGraph(80);
        }
        List<Vector2D> sites = graph.getNodes().stream().map(Node::getPosition).collect(Collectors.toList());
        voronoiDiagram = VoronoiGenerator.generateVoronoiDiagram(sites, rectangle);

        GraphRenderer<AutomatonNode, AutomatonEdge, RectangularCoordinate, AutomatonGraph> renderer = new GraphRenderer<>();
        renderer.getRenderingOptions().setDisplayingEdges(false);
        renderer.getRenderingOptions().setDisplayingNodes(false);
        renderer.drawingWidthProperty().bind(canvas.widthProperty());
        renderer.drawingHeightProperty().bind(canvas.heightProperty());
        renderer.setGraphicsContext(canvas.getGraphicsContext2D());

        canvas.widthProperty().bind(root.widthProperty());
        canvas.heightProperty().bind(root.heightProperty());

        canvas.widthProperty().addListener(observable -> renderer.render(graph));
        canvas.heightProperty().addListener(observable -> renderer.render(graph));

        // define scales
        // todo should be initialized automatically
        EnvironmentalParameters.setSystemLength(Quantities.getQuantity(5, MICRO(METRE)));
        EnvironmentalParameters.setSimulationLength(500);
        EnvironmentalParameters.setTimeStep(Quantities.getQuantity(1, MICRO(SECOND)));
        System.out.println(EnvironmentalParameters.convertSystemToSimulationScale(Quantities.getQuantity(1, MICRO(METRE))));
        System.out.println(EnvironmentalParameters.convertSimulationToSystemScale(1));

        // define compartments
        EnclosedCompartment cytoplasm = new EnclosedCompartment("cytoplasm", "cytoplasm");
        graph.getNodes().forEach(node -> node.setCellSection(cytoplasm));

        renderer.setRenderAfter(graph -> {
            renderer.drawDiagram(voronoiDiagram);
            moveVesicles();
            return null;
        });

        // instantiate vesicles
        for (int i = 0; i < 50; i++) {
            Vesicle vesicle = new Vesicle(String.valueOf(i),
                    Vectors.generateRandom2DVector(vesicleRegion),
                    Quantities.getQuantity(ThreadLocalRandom.current().nextDouble(100, 200), NANO(METRE)),
                    cytoplasm);
            layer.addVesicle(vesicle);
        }

        renderVesicles();

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                renderer.render(graph);
            }
        };
        timer.start();

        // show
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();


    }

    public void moveVesicles() {
        layer.nextEpoch();
        renderVesicles();
    }

    public void renderVesicles() {
        // background
        // getGraphicsContext().setFill(Color.WHITE);
        // getGraphicsContext().fillRect(0, 0, getDrawingWidth(), getDrawingHeight());
        // vesicles
        getGraphicsContext().setLineWidth(1.0);
        for (Vesicle vesicle : layer.getVesicles()) {
            getGraphicsContext().setFill(Color.GREENYELLOW.brighter());
            for (VoronoiCell voronoiCell : voronoiDiagram.getCells()) {
                if (voronoiCell.evaluatePointPosition(vesicle.getPosition()) == VoronoiCell.INSIDE) {
                    fillPolygon(voronoiCell);
                    break;
                }
            }
        }
        for (Vesicle vesicle : layer.getVesicles()) {
            getGraphicsContext().setFill(Color.BLACK);
            circlePoint(vesicle.getPosition(), EnvironmentalParameters.convertSystemToSimulationScale(vesicle.getRadius()));
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
