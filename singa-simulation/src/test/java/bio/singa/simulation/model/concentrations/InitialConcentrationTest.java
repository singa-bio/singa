package bio.singa.simulation.model.concentrations;

import bio.singa.features.parameters.Environment;
import bio.singa.features.units.UnitRegistry;
import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.entities.ChemicalEntity;
import bio.singa.simulation.entities.SimpleEntity;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.agents.pointlike.VesicleLayer;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.simulation.Simulation;
import bio.singa.simulation.model.simulation.error.TimeStepManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.units.indriya.quantity.Quantities;

import static bio.singa.features.units.UnitProvider.NANO_MOLE_PER_LITRE;
import static bio.singa.simulation.model.concentrations.TimedCondition.Relation.GREATER;
import static bio.singa.simulation.model.sections.CellRegions.CELL_INNER_MEMBRANE_REGION;
import static bio.singa.simulation.model.sections.CellRegions.CELL_OUTER_MEMBRANE_REGION;
import static bio.singa.simulation.model.sections.CellSubsections.*;
import static bio.singa.simulation.model.sections.CellTopology.INNER;
import static bio.singa.simulation.model.sections.CellTopology.OUTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.units.indriya.unit.MetricPrefix.NANO;
import static tech.units.indriya.unit.Units.METRE;
import static tech.units.indriya.unit.Units.SECOND;

/**
 * @author cl
 */
class InitialConcentrationTest {

    private static ChemicalEntity entity;

    @BeforeAll
    static void initialize() {
        UnitRegistry.reinitialize();
        Environment.reset();
        // entity
        entity = SimpleEntity.create("entity").build();
    }

    @AfterEach
    void cleanUp() {
        UnitRegistry.reinitialize();
        Environment.reset();
    }

    @Test
    @DisplayName("concentration initialization - topology")
    void testSimpleInitialization1() {
        // setup test simulation
        Simulation simulation = new Simulation();
        // graph
        AutomatonGraph graph = AutomatonGraphs.singularGraph();
        AutomatonNode node = graph.getNode(0, 0);
        node.setCellRegion(CELL_OUTER_MEMBRANE_REGION);
        simulation.setGraph(graph);

        ConcentrationBuilder.create(simulation)
                .entity(entity)
                .topology(INNER)
                .concentrationValue(10)
                .nanoMolar()
                .build();

        simulation.nextEpoch();

        assertEquals(10.0, UnitRegistry.concentration(simulation.getGraph().getNode(0, 0).getConcentrationContainer().get(INNER, entity)).to(NANO_MOLE_PER_LITRE).getValue().doubleValue());
    }

    @Test
    @DisplayName("concentration initialization - subsection")
    void testSimpleInitialization2() {
        // setup test simulation
        Simulation simulation = new Simulation();
        // graph
        AutomatonGraph graph = AutomatonGraphs.singularGraph();
        AutomatonNode node = graph.getNode(0, 0);
        node.setCellRegion(CELL_INNER_MEMBRANE_REGION);
        simulation.setGraph(graph);

        ConcentrationBuilder.create(simulation)
                .entity(entity)
                .subsection(CYTOPLASM)
                .concentrationValue(10)
                .nanoMolar()
                .build();

        simulation.nextEpoch();

        assertEquals(10.0, UnitRegistry.concentration(simulation.getGraph().getNode(0, 0).getConcentrationContainer().get(INNER, entity)).to(NANO_MOLE_PER_LITRE).getValue().doubleValue());
    }

