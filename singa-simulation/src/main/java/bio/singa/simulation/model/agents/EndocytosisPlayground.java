package bio.singa.simulation.model.agents;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.ComplexedChemicalEntity;
import bio.singa.chemistry.entities.Protein;
import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.features.identifiers.ChEBIIdentifier;
import bio.singa.features.identifiers.UniProtIdentifier;
import bio.singa.features.model.QuantityFormatter;
import bio.singa.features.parameters.Environment;
import bio.singa.features.units.UnitRegistry;
import bio.singa.javafx.renderer.Renderer;
import bio.singa.mathematics.geometry.edges.SimpleLineSegment;
import bio.singa.mathematics.geometry.faces.Circle;
import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.geometry.model.Polygon;
import bio.singa.mathematics.topology.grids.rectangular.NeumannRectangularDirection;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.features.*;
import bio.singa.simulation.model.agents.linelike.LineLikeAgent;
import bio.singa.simulation.model.agents.linelike.LineLikeAgentLayer;
import bio.singa.simulation.model.agents.organelles.OrganelleImageParser;
import bio.singa.simulation.model.agents.organelles.OrganelleTemplate;
import bio.singa.simulation.model.agents.organelles.OrganelleTypes;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.agents.pointlike.VesicleStateRegistry;
import bio.singa.simulation.model.agents.surfacelike.Membrane;
import bio.singa.simulation.model.agents.surfacelike.MembraneFactory;
import bio.singa.simulation.model.agents.surfacelike.MembraneLayer;
import bio.singa.simulation.model.agents.surfacelike.MembraneSegment;
import bio.singa.simulation.model.agents.volumelike.ActinCortex;
import bio.singa.simulation.model.agents.volumelike.VolumeLayer;
import bio.singa.simulation.model.agents.volumelike.VolumeLikeAgentFactory;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.displacement.implementations.EndocytosisActinBoost;
import bio.singa.simulation.model.modules.displacement.implementations.VesicleConfinedDiffusion;
import bio.singa.simulation.model.modules.displacement.implementations.VesicleCytoplasmDiffusion;
import bio.singa.simulation.model.modules.qualitative.implementations.ActinCortexAttachment;
import bio.singa.simulation.model.modules.qualitative.implementations.ClathrinMediatedEndocytosis;
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
import tec.uom.se.unit.ProductUnit;

import javax.measure.quantity.Area;
import javax.measure.quantity.Length;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static bio.singa.features.model.Evidence.MANUALLY_ANNOTATED;
import static bio.singa.mathematics.topology.grids.rectangular.MooreRectangularDirection.EAST;
import static tec.uom.se.unit.MetricPrefix.MICRO;
import static tec.uom.se.unit.MetricPrefix.NANO;
import static tec.uom.se.unit.Units.METRE;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
public class EndocytosisPlayground extends Application implements Renderer {

    private Canvas canvas;
    private AutomatonGraph graph;
    private Simulation simulation;
    private ClathrinMediatedEndocytosis budding;

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

        simulation = new Simulation();
        simulation.setMaximalTimeStep(Quantities.getQuantity(10, SECOND));
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

        MembraneLayer membranes = new MembraneLayer();
        Map<Vector2D, CellRegion> inverseRegionMap = template.getInverseRegionMap();
        Membrane membrane = MembraneFactory.createLinearMembrane(template.getGroups().get(-65536), OrganelleTypes.CELL.getInnerRegion(), OrganelleTypes.CELL.getMembraneRegion(), NeumannRectangularDirection.EAST, graph, inverseRegionMap, rectangle);
        Membrane endosome1 = MembraneFactory.createClosedMembrane(template.getGroups().get(-16711936), OrganelleTypes.EARLY_ENDOSOME.getInnerRegion(), OrganelleTypes.EARLY_ENDOSOME.getMembraneRegion(), graph, inverseRegionMap);
        Membrane endosome2 = MembraneFactory.createClosedMembrane(template.getGroups().get(-16776968), OrganelleTypes.EARLY_ENDOSOME.getInnerRegion(), OrganelleTypes.EARLY_ENDOSOME.getMembraneRegion(), graph, inverseRegionMap);
        membranes.addMembrane(membrane);
        membranes.addMembrane(endosome1);
        membranes.addMembrane(endosome2);
        simulation.setMembraneLayer(membranes);

        LineLikeAgentLayer filaments = new LineLikeAgentLayer(simulation, membranes);
        for (int i = 0; i < 7; i++) {
            filaments.spawnFilament(endosome2, membrane);
            filaments.spawnFilament(endosome1, membrane);
        }
        simulation.setLineLayer(filaments);

