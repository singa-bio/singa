package de.bioforscher.singa.simulation.modules.reactions.implementations;

import de.bioforscher.singa.chemistry.descriptive.entities.Enzyme;
import de.bioforscher.singa.chemistry.descriptive.entities.Species;
import de.bioforscher.singa.chemistry.descriptive.features.databases.chebi.ChEBIParserService;
import de.bioforscher.singa.chemistry.descriptive.features.reactions.MichaelisConstant;
import de.bioforscher.singa.chemistry.descriptive.features.reactions.TurnoverNumber;
import de.bioforscher.singa.mathematics.graphs.model.Graphs;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraphs;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import de.bioforscher.singa.simulation.modules.model.SimulationExamples;
import de.bioforscher.singa.simulation.modules.reactions.model.ReactantRole;
import de.bioforscher.singa.simulation.modules.reactions.model.StoichiometricReactant;
import de.bioforscher.singa.structure.features.molarmass.MolarMass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Time;
import java.util.Arrays;

import static de.bioforscher.singa.chemistry.descriptive.features.reactions.TurnoverNumber.PER_MINUTE;
import static de.bioforscher.singa.chemistry.descriptive.features.reactions.TurnoverNumber.PER_SECOND;
import static de.bioforscher.singa.features.model.FeatureOrigin.MANUALLY_ANNOTATED;
import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static org.junit.Assert.assertEquals;
import static tec.uom.se.unit.MetricPrefix.MILLI;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
public class ReactionTest {

    private static final Logger logger = LoggerFactory.getLogger(ReactionTest.class);

    @Test
    public void testEnzymeReaction() {
        // SABIO Entry ID: 28851
        // Kinetic properties of fructose bisphosphate aldolase from Trypanosoma
        // brucei compared to aldolase from rabbit muscle and Staphylococcus
        // aureus.

        logger.info("Testing Biochemical Reaction Module.");
        // create simulation
        Simulation simulation = new Simulation();

        // setup graph
        AutomatonGraph graph = AutomatonGraphs.useStructureFrom(Graphs.buildLinearGraph(1));

        // prepare species
        Species fp = ChEBIParserService.parse("CHEBI:18105");
        Species gp = ChEBIParserService.parse("CHEBI:16108");
        Species ga = ChEBIParserService.parse("CHEBI:17378");

        // prepare enzyme
        Enzyme aldolase = new Enzyme.Builder("P07752")
                .name("Fructose-bisphosphate aldolase")
                .addSubstrate(fp)
                .assignFeature(new MolarMass(82142, MANUALLY_ANNOTATED))
                .assignFeature(new MichaelisConstant(Quantities.getQuantity(9.0e-3, MOLE_PER_LITRE), MANUALLY_ANNOTATED))
                .assignFeature(new TurnoverNumber(Quantities.getQuantity(76.0, PER_MINUTE), MANUALLY_ANNOTATED))
                .build();

        // set concentrations
        for (AutomatonNode node : graph.getNodes()) {
            node.setConcentration(fp, 0.1);
            node.setConcentration(aldolase, 0.2);
            node.setConcentration(ga, 0);
            node.setConcentration(gp, 0);
        }

        // setup reaction
        MichaelisMentenReaction reaction = new MichaelisMentenReaction(simulation, aldolase);
        reaction.getStoichiometricReactants().addAll(Arrays.asList(
                new StoichiometricReactant(fp, ReactantRole.DECREASING),
                new StoichiometricReactant(ga, ReactantRole.INCREASING),
                new StoichiometricReactant(gp, ReactantRole.INCREASING)
        ));

        // add graph
        simulation.setGraph(graph);
        // add the reaction module
        simulation.getModules().add(reaction);

        AutomatonNode node = graph.getNode(0);
        Quantity<Time> currentTime;
        Quantity<Time> firstCheckpoint = Quantities.getQuantity(200.0, MILLI(SECOND));
        boolean firstCheckpointPassed = false;
        Quantity<Time> secondCheckpoint = Quantities.getQuantity(1000.0, MILLI(SECOND));
        // run simulation
        while ((currentTime = simulation.getElapsedTime().to(MILLI(SECOND))).getValue().doubleValue() < secondCheckpoint.getValue().doubleValue()) {
            simulation.nextEpoch();
            if (!firstCheckpointPassed && currentTime.getValue().doubleValue() > firstCheckpoint.getValue().doubleValue()) {
                logger.info("First checkpoint reached at {}.", simulation.getElapsedTime().to(MILLI(SECOND)));
                assertEquals(0.043, node.getConcentration(fp).getValue().doubleValue(), 1e-3);
                assertEquals(0.056, node.getConcentration(gp).getValue().doubleValue(), 1e-3);
                assertEquals(0.056, node.getConcentration(ga).getValue().doubleValue(), 1e-3);
                assertEquals(0.2, node.getConcentration(aldolase).getValue().doubleValue(), 0);
                firstCheckpointPassed = true;
            }
        }
        // check final values
        assertEquals(0.0, node.getConcentration(fp).getValue().doubleValue(), 1e-3);
        assertEquals(0.1, node.getConcentration(gp).getValue().doubleValue(), 1e-3);
        assertEquals(0.1, node.getConcentration(ga).getValue().doubleValue(), 1e-3);
        assertEquals(0.2, node.getConcentration(aldolase).getValue().doubleValue(), 0);
        logger.info("Second and final checkpoint (at {}) reached successfully.", simulation.getElapsedTime().to(MILLI(SECOND)));

    }

