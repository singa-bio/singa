package bio.singa.simulation.model.modules.concentration.imlementations;

import bio.singa.chemistry.entities.Protein;
import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.chemistry.features.diffusivity.Diffusivity;
import bio.singa.features.model.Evidence;
import bio.singa.features.parameters.Environment;
import bio.singa.features.units.UnitRegistry;
import bio.singa.mathematics.graphs.model.Graphs;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.simulation.Simulation;
import bio.singa.structure.features.molarmass.MolarMass;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tec.uom.se.quantity.Quantities;

import static bio.singa.chemistry.features.diffusivity.Diffusivity.SQUARE_CENTIMETRE_PER_SECOND;
import static bio.singa.features.units.UnitProvider.MOLE_PER_LITRE;
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
    private static final SmallMolecule ammonia = new SmallMolecule.Builder("ammonia")
            .name("ammonia")
            .assignFeature(new Diffusivity(Quantities.getQuantity(2.28E-05, SQUARE_CENTIMETRE_PER_SECOND), Evidence.NO_EVIDENCE))
            .build();

    // anchored protein
    private static final Protein anchoredProtein = new Protein.Builder("AP")
            .name("anchored protein")
            .assignFeature(new MolarMass(1000, Evidence.NO_EVIDENCE))
            .setMembraneAnchored(true)
            .build();

    // unanchored protein
    private static final Protein globularProtein = new Protein.Builder("GP")
            .name("globular protein")
            .assignFeature(new MolarMass(1000, Evidence.NO_EVIDENCE))
            .setMembraneAnchored(false)
            .build();

    private static Simulation setupAnchorSimulation() {
        // create simulation
        Simulation simulation = new Simulation();
        // create graph
        AutomatonGraph graph = AutomatonGraphs.createRectangularAutomatonGraph(1, 2);
        simulation.setGraph(graph);
        // distribute nodes to sections
        graph.getNodesOfRow(0).forEach(node -> node.setCellRegion(CellRegion.MEMBRANE));
        graph.getNodesOfRow(1).forEach(node -> node.setCellRegion(CellRegion.CYTOSOL_A));
        // add diffusion
        Diffusion.inSimulation(simulation)
                .forAll(anchoredProtein, globularProtein)
                .build();
        return simulation;
    }

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

    @Test
    void shouldAnchorInMembrane() {
        // create simulation
        Simulation simulation = setupAnchorSimulation();
        // add some protein in cytoplasm
        AutomatonNode first = simulation.getGraph().getNode(0, 0);
        AutomatonNode second = simulation.getGraph().getNode(0, 1);
        // set concentrations
        first.getConcentrationContainer().initialize(SECTION_A, anchoredProtein, Quantities.getQuantity(0.1,MOLE_PER_LITRE));
        first.getConcentrationContainer().initialize(SECTION_A, globularProtein, Quantities.getQuantity(0.1, MOLE_PER_LITRE));

        // observe
        for (int i = 0; i < 10; i++) {
            simulation.nextEpoch();
            // concentration of anchored entity should stay zero in non-membrane node
            double betaGammaConcentration = second.getConcentrationContainer().get(SECTION_A, anchoredProtein);
            assertEquals(0.0, betaGammaConcentration);
            // concentration of globular entity should increase in non-membrane node
            double betaConcentration = second.getConcentrationContainer().get(SECTION_A, globularProtein);
            assertTrue(betaConcentration > 0.0);
            // concentration of anchored entity should stay equal in non-membrane node
            double remainingConcentration = first.getConcentrationContainer().get(SECTION_A, anchoredProtein);
            assertEquals(0.1, UnitRegistry.concentration(remainingConcentration).to(MOLE_PER_LITRE).getValue().doubleValue());
        }

    }

    @Test
    void shouldAbsorbFromCytoplasm() {
        Simulation simulation = setupAnchorSimulation();
        // add some protein in cytoplasm
        AutomatonNode first = simulation.getGraph().getNode(0, 0);
        AutomatonNode second = simulation.getGraph().getNode(0, 1);
        // anchored
        second.getConcentrationContainer().set(SECTION_A, anchoredProtein, 0.1);
        // observe over 10 epochs
        for (int i = 0; i < 100; i++) {
            simulation.nextEpoch();
            // concentration of anchored entity should increase in membrane node
            double availableConcentration = first.getConcentrationContainer().get(SECTION_A, anchoredProtein);
            assertTrue(availableConcentration > 0.0);
            // and decrease in the other node
            double remainingConcentration = second.getConcentrationContainer().get(SECTION_A, anchoredProtein);
            assertTrue(remainingConcentration < 0.1);
        }
    }

    @Test
    void shouldReceiveFromNeighbourMembrane() {
        // simulation
        Simulation simulation = new Simulation();
        // create graph
        AutomatonGraph graph = AutomatonGraphs.createRectangularAutomatonGraph(2, 1);
        simulation.setGraph(graph);
        // distribute nodes to sections
        graph.getNodesOfRow(0).forEach(node -> node.setCellRegion(CellRegion.MEMBRANE));
        // graph.getNodesOfRow(1).forEach(node -> node.setCellSection(innerSection));
        // add diffusion
        Diffusion.inSimulation(simulation)
                .forAll(anchoredProtein, globularProtein)
                .build();

        // add some protein in cytoplasm
        AutomatonNode first = simulation.getGraph().getNode(0, 0);
        AutomatonNode second = simulation.getGraph().getNode(1, 0);
        // bound
        first.getConcentrationContainer().set(SECTION_A, anchoredProtein, 1.0);
        // observe over 10 epochs
        for (int i = 0; i < 100; i++) {
            simulation.nextEpoch();
            // concentration of bound chemical entity should increase in neighboring membrane node
            double availableConcentration = second.getConcentrationContainer().get(SECTION_A, anchoredProtein);
            assertTrue(availableConcentration > 0.0);
        }
    }


}
