package de.bioforscher.singa.simulation.modules.transport;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.entities.ComplexedChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.entities.Protein;
import de.bioforscher.singa.chemistry.descriptive.entities.SmallMolecule;
import de.bioforscher.singa.chemistry.descriptive.features.diffusivity.Diffusivity;
import de.bioforscher.singa.features.identifiers.UniProtIdentifier;
import de.bioforscher.singa.features.model.FeatureOrigin;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.mathematics.graphs.model.Graphs;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraphs;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import de.bioforscher.singa.simulation.model.newsections.CellRegion;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import org.junit.After;
import org.junit.Test;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;

import static de.bioforscher.singa.chemistry.descriptive.features.diffusivity.Diffusivity.SQUARE_CENTIMETRE_PER_SECOND;
import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static de.bioforscher.singa.simulation.model.newsections.CellSubsection.*;
import static de.bioforscher.singa.simulation.model.newsections.CellTopology.INNER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static tec.uom.se.unit.MetricPrefix.NANO;
import static tec.uom.se.unit.Units.METRE;

/**
 * @author cl
 */
public class MembraneRelevantDiffusionTest {

    @After
    public void cleanUp() {
        Environment.reset();
    }

    @Test
    public void shouldSimulateBlockedDiffusion() {

        SmallMolecule ammonia = new SmallMolecule.Builder("ammonia")
                .name("ammonia")
                .assignFeature(new Diffusivity(Quantities.getQuantity(2.28E-05, SQUARE_CENTIMETRE_PER_SECOND), FeatureOrigin.MANUALLY_ANNOTATED))
                .build();

        Simulation simulation = new Simulation();

        final AutomatonGraph automatonGraph = AutomatonGraphs.useStructureFrom(Graphs.buildGridGraph(3, 1));

        AutomatonNode leftNode = automatonGraph.getNode(0, 0);
        leftNode.setCellRegion(CellRegion.CYTOSOL_A);
        leftNode.getConcentrationContainer().set(INNER, ammonia, 1.0);

        AutomatonNode rightNode = automatonGraph.getNode(2, 0);
        rightNode.setCellRegion(CellRegion.CYTOSOL_B);

        AutomatonNode membraneNode = automatonGraph.getNode(1, 0);
        membraneNode.setCellRegion(CellRegion.MEMBRANE);

        simulation.setGraph(automatonGraph);

        FreeDiffusion.inSimulation(simulation)
                .onlyFor(ammonia)
                .build();

        for (int i = 0; i < 10; i++) {
            simulation.nextEpoch();
        }

        // left part should fill with ammonia
        assertTrue(leftNode.getConcentration(SECTION_A, ammonia).getValue().doubleValue() > 0.0);
        assertTrue(membraneNode.getConcentration(SECTION_A, ammonia).getValue().doubleValue() > 0.0);
        // right part and membrane should not
        assertEquals(0.0, membraneNode.getConcentration(MEMBRANE, ammonia).getValue().doubleValue(), 0.0);
        assertEquals(0.0, membraneNode.getConcentration(SECTION_B, ammonia).getValue().doubleValue(), 0.0);
        assertEquals(0.0, rightNode.getConcentration(SECTION_B, ammonia).getValue().doubleValue(), 0.0);
    }

    @Test
    public void shouldSimulateLargeScaleBlockedDiffusion() {

        SmallMolecule ammonia = new SmallMolecule.Builder("ammonia")
                .name("ammonia")
                .assignFeature(new Diffusivity(Quantities.getQuantity(2.28E-05, SQUARE_CENTIMETRE_PER_SECOND), FeatureOrigin.MANUALLY_ANNOTATED))
                .build();

        Simulation simulation = new Simulation();

        // setup node distance to diameter);
        Environment.setNodeSpacingToDiameter(Quantities.getQuantity(2500.0, NANO(METRE)), 11);
        AutomatonGraph graph = AutomatonGraphs.createRectangularAutomatonGraph(11, 11);
        AutomatonGraphs.splitRectangularGraphWithMembrane(graph, SECTION_A, SECTION_B, false);

        // set concentrations
        // only 5 left most nodes
        for (AutomatonNode node : graph.getNodes()) {
            if (node.getIdentifier().getColumn() >= (graph.getNumberOfColumns() / 2)) {
                node.getConcentrationContainer().set(SECTION_A, ammonia, 1.0);
            }
        }

        simulation.setGraph(graph);

        FreeDiffusion.inSimulation(simulation)
                .onlyFor(ammonia)
                .build();

        for (int i = 0; i < 100; i++) {
            simulation.nextEpoch();
        }

        // nothing should permeate to the lower part
        assertEquals(0.0, graph.getNode(6, 0).getConcentrationContainer().get(SECTION_B, ammonia).getValue().doubleValue(), 0.0);
    }

    private Simulation setupAnchorSimulation() {
        Environment.reset();
        // simulation
        Simulation simulation = new Simulation();
        // g-protein subunits
        Protein gProteinBeta = new Protein.Builder("G(B)")
                .name("G protein subunit beta")
                .additionalIdentifier(new UniProtIdentifier("P62873"))
                .build();
        Protein gProteinGamma = new Protein.Builder("G(G)")
                .name("G protein subunit gamma")
                .additionalIdentifier(new UniProtIdentifier("P63211"))
                .build();
        // complexed entity
        ComplexedChemicalEntity gProteinBetaGamma = ComplexedChemicalEntity.create("G(BG)")
                .name("G protein beta gamma complex")
                .addAssociatedPart(gProteinBeta)
                .addAssociatedPart(gProteinGamma)
                .setMembraneAnchored(true)
                .build();
        // create graph
        AutomatonGraph graph = AutomatonGraphs.createRectangularAutomatonGraph(1, 2);
        simulation.setGraph(graph);

        // distribute nodes to sections
        graph.getNodesOfRow(0).forEach(node -> node.setCellRegion(CellRegion.MEMBRANE));
        graph.getNodesOfRow(1).forEach(node -> node.setCellRegion(CellRegion.CYTOSOL_A));

        // add diffusion
        FreeDiffusion.inSimulation(simulation)
                .forAll(gProteinBetaGamma, gProteinBeta)
                .build();

        return simulation;
    }

