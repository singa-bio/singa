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
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.compartments.CellSectionState;
import de.bioforscher.singa.simulation.model.compartments.EnclosedCompartment;
import de.bioforscher.singa.simulation.model.compartments.Membrane;
import de.bioforscher.singa.simulation.model.concentrations.MembraneContainer;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraphs;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import org.junit.Test;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import java.util.HashSet;
import java.util.Set;

import static de.bioforscher.singa.chemistry.descriptive.features.diffusivity.Diffusivity.SQUARE_CENTIMETRE_PER_SECOND;
import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static tec.uom.se.unit.MetricPrefix.NANO;
import static tec.uom.se.unit.Units.METRE;

/**
 * @author cl
 */
public class MembraneRelevantDiffusionTest {

    @Test
    public void shouldSimulateBlockedDiffusion() {

        SmallMolecule ammonia = new SmallMolecule.Builder("ammonia")
                .name("ammonia")
                .assignFeature(new Diffusivity(Quantities.getQuantity(2.28E-05, SQUARE_CENTIMETRE_PER_SECOND), FeatureOrigin.MANUALLY_ANNOTATED))
                .build();

        Simulation simulation = new Simulation();

        final AutomatonGraph automatonGraph = AutomatonGraphs.useStructureFrom(Graphs.buildGridGraph(3, 1));

        EnclosedCompartment left = new EnclosedCompartment("LC", "Left");
        EnclosedCompartment right = new EnclosedCompartment("RC", "Right");
        Membrane membrane = Membrane.forCompartment(right);

        AutomatonNode leftNode = automatonGraph.getNode(0, 0);
        leftNode.setState(CellSectionState.AQUEOUS);
        leftNode.setCellSection(left);
        leftNode.setConcentration(ammonia, 1.0);

        AutomatonNode rightNode = automatonGraph.getNode(2, 0);
        rightNode.setState(CellSectionState.CYTOSOL);
        rightNode.setCellSection(right);

        AutomatonNode membraneNode = automatonGraph.getNode(1, 0);
        membraneNode.setState(CellSectionState.MEMBRANE);
        MembraneContainer concentrationContainer = new MembraneContainer(left, right, membrane);
        concentrationContainer.setAvailableConcentration(left, ammonia, Quantities.getQuantity(1.0, MOLE_PER_LITRE));
        Set<ChemicalEntity> entities = new HashSet<>();
        entities.add(ammonia);
        concentrationContainer.setReferencedEntities(entities);

        Set<CellSection> sections = new HashSet<>();
        sections.add(left);
        sections.add(membrane.getOuterLayer());
        sections.add(membrane.getInnerLayer());
        sections.add(right);
        concentrationContainer.setReferencedSections(sections);

        membraneNode.setConcentrationContainer(concentrationContainer);

        simulation.setGraph(automatonGraph);

        FreeDiffusion.inSimulation(simulation)
                .onlyFor(ammonia)
                .build();

        for (int i = 0; i < 10; i++) {
            simulation.nextEpoch();
        }

        // left part should fill with ammonia
        assertTrue(leftNode.getAvailableConcentration(ammonia, left).getValue().doubleValue() > 0.0);
        assertTrue(membraneNode.getAvailableConcentration(ammonia, left).getValue().doubleValue() > 0.0);
        // right part and membrane should not
        assertEquals(0.0, membraneNode.getAvailableConcentration(ammonia, membrane.getOuterLayer()).getValue().doubleValue(), 0.0);
        assertEquals(0.0, membraneNode.getAvailableConcentration(ammonia, membrane.getInnerLayer()).getValue().doubleValue(), 0.0);
        assertEquals(0.0, membraneNode.getAvailableConcentration(ammonia, right).getValue().doubleValue(), 0.0);
        assertEquals(0.0, rightNode.getAvailableConcentration(ammonia, right).getValue().doubleValue(), 0.0);

        Environment.reset();

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

        EnclosedCompartment up = new EnclosedCompartment("Up", "Up");
        EnclosedCompartment down = new EnclosedCompartment("Down", "Down");
        // Membrane membrane = Membrane.forCompartment(right);

        AutomatonGraph graph = AutomatonGraphs.createRectangularAutomatonGraph(11, 11);
        AutomatonGraphs.splitRectangularGraphWithMembrane(graph, down, up, false);

        // set concentrations
        // only 5 left most nodes
        for (AutomatonNode node : graph.getNodes()) {
            if (node.getIdentifier().getColumn() < (graph.getNumberOfColumns() / 2)) {
                node.setConcentration(ammonia, 1.0);
            } else {
                node.setConcentration(ammonia, 0.0);
            }
            if (node.getConcentrationContainer() instanceof MembraneContainer) {
                node.setAvailableConcentration(ammonia, up, Quantities.getQuantity(1.0, MOLE_PER_LITRE).to(Environment.getTransformedMolarConcentration()));
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
        assertEquals(0.0, graph.getNode(6, 0).getAvailableConcentration(ammonia, down).getValue().doubleValue(), 0.0);

        Environment.reset();
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
        // sections
        EnclosedCompartment outerSection = new EnclosedCompartment("Ext", "Extracellular region");
        EnclosedCompartment innerSection = new EnclosedCompartment("Cyt", "Cytoplasm");
        Membrane membrane = Membrane.forCompartment(innerSection);
        // distribute nodes to sections
        graph.getNodesOfRow(0).forEach(node -> {
            node.setCellSection(membrane);
            node.setConcentrationContainer(new MembraneContainer(outerSection, innerSection, membrane));
        });
        graph.getNodesOfRow(1).forEach(node -> node.setCellSection(innerSection));
        // reference sections in graph
        graph.addCellSection(outerSection);
        graph.addCellSection(innerSection);
        graph.addCellSection(membrane);
        // add diffusion
        FreeDiffusion.inSimulation(simulation)
                .forAll(gProteinBetaGamma, gProteinBeta)
                .build();

        return simulation;
    }

    @Test
    public void shouldAnchorInMembrane() {
        Simulation simulation = setupAnchorSimulation();

        // get entities
        ChemicalEntity gProteinBeta = simulation.getChemicalEntity("G(B)");
        ChemicalEntity gProteinBetaGamma = simulation.getChemicalEntity("G(BG)");
        // get cell sections
        CellSection cytoplasm = simulation.getCellSection("Cyt");

        // add some protein in cytoplasm
        AutomatonNode node1 = simulation.getGraph().getNode(0, 0);
        AutomatonNode node2 = simulation.getGraph().getNode(0, 1);
        // bond
        node1.setAvailableConcentration(gProteinBetaGamma, cytoplasm, Quantities.getQuantity(0.1, MOLE_PER_LITRE)
                .to(Environment.getTransformedMolarConcentration()));
        // unbound
        node1.setAvailableConcentration(gProteinBeta, cytoplasm, Quantities.getQuantity(0.1, MOLE_PER_LITRE)
                .to(Environment.getTransformedMolarConcentration()));

        for (int i = 0; i < 10; i++) {
            simulation.nextEpoch();
            // concentration of bound chemical entity should stay zero in non-membrane node
            Quantity<MolarConcentration> betaGammaConcentration = node2.getConcentration(gProteinBetaGamma);
            assertEquals(0.0, betaGammaConcentration.getValue().doubleValue(), 0.0);
            // concentration of unbound chemical entity should increase in non-membrane node
            Quantity<MolarConcentration> betaConcentration = node2.getConcentration(gProteinBeta);
            assertTrue(betaConcentration.getValue().doubleValue() > 0.0);
            // concentration of bound chemical entity should stay equal in non-membrane node
            Quantity<MolarConcentration> remainingConcentration = node1.getAvailableConcentration(gProteinBetaGamma, cytoplasm);
            assertEquals(0.1, remainingConcentration.getValue().doubleValue(), 0.0);
        }

    }

    @Test
    public void shouldAbsorbFromCytoplasm() {
        Simulation simulation = setupAnchorSimulation();

        // get entities
        ChemicalEntity gProteinBetaGamma = simulation.getChemicalEntity("G(BG)");
        // get cell sections
        CellSection cytoplasm = simulation.getCellSection("Cyt");

        // add some protein in cytoplasm
        AutomatonNode node1 = simulation.getGraph().getNode(0, 0);
        AutomatonNode node2 = simulation.getGraph().getNode(0, 1);
        // bond
        node2.setAvailableConcentration(gProteinBetaGamma, cytoplasm, Quantities.getQuantity(0.1, MOLE_PER_LITRE)
                .to(Environment.getTransformedMolarConcentration()));

        for (int i = 0; i < 10; i++) {
            simulation.nextEpoch();
            // concentration of bound chemical entity should increase in membrane node
            Quantity<MolarConcentration> availableConcentration = node1.getAvailableConcentration(gProteinBetaGamma, cytoplasm);
            assertTrue(availableConcentration.getValue().doubleValue() > 0.0);
            // and decrease in the other node
            Quantity<MolarConcentration> remainingConcentration = node2.getAvailableConcentration(gProteinBetaGamma, cytoplasm);
            assertTrue(remainingConcentration.getValue().doubleValue() < 0.1);
        }
    }

    @Test
    public void shouldRecieveFromNeighbourMembrane() {
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
        AutomatonGraph graph = AutomatonGraphs.createRectangularAutomatonGraph(2, 1);
        simulation.setGraph(graph);
        // sections
        EnclosedCompartment outerSection = new EnclosedCompartment("Ext", "Extracellular region");
        EnclosedCompartment innerSection = new EnclosedCompartment("Cyt", "Cytoplasm");
        Membrane membrane = Membrane.forCompartment(innerSection);
        // distribute nodes to sections
        graph.getNodesOfRow(0).forEach(node -> {
            node.setCellSection(membrane);
            node.setConcentrationContainer(new MembraneContainer(outerSection, innerSection, membrane));
        });
        // graph.getNodesOfRow(1).forEach(node -> node.setCellSection(innerSection));
        // reference sections in graph
        graph.addCellSection(outerSection);
        graph.addCellSection(innerSection);
        graph.addCellSection(membrane);
        // add diffusion
        FreeDiffusion.inSimulation(simulation)
                .forAll(gProteinBetaGamma, gProteinBeta)
                .build();

        // add some protein in cytoplasm
        AutomatonNode node1 = simulation.getGraph().getNode(0, 0);
        AutomatonNode node2 = simulation.getGraph().getNode(1, 0);
        // bond
        node1.setAvailableConcentration(gProteinBetaGamma, innerSection, Quantities.getQuantity(0.1, MOLE_PER_LITRE)
                .to(Environment.getTransformedMolarConcentration()));

        for (int i = 0; i < 10; i++) {
            simulation.nextEpoch();
            // concentration of bound chemical entity should increase in neighboring membrane node
            Quantity<MolarConcentration> availableConcentration = node2.getAvailableConcentration(gProteinBetaGamma, innerSection);
            assertTrue(availableConcentration.getValue().doubleValue() > 0.0);
        }
    }


}