    @Test
    @DisplayName("concentration initialization - vesicle")
    void testSimpleInitialization3() {
        // setup test simulation
        Simulation simulation = new Simulation();
        // graph
        AutomatonGraph graph = AutomatonGraphs.singularGraph();
        simulation.setGraph(graph);

        VesicleLayer vesicleLayer = new VesicleLayer(simulation);
        Vesicle vesicle = new Vesicle(new Vector2D(0,0), Quantities.getQuantity(50.0, NANO(METRE)));
        vesicleLayer.addVesicle(vesicle);
        simulation.setVesicleLayer(vesicleLayer);

        ConcentrationBuilder.create(simulation)
                .entity(entity)
                .subsection(VESICLE_LUMEN)
                .concentrationValue(10)
                .nanoMolar()
                .build();

        simulation.nextEpoch();

        assertEquals(10.0, UnitRegistry.concentration(simulation.getVesicleLayer().getVesicles().iterator().next().getConcentrationContainer().get(OUTER, entity)).to(NANO_MOLE_PER_LITRE).getValue().doubleValue());
    }

    @Test
    @DisplayName("concentration initialization - area")
    void testAreaInitialization() {
        // setup test simulation
        Simulation simulation = new Simulation();
        Environment.setSimulationExtend(200);
        // graph
        AutomatonGraph graph = AutomatonGraphs.createRectangularAutomatonGraph(2, 1);
        simulation.setGraph(graph);

        ConcentrationBuilder.create(simulation)
                .entity(entity)
                .subsection(EXTRACELLULAR_REGION)
                .concentrationValue(10)
                .nanoMolar()
                .inArea(new Rectangle(100, 200))
                .build();

        simulation.nextEpoch();

        assertEquals(10.0, UnitRegistry.concentration(simulation.getGraph().getNode(0, 0).getConcentrationContainer().get(INNER, entity)).to(NANO_MOLE_PER_LITRE).getValue().doubleValue());
        assertEquals(0.0, UnitRegistry.concentration(simulation.getGraph().getNode(1, 0).getConcentrationContainer().get(INNER, entity)).to(NANO_MOLE_PER_LITRE).getValue().doubleValue());
    }

    @Test
    @DisplayName("concentration initialization - fixed")
    void testFixedInitialization() {
        // setup test simulation
        Simulation simulation = new Simulation();
        // graph
        AutomatonGraph graph = AutomatonGraphs.singularGraph();
        AutomatonNode node = graph.getNode(0, 0);
        node.setCellRegion(CELL_INNER_MEMBRANE_REGION);
        simulation.setGraph(graph);

        ConcentrationBuilder.create(simulation)
                .entity(entity)
                .subsection(CYTOPLASM)
                .concentrationValue(10)
                .nanoMolar()
                .fixed()
                .build();

        simulation.nextEpoch();

        assertEquals(10.0, UnitRegistry.concentration(simulation.getGraph().getNode(0, 0).getConcentrationContainer().get(INNER, entity)).to(NANO_MOLE_PER_LITRE).getValue().doubleValue());
        assertTrue(simulation.getGraph().getNode(0, 0).getConcentrationManager().getFixedEntities().contains(entity));
    }

    @Test
    @DisplayName("concentration initialization - timed")
    void testTimedInitialization() {
        // setup test simulation
        Simulation simulation = new Simulation();
        // graph
        AutomatonGraph graph = AutomatonGraphs.singularGraph();
        AutomatonNode node = graph.getNode(0, 0);
        node.setCellRegion(CELL_INNER_MEMBRANE_REGION);
        simulation.setGraph(graph);

        ConcentrationBuilder.create(simulation)
                .entity(entity)
                .subsection(CYTOPLASM)
                .concentrationValue(10)
                .nanoMolar()
                .timed(GREATER, 10)
                .seconds()
                .build();

        assertEquals(0.0, UnitRegistry.concentration(simulation.getGraph().getNode(0, 0).getConcentrationContainer().get(INNER, entity)).to(NANO_MOLE_PER_LITRE).getValue().doubleValue());

        while (TimeStepManager.getElapsedTime().isLessThanOrEqualTo(Quantities.getQuantity(11, SECOND))) {
            simulation.nextEpoch();
        }

        assertEquals(10.0, UnitRegistry.concentration(simulation.getGraph().getNode(0, 0).getConcentrationContainer().get(INNER, entity)).to(NANO_MOLE_PER_LITRE).getValue().doubleValue());
    }

}