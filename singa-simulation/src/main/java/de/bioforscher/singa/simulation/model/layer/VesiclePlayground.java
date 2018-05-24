package de.bioforscher.singa.simulation.model.layer;

import de.bioforscher.singa.features.parameters.EnvironmentalParameters;
import de.bioforscher.singa.javafx.renderer.Renderer;
import de.bioforscher.singa.javafx.renderer.graphs.GraphRenderer;
import de.bioforscher.singa.mathematics.geometry.faces.Circle;
import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.geometry.model.Polygon;
import de.bioforscher.singa.mathematics.topology.grids.rectangular.RectangularCoordinate;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.model.compartments.EnclosedCompartment;
import de.bioforscher.singa.simulation.model.graphs.AutomatonEdge;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraphs;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Area;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
    private Rectangle rectangle;

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
        Rectangle vesicleRegion = new Rectangle(new Vector2D(100, 100), new Vector2D(700, 700));
        layer = new VesicleLayer(rectangle);

        Simulation simulation = new Simulation();

        // define scales
        EnvironmentalParameters.setSystemExtend(Quantities.getQuantity(10, MICRO(METRE)));
        EnvironmentalParameters.setSimulationExtend(simulationExtend);
        EnvironmentalParameters.setNodeSpacingToDiameter(EnvironmentalParameters.getSystemExtend(), 10);
        EnvironmentalParameters.setTimeStep(Quantities.getQuantity(1, MICRO(SECOND)));

        // define graphs
        graph = AutomatonGraphs.createRectangularAutomatonGraph(10, 10);
        simulation.setGraph(graph);
        simulation.initializeSpatialRepresentations();

        // define compartments
        EnclosedCompartment cytoplasm = new EnclosedCompartment("cytoplasm", "cytoplasm");
        graph.getNodes().forEach(node -> node.setCellSection(cytoplasm));

        // instantiate some vesicles
        for (int i = 0; i < 1; i++) {
            Vesicle vesicle = new Vesicle(String.valueOf(i),
                    new Vector2D(480, 400),
                    Quantities.getQuantity(150, NANO(METRE)),
                    cytoplasm);
            layer.addVesicle(vesicle);
        }

        // initialize renderer and options
        GraphRenderer<AutomatonNode, AutomatonEdge, RectangularCoordinate, AutomatonGraph> renderer = new GraphRenderer<>();
        renderer.getRenderingOptions().setDisplayingEdges(false);
        renderer.getRenderingOptions().setDisplayingNodes(false);
        renderer.setGraphicsContext(canvas.getGraphicsContext2D());

        renderer.setRenderAfter(graph -> {
            moveVesicles();
            return null;
        });

        AnimationTimer timer = new AnimationTimer() {
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
        fillPolygon(rectangle);
        graph.getNodes().stream().map(AutomatonNode::getSpatialRepresentation).forEach(this::drawPolygon);
        for (Vesicle vesicle : layer.getVesicles()) {
            getGraphicsContext().setLineWidth(1);
            double radius = EnvironmentalParameters.convertSystemToSimulationScale(vesicle.getRadius());
            getGraphicsContext().setFill(Color.BLACK);
            circlePoint(vesicle.getPosition(), radius * 2);
            AutomatonNode representativeNode = null;
            Map<AutomatonNode, Set<Vector2D>> associatedNodes = new HashMap<>();
            Circle vesicleCircle = new Circle(vesicle.getPosition(), radius);
            for (AutomatonNode node : graph.getNodes()) {
                Polygon polygon = node.getSpatialRepresentation();
                getGraphicsContext().setLineWidth(2);
                // associate vesicle to the node with the largest part of the vesicle (midpoint is inside)
                if (representativeNode == null && polygon.evaluatePointPosition(vesicle.getPosition()) == Polygon.INSIDE) {
                    drawStraight(node.getPosition(), vesicle.getPosition());
                    representativeNode = node;
                }
                // associate partial containment to other nodes
                getGraphicsContext().setLineWidth(1);
                Set<Vector2D> intersections = polygon.getIntersections(vesicleCircle);
                if (!intersections.isEmpty()) {
                    drawStraight(node.getPosition(), vesicle.getPosition());
                    intersections.forEach(intersection -> circlePoint(intersection, 3));
                    associatedNodes.put(node, intersections);
                }
            }
            associatedNodes.remove(representativeNode);
            // the surface of the implicit sphere
            final double totalSurface = 4.0 * Math.PI * radius * radius;
            double reducedSurface = totalSurface;
            System.out.println("Surface area: " + totalSurface);
            for (Map.Entry<AutomatonNode, Set<Vector2D>> entry : associatedNodes.entrySet()) {
                Set<Vector2D> intersections = entry.getValue();
                if (intersections.size() == 2) {
                    Iterator<Vector2D> iterator = intersections.iterator();
                    Vector2D first = iterator.next();
                    Vector2D second = iterator.next();
                    double theta = vesicleCircle.getCentralAngleBetween(first, second);
                    // the spherical lune of the implicit sphere associated to the automaton node
                    // http://mathworld.wolfram.com/SphericalLune.html
                    double associatedSurface = 2 * radius * radius * theta;
                    double fraction = associatedSurface / totalSurface;
                    reducedSurface -= associatedSurface;
                    Quantity<Area> nodeSurface = vesicle.getArea().multiply(fraction);
                    entry.getKey().associateVesicle(vesicle, nodeSurface);
                }
            }
            double fraction = reducedSurface / totalSurface;
            Quantity<Area> nodeSurface = vesicle.getArea().multiply(fraction);
            assert representativeNode != null;
            representativeNode.associateVesicle(vesicle, nodeSurface);
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
