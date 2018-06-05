package de.bioforscher.singa.simulation.modules.reactions.implementations;

import de.bioforscher.singa.chemistry.descriptive.entities.Enzyme;
import de.bioforscher.singa.chemistry.descriptive.entities.SmallMolecule;
import de.bioforscher.singa.chemistry.descriptive.features.databases.chebi.ChEBIParserService;
import de.bioforscher.singa.chemistry.descriptive.features.reactions.MichaelisConstant;
import de.bioforscher.singa.chemistry.descriptive.features.reactions.RateConstant;
import de.bioforscher.singa.chemistry.descriptive.features.reactions.TurnoverNumber;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraphs;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import de.bioforscher.singa.simulation.model.newsections.CellRegion;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import de.bioforscher.singa.simulation.modules.model.SimulationExamples;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Time;

import static de.bioforscher.singa.chemistry.descriptive.features.reactions.TurnoverNumber.PER_MINUTE;
import static de.bioforscher.singa.features.model.FeatureOrigin.MANUALLY_ANNOTATED;
import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static de.bioforscher.singa.simulation.model.newsections.CellSubsection.SECTION_A;
import static de.bioforscher.singa.simulation.model.newsections.CellTopology.INNER;
import static org.junit.Assert.assertEquals;
import static tec.uom.se.unit.MetricPrefix.MILLI;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
public class ReactionTest {

    private static final Logger logger = LoggerFactory.getLogger(ReactionTest.class);

    @After
    public void cleanUp() {
        Environment.reset();
    }

