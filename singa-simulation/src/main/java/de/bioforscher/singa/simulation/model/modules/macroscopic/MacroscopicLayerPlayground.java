package de.bioforscher.singa.simulation.model.modules.macroscopic;

import de.bioforscher.singa.chemistry.annotations.Annotation;
import de.bioforscher.singa.chemistry.annotations.AnnotationType;
import de.bioforscher.singa.chemistry.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.entities.ComplexedChemicalEntity;
import de.bioforscher.singa.chemistry.entities.Protein;
import de.bioforscher.singa.features.identifiers.UniProtIdentifier;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.javafx.renderer.Renderer;
import de.bioforscher.singa.mathematics.geometry.edges.LineSegment;
import de.bioforscher.singa.mathematics.geometry.edges.SimpleLineSegment;
import de.bioforscher.singa.mathematics.geometry.faces.Circle;
import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.geometry.model.Polygon;
import de.bioforscher.singa.mathematics.topology.grids.rectangular.RectangularCoordinate;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraphs;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import de.bioforscher.singa.simulation.model.modules.concentration.imlementations.NthOrderReaction;
import de.bioforscher.singa.simulation.model.modules.displacement.Vesicle;
import de.bioforscher.singa.simulation.model.modules.displacement.implementations.*;
import de.bioforscher.singa.simulation.model.modules.macroscopic.filaments.FilamentLayer;
import de.bioforscher.singa.simulation.model.modules.macroscopic.filaments.SkeletalFilament;
import de.bioforscher.singa.simulation.model.modules.macroscopic.membranes.MacroscopicMembrane;
import de.bioforscher.singa.simulation.model.modules.macroscopic.membranes.MacroscopicMembraneLayer;
import de.bioforscher.singa.simulation.model.modules.macroscopic.membranes.MacroscopicMembraneSegment;
import de.bioforscher.singa.simulation.model.modules.macroscopic.membranes.MacroscopicMembraneTracer;
import de.bioforscher.singa.simulation.model.modules.macroscopic.organelles.MicrotubuleOrganizingCentre;
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
import tec.uom.se.unit.ProductUnit;

import javax.measure.quantity.Area;
import javax.measure.quantity.Length;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static de.bioforscher.singa.simulation.features.endocytosis.ActinBoostVelocity.DEFAULT_ACTIN_VELOCITY;
import static de.bioforscher.singa.simulation.features.endocytosis.AttachmentDistance.DEFAULT_DYNEIN_ATTACHMENT_DISTANCE;
import static de.bioforscher.singa.simulation.features.endocytosis.BuddingRate.DEFAULT_BUDDING_RATE;
import static de.bioforscher.singa.simulation.features.endocytosis.MaturationTime.DEFAULT_MATURATION_TIME;
import static de.bioforscher.singa.simulation.features.endocytosis.MotorMovementVelocity.DEFAULT_MOTOR_VELOCITY;
import static de.bioforscher.singa.simulation.features.endocytosis.TetheringTime.DEFAULT_TETHERING_TIME;
import static de.bioforscher.singa.simulation.features.endocytosis.VesicleRadius.DEFAULT_VESICLE_RADIUS;
import static de.bioforscher.singa.simulation.model.modules.displacement.implementations.EndocytosisActinBoost.DEFAULT_CLATHRIN_DEPOLYMERIZATION_RATE;
import static tec.uom.se.unit.MetricPrefix.MICRO;
import static tec.uom.se.unit.MetricPrefix.NANO;
import static tec.uom.se.unit.Units.METRE;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
public class MacroscopicLayerPlayground extends Application implements Renderer {

    private Canvas canvas;
    private FilamentLayer filamentLayer;
    private MacroscopicMembraneLayer membraneLayer;
    private Rectangle rectangle;
    private AutomatonGraph graph;
    private Simulation simulation;
    private ClathrinMediatedEndocytosis budding;
    private VesicleFusion fusion;

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
        simulation.setSimulationRegion(rectangle);

        // setup scaling
        ComparableQuantity<Length> systemExtend = Quantities.getQuantity(22, MICRO(METRE));
        Environment.setSystemExtend(systemExtend);
        Environment.setSimulationExtend(simulationExtend);
        Environment.setNodeSpacingToDiameter(systemExtend, nodesHorizontal);
        Environment.setTimeStep(Quantities.getQuantity(1, MICRO(SECOND)));

        // setup regions
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

