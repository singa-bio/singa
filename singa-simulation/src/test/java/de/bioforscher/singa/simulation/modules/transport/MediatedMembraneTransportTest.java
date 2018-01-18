package de.bioforscher.singa.simulation.modules.transport;

import de.bioforscher.singa.chemistry.descriptive.entities.Species;
import de.bioforscher.singa.chemistry.descriptive.entities.Transporter;
import de.bioforscher.singa.chemistry.descriptive.features.databases.chebi.ChEBIParserService;
import de.bioforscher.singa.chemistry.descriptive.features.databases.uniprot.UniProtParserService;
import de.bioforscher.singa.chemistry.descriptive.features.permeability.OsmoticPermeability;
import de.bioforscher.singa.features.model.FeatureOrigin;
import de.bioforscher.singa.simulation.model.compartments.EnclosedCompartment;
import de.bioforscher.singa.simulation.model.compartments.Membrane;
import de.bioforscher.singa.simulation.model.concentrations.MembraneContainer;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import de.bioforscher.singa.structure.features.molarvolume.MolarVolume;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.units.ri.quantity.Quantities;

import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static de.bioforscher.singa.structure.features.molarvolume.MolarVolume.CUBIC_METRE_PER_MOLE;
import static tec.units.ri.unit.MetricPrefix.CENTI;

/**
 * @author cl
 */
public class MediatedMembraneTransportTest {

    private static final Logger logger = LoggerFactory.getLogger(MediatedMembraneTransportTest.class);

    @Test
    public void testMediatedTransportTest() {
        // sources
        final FeatureOrigin yang1997 = new FeatureOrigin(FeatureOrigin.OriginType.MANUAL_ANNOTATION, "Yang 1997", "Yang 1997");
        final FeatureOrigin kell1977 = new FeatureOrigin(FeatureOrigin.OriginType.MANUAL_ANNOTATION, "Kell 1977", "G .S. Kell, Effect of isotopic composition, temperature, pressure, and dissolved gases on the density of liquid water, Journal of Physical Chemistry Reference Data, 6 (1977), pp. 1109-1131.");
        // setup species
        Species water = ChEBIParserService.parse("CHEBI:15377");
        water.setFeature(new MolarVolume(Quantities.getQuantity(18.0182, CENTI(CUBIC_METRE_PER_MOLE)), kell1977));
        // transporter
        Transporter aquaporin2 = UniProtParserService.parse("P41181").asTransporter();
        aquaporin2.setFeature(new OsmoticPermeability(3.3e-14, yang1997));
        Simulation simulation = new Simulation();
        // create compartments and membrane
        EnclosedCompartment left = new EnclosedCompartment("LC", "Left");
        EnclosedCompartment right = new EnclosedCompartment("RC", "Right");
        Membrane membrane = Membrane.forCompartment(right);
        // create node
        AutomatonNode node = new AutomatonNode(0);
        node.setCellSection(membrane);
        node.setConcentrationContainer(new MembraneContainer(left, right, membrane));
        // create graph and assign node
        AutomatonGraph graph = new AutomatonGraph();
        graph.addNode(node);
        // assign concentration
        node.setAvailableConcentration(water, left, Quantities.getQuantity(1, MOLE_PER_LITRE));
        node.setAvailableConcentration(aquaporin2, membrane.getInnerLayer(), Quantities.getQuantity(0.01, MOLE_PER_LITRE));
        simulation.setGraph(graph);
        // create module
        // MediatedMembraneTransport transport = new MediatedMembraneTransport(simulation, aquaporin2, water);
        // assign module
        // simulation.getModules().add(transport);

        for (int i = 0; i < 2; i++) {
            simulation.nextEpoch();
        }

    }

}