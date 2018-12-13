package bio.singa.simulation.model.modules.concentration.imlementations;

import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.chemistry.features.reactions.RateConstant;
import bio.singa.features.units.UnitRegistry;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.simulation.Simulation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Time;

import static bio.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static bio.singa.simulation.model.sections.CellRegions.EXTRACELLULAR_REGION;
import static org.junit.jupiter.api.Assertions.*;
import static tec.uom.se.unit.MetricPrefix.MILLI;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
class ReversibleReactionTest {

    @BeforeAll
    static void initialize() {
        UnitRegistry.reinitialize();
    }

    @AfterEach
    void cleanUp() {
        UnitRegistry.reinitialize();
    }

    @Test
    @DisplayName("example reaction - approaching 2/3 product")
    void testReversibleReaction() {
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
        CellSubsection subsection = EXTRACELLULAR_REGION.getInnerSubsection();
        for (AutomatonNode node : graph.getNodes()) {
            node.getConcentrationContainer().initialize(subsection, speciesA, Quantities.getQuantity(1.0, MOLE_PER_LITRE));
        }

        RateConstant forwardsRate = RateConstant.create(5)
                .forward().firstOrder()
                .timeUnit(SECOND)
                .build();

        RateConstant backwardsRate = RateConstant.create(10)
                .backward().firstOrder()
                .timeUnit(SECOND)
                .build();

        // setup reaction
        ReversibleReaction.inSimulation(simulation)
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
                assertEquals(0.8901, UnitRegistry.concentration(node.getConcentrationContainer().get(subsection, speciesA)).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
                assertEquals(0.1108, UnitRegistry.concentration(node.getConcentrationContainer().get(subsection, speciesB)).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
                firstCheckpointPassed = true;
            }
        }

        // check final values
        assertEquals(0.66666, UnitRegistry.concentration(node.getConcentrationContainer().get(subsection, speciesA)).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-5);
        assertEquals(0.33333, UnitRegistry.concentration(node.getConcentrationContainer().get(subsection, speciesB)).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-5);

    }

}