        // setup species for clathrin decay
        ChemicalEntity clathrinHeavyChain = new Protein.Builder("Clathrin heavy chain")
                .assignFeature(new UniProtIdentifier("Q00610"))
                .build();

        ChemicalEntity clathrinLightChain = new Protein.Builder("Clathrin light chain")
                .assignFeature(new UniProtIdentifier("P09496"))
                .build();

        ComplexedChemicalEntity clathrinTriskelion = ComplexedChemicalEntity.create("Clathrin Triskelion")
                .addAssociatedPart(clathrinHeavyChain, 3)
                .addAssociatedPart(clathrinLightChain, 3)
                .build();

        // setup snares for fusion
        Protein vamp2 = new Protein.Builder("VAMP2")
                .assignFeature(new UniProtIdentifier("Q15836"))
                .annotation(new Annotation<>(AnnotationType.NOTE, "SNARE type", "R-SNARE"))
                .build();

        Protein vamp3 = new Protein.Builder("VAMP3")
                .assignFeature(new UniProtIdentifier("P63027"))
                .annotation(new Annotation<>(AnnotationType.NOTE, "SNARE type", "R-SNARE"))
                .build();

        Protein syntaxin3 = new Protein.Builder("Syntaxin 3")
                .assignFeature(new UniProtIdentifier("Q13277"))
                .annotation(new Annotation<>(AnnotationType.NOTE, "SNARE type", "Qa-SNARE"))
                .build();

        Protein syntaxin4 = new Protein.Builder("Syntaxin 4")
                .assignFeature(new UniProtIdentifier("Q12846"))
                .annotation(new Annotation<>(AnnotationType.NOTE, "SNARE type", "Qa-SNARE"))
                .build();

        Protein snap23 = new Protein.Builder("SNAP23")
                .assignFeature(new UniProtIdentifier("O00161"))
                .annotation(new Annotation<>(AnnotationType.NOTE, "SNARE type", "Qbc-SNARE"))
                .build();

        ComplexedChemicalEntity snareComplex1 = ComplexedChemicalEntity.create(syntaxin3.getIdentifier().getIdentifier() + ":" + snap23.getIdentifier().getIdentifier())
                .addAssociatedPart(syntaxin3)
                .addAssociatedPart(snap23)
                .annotation(new Annotation<>(AnnotationType.NOTE, "SNARE type", "Qabc-SNARE"))
                .build();

        ComplexedChemicalEntity snareComplex2 = ComplexedChemicalEntity.create(syntaxin4.getIdentifier().getIdentifier() + ":" + snap23.getIdentifier().getIdentifier())
                .addAssociatedPart(syntaxin4)
                .addAssociatedPart(snap23)
                .annotation(new Annotation<>(AnnotationType.NOTE, "SNARE type", "Qabc-SNARE"))
                .build();

        // setup clathrin decay reaction
        NthOrderReaction.inSimulation(simulation)
                .rateConstant(DEFAULT_CLATHRIN_DEPOLYMERIZATION_RATE)
                .addSubstrate(clathrinTriskelion)
                .build();

        // setup endocytosis budding
        budding = new ClathrinMediatedEndocytosis();
        budding.setSimulation(simulation);
        budding.addMembraneCargo(Quantities.getQuantity(31415.93, new ProductUnit<Area>(NANO(METRE).pow(2))), 60.0, clathrinTriskelion);
        budding.addMembraneCargo(Quantities.getQuantity(10000, new ProductUnit<Area>(NANO(METRE).pow(2))), 10, vamp3);
        budding.setFeature(DEFAULT_BUDDING_RATE);
        budding.setFeature(DEFAULT_VESICLE_RADIUS);
        budding.setFeature(DEFAULT_MATURATION_TIME);
        simulation.getModules().add(budding);

        // setup vesicle diffusion
        VesicleDiffusion diffusion = new VesicleDiffusion();
        diffusion.useLiteratureDiffusivity();
        diffusion.setSimulation(simulation);
        simulation.getModules().add(diffusion);

        // setup actin boost
        EndocytosisActinBoost boost = new EndocytosisActinBoost();
        boost.setDecayingEntity(clathrinTriskelion);
        boost.setFeature(DEFAULT_ACTIN_VELOCITY);
        boost.setSimulation(simulation);
        simulation.getModules().add(boost);

        // setup attachment
        VesicleAttachment attachment = new VesicleAttachment();
        attachment.setFeature(DEFAULT_DYNEIN_ATTACHMENT_DISTANCE);
        attachment.setSimulation(simulation);
        simulation.getModules().add(attachment);

