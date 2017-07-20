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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Time;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static de.bioforscher.singa.features.model.FeatureOrigin.MANUALLY_ANNOTATED;
import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static tec.units.ri.unit.MetricPrefix.MICRO;
import static tec.units.ri.unit.MetricPrefix.NANO;
import static tec.units.ri.unit.Units.SECOND;

/**
 * @author cl
 */
public class PassiveMembraneTransportTest {

    private static final Logger logger = LoggerFactory.getLogger(PassiveMembraneTransportTest.class);

    private static final double epsilon = 0.01;
    private Map<String, ChemicalEntity<?>> entities;
    private List<String> logContent;

    private void initialize() {
        this.entities = new HashMap<>();
        this.logContent = new ArrayList<>();
    }

    private void setupEntities() {
        this.entities.put("domperidone", createEntity("domperidone", 1.48e9, 1.76e3, 3.50e2));
        this.entities.put("labetalol", createEntity("labetalol", 1.17e9, 1.14e4, 1.30e4));
        this.entities.put("loperamide", createEntity("loperamide", 1.03e8, 1.81e3, 6.71e5));
        this.entities.put("verapamil", createEntity("verapamil", 4.56e8, 1.43e2, 7.53e6));
        this.entities.put("propranolol", createEntity("propranolol", 1.27e9, 3.09e4, 4.75e6));
        this.entities.put("chlorpromazine", createEntity("chlorpromazine", 1.94e9, 7.75e2, 1.74e7));
        this.entities.put("desipramine", createEntity("desipramine", 2.13e9, 4.86e4, 1.09e7));
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
        updateTimestep(entity);
        // return simulation
        return simulation;
    }

    private ChemicalEntity createEntity(String name, double kIn, double kout, double kflip) {
        return new Species.Builder(name)
                .name(name)
                .assignFeature(new MembraneEntry(kIn, MANUALLY_ANNOTATED))
                .assignFeature(new MembraneExit(kout, MANUALLY_ANNOTATED))
                .assignFeature(new MembraneFlipFlop(kflip, MANUALLY_ANNOTATED))
                .build();
    }

    private void updateTimestep(ChemicalEntity<?> entity) {
        MembraneEntry membraneEntry = entity.getFeature(MembraneEntry.class);
        membraneEntry.scale(Quantities.getQuantity(1.0, NANO(SECOND)));
        Quantity<Time> timeStep = Quantities.getQuantity(epsilon / membraneEntry.getScaledQuantity().getValue().doubleValue(), NANO(SECOND));
        EnvironmentalParameters.getInstance().setTimeStep(timeStep);
    }

    private void writeLogContent(ChemicalEntity<?> entity) {
        String lines = this.logContent.stream()
                .collect(Collectors.joining("\n"));
        Path path = Paths.get(System.getProperty("user.home") + "/git/my_data/data_analysis/passive_membrane_transport/raw/" + entity.getName() + ".csv");
        try {
            Files.write(path, lines.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void passiveDiffusionTest() {
        initialize();
        setupEntities();
        for (ChemicalEntity<?> entity : this.entities.values()) {
            this.logContent.clear();
            EnclosedCompartment left = new EnclosedCompartment("LC", "Left");
            Simulation simulation = setupSimulation(entity);
            BioNode node = simulation.getGraph().getNode(0);
            logger.info("Calculating passive membrane diffusion for {}.", entity.getName());
            double previousConcentration = 0.0;
            double currentConcentration;
            double changeInConcentration = 1.0;
            double currentTime = 0;
            double previousTime = 0.0;
            // as long as something changes
            while (currentTime < 0.8) {
                // get concentration
                currentConcentration = node.getAvailableConcentration(entity, left).getValue().doubleValue();
                // current time
                currentTime = simulation.getElapsedTime().getValue().doubleValue();
                // calculate change
                if (currentConcentration != 0 && previousConcentration != 0) {
                    changeInConcentration = 1 - previousConcentration / currentConcentration;
                }
                // print log
                if (currentTime - previousTime > 1e-5 || previousTime == 0.0) {
                    Number elapsedTime = simulation.getElapsedTime().to(SECOND).getValue();
                    String logString = elapsedTime + ", " + currentConcentration;
                    this.logContent.add(logString);
                    previousTime = currentTime;
                }
                // update
                previousConcentration = currentConcentration;
                simulation.nextEpoch();
            }
            if (changeInConcentration == 0) {
                logger.info("Exited because concentration was not changing anymore");
            } else if (currentTime >= 0.8) {
                logger.info("Exited because goal time was reached");
            }
            writeLogContent(entity);
        }

    }


}