        ActinCortex cortex = VolumeLikeAgentFactory.createActinCortex(membrane, EAST, Quantities.getQuantity(180, NANO(METRE)), Quantities.getQuantity(20, NANO(METRE)));
        VolumeLayer volumeLayer = new VolumeLayer();
        volumeLayer.setCortex(cortex);
        simulation.setVolumeLayer(volumeLayer);

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

        // setup clathrin decay reaction
//        NthOrderReaction.inSimulation(simulation)
//                .rateConstant(DEFAULT_CLATHRIN_DEPOLYMERIZATION_RATE)
//                .addSubstrate(clathrinTriskelion)
//                .build();

        // setup endocytosis budding
        budding = new ClathrinMediatedEndocytosis();
        budding.setSimulation(simulation);
        budding.addMembraneCargo(Quantities.getQuantity(31415.93, new ProductUnit<Area>(NANO(METRE).pow(2))), 60.0, clathrinTriskelion);
        budding.setFeature(PitFormationRate.DEFAULT_BUDDING_RATE);
        budding.setFeature(VesicleRadius.DEFAULT_VESICLE_RADIUS);
        budding.setFeature(MaturationTime.DEFAULT_MATURATION_TIME);
        simulation.getModules().add(budding);

        // setup actin boost
        EndocytosisActinBoost boost = new EndocytosisActinBoost();
        boost.setFeature(new DecayingEntity(clathrinTriskelion, MANUALLY_ANNOTATED));
        boost.setFeature(ActinBoostVelocity.DEFAULT_ACTIN_VELOCITY);
        boost.setSimulation(simulation);
        simulation.getModules().add(boost);

//        for (Membrane current : simulation.getMembraneLayer().getMembranes()) {
//            if (current.getIdentifier().equals("cell outer membrane")) {
//                for (MembraneSegment membraneSegment : current.getSegments()) {
//                    if (membraneSegment.getNode().getIdentifier().getRow() >= 3 && membraneSegment.getNode().getIdentifier().getRow() <= 12) {
//                        budding.addMembraneSegment(membraneSegment);
//                    }
//                }
//            }
//        }

        ActinCortexAttachment cortexAttachment = new ActinCortexAttachment();
        cortexAttachment.setSimulation(simulation);
        simulation.getModules().add(cortexAttachment);

        // setup vesicle diffusion
        VesicleCytoplasmDiffusion diffusion = new VesicleCytoplasmDiffusion();
        diffusion.setSimulation(simulation);
        simulation.getModules().add(diffusion);

        // setup vesicle diffusion
        VesicleConfinedDiffusion confinedDiffusion = new VesicleConfinedDiffusion(VesicleStateRegistry.ACTIN_ATTACHED, cortex);
        confinedDiffusion.setSimulation(simulation);
        simulation.getModules().add(confinedDiffusion);

        while (filaments.hasGrowingFilaments()) {
            filaments.nextEpoch();
        }

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

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                simulation.nextEpoch();
                System.out.println(QuantityFormatter.formatTime(simulation.getElapsedTime()) + "(+ " + QuantityFormatter.formatTime(UnitRegistry.getTime())+")");
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
        MembraneLayer membraneLayer = simulation.getMembraneLayer();
        if (membraneLayer != null) {
            for (Membrane membrane : membraneLayer.getMembranes()) {
                List<MembraneSegment> segments = membrane.getSegments();
                for (MembraneSegment segment : segments) {
                    getGraphicsContext().setLineWidth(3);
                    strokeLineSegment(segment);
                }
            }
        }

        // draw cortex
        getGraphicsContext().setFill(Color.MEDIUMSEAGREEN);
        fillPolygon(simulation.getVolumeLayer().getCortex().getArea());

        // draw filaments
        getGraphicsContext().setStroke(Color.BLACK);
        LineLikeAgentLayer lineLayer = simulation.getLineLayer();
        if (lineLayer != null) {
            for (LineLikeAgent skeletalFilament : lineLayer.getFilaments()) {
                if (skeletalFilament.getPath().size() > 1) {
                    Vector2D previous = skeletalFilament.getPath().getTail();
                    Iterator<Vector2D> vector2DIterator = skeletalFilament.getPath().getSegments().descendingIterator();
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
        // draw budding vesicles
        getGraphicsContext().setLineWidth(1);
        getGraphicsContext().setFill(Color.WHITE);
        for (ClathrinMediatedEndocytosis.Pit event : budding.getAspiringPits()) {
            fillPoint(event.getSpawnSite(), 5);
        }
        // draw moving vesicles
        getGraphicsContext().setFill(Color.WHITE);
        for (Vesicle vesicle : simulation.getVesicleLayer().getVesicles()) {
            Circle circle = vesicle.getCircleRepresentation();
            fillCircle(circle);
            strokeCircle(circle);
            for (AutomatonNode node : vesicle.getAssociatedNodes().keySet()) {
                strokeStraight(node.getPosition(), circle.getMidpoint());
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
