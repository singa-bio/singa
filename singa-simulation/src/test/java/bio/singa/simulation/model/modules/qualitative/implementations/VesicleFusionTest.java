package bio.singa.simulation.model.modules.qualitative.implementations;

import bio.singa.features.identifiers.UniProtIdentifier;
import bio.singa.features.parameters.Environment;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.features.units.UnitRegistry;
import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.entities.ChemicalEntity;
import bio.singa.simulation.entities.ComplexEntity;
import bio.singa.simulation.entities.SimpleEntity;
import bio.singa.simulation.features.*;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.agents.pointlike.VesicleLayer;
import bio.singa.simulation.model.agents.pointlike.VesicleStateRegistry;
import bio.singa.simulation.model.agents.surfacelike.Membrane;
import bio.singa.simulation.model.agents.surfacelike.MembraneLayer;
import bio.singa.simulation.model.agents.surfacelike.MembraneTracer;
import bio.singa.simulation.model.concentrations.ConcentrationBuilder;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.sections.CellRegions;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.simulation.Simulation;
import bio.singa.simulation.model.simulation.error.TimeStepManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

import javax.measure.quantity.Length;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tech.units.indriya.unit.MetricPrefix.MICRO;
import static tech.units.indriya.unit.MetricPrefix.NANO;
import static tech.units.indriya.unit.Units.METRE;
import static tech.units.indriya.unit.Units.SECOND;

/**
 * @author cl
 */
class VesicleFusionTest {

    @BeforeAll
    static void initialize() {
        UnitRegistry.reinitialize();
        Environment.reset();
    }

    @AfterEach
    void cleanUp() {
        UnitRegistry.reinitialize();
        Environment.reset();
    }

    @Test
    @DisplayName("fusion - with snares")
    void testModuleWithSnares() {

        // setup simulation
        Simulation simulation = new Simulation();
        final double simulationExtend = 100;
        Rectangle rectangle = new Rectangle(simulationExtend, simulationExtend);
        simulation.setSimulationRegion(rectangle);
        ComparableQuantity<Length> systemExtend = Quantities.getQuantity(2, MICRO(METRE));
        Environment.setSystemExtend(systemExtend);
        Environment.setSimulationExtend(simulationExtend);
        UnitRegistry.setTime(Quantities.getQuantity(1, MICRO(SECOND)));
        simulation.setMaximalTimeStep(Quantities.getQuantity(0.5, SECOND));

        // setup snares for fusion
        ChemicalEntity vamp2 = SimpleEntity.create("VAMP2")
                .assignFeature(new UniProtIdentifier("Q15836"))
                .build();

        ChemicalEntity vamp3 = SimpleEntity.create("VAMP3")
                .assignFeature(new UniProtIdentifier("P63027"))
                .build();

        ChemicalEntity syntaxin3 = SimpleEntity.create("Syntaxin 3")
                .assignFeature(new UniProtIdentifier("Q13277"))
                .build();

        ChemicalEntity syntaxin4 = SimpleEntity.create("Syntaxin 4")
                .assignFeature(new UniProtIdentifier("Q12846"))
                .build();

        ChemicalEntity snap23 = SimpleEntity.create("SNAP23")
                .assignFeature(new UniProtIdentifier("O00161"))
                .build();

        ComplexEntity snareComplex1 = ComplexEntity.from(syntaxin3, snap23);
        ComplexEntity snareComplex2 = ComplexEntity.from(syntaxin4, snap23);

        // setup graph
        AutomatonGraph graph = AutomatonGraphs.singularGraph();
        AutomatonNode node = graph.getNode(0, 0);
        node.setPosition(new Vector2D(50.0, 50.0));
        node.setCellRegion(CellRegions.CELL_OUTER_MEMBRANE_REGION);
        node.getConcentrationContainer().set(CellTopology.MEMBRANE, snareComplex1, MolarConcentration.moleculesToConcentration(10));
        simulation.setGraph(graph);

        // setup membrane
        List<Membrane> membranes = MembraneTracer.regionsToMembrane(graph);
        MembraneLayer membraneLayer = new MembraneLayer();
        membraneLayer.addMembranes(membranes);
        simulation.setMembraneLayer(membraneLayer);

        // setup vesicle
        VesicleLayer vesicleLayer = new VesicleLayer(simulation);
        Vesicle vesicle = new Vesicle(new Vector2D(49.0, 49.0), Quantities.getQuantity(100.0, NANO(METRE)));
        vesicle.getConcentrationContainer().set(CellTopology.MEMBRANE, vamp3, MolarConcentration.moleculesToConcentration(10));
        vesicleLayer.addVesicle(vesicle);
        simulation.setVesicleLayer(vesicleLayer);

        // setup fusion module
        VesicleFusion fusion = new VesicleFusion();
        fusion.setFeature(MatchingQSnares.of(snareComplex1, snareComplex2).build());
        fusion.setFeature(MatchingRSnares.of(vamp2, vamp3).build());
        fusion.setFeature(SNAREFusionPairs.of(3).build());
        fusion.setFeature(FusionTime.of(18.0, SECOND).build());
        fusion.setFeature(AttachmentDistance.of(61, NANO(METRE)).build());
        simulation.addModule(fusion);

        while (TimeStepManager.getElapsedTime().isLessThanOrEqualTo(Quantities.getQuantity(20.0, SECOND))) {
            simulation.nextEpoch();
        }

        assertEquals(7.0, MolarConcentration.concentrationToMolecules(node.getConcentrationContainer().get(CellTopology.MEMBRANE, vamp3)).getValue().doubleValue(), 1e-10);
        assertEquals(7.0, MolarConcentration.concentrationToMolecules(node.getConcentrationContainer().get(CellTopology.MEMBRANE, snareComplex1)).getValue().doubleValue(), 1e-10);

    }