    @Test
    public void testEquilibriumReaction() {
        logger.info("Testing Equilibrium Reaction Module.");
        // create simulation
        Simulation simulation = new Simulation();

        // setup graph
        AutomatonGraph graph = AutomatonGraphs.useStructureFrom(Graphs.buildLinearGraph(1));

        // prepare species
        Species speciesA = new Species.Builder("Species A")
                .build();
        Species speciesB = new Species.Builder("Species B")
                .build();

        // set concentrations
        for (AutomatonNode node : graph.getNodes()) {
            node.setConcentration(speciesA, 1.0);
            node.setConcentration(speciesB, 0.0);
        }

        EquilibriumReaction reaction = new EquilibriumReaction(simulation,
                Quantities.getQuantity(5.0, PER_SECOND),
                Quantities.getQuantity(10.0, PER_SECOND));
        // set reactants
        reaction.getStoichiometricReactants().addAll(Arrays.asList(
                new StoichiometricReactant(speciesA, ReactantRole.DECREASING),
                new StoichiometricReactant(speciesB, ReactantRole.INCREASING)
        ));
        // set as elementary (no complex reaction)
        reaction.setElementary(true);

        // add graph
        simulation.setGraph(graph);
        // add the reaction module
        simulation.getModules().add(reaction);

        AutomatonNode node = graph.getNode(0);
        Quantity<Time> currentTime;
        Quantity<Time> firstCheckpoint = Quantities.getQuantity(25.0, MILLI(SECOND));
        boolean firstCheckpointPassed = false;
        Quantity<Time> secondCheckpoint = Quantities.getQuantity(800.0, MILLI(SECOND));
        // run simulation
        while ((currentTime = simulation.getElapsedTime().to(MILLI(SECOND))).getValue().doubleValue() < secondCheckpoint.getValue().doubleValue()) {
            simulation.nextEpoch();
            if (!firstCheckpointPassed && currentTime.getValue().doubleValue() > firstCheckpoint.getValue().doubleValue()) {
                logger.info("First checkpoint reached at {}.", simulation.getElapsedTime().to(MILLI(SECOND)));
                assertEquals(0.8901, node.getConcentration(speciesA).getValue().doubleValue(), 1e-3);
                assertEquals(0.1098, node.getConcentration(speciesB).getValue().doubleValue(), 1e-3);
                firstCheckpointPassed = true;
            }
        }

        // check final values
        assertEquals(0.66666, node.getConcentration(speciesA).getValue().doubleValue(), 1e-5);
        assertEquals(0.33333, node.getConcentration(speciesB).getValue().doubleValue(), 1e-5);
        logger.info("Second and final checkpoint (at {}) reached successfully.", simulation.getElapsedTime().to(MILLI(SECOND)));

    }

