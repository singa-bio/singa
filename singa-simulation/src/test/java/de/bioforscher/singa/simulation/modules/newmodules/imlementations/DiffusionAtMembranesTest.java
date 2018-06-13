package de.bioforscher.singa.simulation.modules.newmodules.imlementations;

import de.bioforscher.singa.chemistry.descriptive.entities.Protein;
import de.bioforscher.singa.chemistry.descriptive.entities.SmallMolecule;
import de.bioforscher.singa.chemistry.descriptive.features.diffusivity.Diffusivity;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.mathematics.graphs.model.Graphs;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraphs;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import de.bioforscher.singa.simulation.model.newsections.CellRegion;
import de.bioforscher.singa.simulation.model.newsections.CellSubsection;
import de.bioforscher.singa.simulation.modules.newmodules.simulation.Simulation;
import de.bioforscher.singa.structure.features.molarmass.MolarMass;
import org.junit.After;
import org.junit.Test;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;

import static de.bioforscher.singa.chemistry.descriptive.features.diffusivity.Diffusivity.SQUARE_CENTIMETRE_PER_SECOND;
import static de.bioforscher.singa.features.model.FeatureOrigin.MANUALLY_ANNOTATED;
import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static de.bioforscher.singa.simulation.model.newsections.CellSubsection.SECTION_A;
import static de.bioforscher.singa.simulation.model.newsections.CellSubsection.SECTION_B;
import static de.bioforscher.singa.simulation.model.newsections.CellTopology.INNER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static tec.uom.se.unit.MetricPrefix.NANO;
import static tec.uom.se.unit.Units.METRE;

/**
 * @author cl
 */
public class DiffusionAtMembranesTest {

    // ammonia
    private static SmallMolecule ammonia = new SmallMolecule.Builder("ammonia")
            .name("ammonia")
            .assignFeature(new Diffusivity(Quantities.getQuantity(2.28E-05, SQUARE_CENTIMETRE_PER_SECOND), MANUALLY_ANNOTATED))
            .build();

    // anchored protein
    private static Protein anchoredProtein = new Protein.Builder("AP")
            .name("anchored protein")
            .assignFeature(new MolarMass(1000, MANUALLY_ANNOTATED))
            .setMembraneAnchored(true)
            .build();

    // unanchored protein
    private static Protein globularProtein = new Protein.Builder("GP")
            .name("globular protein")
            .assignFeature(new MolarMass(1000, MANUALLY_ANNOTATED))
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

    @After
    public void cleanUp() {
        Environment.reset();
    }

    @Test
    public void shouldSimulateBlockedDiffusion() {
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
        // simulate som epochs
        for (int i = 0; i < 100; i++) {
            simulation.nextEpoch();
        }

        // left part of the central node should fill with ammonia
        assertTrue(leftNode.getConcentration(SECTION_A, ammonia).getValue().doubleValue() > 0.0);
        assertTrue(membraneNode.getConcentration(SECTION_A, ammonia).getValue().doubleValue() > 0.0);
        // right part of the central node and right node should not
        assertEquals(0.0, membraneNode.getConcentration(CellSubsection.MEMBRANE, ammonia).getValue().doubleValue(), 0.0);
        assertEquals(0.0, membraneNode.getConcentration(SECTION_B, ammonia).getValue().doubleValue(), 0.0);
        assertEquals(0.0, rightNode.getConcentration(SECTION_B, ammonia).getValue().doubleValue(), 0.0);
    }

    @Test
    public void shouldSimulateLargeScaleBlockedDiffusion() {
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
                assertEquals(0.0, node.getConcentrationContainer().get(SECTION_B, ammonia).getValue().doubleValue(), 0.0);
            }
        }

    }

    @Test
    public void shouldAnchorInMembrane() {
        // create simulation
        Simulation simulation = setupAnchorSimulation();
        // add some protein in cytoplasm
        AutomatonNode node1 = simulation.getGraph().getNode(0, 0);
        AutomatonNode node2 = simulation.getGraph().getNode(0, 1);
        // set concentrations
        node1.getConcentrationContainer().set(SECTION_A, anchoredProtein, 0.1);
        node1.getConcentrationContainer().set(SECTION_A, globularProtein, 0.1);

        // observe
        for (int i = 0; i < 10; i++) {
            simulation.nextEpoch();
            // concentration of anchored entity should stay zero in non-membrane node
            Quantity<MolarConcentration> betaGammaConcentration = node2.getConcentration(SECTION_A, anchoredProtein);
            assertEquals(0.0, betaGammaConcentration.getValue().doubleValue(), 0.0);
            // concentration of globular entity should increase in non-membrane node
            Quantity<MolarConcentration> betaConcentration = node2.getConcentration(SECTION_A, globularProtein);
            assertTrue(betaConcentration.getValue().doubleValue() > 0.0);
            // concentration of anchored entity should stay equal in non-membrane node
            Quantity<MolarConcentration> remainingConcentration = node1.getConcentration(SECTION_A, anchoredProtein);
            assertEquals(0.1, remainingConcentration.to(MOLE_PER_LITRE).getValue().doubleValue(), 0.0);
        }

    }

    @Test
    public void shouldAbsorbFromCytoplasm() {
        Simulation simulation = setupAnchorSimulation();
        // add some protein in cytoplasm
        AutomatonNode node1 = simulation.getGraph().getNode(0, 0);
        AutomatonNode node2 = simulation.getGraph().getNode(0, 1);
        // anchored
        node2.getConcentrationContainer().set(SECTION_A, anchoredProtein, 0.1);
        // observe over 10 epochs
        for (int i = 0; i < 100; i++) {
            simulation.nextEpoch();
            // concentration of anchored entity should increase in membrane node
            Quantity<MolarConcentration> availableConcentration = node1.getConcentration(SECTION_A, anchoredProtein);
            assertTrue(availableConcentration.getValue().doubleValue() > 0.0);
            // and decrease in the other node
            Quantity<MolarConcentration> remainingConcentration = node2.getConcentration(SECTION_A, anchoredProtein);
            assertTrue(remainingConcentration.getValue().doubleValue() < 0.1);
        }
    }

    @Test
    public void shouldReceiveFromNeighbourMembrane() {
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
        AutomatonNode node1 = simulation.getGraph().getNode(0, 0);
        AutomatonNode node2 = simulation.getGraph().getNode(1, 0);
        // bound
        node1.getConcentrationContainer().set(SECTION_A, anchoredProtein, 1.0);
        // observe over 10 epochs
        for (int i = 0; i < 100; i++) {
            simulation.nextEpoch();
            // concentration of bound chemical entity should increase in neighboring membrane node
            Quantity<MolarConcentration> availableConcentration = node2.getConcentration(SECTION_A, anchoredProtein);
            assertTrue(availableConcentration.getValue().doubleValue() > 0.0);
        }
    }


}
