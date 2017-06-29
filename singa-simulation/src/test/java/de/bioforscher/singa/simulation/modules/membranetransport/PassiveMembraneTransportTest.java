package de.bioforscher.singa.simulation.modules.membranetransport;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.entities.Species;
import de.bioforscher.singa.features.parameters.EnvironmentalParameters;
import de.bioforscher.singa.simulation.features.permeability.MembraneEntry;
import de.bioforscher.singa.simulation.features.permeability.MembraneExit;
import de.bioforscher.singa.simulation.features.permeability.MembraneFlipFlop;
import de.bioforscher.singa.simulation.model.compartments.EnclosedCompartment;
import de.bioforscher.singa.simulation.model.compartments.Membrane;
import de.bioforscher.singa.simulation.model.concentrations.MembraneContainer;
import de.bioforscher.singa.simulation.model.graphs.BioNode;
import org.junit.Test;
import tec.units.ri.quantity.Quantities;

import static de.bioforscher.singa.features.model.FeatureOrigin.MANUALLY_ANNOTATED;
import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static tec.units.ri.unit.MetricPrefix.MICRO;
import static tec.units.ri.unit.MetricPrefix.NANO;
import static tec.units.ri.unit.Units.METRE;
import static tec.units.ri.unit.Units.SECOND;

/**
 * @author cl
 */
public class PassiveMembraneTransportTest {

    @Test
    public void passiveDiffusionTest() {

        EnvironmentalParameters.getInstance().setNodeDistance(Quantities.getQuantity(1, MICRO(METRE)));
        EnvironmentalParameters.getInstance().setTimeStep(Quantities.getQuantity(100, NANO(SECOND)));

        ChemicalEntity entity = new Species.Builder("A")
                .assignFeature(new MembraneEntry(1.48e9, MANUALLY_ANNOTATED))
                .assignFeature(new MembraneExit(1.76e3, MANUALLY_ANNOTATED))
                .assignFeature(new MembraneFlipFlop(3.50e2, MANUALLY_ANNOTATED))
                .build();

        EnclosedCompartment left = new EnclosedCompartment("LC", "Left");
        EnclosedCompartment right = new EnclosedCompartment("RC", "Right");
        Membrane membrane = Membrane.forCompartment(left);

        BioNode node = new BioNode(0);
        node.setCellSection(membrane);
        node.setConcentrations(new MembraneContainer(right, left, membrane));

        node.setAvailableConcentration(entity, right, Quantities.getQuantity(1.0, MOLE_PER_LITRE));

        PassiveMembraneTransport transport = new PassiveMembraneTransport();
        transport.calculateDelta(node, entity);

        System.out.println(node.getDeltas());


    }


}