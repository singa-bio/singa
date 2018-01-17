package de.bioforscher.singa.simulation.modules.transport;

import de.bioforscher.singa.chemistry.descriptive.entities.Species;
import de.bioforscher.singa.chemistry.descriptive.features.diffusivity.Diffusivity;
import de.bioforscher.singa.features.model.FeatureOrigin;
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
import org.junit.Test;
import tec.units.ri.quantity.Quantities;

import static de.bioforscher.singa.chemistry.descriptive.features.diffusivity.Diffusivity.SQUARE_CENTIMETER_PER_SECOND;
import static org.junit.Assert.assertTrue;

/**
 * @author cl
 */
public class MembraneBlockedDiffusionTest {

    private static final Rectangle boundingBox = new Rectangle(400, 400);

    private static final Species ammonia = new Species.Builder("ammonia")
            .name("ammonia")
            .assignFeature(new Diffusivity(Quantities.getQuantity(2.28E-05, SQUARE_CENTIMETER_PER_SECOND), FeatureOrigin.MANUALLY_ANNOTATED))
            .build();

    @Test
    public void shouldSimulateBlockedDiffusion() {
        Simulation simulation = new Simulation();

        final AutomatonGraph automatonGraph = AutomatonGraphs.useStructureFrom(Graphs.buildLinearGraph(3, boundingBox));

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
        membraneNode.setConcentrationContainer(new MembraneContainer(left, right, membrane));

        simulation.setGraph(automatonGraph);

        simulation.getModules().add(new FreeDiffusion(simulation));

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
