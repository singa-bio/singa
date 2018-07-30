package bio.singa.simulation.model.modules.macroscopic;

import bio.singa.chemistry.annotations.Annotation;
import bio.singa.chemistry.annotations.AnnotationType;
import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.ComplexedChemicalEntity;
import bio.singa.chemistry.entities.Protein;
import bio.singa.features.identifiers.UniProtIdentifier;
import bio.singa.features.parameters.Environment;
import bio.singa.javafx.renderer.Renderer;
import bio.singa.mathematics.geometry.edges.SimpleLineSegment;
import bio.singa.mathematics.geometry.faces.Circle;
import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.geometry.model.Polygon;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.features.endocytosis.*;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.concentration.imlementations.NthOrderReaction;
import bio.singa.simulation.model.modules.displacement.Vesicle;
import bio.singa.simulation.model.modules.displacement.implementations.*;
import bio.singa.simulation.model.modules.macroscopic.filaments.FilamentLayer;
import bio.singa.simulation.model.modules.macroscopic.filaments.SkeletalFilament;
import bio.singa.simulation.model.modules.macroscopic.membranes.Membrane;
import bio.singa.simulation.model.modules.macroscopic.membranes.MembraneLayer;
import bio.singa.simulation.model.modules.macroscopic.membranes.MembraneSegment;
import bio.singa.simulation.model.modules.macroscopic.membranes.MembraneTracer;
import bio.singa.simulation.model.modules.macroscopic.organelles.MicrotubuleOrganizingCentre;
import bio.singa.simulation.model.modules.macroscopic.organelles.Organelle;
import bio.singa.simulation.model.modules.macroscopic.organelles.OrganelleTypes;
import bio.singa.simulation.model.sections.CellRegions;
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
import tec.uom.se.unit.ProductUnit;