    @Test
    public void shouldAnchorInMembrane() {
        // FIXME
        Simulation simulation = setupAnchorSimulation();
        // get entities
        ChemicalEntity gProteinBeta = simulation.getChemicalEntity("G(B)");
        ChemicalEntity gProteinBetaGamma = simulation.getChemicalEntity("G(BG)");
        // add some protein in cytoplasm
        AutomatonNode node1 = simulation.getGraph().getNode(0, 0);
        AutomatonNode node2 = simulation.getGraph().getNode(0, 1);
        // bound
        node1.getConcentrationContainer().set(SECTION_A, gProteinBetaGamma, 0.1);
        // unbound
        node1.getConcentrationContainer().set(SECTION_A, gProteinBeta, 0.1);
        // observe over 10 epochs
        for (int i = 0; i < 10; i++) {
            simulation.nextEpoch();
            // concentration of bound chemical entity should stay zero in non-membrane node
            Quantity<MolarConcentration> betaGammaConcentration = node2.getConcentration(SECTION_A, gProteinBetaGamma);
            assertEquals(0.0, betaGammaConcentration.getValue().doubleValue(), 0.0);
            // concentration of unbound chemical entity should increase in non-membrane node
            Quantity<MolarConcentration> betaConcentration = node2.getConcentration(SECTION_A, gProteinBeta);
            assertTrue(betaConcentration.getValue().doubleValue() > 0.0);
            // concentration of bound chemical entity should stay equal in non-membrane node
            Quantity<MolarConcentration> remainingConcentration = node1.getConcentration(SECTION_A, gProteinBetaGamma);
            assertEquals(0.1, remainingConcentration.to(MOLE_PER_LITRE).getValue().doubleValue(), 0.0);
        }

    }

    @Test
    public void shouldAbsorbFromCytoplasm() {
        Simulation simulation = setupAnchorSimulation();
        // get entities
        ChemicalEntity gProteinBetaGamma = simulation.getChemicalEntity("G(BG)");
        // add some protein in cytoplasm
        AutomatonNode node1 = simulation.getGraph().getNode(0, 0);
        AutomatonNode node2 = simulation.getGraph().getNode(0, 1);
        // bound
        node2.getConcentrationContainer().set(SECTION_A, gProteinBetaGamma, 0.1);
        // observe over 10 epochs
        for (int i = 0; i < 10; i++) {
            simulation.nextEpoch();
            // concentration of bound chemical entity should increase in membrane node
            Quantity<MolarConcentration> availableConcentration = node1.getConcentration(SECTION_A, gProteinBetaGamma);
            assertTrue(availableConcentration.getValue().doubleValue() > 0.0);
            // and decrease in the other node
            Quantity<MolarConcentration> remainingConcentration = node2.getConcentration(SECTION_A, gProteinBetaGamma);
            assertTrue(remainingConcentration.getValue().doubleValue() < 0.1);
        }
    }

    @Test
    public void shouldReceiveFromNeighbourMembrane() {
        // simulation
        Simulation simulation = new Simulation();
        // g-protein subunits
        Protein gProteinBeta = new Protein.Builder("G(B)")
                .name("G protein subunit beta")
                .additionalIdentifier(new UniProtIdentifier("P62873"))
                .build();
        Protein gProteinGamma = new Protein.Builder("G(G)")
                .name("G protein subunit gamma")
                .additionalIdentifier(new UniProtIdentifier("P63211"))
                .build();
        // complexed entity
        ComplexedChemicalEntity gProteinBetaGamma = ComplexedChemicalEntity.create("G(BG)")
                .name("G protein beta gamma complex")
                .addAssociatedPart(gProteinBeta)
                .addAssociatedPart(gProteinGamma)
                .setMembraneAnchored(true)
                .build();
        // create graph
        AutomatonGraph graph = AutomatonGraphs.createRectangularAutomatonGraph(2, 1);
        simulation.setGraph(graph);
        // distribute nodes to sections
        graph.getNodesOfRow(0).forEach(node -> node.setCellRegion(CellRegion.MEMBRANE));
        // graph.getNodesOfRow(1).forEach(node -> node.setCellSection(innerSection));
        // add diffusion
        FreeDiffusion.inSimulation(simulation)
                .forAll(gProteinBetaGamma, gProteinBeta)
                .build();

        // add some protein in cytoplasm
        AutomatonNode node1 = simulation.getGraph().getNode(0, 0);
        AutomatonNode node2 = simulation.getGraph().getNode(1, 0);
        // bound
        node1.getConcentrationContainer().set(SECTION_A, gProteinBetaGamma, 1.0);
        // observe over 10 epochs
        for (int i = 0; i < 10; i++) {
            simulation.nextEpoch();
            // concentration of bound chemical entity should increase in neighboring membrane node
            Quantity<MolarConcentration> availableConcentration = node2.getConcentration(SECTION_A, gProteinBetaGamma);
            assertTrue(availableConcentration.getValue().doubleValue() > 0.0);
        }
    }


}
