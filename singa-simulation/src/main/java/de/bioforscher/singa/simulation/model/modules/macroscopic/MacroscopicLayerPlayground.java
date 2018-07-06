package de.bioforscher.singa.simulation.model.modules.macroscopic;

import de.bioforscher.singa.features.model.FeatureOrigin;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.javafx.renderer.Renderer;
import de.bioforscher.singa.mathematics.geometry.edges.LineSegment;
import de.bioforscher.singa.mathematics.geometry.edges.SimpleLineSegment;
import de.bioforscher.singa.mathematics.geometry.faces.Circle;
import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.geometry.model.Polygon;
import de.bioforscher.singa.mathematics.topology.grids.rectangular.RectangularCoordinate;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.features.endocytosis.BuddingFrequency;
import de.bioforscher.singa.simulation.features.endocytosis.MaturationTime;
import de.bioforscher.singa.simulation.features.endocytosis.VesicleRadius;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraphs;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import de.bioforscher.singa.simulation.model.modules.displacement.Vesicle;
import de.bioforscher.singa.simulation.model.modules.displacement.implementations.ConstitutiveEndocytosis;
import de.bioforscher.singa.simulation.model.modules.displacement.implementations.VesicleDiffusion;
import de.bioforscher.singa.simulation.model.sections.CellRegion;
import de.bioforscher.singa.simulation.model.sections.CellSubsection;
import de.bioforscher.singa.simulation.model.sections.CellTopology;
import de.bioforscher.singa.simulation.model.simulation.Simulation;
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
import java.util.Iterator;
import java.util.List;

import static tec.uom.se.unit.MetricPrefix.MICRO;
import static tec.uom.se.unit.MetricPrefix.NANO;
import static tec.uom.se.unit.Units.*;

/**
 * @author cl
 */
public class MacroscopicLayerPlayground extends Application implements Renderer {

