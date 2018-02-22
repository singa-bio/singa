package de.bioforscher.singa.simulation.modules.transport;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.entities.Species;
import de.bioforscher.singa.chemistry.descriptive.features.diffusivity.Diffusivity;
import de.bioforscher.singa.features.model.FeatureOrigin;
import de.bioforscher.singa.mathematics.graphs.model.Graphs;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.compartments.EnclosedCompartment;
import de.bioforscher.singa.simulation.model.compartments.Membrane;
import de.bioforscher.singa.simulation.model.compartments.NodeState;
import de.bioforscher.singa.simulation.model.concentrations.MembraneContainer;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraphs;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import org.junit.Test;
import tec.uom.se.quantity.Quantities;

import java.util.HashSet;
import java.util.Set;

import static de.bioforscher.singa.chemistry.descriptive.features.diffusivity.Diffusivity.SQUARE_CENTIMETER_PER_SECOND;
import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static org.junit.Assert.assertTrue;

/**
 * @author cl
 */
public class MembraneBlockedDiffusionTest {

    @Test
    public void shouldSimulateBlockedDiffusion() {

        Species ammonia = new Species.Builder("ammonia")
                .name("ammonia")
                .assignFeature(new Diffusivity(Quantities.getQuantity(2.28E-05, SQUARE_CENTIMETER_PER_SECOND), FeatureOrigin.MANUALLY_ANNOTATED))
                .build();

        Simulation simulation = new Simulation();

        final AutomatonGraph automatonGraph = AutomatonGraphs.useStructureFrom(Graphs.buildLinearGraph(3));

        EnclosedCompartment left = new EnclosedCompartment("LC", "Left");
        EnclosedCompartment right = new EnclosedCompartment("RC", "Right");
        Membrane membrane = Membrane.forCompartment(right);

        AutomatonNode leftNode = automatonGraph.getNode(0);
        leftNode.setState(NodeState.AQUEOUS);
        leftNode.setCellSection(left);
        leftNode.setConcentration(ammonia, 1.0);

        AutomatonNode rightNode = automatonGraph.getNode(2);
        rightNode.setState(NodeState.CYTOSOL);
        rightNode.setCellSection(right);

        AutomatonNode membraneNode = automatonGraph.getNode(1);
        membraneNode.setState(NodeState.MEMBRANE);
        MembraneContainer concentrationContainer = new MembraneContainer(left, right, membrane);
        concentrationContainer.setAvailableConcentration(left, ammonia, Quantities.getQuantity(1.0, MOLE_PER_LITRE));
        Set<ChemicalEntity<?>> entities = new HashSet<>();
        entities.add(ammonia);
        concentrationContainer.setReferencedEntities(entities);

        Set<CellSection> sections = new HashSet<>();
        sections.add(left);
        sections.add(membrane.getOuterLayer());
        sections.add(membrane.getInnerLayer());
        sections.add(right);
        concentrationContainer.setRefencedSections(sections);

        membraneNode.setConcentrationContainer(concentrationContainer);

        simulation.setGraph(automatonGraph);
        simulation.getChemicalEntities().add(ammonia);


        simulation.getModules().add(new FreeDiffusion(simulation, simulation.getChemicalEntities()));

        for (int i = 0; i < 10; i++) {
            simulation.nextEpoch();
        }

        // left part should fill with ammonia
        assertTrue(leftNode.getAvailableConcentration(ammonia, left).getValue().doubleValue() > 0.0);
        assertTrue(membraneNode.getAvailableConcentration(ammonia, left).getValue().doubleValue() > 0.0);
        // right part and membrane should not
        assertTrue(membraneNode.getAvailableConcentration(ammonia, membrane.getOuterLayer()).getValue().doubleValue() == 0.0);
        assertTrue(membraneNode.getAvailableConcentration(ammonia, membrane.getInnerLayer()).getValue().doubleValue() == 0.0);
        assertTrue(membraneNode.getAvailableConcentration(ammonia, right).getValue().doubleValue() == 0.0);
        assertTrue(rightNode.getAvailableConcentration(ammonia, right).getValue().doubleValue() == 0.0);

    }


}
