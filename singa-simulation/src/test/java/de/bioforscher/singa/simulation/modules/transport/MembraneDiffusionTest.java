package de.bioforscher.singa.simulation.modules.transport;

import de.bioforscher.singa.chemistry.descriptive.entities.SmallMolecule;
import de.bioforscher.singa.chemistry.descriptive.features.permeability.MembranePermeability;
import de.bioforscher.singa.features.model.FeatureOrigin;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.simulation.model.compartments.CellSectionState;
import de.bioforscher.singa.simulation.model.compartments.EnclosedCompartment;
import de.bioforscher.singa.simulation.model.compartments.Membrane;
import de.bioforscher.singa.simulation.model.concentrations.MembraneContainer;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraphs;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import org.junit.Test;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

import static de.bioforscher.singa.chemistry.descriptive.features.permeability.MembranePermeability.CENTIMETRE_PER_SECOND;
import static de.bioforscher.singa.features.parameters.Environment.*;
import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static org.junit.Assert.assertEquals;
import static tec.uom.se.unit.MetricPrefix.MICRO;
import static tec.uom.se.unit.Units.METRE;
import static tec.uom.se.unit.Units.SECOND;

public class MembraneDiffusionTest {

    @Test
    public void shouldSimulateMembraneDiffusion() {

        setNodeDistance(Quantities.getQuantity(1, MICRO(METRE)));
        setTimeStep(Quantities.getQuantity(1, MICRO(SECOND)));

        Simulation simulation = new Simulation();

        SmallMolecule water = new SmallMolecule.Builder("water")
                .name("water")
                .assignFeature(new MembranePermeability(Quantities.getQuantity(35E-04, CENTIMETRE_PER_SECOND), FeatureOrigin.MANUALLY_ANNOTATED))
                .build();

        final AutomatonGraph automatonGraph = AutomatonGraphs.singularGraph();
        simulation.setGraph(automatonGraph);

        EnclosedCompartment left = new EnclosedCompartment("LC", "Left");
        EnclosedCompartment right = new EnclosedCompartment("RC", "Right");
        Membrane membrane = Membrane.forCompartment(right);

        AutomatonNode membraneNode = automatonGraph.getNode(0,0);
        membraneNode.setState(CellSectionState.MEMBRANE);
        membraneNode.setConcentrationContainer(new MembraneContainer(left, right, membrane));
        membraneNode.setAvailableConcentration(water, left, Quantities.getQuantity(2.0, MOLE_PER_LITRE).to(getTransformedMolarConcentration()));
        membraneNode.setAvailableConcentration(water, right, Quantities.getQuantity(1.0, MOLE_PER_LITRE).to(getTransformedMolarConcentration()));
        automatonGraph.addNode(membraneNode);

        MembraneDiffusion.inSimulation(simulation)
                .cargo(water)
                .build();

        System.out.println(water.getStringForProtocol());

        simulation.nextEpoch();
        ComparableQuantity<MolarConcentration> expectedLeft = Quantities.getQuantity(2.0, MOLE_PER_LITRE).to(getTransformedMolarConcentration()).subtract(Quantities.getQuantity(3.5e-19, getTransformedMolarConcentration()));
        ComparableQuantity<MolarConcentration> expectedRight = Quantities.getQuantity(1.0, MOLE_PER_LITRE).to(getTransformedMolarConcentration()).add(Quantities.getQuantity(3.5e-19, getTransformedMolarConcentration()));

        assertEquals(expectedLeft.getValue().doubleValue(), membraneNode.getAvailableConcentration(water, left).getValue().doubleValue(), 1e-5);
        assertEquals(expectedRight.getValue().doubleValue(), membraneNode.getAvailableConcentration(water, right).getValue().doubleValue(), 1e-5);

        Environment.reset();

    }

}