    @Test
    public void testNthOrderReaction() {
        logger.info("Testing Nth Order Reaction Module.");
        // create simulation
        Simulation simulation = new Simulation();

        // setup graph
        AutomatonGraph graph = AutomatonGraphs.useStructureFrom(Graphs.buildLinearGraph(1));

        // prepare species
        Species dpo = ChEBIParserService.parse("CHEBI:29802");
        Species ndo = ChEBIParserService.parse("CHEBI:33101");
        Species oxygen = ChEBIParserService.parse("CHEBI:15379");

        for (AutomatonNode node : graph.getNodes()) {
            node.setConcentration(dpo, 0.020);
            node.setConcentration(ndo, 0.0);
            node.setConcentration(oxygen, 0.0);
        }

        // create reaction
        NthOrderReaction reaction = new NthOrderReaction(simulation, Quantities.getQuantity(0.07, PER_SECOND));
        // set reactants
        reaction.getStoichiometricReactants().addAll(Arrays.asList(
                new StoichiometricReactant(dpo, ReactantRole.DECREASING, 2),
                new StoichiometricReactant(ndo, ReactantRole.INCREASING, 4),
                new StoichiometricReactant(oxygen, ReactantRole.INCREASING)
        ));
        // set as elementary (no complex reaction)
        reaction.setElementary(true);

        // add graph
        simulation.setGraph(graph);
        // add the reaction
        simulation.getModules().add(reaction);

        AutomatonNode node = graph.getNode(0);
        Quantity<Time> currentTime;
        Quantity<Time> firstCheckpoint = Quantities.getQuantity(500.0, MILLI(SECOND));
        boolean firstCheckpointPassed = false;
        Quantity<Time> secondCheckpoint = Quantities.getQuantity(7000.0, MILLI(SECOND));
        // run simulation
        while ((currentTime = simulation.getElapsedTime().to(MILLI(SECOND))).getValue().doubleValue() < secondCheckpoint.getValue().doubleValue()) {
            simulation.nextEpoch();
            if (!firstCheckpointPassed && currentTime.getValue().doubleValue() > firstCheckpoint.getValue().doubleValue()) {
                logger.info("First checkpoint reached at {}.", simulation.getElapsedTime().to(MILLI(SECOND)));
                assertEquals(9E-4, node.getConcentration(oxygen).getValue().doubleValue(), 1e-3);
                assertEquals(0.003, node.getConcentration(ndo).getValue().doubleValue(), 1e-3);
                assertEquals(0.018, node.getConcentration(dpo).getValue().doubleValue(), 1e-3);
                firstCheckpointPassed = true;
            }
        }

        // check final values
        assertEquals(0.006, node.getConcentration(oxygen).getValue().doubleValue(), 1e-3);
        assertEquals(0.025, node.getConcentration(ndo).getValue().doubleValue(), 1e-3);
        assertEquals(0.007, node.getConcentration(dpo).getValue().doubleValue(), 1e-3);
        logger.info("Second and final checkpoint (at {}) reached successfully.", simulation.getElapsedTime().to(MILLI(SECOND)));
    }


    @Test
    public void shouldPerformCalciumOscillationExample() {
        logger.info("Testing Dynamic Reaction Module.");
        Simulation simulation = SimulationExamples.createSimulationFromSBML();

        Species x = new Species.Builder("X").build();
        AutomatonNode node = simulation.getGraph().getNodes().iterator().next();
        logger.info("Starting simulation ...");
        Quantity<Time> currentTime;
        Quantity<Time> firstCheckpoint = Quantities.getQuantity(169.0, MILLI(SECOND));
        boolean firstCheckpointPassed = false;
        Quantity<Time> secondCheckpoint = Quantities.getQuantity(351.0, MILLI(SECOND));
        // run simulation
        while ((currentTime = simulation.getElapsedTime().to(MILLI(SECOND))).getValue().doubleValue() < secondCheckpoint.getValue().doubleValue()) {
            simulation.nextEpoch();
            if (!firstCheckpointPassed && currentTime.getValue().doubleValue() > firstCheckpoint.getValue().doubleValue()) {
                logger.info("First checkpoint reached at {}.", simulation.getElapsedTime().to(SECOND));
                assertEquals(0.2958, node.getConcentration(x).getValue().doubleValue(), 1e-4);
                firstCheckpointPassed = true;
            }
        }

        // check final values
        assertEquals(0.2975, node.getConcentration(x).getValue().doubleValue(), 1e-4);
        logger.info("Second and final checkpoint (at {}) reached successfully.", simulation.getElapsedTime().to(SECOND));
    }

}