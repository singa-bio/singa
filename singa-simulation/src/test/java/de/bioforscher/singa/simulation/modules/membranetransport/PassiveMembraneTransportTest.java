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
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.BioNode;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import org.junit.Test;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Time;
import java.util.HashMap;
import java.util.Map;

import static de.bioforscher.singa.features.model.FeatureOrigin.MANUALLY_ANNOTATED;
import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static tec.units.ri.unit.MetricPrefix.MICRO;
import static tec.units.ri.unit.MetricPrefix.NANO;
import static tec.units.ri.unit.Units.SECOND;

/**
 * @author cl
 */
public class PassiveMembraneTransportTest {

    private static final double epsilon = 0.1;
    private Map<String, ChemicalEntity<?>> entities;

    private void initialize() {
        this.entities = new HashMap<>();
    }

    private void setupEntities() {
        entities.put("domperidone", createEntity("domperidone", 1.48e9, 1.76e3, 3.50e2));
        entities.put("labetalol", createEntity("labetalol", 1.17e9, 1.14e4, 1.30e4));
        entities.put("loperamide", createEntity("loperamide", 1.03e8, 1.81e3, 6.71e5));
        entities.put("verapamil", createEntity("verapamil", 4.56e8, 1.43e2, 7.53e6));
        entities.put("propranolol", createEntity("propranolol", 1.27e9, 3.09e4, 4.75e6));
        entities.put("chlorpromazine", createEntity("chlorpromazine", 1.94e9, 7.75e2, 1.74e7));
        entities.put("desipramine", createEntity("desipramine", 2.13e9, 4.86e4, 1.09e7));
    }

    private Simulation setupSimulation(ChemicalEntity<?> entity) {
        // create compartments and membrane
        EnclosedCompartment left = new EnclosedCompartment("LC", "Left");
        EnclosedCompartment right = new EnclosedCompartment("RC", "Right");
        Membrane membrane = Membrane.forCompartment(left);
        // create node
        BioNode node = new BioNode(0);
        node.setCellSection(membrane);
        node.setConcentrations(new MembraneContainer(right, left, membrane));
        // create graph and assign node
        AutomatonGraph graph = new AutomatonGraph();
        graph.addNode(node);
        // assign concentration
        node.setAvailableConcentration(entity, right, Quantities.getQuantity(20, MICRO(MOLE_PER_LITRE)).to(MOLE_PER_LITRE));
        // create module
        PassiveMembraneTransport transport = new PassiveMembraneTransport();
        // create simulation and assign module
        Simulation simulation = new Simulation();
        simulation.setGraph(graph);
        simulation.getModules().add(transport);
        // scale time step with epsilon
        MembraneEntry membraneEntry = entity.getFeature(MembraneEntry.class);
        membraneEntry.scale(Quantities.getQuantity(1.0, NANO(SECOND)));
        Quantity<Time> timeStep = Quantities.getQuantity(epsilon / membraneEntry.getScaledQuantity().getValue().doubleValue(), NANO(SECOND));
        EnvironmentalParameters.getInstance().setTimeStep(timeStep);
        // return simulation
        return simulation;
    }

    private ChemicalEntity createEntity(String name, double kIn, double kout, double kflip) {
        return new Species.Builder(name)
                .assignFeature(new MembraneEntry(kIn, MANUALLY_ANNOTATED))
                .assignFeature(new MembraneExit(kout, MANUALLY_ANNOTATED))
                .assignFeature(new MembraneFlipFlop(kflip, MANUALLY_ANNOTATED))
                .build();
    }

    @Test
    public void passiveDiffusionTest() {

        initialize();
        setupEntities();

        for (ChemicalEntity<?> entity : this.entities.values()) {

            EnclosedCompartment left = new EnclosedCompartment("LC", "Left");
            Simulation simulation = setupSimulation(entity);
            BioNode node = simulation.getGraph().getNode(0);

            double previous = 0.0;
            double current;
            double change = 1.0;
            // as long as something changes
            while (change > 0) {
                // get concentration
                current = node.getAvailableConcentration(entity, left).getValue().doubleValue();
                // calculate change
                if (current != 0 && previous != 0) {
                    change = 1 - previous / current;
                }
                // print log
                if (simulation.getEpoch() % 100 == 0 || simulation.getEpoch() == 0) {
                    System.out.println(simulation.getElapsedTime().to(SECOND).getValue() + ", " + current);
                }
                // update
                previous = current;
                simulation.nextEpoch();
            }

        }

    }


}