    @Test
    @DisplayName("fusion - without snares")
    void testModuleWithoutSnares() {

        // setup simulation
        Simulation simulation = new Simulation();
        final double simulationExtend = 100;
        Rectangle rectangle = new Rectangle(simulationExtend, simulationExtend);
        simulation.setSimulationRegion(rectangle);
        ComparableQuantity<Length> systemExtend = Quantities.getQuantity(2, MICRO(METRE));
        Environment.setSystemExtend(systemExtend);
        Environment.setSimulationExtend(simulationExtend);
        UnitRegistry.setTime(Quantities.getQuantity(1, MICRO(SECOND)));
        simulation.setMaximalTimeStep(Quantities.getQuantity(0.5, SECOND));

        // setup graph
        AutomatonGraph graph = AutomatonGraphs.singularGraph();
        AutomatonNode node = graph.getNode(0, 0);
        node.setPosition(new Vector2D(50.0, 50.0));
        node.setCellRegion(CellRegions.CELL_OUTER_MEMBRANE_REGION);
        simulation.setGraph(graph);

        // setup membrane
        List<Membrane> membranes = MembraneTracer.regionsToMembrane(graph);
        MembraneLayer membraneLayer = new MembraneLayer();
        membraneLayer.addMembranes(membranes);
        simulation.setMembraneLayer(membraneLayer);

        // setup vesicle
        VesicleLayer vesicleLayer = new VesicleLayer(simulation);
        Vesicle vesicle = new Vesicle(new Vector2D(49.0, 49.0), Quantities.getQuantity(100.0, NANO(METRE)));
        vesicle.setState(VesicleStateRegistry.MICROTUBULE_ATTACHED);
        vesicleLayer.addVesicle(vesicle);
        simulation.setVesicleLayer(vesicleLayer);

        ChemicalEntity testEntity = SimpleEntity.create("TEST")
                .membraneBound()
                .build();

        ConcentrationBuilder.create(simulation)
                .entity(testEntity)
                .subsection(vesicle.getRegion().getMembraneSubsection())
                .molecules(100)
                .build();

        // setup fusion module
        VesicleFusion fusion = new VesicleFusion();
        fusion.setFeature(new AppliedVesicleState(VesicleStateRegistry.MICROTUBULE_ATTACHED));
        fusion.setFeature(FusionTime.of(18, SECOND).build());
        fusion.setFeature(new AttachmentDistance(Quantities.getQuantity(61, NANO(METRE))));
        simulation.addModule(fusion);

        while (TimeStepManager.getElapsedTime().isLessThanOrEqualTo(Quantities.getQuantity(20.0, SECOND))) {
            simulation.nextEpoch();
        }

        assertEquals(100, MolarConcentration.concentrationToMolecules(node.getConcentrationContainer().get(CellTopology.MEMBRANE, testEntity)).getValue().intValue());

    }


}