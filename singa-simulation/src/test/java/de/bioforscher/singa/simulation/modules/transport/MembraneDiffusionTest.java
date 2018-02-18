package de.bioforscher.singa.simulation.modules.transport;

import de.bioforscher.singa.chemistry.descriptive.entities.Species;
import de.bioforscher.singa.chemistry.descriptive.features.diffusivity.Diffusivity;
import de.bioforscher.singa.chemistry.descriptive.features.permeability.MembranePermeability;
import de.bioforscher.singa.features.model.FeatureOrigin;
import de.bioforscher.singa.features.parameters.EnvironmentalParameters;
import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.graphs.model.Graphs;
import de.bioforscher.singa.simulation.model.compartments.EnclosedCompartment;
import de.bioforscher.singa.simulation.model.compartments.Membrane;
import de.bioforscher.singa.simulation.model.compartments.NodeState;
import de.bioforscher.singa.simulation.model.concentrations.MembraneContainer;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraphs;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import de.bioforscher.singa.structure.features.molarmass.MolarMass;
import org.junit.Test;
import tec.uom.se.quantity.Quantities;

import static de.bioforscher.singa.chemistry.descriptive.features.permeability.MembranePermeability.CENTIMETRE_PER_SECOND;
import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static de.bioforscher.singa.structure.features.molarmass.MolarMass.GRAM_PER_MOLE;
import static org.junit.Assert.assertTrue;
import static tec.uom.se.unit.MetricPrefix.MICRO;
import static tec.uom.se.unit.Units.METRE;

public class MembraneDiffusionTest {

    private static final Rectangle boundingBox = new Rectangle(400, 400);

    static {
        EnvironmentalParameters.setNodeDistance(Quantities.getQuantity(1, MICRO(METRE)));
    }

    private static final Species water = new Species.Builder("water")
            .name("water")
            .assignFeature(new MolarMass(Quantities.getQuantity(18, GRAM_PER_MOLE), FeatureOrigin.MANUALLY_ANNOTATED))
            .assignFeature(new MembranePermeability(Quantities.getQuantity(35E-04, CENTIMETRE_PER_SECOND), FeatureOrigin.MANUALLY_ANNOTATED))
            .build();

    @Test
    public void shouldSimulateMembraneDiffusion() {

        water.setFeature(Diffusivity.class);

        Simulation simulation = new Simulation();

        final AutomatonGraph automatonGraph = AutomatonGraphs.useStructureFrom(Graphs.buildLinearGraph(3, boundingBox));
        simulation.setGraph(automatonGraph);

        EnclosedCompartment left = new EnclosedCompartment("LC", "Left");
        EnclosedCompartment right = new EnclosedCompartment("RC", "Right");
        Membrane membrane = Membrane.forCompartment(right);

        AutomatonNode leftNode = automatonGraph.getNode(0);
        leftNode.setState(NodeState.AQUEOUS);
        leftNode.setCellSection(left);
        leftNode.setConcentration(water, Quantities.getQuantity(2.0, MOLE_PER_LITRE).to(EnvironmentalParameters.getTransformedMolarConcentration()));

        AutomatonNode rightNode = automatonGraph.getNode(2);
        rightNode.setState(NodeState.CYTOSOL);
        rightNode.setCellSection(right);
        rightNode.setConcentration(water, Quantities.getQuantity(1.0, MOLE_PER_LITRE).to(EnvironmentalParameters.getTransformedMolarConcentration()));

        AutomatonNode membraneNode = automatonGraph.getNode(1);
        membraneNode.setState(NodeState.MEMBRANE);
        membraneNode.setConcentrationContainer(new MembraneContainer(left, right, membrane));
        membraneNode.setAvailableConcentration(water, left, Quantities.getQuantity(2.0, MOLE_PER_LITRE).to(EnvironmentalParameters.getTransformedMolarConcentration()));
        membraneNode.setAvailableConcentration(water, right, Quantities.getQuantity(1.0, MOLE_PER_LITRE).to(EnvironmentalParameters.getTransformedMolarConcentration()));

        simulation.getModules().add(new FreeDiffusion(simulation));
        simulation.getModules().add(new MembraneDiffusion(simulation, water));
        simulation.getChemicalEntities().add(water);
        System.out.println(water.getStringForProtocol());
        for (int i = 0; i < 10; i++) {
            simulation.nextEpoch();
        }

        // left part should fill with ammonia
        assertTrue(leftNode.getAvailableConcentration(water, left).getValue().doubleValue() > 0.0);
        assertTrue(membraneNode.getAvailableConcentration(water, left).getValue().doubleValue() > 0.0);
        // right part and membrane should not
        assertTrue(membraneNode.getAvailableConcentration(water, membrane.getOuterLayer()).getValue().doubleValue() == 0.0);
        assertTrue(membraneNode.getAvailableConcentration(water, membrane.getInnerLayer()).getValue().doubleValue() == 0.0);
        assertTrue(membraneNode.getAvailableConcentration(water, right).getValue().doubleValue() == 0.0);
        assertTrue(rightNode.getAvailableConcentration(water, right).getValue().doubleValue() == 0.0);

    }

}