        // setup transport
        VesicleTransport transport = new VesicleTransport();
        transport.setFeature(DEFAULT_MOTOR_VELOCITY);
        transport.setSimulation(simulation);
        simulation.getModules().add(transport);

        // setup tethering and fusion
        fusion = new VesicleFusion();
        fusion.addMatchingQSnare(snareComplex1);
        fusion.addMatchingQSnare(snareComplex2);
        fusion.addMatchingRSnare(vamp2);
        fusion.addMatchingRSnare(vamp3);
        fusion.initializeComplexes();
        fusion.setMinimalPairs(3);
        fusion.setFeature(DEFAULT_TETHERING_TIME);
        fusion.setFeature(DEFAULT_DYNEIN_ATTACHMENT_DISTANCE);
        fusion.setSimulation(simulation);
        simulation.getModules().add(fusion);

        // setup graph and assign regions
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

        Set<AutomatonNode> envelopeNodes = AutomatonGraphs.circleRegion(graph, nuclearEnvelope, new RectangularCoordinate(11, 11), 3);
        for (AutomatonNode envelopeNode : envelopeNodes) {
            envelopeNode.getConcentrationContainer().set(CellTopology.MEMBRANE, snareComplex2, MolarConcentration.moleculesToConcentration(20, Environment.getSubsectionVolume()));
        }
        AutomatonGraphs.fillRegion(graph, nucleus, new RectangularCoordinate(11, 11), 2);

        // setup spatial representations
        simulation.initializeSpatialRepresentations();
        // setup membrane layer
        membraneLayer = new MacroscopicMembraneLayer(MacroscopicMembraneTracer.composeMacroscopicMembrane(graph));
        simulation.setMembraneLayer(membraneLayer);

        // add left membrane to as endocytosis site
        List<MacroscopicMembrane> membranes = membraneLayer.getMembranes();
        for (MacroscopicMembrane membrane : membranes) {
            if (membrane.getIdentifier().equals("Cell membrane")) {
                for (MacroscopicMembraneSegment membraneSegment : membrane.getSegments()) {
                    if (membraneSegment.getNode().getIdentifier().getColumn() == 1) {
                        budding.addMembraneSegment(membraneSegment);
                    }
                }
            }
        }

        // setup microtubules
        MicrotubuleOrganizingCentre moc = new MicrotubuleOrganizingCentre(simulation, membraneLayer, new Circle(new Vector2D(310, 400),
                Environment.convertSystemToSimulationScale(Quantities.getQuantity(250, NANO(METRE)))), 60);
        filamentLayer = moc.initializeFilaments();

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
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
                List<MacroscopicMembraneSegment> segments = membrane.getSegments();
                for (MacroscopicMembraneSegment segment : segments) {
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
        for (Vesicle vesicle : budding.getMaturingVesicles().keySet()) {
            Circle circle = vesicle.getCircleRepresentation();
            fillCircle(circle);
            strokeCircle(circle);
            strokeTextCenteredOnPoint(vesicle.getStringIdentifier(), circle.getMidpoint().add(Vector2D.UNIT_VECTOR_RIGHT));
        }
        // draw moving vesicles
        getGraphicsContext().setFill(Color.BLUE);
        for (Vesicle vesicle : simulation.getVesicleLayer().getVesicles()) {
            Circle circle = vesicle.getCircleRepresentation();
            fillCircle(circle);
            strokeCircle(circle);
            strokeTextCenteredOnPoint(vesicle.getStringIdentifier(), circle.getMidpoint().add(Vector2D.UNIT_VECTOR_RIGHT));
        }
        // draw fusing vesicles
        getGraphicsContext().setFill(Color.RED);
        for (Vesicle vesicle : fusion.getTetheredVesicles().keySet()) {
            Circle circle = vesicle.getCircleRepresentation();
            fillCircle(circle);
            strokeCircle(circle);
            strokeTextCenteredOnPoint(vesicle.getStringIdentifier(), circle.getMidpoint().add(Vector2D.UNIT_VECTOR_RIGHT));
        }
        // draw filaments
        getGraphicsContext().setStroke(Color.BLACK);
        getGraphicsContext().setLineWidth(3);
        for (SkeletalFilament skeletalFilament : filamentLayer.getFilaments()) {
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
            getGraphicsContext().setStroke(Color.GRAY);
            getGraphicsContext().setLineWidth(1);
            for (AutomatonNode node : skeletalFilament.getAssociatedNodes()) {
                strokeStraight(node.getPosition(), skeletalFilament.getHead());
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
