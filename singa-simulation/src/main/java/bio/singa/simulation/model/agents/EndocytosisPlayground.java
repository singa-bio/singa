package bio.singa.simulation.model.agents;

import bio.singa.chemistry.entities.Protein;
import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.features.identifiers.ChEBIIdentifier;
import bio.singa.features.identifiers.UniProtIdentifier;
import bio.singa.features.parameters.Environment;
import bio.singa.javafx.renderer.Renderer;
import bio.singa.mathematics.geometry.edges.SimpleLineSegment;
import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.geometry.model.Polygon;
import bio.singa.mathematics.topology.grids.rectangular.NeumannRectangularDirection;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.agents.filaments.FilamentLayer;
import bio.singa.simulation.model.agents.filaments.SkeletalFilament;
import bio.singa.simulation.model.agents.membranes.Membrane;
import bio.singa.simulation.model.agents.membranes.MembraneFactory;
import bio.singa.simulation.model.agents.membranes.MembraneLayer;
import bio.singa.simulation.model.agents.membranes.MembraneSegment;
import bio.singa.simulation.model.agents.organelles.OrganelleImageParser;
import bio.singa.simulation.model.agents.organelles.OrganelleTemplate;
import bio.singa.simulation.model.agents.organelles.OrganelleTypes;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.CellSubsections;
import bio.singa.simulation.model.simulation.Simulation;
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
import java.util.Map;

import static tec.uom.se.unit.MetricPrefix.MICRO;
import static tec.uom.se.unit.Units.METRE;

/**
 * @author cl
 */
public class EndocytosisPlayground extends Application implements Renderer {

    private Canvas canvas;
    private AutomatonGraph graph;
    private MembraneLayer membranes;
    private FilamentLayer filaments;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {

        double simulationExtend = 800;
        int nodesHorizontal = 15;
        int nodesVertical = 15;

        ComparableQuantity<Length> systemExtend = Quantities.getQuantity(4.9, MICRO(METRE));
        Environment.setSystemExtend(systemExtend);
        Environment.setSimulationExtend(simulationExtend);
        Environment.setNodeSpacingToDiameter(systemExtend, nodesHorizontal);
        Rectangle rectangle = new Rectangle(simulationExtend, simulationExtend);

        Simulation simulation = new Simulation();
        graph = AutomatonGraphs.createRectangularAutomatonGraph(nodesHorizontal, nodesVertical);
        simulation.setGraph(graph);
        simulation.setSimulationRegion(rectangle);
        simulation.initializeGraph();
        simulation.initializeSpatialRepresentations();

        canvas = new Canvas(simulationExtend, simulationExtend);
        BorderPane root = new BorderPane();
        root.setCenter(canvas);

        OrganelleTemplate template = OrganelleImageParser.getOrganelleTemplate("organelle_templates/endocytossis_system.png");
        template.initializeGroup(-65536, OrganelleTypes.CELL.getMembraneRegion());
        template.initializeGroup(-16711936, OrganelleTypes.EARLY_ENDOSOME.getMembraneRegion());
        template.initializeGroup(-16776968, OrganelleTypes.EARLY_ENDOSOME.getMembraneRegion());
        template.reduce();
        template.mapToSystemExtend();

        membranes = new MembraneLayer();
        simulation.setMembraneLayer(membranes);
        Map<Vector2D, CellRegion> inverseRegionMap = template.getInverseRegionMap();
        Membrane membrane = MembraneFactory.createLinearMembrane(template.getGroups().get(-65536), OrganelleTypes.CELL.getInnerRegion(), OrganelleTypes.CELL.getMembraneRegion(), NeumannRectangularDirection.EAST, graph, inverseRegionMap, rectangle);
        Membrane endosome1 = MembraneFactory.createClosedMembrane(template.getGroups().get(-16711936), OrganelleTypes.EARLY_ENDOSOME.getInnerRegion(), OrganelleTypes.EARLY_ENDOSOME.getMembraneRegion(), graph, inverseRegionMap);
        Membrane endosome2 = MembraneFactory.createClosedMembrane(template.getGroups().get(-16776968), OrganelleTypes.EARLY_ENDOSOME.getInnerRegion(), OrganelleTypes.EARLY_ENDOSOME.getMembraneRegion(), graph, inverseRegionMap);
        membranes.addMembrane(membrane);
        membranes.addMembrane(endosome1);
        membranes.addMembrane(endosome2);

        filaments = new FilamentLayer(simulation, membranes);
        for (int i = 0; i < 7; i++) {
            filaments.spawnFilament(endosome2, membrane);
            filaments.spawnFilament(endosome1, membrane);
        }

//        while (filaments.hasGrowingFilaments()) {
//            filaments.nextEpoch();
//        }

        // species

        // catalytic subunit of PKA
        Protein catalytic = new Protein.Builder("PKAC")
                .additionalIdentifier(new UniProtIdentifier("P22694"))
                .build();

        // aquaporin 2
        Protein aqp2 = new Protein.Builder("AQP2")
                .additionalIdentifier(new UniProtIdentifier("P41181"))
                .build();

        // atp
        SmallMolecule atp = SmallMolecule.create("ATP")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:15422"))
                .build();




        // initialize




        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                render();
            }
        };
        animationTimer.start();

    }

    private void render() {
        getGraphicsContext().setLineWidth(1);
        getGraphicsContext().setStroke(Color.BLACK);
        for (AutomatonNode node : graph.getNodes()) {
            Polygon nodePolygon = node.getSpatialRepresentation();
            if (node.getCellRegion().hasMembrane()) {
                getGraphicsContext().setFill(CellSubsections.getColor(node.getCellRegion().getOuterSubsection()));
                fillPolygon(nodePolygon);
                getGraphicsContext().setFill(CellSubsections.getColor(node.getCellRegion().getInnerSubsection()));
                Polygon subsectionPolygon = node.getSubsectionRepresentations().get(node.getCellRegion().getInnerSubsection());
                fillPolygon(subsectionPolygon);
            } else {
                getGraphicsContext().setFill(CellSubsections.getColor(node.getCellRegion().getInnerSubsection()));
                fillPolygon(nodePolygon);
            }
            strokePolygon(nodePolygon);
        }

        //  draw membrane
        getGraphicsContext().setStroke(Color.BLACK);
        if (membranes != null) {
            for (Membrane membrane : membranes.getMembranes()) {
                List<MembraneSegment> segments = membrane.getSegments();
                for (MembraneSegment segment : segments) {
                    getGraphicsContext().setLineWidth(3);
                    strokeLineSegment(segment);
                }
            }
        }

        // draw filaments
        getGraphicsContext().setStroke(Color.BLACK);
        if (filaments != null) {
            for (SkeletalFilament skeletalFilament : filaments.getFilaments()) {
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
