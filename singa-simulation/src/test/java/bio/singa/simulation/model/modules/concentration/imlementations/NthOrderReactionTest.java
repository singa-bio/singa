package bio.singa.simulation.model.modules.concentration.imlementations;

import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.chemistry.features.reactions.RateConstant;
import bio.singa.features.identifiers.ChEBIIdentifier;
import bio.singa.features.parameters.Environment;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.features.units.UnitRegistry;
import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.features.DefaultFeatureSources;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.agents.pointlike.VesicleLayer;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.ReactionBuilder;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.simulation.Simulation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import static bio.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static bio.singa.simulation.model.sections.CellRegions.EXTRACELLULAR_REGION;
import static bio.singa.simulation.model.sections.CellTopology.MEMBRANE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static tec.uom.se.unit.MetricPrefix.*;
import static tec.uom.se.unit.Units.METRE;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
class NthOrderReactionTest {

    @BeforeAll
    static void initialize() {
        UnitRegistry.reinitialize();
    }

    @AfterEach
    void cleanUp() {
        UnitRegistry.reinitialize();
    }

    @Test
    @DisplayName("example reaction - with stoichiometry")
    void testNthOrderReaction() {
        // create simulation
        Simulation simulation = new Simulation();

        // setup graph
        AutomatonGraph graph = AutomatonGraphs.singularGraph();

        // prepare species
        SmallMolecule dpo = SmallMolecule.create("DPO")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:29802"))
                .build();

        SmallMolecule ndo = SmallMolecule.create("NDO")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:33101"))
                .build();

        SmallMolecule oxygen = SmallMolecule.create("O")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:15379"))
                .build();

        CellSubsection subsection = EXTRACELLULAR_REGION.getInnerSubsection();
        for (AutomatonNode node : graph.getNodes()) {
            node.getConcentrationContainer().initialize(subsection, dpo, Quantities.getQuantity(0.02, MOLE_PER_LITRE));
        }

        RateConstant rateConstant = RateConstant.create(0.07)
                .forward().firstOrder()
                .timeUnit(SECOND)
                .build();

        // create reaction
        ReactionBuilder.dynamicReactants(simulation)
                .addSubstrate(dpo, 2)
                .addProduct(ndo, 4)
                .addProduct(oxygen)
                .irreversible()
                .rate(rateConstant)
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
                assertEquals(9E-4, UnitRegistry.concentration(node.getConcentrationContainer().get(subsection, oxygen)).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
                assertEquals(0.003, UnitRegistry.concentration(node.getConcentrationContainer().get(subsection, ndo)).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
                assertEquals(0.018, UnitRegistry.concentration(node.getConcentrationContainer().get(subsection, dpo)).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
                firstCheckpointPassed = true;
            }
        }

        // check final values
        assertEquals(0.006, UnitRegistry.concentration(node.getConcentrationContainer().get(subsection, oxygen)).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
        assertEquals(0.025, UnitRegistry.concentration(node.getConcentrationContainer().get(subsection, ndo)).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
        assertEquals(0.007, UnitRegistry.concentration(node.getConcentrationContainer().get(subsection, dpo)).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
    }

    @Test
    @DisplayName("rate independence from space scale and approaching 0")
    void testReactionSpeedScaling() {
        Environment.reset();
        // create simulation
        double simulationExtend = 800;
        int nodesHorizontal = 1;
        int nodesVertical = 1;
        int numberOfMolecules = 60;

        Simulation simulation = new Simulation();
        simulation.setMaximalTimeStep(Quantities.getQuantity(0.5, SECOND));

        ComparableQuantity<Length> systemExtend = Quantities.getQuantity(2, MICRO(METRE));
        Environment.setSystemExtend(systemExtend);
        Environment.setSimulationExtend(simulationExtend);
        Environment.setNodeSpacingToDiameter(systemExtend, nodesHorizontal);
        Rectangle rectangle = new Rectangle(simulationExtend, simulationExtend);

        simulation.setSimulationRegion(rectangle);
        AutomatonGraph graph = AutomatonGraphs.createRectangularAutomatonGraph(nodesHorizontal, nodesVertical);
        simulation.setGraph(graph);

        // prepare species
        SmallMolecule sm = SmallMolecule.create("A").build();

        VesicleLayer layer = new VesicleLayer(simulation);
        Vesicle vesicle = new Vesicle(new Vector2D(400, 400.0), Quantities.getQuantity(50, NANO(METRE)));
        vesicle.getConcentrationContainer().set(MEMBRANE, sm, MolarConcentration.moleculesToConcentration(numberOfMolecules));
        layer.addVesicle(vesicle);
        simulation.setVesicleLayer(layer);

        RateConstant rateConstant = RateConstant.create(MolarConcentration.moleculesToConcentration(numberOfMolecules) / 11.0)
                .forward().zeroOrder()
                .concentrationUnit(UnitRegistry.getConcentrationUnit())
                .timeUnit(SECOND)
                .evidence(DefaultFeatureSources.EHRLICH2004)
                .build();

        ReactionBuilder.staticReactants(simulation)
                .addSubstrate(sm, MEMBRANE)
                .irreversible()
                .rate(rateConstant)
                .build();

        Quantity<Dimensionless> molecules = MolarConcentration.concentrationToMolecules(vesicle.getConcentrationContainer().get(MEMBRANE, sm));
        assertEquals(60, molecules.getValue().intValue());
        while (simulation.getElapsedTime().isLessThanOrEqualTo(Quantities.getQuantity(11, SECOND))) {
            simulation.nextEpoch();
            molecules = MolarConcentration.concentrationToMolecules(vesicle.getConcentrationContainer().get(MEMBRANE, sm));
        }
        assertEquals(0.0, molecules.getValue().doubleValue());
    }

}