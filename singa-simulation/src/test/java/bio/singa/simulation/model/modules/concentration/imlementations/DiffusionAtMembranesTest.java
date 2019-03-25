package bio.singa.simulation.model.modules.concentration.imlementations;

import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.chemistry.features.diffusivity.Diffusivity;
import bio.singa.features.model.Evidence;
import bio.singa.features.parameters.Environment;
import bio.singa.features.units.UnitRegistry;
import bio.singa.mathematics.graphs.model.Graphs;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.concentration.imlementations.transport.Diffusion;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.simulation.Simulation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tec.uom.se.quantity.Quantities;

import static bio.singa.chemistry.features.diffusivity.Diffusivity.SQUARE_CENTIMETRE_PER_SECOND;
import static bio.singa.simulation.model.sections.CellSubsection.SECTION_A;
import static bio.singa.simulation.model.sections.CellSubsection.SECTION_B;
import static bio.singa.simulation.model.sections.CellTopology.INNER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tec.uom.se.unit.MetricPrefix.NANO;
import static tec.uom.se.unit.Units.METRE;

/**
 * @author cl
 */
class DiffusionAtMembranesTest {

    // ammonia
    private static final SmallMolecule ammonia = SmallMolecule.create("ammonia")
            .assignFeature(new Diffusivity(Quantities.getQuantity(2.28E-05, SQUARE_CENTIMETRE_PER_SECOND), Evidence.NO_EVIDENCE))
            .build();

    @BeforeAll
    static void initialize() {
        UnitRegistry.reinitialize();
    }

    @AfterEach
    void cleanUp() {
        UnitRegistry.reinitialize();
    }

    @Test
    void shouldSimulateBlockedDiffusion() {
        // create simulation
        Simulation simulation = new Simulation();
        // 0-0-0
        AutomatonGraph automatonGraph = AutomatonGraphs.useStructureFrom(Graphs.buildGridGraph(3, 1));
        simulation.setGraph(automatonGraph);

        // left contains ammonia
        AutomatonNode leftNode = automatonGraph.getNode(0, 0);
        leftNode.setCellRegion(CellRegion.CYTOSOL_A);
        leftNode.getConcentrationContainer().set(INNER, ammonia, 1.0);
        // middle is blocking via membrane
        AutomatonNode membraneNode = automatonGraph.getNode(1, 0);
        membraneNode.setCellRegion(CellRegion.MEMBRANE);
        // right is empty
        AutomatonNode rightNode = automatonGraph.getNode(2, 0);
        rightNode.setCellRegion(CellRegion.CYTOSOL_B);

        // setup diffusion
        Diffusion.inSimulation(simulation)
                .onlyFor(ammonia)
                .build();
        // simulate some epochs
        for (int i = 0; i < 100; i++) {
            simulation.nextEpoch();
        }

        // left part of the central node should fill with ammonia
        assertTrue(leftNode.getConcentrationContainer().get(SECTION_A, ammonia) > 0.0);
        assertTrue(membraneNode.getConcentrationContainer().get(SECTION_A, ammonia) > 0.0);
        // right part of the central node and right node should not
        assertEquals(0.0, membraneNode.getConcentrationContainer().get(CellSubsection.MEMBRANE, ammonia));
        assertEquals(0.0, membraneNode.getConcentrationContainer().get(SECTION_B, ammonia));
        assertEquals(0.0, rightNode.getConcentrationContainer().get(SECTION_B, ammonia));
    }

    @Test
    void shouldSimulateLargeScaleBlockedDiffusion() {
        // create simulation
        Simulation simulation = new Simulation();
        // set node distance to diameter
        Environment.setNodeSpacingToDiameter(Quantities.getQuantity(2500.0, NANO(METRE)), 11);
        // create grid graph 11x11
        AutomatonGraph graph = AutomatonGraphs.createRectangularAutomatonGraph(11, 11);
        // split with membrane
        AutomatonGraphs.splitRectangularGraphWithMembrane(graph, SECTION_A, SECTION_B, false);
        // set graph
        simulation.setGraph(graph);

        // set concentrations
        // only 5 right most columns
        for (AutomatonNode node : graph.getNodes()) {
            if (node.getIdentifier().getColumn() >= (graph.getNumberOfColumns() / 2)) {
                node.getConcentrationContainer().set(SECTION_A, ammonia, 1.0);
            }
        }

        Diffusion.inSimulation(simulation)
                .onlyFor(ammonia)
                .build();

        for (int i = 0; i < 10; i++) {
            simulation.nextEpoch();
        }

        for (AutomatonNode node : graph.getNodes()) {
            if (node.getIdentifier().getColumn() < (graph.getNumberOfColumns() / 2)) {
                // nothing should permeate to the lower part
                assertEquals(0.0, node.getConcentrationContainer().get(SECTION_B, ammonia));
            }
        }

    }

}