    private Canvas canvas;
    private FilamentLayer skeletalLayer;
    private MembraneLayer membraneLayer;
    private Rectangle rectangle;
    private AutomatonGraph graph;
    private Simulation simulation;
    private ConstitutiveEndocytosis endocytosis;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {

        double simulationExtend = 800;
        int nodesHorizontal = 22;
        int nodesVertical = 22;

        canvas = new Canvas(simulationExtend, simulationExtend);
        rectangle = new Rectangle(simulationExtend, simulationExtend);
        BorderPane root = new BorderPane();
        root.setCenter(canvas);

        simulation = new Simulation();

        // setup regions
        ComparableQuantity<Length> systemExtend = Quantities.getQuantity(22, MICRO(METRE));
        Environment.setSystemExtend(systemExtend);
        Environment.setSimulationExtend(simulationExtend);
        Environment.setNodeSpacingToDiameter(systemExtend, nodesHorizontal);
        Environment.setTimeStep(Quantities.getQuantity(1, MICRO(SECOND)));

        // distribute nodes to sections
        CellRegion outer = new CellRegion("Outer");
        CellSubsection outerSubsection = new CellSubsection("Aqueous Solution");
        outer.addSubSection(CellTopology.INNER, outerSubsection);

        CellRegion inner = new CellRegion("Inner");
        CellSubsection cytoplasm = new CellSubsection("Cytoplasm");
        inner.addSubSection(CellTopology.INNER, cytoplasm);

        CellRegion cellMembrane = new CellRegion("Cell membrane");
        cellMembrane.addSubSection(CellTopology.INNER, cytoplasm);
        cellMembrane.addSubSection(CellTopology.MEMBRANE, new CellSubsection("Plasma membrane"));
        cellMembrane.addSubSection(CellTopology.OUTER, outerSubsection);

        CellRegion nuclearEnvelope = new CellRegion("Nuclear envelope");
        CellSubsection nucleoplasm = new CellSubsection("Nucleoplasm");
        nuclearEnvelope.addSubSection(CellTopology.INNER, nucleoplasm);
        nuclearEnvelope.addSubSection(CellTopology.MEMBRANE, new CellSubsection("Nuclear membrane"));
        nuclearEnvelope.addSubSection(CellTopology.OUTER, cytoplasm);

        CellRegion nucleus = new CellRegion("Nucleus");
        nucleus.addSubSection(CellTopology.INNER, nucleoplasm);

        // setup endocytosis
        endocytosis = new ConstitutiveEndocytosis();
        endocytosis.setSimulation(simulation);;
        endocytosis.setFeature(new BuddingFrequency(Quantities.getQuantity(0.1, HERTZ), FeatureOrigin.MANUALLY_ANNOTATED));
        endocytosis.setFeature(new VesicleRadius(Quantities.getQuantity(75.0, NANO(METRE)), FeatureOrigin.MANUALLY_ANNOTATED));
        endocytosis.setFeature(new MaturationTime(Quantities.getQuantity(100, SECOND), FeatureOrigin.MANUALLY_ANNOTATED));

        simulation.getModules().add(endocytosis);

        // setup vesicle diffusion
        VesicleDiffusion vesicleDiffusion = new VesicleDiffusion();
        vesicleDiffusion.setSimulation(simulation);
        simulation.getModules().add(vesicleDiffusion);

        graph = AutomatonGraphs.createRectangularAutomatonGraph(nodesHorizontal, nodesVertical);
        simulation.setGraph(graph);
        for (AutomatonNode node : graph.getNodes()) {
            int column = node.getIdentifier().getColumn();
            int row = node.getIdentifier().getRow();
            if ((column == 1 && row != 0 && row != nodesVertical - 1) ||
                    (column == nodesVertical - 2 && row != 0 && row != nodesVertical - 1) ||
                    (row == 1 && column != 0 && column != nodesVertical - 1) ||
                    (row == nodesVertical - 2 && column != 0 && column != nodesVertical - 1)) {
                node.setCellRegion(cellMembrane);
            } else if (column == 0 || column == nodesVertical - 1 || row == 0 || row == nodesVertical - 1) {
                node.setCellRegion(outer);
            } else {
                node.setCellRegion(inner);
            }
        }

        AutomatonGraphs.circleRegion(graph, nuclearEnvelope, new RectangularCoordinate(11, 11), 3);
        AutomatonGraphs.fillRegion(graph, nucleus, new RectangularCoordinate(11, 11), 2);

        // 1 setup spatial representations
        simulation.initializeSpatialRepresentations();
        // setup membrane layer
        membraneLayer = new MembraneLayer(MembraneComposer.composeMacroscopicMembrane(graph));

        // add left membrane to as endocytosis site
        // TODO the spawn frequency should be depending on the area of segments
        List<MacroscopicMembrane> membranes = membraneLayer.getMembranes();
        for (MacroscopicMembrane membrane : membranes) {
            if (membrane.getIdentifier().equals("Cell membrane")) {
                for (MembraneSegment membraneSegment : membrane.getSegments()) {
                    if (membraneSegment.getNode().getIdentifier().getColumn() == 1) {
                        endocytosis.addMembraneSegment(membraneSegment);
                    }
                }
            }
        }

        // setup filament layer
        skeletalLayer = new FilamentLayer(rectangle, membraneLayer);
//        Iterator<MacroscopicMembrane> iterator = membraneLayer.getMembranes().iterator();
//        iterator.next();
//        MacroscopicMembrane membrane = iterator.next();
//        for (int i = 0; i < 30; i++) {
//            skeletalLayer.spawnHorizontalFilament(membrane);
//        }

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // skeletalLayer.nextEpoch();
                simulation.nextEpoch();
                System.out.println(simulation.getElapsedTime().to(SECOND));
                render();
            }
        };
        animationTimer.start();

    }

    public void render() {
        getGraphicsContext().setFill(Color.WHITE);
        fillPolygon(rectangle);
        // draw cells
        getGraphicsContext().setFill(Color.BLACK);
        getGraphicsContext().setLineWidth(1);
        for (AutomatonNode node : graph.getNodes()) {
            Polygon polygon = node.getSpatialRepresentation();
            // http://colorbrewer2.org/?type=diverging&scheme=PRGn&n=7
            // todo define some better color handling
            switch (node.getCellRegion().getIdentifier()) {
                case "Outer":
                    getGraphicsContext().setFill(Color.color(231.0 / 256.0, 212.0 / 256.0, 232.0 / 256.0));
                    break;
                case "Inner":
                    getGraphicsContext().setFill(Color.color(217.0 / 256.0, 240.0 / 256.0, 211.0 / 256.0));
                    break;
                case "Cell membrane":
                    getGraphicsContext().setFill(Color.color(175.0 / 256.0, 141.0 / 256.0, 195.0 / 256.0));
                    break;
                case "Nuclear envelope":
                    getGraphicsContext().setFill(Color.color(127.0 / 256.0, 191.0 / 256.0, 123.0 / 256.0));
                    break;
                case "Nucleus":
                    getGraphicsContext().setFill(Color.color(27.0 / 256.0, 120.0 / 256.0, 55.0 / 256.0));
                    break;
            }
            fillPolygon(polygon);
            getGraphicsContext().setStroke(Color.BLACK);
            strokePolygon(polygon);
        }
        // draw membrane
        getGraphicsContext().setLineWidth(3);
        if (membraneLayer != null) {
            for (MacroscopicMembrane membrane : membraneLayer.getMembranes()) {
                List<MembraneSegment> segments = membrane.getSegments();
                for (MembraneSegment segment : segments) {
                    List<LineSegment> lineSegments = segment.getLineSegments();
                    for (LineSegment lineSegment : lineSegments) {
                        strokeLineSegment(lineSegment);
                    }
                }
            }
        }
        // draw budding vesicles
        getGraphicsContext().setLineWidth(1);
        getGraphicsContext().setFill(Color.YELLOWGREEN);
        for (Vesicle vesicle : endocytosis.getMaturingVesicles().keySet()) {
            Circle circle = vesicle.getCircleRepresentation();
            fillCircle(circle);
            strokeCircle(circle);
        }

        getGraphicsContext().setFill(Color.BLUE);
        for (Vesicle vesicle : simulation.getVesicleLayer().getVesicles()) {
            Circle circle = vesicle.getCircleRepresentation();
            fillCircle(circle);
            strokeCircle(circle);
        }
        // draw filaments
        getGraphicsContext().setStroke(Color.BLACK);
        getGraphicsContext().setLineWidth(3);
        for (SkeletalFilament skeletalFilament : skeletalLayer.getFilaments()) {
            switch (skeletalFilament.getPlusEndBehaviour()) {
                case GROW:
                    getGraphicsContext().setStroke(Color.GREEN);
                    break;
                case STAGNANT:
                    getGraphicsContext().setStroke(Color.BLACK);
                    break;
                case FOLLOW:
                    getGraphicsContext().setStroke(Color.BLUE);
                    break;
            }
            if (skeletalFilament.getSegments().size() > 1) {
                Vector2D previous = skeletalFilament.getSegments().getLast();
                Iterator<Vector2D> vector2DIterator = skeletalFilament.getSegments().descendingIterator();
                while (vector2DIterator.hasNext()) {
                    Vector2D current = vector2DIterator.next();
                    if (current != previous) {
                        SimpleLineSegment lineSegment = new SimpleLineSegment(previous, current);
                        strokeLineSegment(lineSegment);
                    }
                    previous = current;
                }
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