    @Test
    @Ignore
    public void testEnzymeReaction() {
        // FIXME currently there are no relation to time and space when working with dynamic reactions
        // MichaelisConstant is not a scalable feature

        // SABIO Entry ID: 28851
        // Kinetic properties of fructose bisphosphate aldolase from Trypanosoma
        // brucei compared to aldolase from rabbit muscle and Staphylococcus
        // aureus.

        logger.info("Testing Biochemical Reaction Module.");
        // create simulation
        Simulation simulation = new Simulation();

        // setup graph
        AutomatonGraph graph = AutomatonGraphs.singularGraph();

        // prepare species
        SmallMolecule fp = ChEBIParserService.parse("CHEBI:18105");
        SmallMolecule gp = ChEBIParserService.parse("CHEBI:16108");
        SmallMolecule ga = ChEBIParserService.parse("CHEBI:17378");

        // prepare enzyme
        Enzyme aldolase = new Enzyme.Builder("P07752")
                .name("Fructose-bisphosphate aldolase")
                .addSubstrate(fp)
                .assignFeature(new MichaelisConstant(Quantities.getQuantity(9.0e-3, MOLE_PER_LITRE), MANUALLY_ANNOTATED))
                .assignFeature(new TurnoverNumber(Quantities.getQuantity(76.0, PER_MINUTE), MANUALLY_ANNOTATED))
                .build();

        // set concentrations
        for (AutomatonNode node : graph.getNodes()) {
            node.setCellRegion(CellRegion.CYTOSOL_A);
            node.getConcentrationContainer().set(SECTION_A, fp, 0.1);
            node.getConcentrationContainer().set(SECTION_A, aldolase, 0.2);
            node.getConcentrationContainer().set(SECTION_A, ga, 0);
            node.getConcentrationContainer().set(SECTION_A, gp, 0);
        }

        // setup reaction
        MichaelisMentenReaction.inSimulation(simulation)
                .enzyme(aldolase)
                .addSubstrate(fp)
                .addProduct(ga)
                .addProduct(gp)
                .build();

        // add graph
        simulation.setGraph(graph);

        AutomatonNode node = graph.getNode(0, 0);
        Quantity<Time> currentTime;
        Quantity<Time> firstCheckpoint = Quantities.getQuantity(100.0, MILLI(SECOND));
        boolean firstCheckpointPassed = false;
        Quantity<Time> secondCheckpoint = Quantities.getQuantity(1000.0, MILLI(SECOND));
        // run simulation
        while ((currentTime = simulation.getElapsedTime().to(MILLI(SECOND))).getValue().doubleValue() < secondCheckpoint.getValue().doubleValue()) {
            simulation.nextEpoch();
            if (!firstCheckpointPassed && currentTime.getValue().doubleValue() > firstCheckpoint.getValue().doubleValue()) {
                logger.info("First checkpoint reached at {}.", simulation.getElapsedTime().to(MILLI(SECOND)));
                assertEquals(0.043, node.getConcentrationContainer().get(SECTION_A, fp).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
                assertEquals(0.056, node.getConcentrationContainer().get(SECTION_A, gp).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
                assertEquals(0.056, node.getConcentrationContainer().get(SECTION_A, ga).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
                assertEquals(0.2, node.getConcentrationContainer().get(SECTION_A, aldolase).to(MOLE_PER_LITRE).getValue().doubleValue(), 0);
                firstCheckpointPassed = true;
            }
        }
        // check final values
        assertEquals(0.0, node.getConcentrationContainer().get(SECTION_A, fp).getValue().doubleValue(), 1e-3);
        assertEquals(0.1, node.getConcentrationContainer().get(SECTION_A, gp).getValue().doubleValue(), 1e-3);
        assertEquals(0.1, node.getConcentrationContainer().get(SECTION_A, ga).getValue().doubleValue(), 1e-3);
        assertEquals(0.2, node.getConcentrationContainer().get(SECTION_A, aldolase).getValue().doubleValue(), 0);
        logger.info("Second and final checkpoint (at {}) reached successfully.", simulation.getElapsedTime().to(MILLI(SECOND)));
    }

    @Test
    public void testEquilibriumReaction() {
        logger.info("Testing Equilibrium Reaction Module.");
        // create simulation
        Simulation simulation = new Simulation();

        // setup graph
        AutomatonGraph graph = AutomatonGraphs.singularGraph();

        // prepare species
        SmallMolecule speciesA = new SmallMolecule.Builder("Species A")
                .build();
        SmallMolecule speciesB = new SmallMolecule.Builder("Species B")
                .build();

        // set concentrations
        for (AutomatonNode node : graph.getNodes()) {
            node.getConcentrationContainer().set(SECTION_A, speciesA, 1.0);
            node.getConcentrationContainer().set(SECTION_A, speciesB, 0.0);
        }

        RateConstant forwardsRate = RateConstant.create(5).forward().firstOrder().timeUnit(SECOND).build();
        RateConstant backwardsRate = RateConstant.create(10).backward().firstOrder().timeUnit(SECOND).build();

        // setup reaction
        EquilibriumReaction.inSimulation(simulation)
                .addSubstrate(speciesA)
                .addProduct(speciesB)
                .forwardsRateConstant(forwardsRate)
                .backwardsRateConstant(backwardsRate)
                .build();

        // add graph
        simulation.setGraph(graph);

        AutomatonNode node = graph.getNode(0, 0);
        Quantity<Time> currentTime;
        Quantity<Time> firstCheckpoint = Quantities.getQuantity(25.0, MILLI(SECOND));
        boolean firstCheckpointPassed = false;
        Quantity<Time> secondCheckpoint = Quantities.getQuantity(800.0, MILLI(SECOND));
        // run simulation
        while ((currentTime = simulation.getElapsedTime().to(MILLI(SECOND))).getValue().doubleValue() < secondCheckpoint.getValue().doubleValue()) {
            simulation.nextEpoch();
            if (!firstCheckpointPassed && currentTime.getValue().doubleValue() > firstCheckpoint.getValue().doubleValue()) {
                logger.info("First checkpoint reached at {}.", simulation.getElapsedTime().to(MILLI(SECOND)));
                assertEquals(0.8901, node.getConcentrationContainer().get(SECTION_A, speciesA).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
                assertEquals(0.1108, node.getConcentrationContainer().get(SECTION_A, speciesB).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
                firstCheckpointPassed = true;
            }
        }

        // check final values
        assertEquals(0.66666, node.getConcentrationContainer().get(SECTION_A, speciesA).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-5);
        assertEquals(0.33333, node.getConcentrationContainer().get(SECTION_A, speciesB).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-5);
        logger.info("Second and final checkpoint (at {}) reached successfully.", simulation.getElapsedTime().to(MILLI(SECOND)));

    }

    @Test
    public void testNthOrderReaction() {
        logger.info("Testing Nth Order Reaction Module.");
        // create simulation
        Simulation simulation = new Simulation();

        // setup graph
        AutomatonGraph graph = AutomatonGraphs.singularGraph();

        // prepare species
        SmallMolecule dpo = ChEBIParserService.parse("CHEBI:29802");
        SmallMolecule ndo = ChEBIParserService.parse("CHEBI:33101");
        SmallMolecule oxygen = ChEBIParserService.parse("CHEBI:15379");

        for (AutomatonNode node : graph.getNodes()) {
            node.getConcentrationContainer().set(SECTION_A, dpo, 0.02);
            node.getConcentrationContainer().set(SECTION_A, ndo, 0.0);
            node.getConcentrationContainer().set(SECTION_A, oxygen, 0.0);
        }

        RateConstant rateConstant = RateConstant.create(0.07).forward().firstOrder().timeUnit(SECOND).build();

        // create reaction
        NthOrderReaction.inSimulation(simulation)
                .addSubstrate(dpo, 2)
                .addProduct(ndo, 4)
                .addProduct(oxygen)
                .rateConstant(rateConstant)
                .build();

        // add graph
        simulation.setGraph(graph);

        AutomatonNode node = graph.getNode(0, 0);
        Quantity<Time> currentTime;
        Quantity<Time> firstCheckpoint = Quantities.getQuantity(500.0, MILLI(SECOND));
        boolean firstCheckpointPassed = false;
        Quantity<Time> secondCheckpoint = Quantities.getQuantity(7000.0, MILLI(SECOND));
        // run simulation
        while ((currentTime = simulation.getElapsedTime().to(MILLI(SECOND))).getValue().doubleValue() < secondCheckpoint.getValue().doubleValue()) {
            simulation.nextEpoch();
            if (!firstCheckpointPassed && currentTime.getValue().doubleValue() > firstCheckpoint.getValue().doubleValue()) {
                logger.info("First checkpoint reached at {}.", simulation.getElapsedTime().to(MILLI(SECOND)));
                assertEquals(9E-4, node.getConcentrationContainer().get(SECTION_A, oxygen).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
                assertEquals(0.003, node.getConcentrationContainer().get(SECTION_A, ndo).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
                assertEquals(0.018, node.getConcentrationContainer().get(SECTION_A, dpo).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
                firstCheckpointPassed = true;
            }
        }

        // check final values
        assertEquals(0.006, node.getConcentrationContainer().get(SECTION_A, oxygen).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
        assertEquals(0.025, node.getConcentrationContainer().get(SECTION_A, ndo).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
        assertEquals(0.007, node.getConcentrationContainer().get(SECTION_A, dpo).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
        logger.info("Second and final checkpoint (at {}) reached successfully.", simulation.getElapsedTime().to(MILLI(SECOND)));

    }


    @Test
    @Ignore
    public void shouldPerformCalciumOscillationExample() {
        // FIXME currently there are no relation to time and space when working with dynamic reactions
        // it should be recognized which reaction rate is required and they should be transformed to the corresponding
        // scales
        logger.info("Testing Dynamic Reaction Module.");
        Simulation simulation = SimulationExamples.createSimulationFromSBML();

        SmallMolecule x = new SmallMolecule.Builder("X").build();
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
                assertEquals(0.2958, node.getConcentrationContainer().get(INNER, x).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-4);
                firstCheckpointPassed = true;
            }
        }

        // check final values
        assertEquals(0.2975, node.getConcentrationContainer().get(INNER, x).getValue().doubleValue(), 1e-4);
        logger.info("Second and final checkpoint (at {}) reached successfully.", simulation.getElapsedTime().to(SECOND));

    }

}