import javax.measure.quantity.Area;
import javax.measure.quantity.Length;
import java.util.Iterator;
import java.util.List;

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
    private MembraneLayer membraneLayer;
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
                .rateConstant(EndocytosisActinBoost.DEFAULT_CLATHRIN_DEPOLYMERIZATION_RATE)
                .addSubstrate(clathrinTriskelion)
                .build();

        // setup endocytosis budding
        budding = new ClathrinMediatedEndocytosis();
        budding.setSimulation(simulation);
        budding.addMembraneCargo(Quantities.getQuantity(31415.93, new ProductUnit<Area>(NANO(METRE).pow(2))), 60.0, clathrinTriskelion);
        budding.addMembraneCargo(Quantities.getQuantity(10000, new ProductUnit<Area>(NANO(METRE).pow(2))), 10, vamp3);
        budding.setFeature(BuddingRate.DEFAULT_BUDDING_RATE);
        budding.setFeature(VesicleRadius.DEFAULT_VESICLE_RADIUS);
        budding.setFeature(MaturationTime.DEFAULT_MATURATION_TIME);
        simulation.getModules().add(budding);

        // setup vesicle diffusion
        VesicleDiffusion diffusion = new VesicleDiffusion();
        diffusion.useLiteratureDiffusivity();
        diffusion.setSimulation(simulation);
        simulation.getModules().add(diffusion);

        // setup actin boost
        EndocytosisActinBoost boost = new EndocytosisActinBoost();
        boost.setDecayingEntity(clathrinTriskelion);
        boost.setFeature(ActinBoostVelocity.DEFAULT_ACTIN_VELOCITY);
        boost.setSimulation(simulation);
        simulation.getModules().add(boost);

        // setup attachment
        VesicleAttachment attachment = new VesicleAttachment();
        attachment.setFeature(AttachmentDistance.DEFAULT_DYNEIN_ATTACHMENT_DISTANCE);
        attachment.setSimulation(simulation);
        simulation.getModules().add(attachment);

        // setup transport
        VesicleTransport transport = new VesicleTransport();
        transport.setFeature(MotorMovementVelocity.DEFAULT_MOTOR_VELOCITY);
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
        fusion.setFeature(TetheringTime.DEFAULT_TETHERING_TIME);
        fusion.setFeature(AttachmentDistance.DEFAULT_DYNEIN_ATTACHMENT_DISTANCE);
        fusion.setSimulation(simulation);
        simulation.getModules().add(fusion);

        // setup graph and assign regions
        graph = AutomatonGraphs.createRectangularAutomatonGraph(nodesHorizontal, nodesVertical);
        simulation.setGraph(graph);

        // setup spatial representations
        simulation.initializeSpatialRepresentations();
        // setup membrane layer
        membraneLayer = new MembraneLayer();
        simulation.setMembraneLayer(membraneLayer);

        // initialize extracellular space as default
        for (AutomatonNode automatonNode : graph.getNodes()) {
            automatonNode.setCellRegion(CellRegions.EXTRACELLULAR_REGION);
        }

        // initialize cell membrane and nucleus
        Organelle cell = OrganelleTypes.CELL.create();
        Organelle nucleus = OrganelleTypes.NUCLEUS.create();
        Membrane cellMembrane = MembraneTracer.membraneToRegion(cell, graph, rectangle);
        membraneLayer.addMembrane(cellMembrane);
        Membrane nuclearMembrane = MembraneTracer.membraneToRegion(nucleus, graph, rectangle);
        membraneLayer.addMembrane(nuclearMembrane);

        // initialize
        Organelle endosome = OrganelleTypes.EARLY_ENDOSOME.create();
        endosome.getPolygon().move(new Vector2D(200, 300));
        Membrane endosomeMembrane = MembraneTracer.membraneToRegion(endosome, graph, rectangle);
        membraneLayer.addMembrane(endosomeMembrane);

        // TODO consider area of membranes in membrane diffusion

        // add left membrane to as endocytosis site
        List<Membrane> membranes = membraneLayer.getMembranes();
        for (Membrane membrane : membranes) {
            if (membrane.getIdentifier().equals("cell outer membrane")) {
                for (MembraneSegment membraneSegment : membrane.getSegments()) {
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
        // background is extracellular
        getGraphicsContext().setFill(CellSubsections.getColor(CellSubsections.EXTRACELLULAR_REGION));
        fillPolygon(rectangle);
        // draw cells and polygons for subsections
        getGraphicsContext().setStroke(Color.BLACK);
        getGraphicsContext().setLineWidth(1);
        for (AutomatonNode node : graph.getNodes()) {
            Polygon nodePolygon = node.getSpatialRepresentation();
            if (node.getCellRegion().hasMembrane()) {
                getGraphicsContext().setFill(CellSubsections.getColor(node.getCellRegion().getOuterSubsection()));
                fillPolygon(nodePolygon);
                getGraphicsContext().setFill(CellSubsections.getColor(node.getCellRegion().getInnerSubsection()));
                Polygon organellePolygon = node.getSubsectionRepresentations().get(node.getCellRegion().getInnerSubsection());
                fillPolygon(organellePolygon);
            } else {
                getGraphicsContext().setFill(CellSubsections.getColor(node.getCellRegion().getInnerSubsection()));
                fillPolygon(nodePolygon);
            }
        }
        // draw membrane
        if (membraneLayer != null) {
            for (Membrane membrane : membraneLayer.getMembranes()) {
                List<MembraneSegment> segments = membrane.getSegments();
                for (MembraneSegment segment : segments) {
                    getGraphicsContext().setLineWidth(1);
                    strokeLineSegment(segment);
                    // strokeCircle(segment.getStartingPoint(), 2);
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
        if (filamentLayer != null) {
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
//            getGraphicsContext().setStroke(Color.GRAY);
//            getGraphicsContext().setLineWidth(1);
//            for (AutomatonNode node : skeletalFilament.getAssociatedNodes()) {
//                strokeStraight(node.getPosition(), skeletalFilament.getHead());
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
