package bio.singa.simulation.model.modules.concentration.imlementations;

import bio.singa.chemistry.entities.*;
import bio.singa.chemistry.features.reactions.RateConstant;
import bio.singa.features.identifiers.ChEBIIdentifier;
import bio.singa.features.identifiers.UniProtIdentifier;
import bio.singa.features.model.Evidence;
import bio.singa.features.parameters.Environment;
import bio.singa.features.units.UnitRegistry;
import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.agents.pointlike.VesicleLayer;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.sections.ConcentrationContainer;
import bio.singa.simulation.model.simulation.Simulation;
import bio.singa.structure.features.molarmass.MolarMass;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import static bio.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static bio.singa.simulation.model.sections.CellSubsection.SECTION_A;
import static bio.singa.simulation.model.sections.CellTopology.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tec.uom.se.unit.MetricPrefix.*;
import static tec.uom.se.unit.Units.*;

/**
 * @author cl
 */
class ComplexBuildingReactionTest {

    @BeforeAll
    static void initialize() {
        UnitRegistry.reinitialize();
    }

    @AfterEach
    void cleanUp() {
        UnitRegistry.reinitialize();
    }

    @Test
    @DisplayName("complex building reaction - minimal setup")
    void minimalSetUpTest() {
        // the rate constants
        RateConstant forwardRate = RateConstant.create(1).forward().secondOrder().concentrationUnit(MOLE_PER_LITRE).timeUnit(SECOND).build();
        RateConstant backwardRate = RateConstant.create(1).backward().firstOrder().timeUnit(SECOND).build();

        // the ligand
        ChemicalEntity bindee = new SmallMolecule.Builder("bindee")
                .name("bindee")
                .build();

        // the receptor
        Protein binder = new Protein.Builder("binder")
                .name("binder")
                .build();

        // create simulation
        Simulation simulation = new Simulation();

        // create and add module
        ComplexBuildingReaction binding = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("binding")
                .of(bindee, forwardRate)
                .in(OUTER)
                .by(binder, backwardRate)
                .to(MEMBRANE)
                .build();
        ComplexedChemicalEntity complex = binding.getComplex();

        // setup graph
        final AutomatonGraph automatonGraph = AutomatonGraphs.singularGraph();
        simulation.setGraph(automatonGraph);
        // set concentrations
        AutomatonNode membraneNode = automatonGraph.getNode(0, 0);
        membraneNode.setCellRegion(CellRegion.MEMBRANE);
        membraneNode.getConcentrationContainer().initialize(OUTER, bindee, Quantities.getQuantity(1.0, MOLE_PER_LITRE));
        membraneNode.getConcentrationContainer().initialize(MEMBRANE, binder, Quantities.getQuantity(1.0, MOLE_PER_LITRE));
        membraneNode.getConcentrationContainer().initialize(MEMBRANE, complex, Quantities.getQuantity(1.0, MOLE_PER_LITRE));

        // forward and backward reactions should cancel each other out
        for (int i = 0; i < 10; i++) {
            ConcentrationContainer container = membraneNode.getConcentrationContainer();

            assertEquals(0.0, container.get(INNER, bindee));
            assertEquals(0.0, container.get(INNER, binder));
            assertEquals(0.0, container.get(INNER, complex));

            assertEquals(0.0, container.get(MEMBRANE, bindee));
            assertEquals(1.0, UnitRegistry.concentration(container.get(MEMBRANE, binder)).to(MOLE_PER_LITRE).getValue().doubleValue());
            assertEquals(1.0, UnitRegistry.concentration(container.get(MEMBRANE, complex)).to(MOLE_PER_LITRE).getValue().doubleValue());

            assertEquals(1.0, UnitRegistry.concentration(container.get(OUTER, bindee)).to(MOLE_PER_LITRE).getValue().doubleValue());
            assertEquals(0, container.get(OUTER, binder));
            assertEquals(0, container.get(OUTER, complex));

            simulation.nextEpoch();
        }
    }

    @Test
    @DisplayName("complex building reaction - monovalent receptor binding")
    void testPrazosinExample() {
        UnitRegistry.setSpace(Quantities.getQuantity(1.0, MILLI(METRE)));

        // see Receptors (Lauffenburger) p. 30
        // prazosin, CHEBI:8364
        ChemicalEntity ligand = new SmallMolecule.Builder("ligand")
                .name("prazosin")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:8364"))
                .build();

        // alpha-1 adrenergic receptor, P35348
        Receptor receptor = new Receptor.Builder("receptor")
                .name("alpha-1 adrenergic receptor")
                .additionalIdentifier(new UniProtIdentifier("P35348"))
                .build();

        // create simulation
        Simulation simulation = new Simulation();

        // setup graph
        final AutomatonGraph automatonGraph = AutomatonGraphs.singularGraph();
        simulation.setGraph(automatonGraph);
        // concentrations
        AutomatonNode membraneNode = automatonGraph.getNode(0, 0);
        membraneNode.setCellRegion(CellRegion.MEMBRANE);
        membraneNode.getConcentrationContainer().initialize(SECTION_A, ligand, UnitRegistry.concentration(0.1, MOLE_PER_LITRE));
        membraneNode.getConcentrationContainer().initialize(CellSubsection.MEMBRANE, receptor, UnitRegistry.concentration(0.1, MOLE_PER_LITRE));

        // the corresponding rate constants
        RateConstant forwardsRate = RateConstant.create(2.4e8)
                .forward().secondOrder()
                .concentrationUnit(MOLE_PER_LITRE)
                .timeUnit(MINUTE)
                .build();

        RateConstant backwardsRate = RateConstant.create(0.018)
                .backward().firstOrder()
                .timeUnit(MINUTE)
                .build();

        // create and add module
        ComplexBuildingReaction reaction = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("binding reaction")
                .of(ligand, forwardsRate)
                .in(INNER)
                .by(receptor, backwardsRate)
                .to(MEMBRANE)
                .build();
        ComplexedChemicalEntity complex = reaction.getComplex();

        // checkpoints
        Quantity<Time> currentTime;
        Quantity<Time> firstCheckpoint = Quantities.getQuantity(0.05, MILLI(SECOND));
        boolean firstCheckpointPassed = false;
        Quantity<Time> secondCheckpoint = Quantities.getQuantity(2.0, MILLI(SECOND));
        // run simulation
        while ((currentTime = simulation.getElapsedTime().to(MILLI(SECOND))).getValue().doubleValue() < secondCheckpoint.getValue().doubleValue()) {
            simulation.nextEpoch();
            if (!firstCheckpointPassed && currentTime.getValue().doubleValue() > firstCheckpoint.getValue().doubleValue()) {
                assertEquals(0.00476, UnitRegistry.concentration(membraneNode.getConcentrationContainer().get(CellSubsection.MEMBRANE, receptor)).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
                assertEquals(0.00476, UnitRegistry.concentration(membraneNode.getConcentrationContainer().get(INNER, ligand)).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
                assertEquals(0.09523, UnitRegistry.concentration(membraneNode.getConcentrationContainer().get(CellSubsection.MEMBRANE, complex)).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
                firstCheckpointPassed = true;
            }
        }

        // check final values
        assertEquals(0.0001, UnitRegistry.concentration(membraneNode.getConcentrationContainer().get(CellSubsection.MEMBRANE, receptor)).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
        assertEquals(0.0001, UnitRegistry.concentration(membraneNode.getConcentrationContainer().get(INNER, ligand)).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
        assertEquals(0.0998, UnitRegistry.concentration(membraneNode.getConcentrationContainer().get(CellSubsection.MEMBRANE, complex)).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
    }

    @Test
    @DisplayName("complex building reaction - simple section changing binding")
    void testMembraneAbsorption() {
        // the rate constants
        RateConstant forwardRate = RateConstant.create(1.0e6)
                .forward().secondOrder()
                .concentrationUnit(MOLE_PER_LITRE)
                .timeUnit(MINUTE)
                .build();

        RateConstant backwardRate = RateConstant.create(0.01)
                .backward().firstOrder()
                .timeUnit(MINUTE)
                .build();

        // the ligand
        ChemicalEntity bindee = new SmallMolecule.Builder("bindee")
                .name("bindee")
                .assignFeature(new MolarMass(10, Evidence.NO_EVIDENCE))
                .build();

        // the receptor
        Protein binder = new Protein.Builder("binder")
                .name("binder")
                .assignFeature(new MolarMass(100, Evidence.NO_EVIDENCE))
                .build();

        // create simulation
        Simulation simulation = new Simulation();

        // create and add module
        ComplexBuildingReaction binding = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("binding")
                .of(bindee, forwardRate)
                .in(OUTER)
                .by(binder, backwardRate)
                .to(MEMBRANE)
                .build();

        // setup graph
        final AutomatonGraph automatonGraph = AutomatonGraphs.singularGraph();
        simulation.setGraph(automatonGraph);
        // concentrations
        AutomatonNode membraneNode = automatonGraph.getNode(0, 0);
        membraneNode.setCellRegion(CellRegion.MEMBRANE);
        membraneNode.getConcentrationContainer().set(OUTER, bindee, 1.0);
        membraneNode.getConcentrationContainer().set(MEMBRANE, binder, 0.1);
        membraneNode.getConcentrationContainer().set(MEMBRANE, binding.getComplex(), 0.0);

        double previousConcentration = 0.0;
        for (int i = 0; i < 10; i++) {
            simulation.nextEpoch();
            double currentConcentration = membraneNode.getConcentrationContainer().get(CellSubsection.MEMBRANE, binding.getComplex());
            assertTrue(currentConcentration > previousConcentration);
            previousConcentration = currentConcentration;
        }
    }

    @Test
    @DisplayName("complex building reaction - section changing binding with concurrent inside and outside reactions")
    void shouldReactInsideAndOutside() {
        // the rate constants
        RateConstant innerForwardsRateConstant = RateConstant.create(1.0e6).forward().secondOrder().concentrationUnit(MOLE_PER_LITRE).timeUnit(MINUTE).build();
        RateConstant innerBackwardsRateConstant = RateConstant.create(0.01).backward().firstOrder().timeUnit(MINUTE).build();

        // the rate constants
        RateConstant outerForwardsRateConstant = RateConstant.create(1.0e6).forward().secondOrder().concentrationUnit(MOLE_PER_LITRE).timeUnit(MINUTE).build();
        RateConstant outerBackwardsRateConstant = RateConstant.create(0.01).backward().firstOrder().timeUnit(MINUTE).build();

        // the inner ligand
        ChemicalEntity innerBindee = new SmallMolecule.Builder("inner bindee")
                .name("inner bindee")
                .assignFeature(new MolarMass(10, Evidence.NO_EVIDENCE))
                .build();

        // the outer ligand
        ChemicalEntity outerBindee = new SmallMolecule.Builder("outer bindee")
                .name("outer bindee")
                .assignFeature(new MolarMass(10, Evidence.NO_EVIDENCE))
                .build();

        // the receptor
        Protein binder = new Protein.Builder("binder")
                .name("binder")
                .assignFeature(new MolarMass(100, Evidence.NO_EVIDENCE))
                .build();

        // create simulation
        Simulation simulation = new Simulation();

        // create and add inner module
        ComplexBuildingReaction innerBinding = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("Inner Binding")
                .of(innerBindee, innerForwardsRateConstant)
                .in(INNER)
                .by(binder, innerBackwardsRateConstant)
                .to(MEMBRANE)
                .build();

        // create and add outer module
        ComplexBuildingReaction outerBinding = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("Outer Binding")
                .of(outerBindee, outerForwardsRateConstant)
                .in(OUTER)
                .by(binder, outerBackwardsRateConstant)
                .to(MEMBRANE)
                .build();

        // setup graph
        final AutomatonGraph automatonGraph = AutomatonGraphs.singularGraph();
        simulation.setGraph(automatonGraph);
        // concentrations
        AutomatonNode membraneNode = automatonGraph.getNode(0, 0);
        membraneNode.setCellRegion(CellRegion.MEMBRANE);

        membraneNode.getConcentrationContainer().initialize(INNER, innerBindee, Quantities.getQuantity(0.1, MOLE_PER_LITRE));
        membraneNode.getConcentrationContainer().initialize(OUTER, outerBindee, Quantities.getQuantity(0.1, MOLE_PER_LITRE));
        membraneNode.getConcentrationContainer().initialize(MEMBRANE, binder, Quantities.getQuantity(0.1, MOLE_PER_LITRE));

        double previousInnerConcentration = 0.0;
        double previousOuterConcentration = 0.0;
        for (int i = 0; i < 10; i++) {
            simulation.nextEpoch();
            // inner assertions
            double currentInnerConcentration = membraneNode.getConcentrationContainer().get(MEMBRANE, innerBinding.getComplex());
            assertTrue(currentInnerConcentration > previousInnerConcentration);
            previousInnerConcentration = currentInnerConcentration;
            // outer assertions
            double currentOuterConcentration = membraneNode.getConcentrationContainer().get(MEMBRANE, outerBinding.getComplex());
            assertTrue(currentOuterConcentration > previousOuterConcentration);
            previousOuterConcentration = currentOuterConcentration;
        }
    }

    @Test
    @DisplayName("complex building reaction - section changing binding with fully contained vesicle")
    void testComplexBuildingWithVesicle() {
        double simulationExtend = 150;
        int nodesHorizontal = 3;
        int nodesVertical = 3;

        Rectangle rectangle = new Rectangle(simulationExtend, simulationExtend);
        Simulation simulation = new Simulation();
        simulation.setSimulationRegion(rectangle);

        // setup scaling
        ComparableQuantity<Length> systemExtend = Quantities.getQuantity(1, MICRO(METRE));
        Environment.setSystemExtend(systemExtend);
        Environment.setSimulationExtend(simulationExtend);
        Environment.setNodeSpacingToDiameter(systemExtend, nodesHorizontal);

        // setup graph and assign regions
        AutomatonGraph graph = AutomatonGraphs.createRectangularAutomatonGraph(nodesHorizontal, nodesVertical);
        simulation.setGraph(graph);

        // the rate constants
        RateConstant forwardRate = RateConstant.create(1.0e6)
                .forward().secondOrder()
                .concentrationUnit(MOLE_PER_LITRE)
                .timeUnit(MINUTE)
                .build();

        RateConstant backwardRate = RateConstant.create(0.01)
                .backward().firstOrder()
                .timeUnit(MINUTE)
                .build();

        // the ligand
        ChemicalEntity bindee = new SmallMolecule.Builder("bindee")
                .name("bindee")
                .assignFeature(new MolarMass(10, Evidence.NO_EVIDENCE))
                .build();

        // the receptor
        Protein binder = new Protein.Builder("binder")
                .name("binder")
                .assignFeature(new MolarMass(100, Evidence.NO_EVIDENCE))
                .build();

        // create and add module
        ComplexBuildingReaction binding = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("binding")
                .of(bindee, forwardRate)
                .in(INNER)
                .by(binder, backwardRate)
                .to(MEMBRANE)
                .build();

        // initialize vesicle layer
        VesicleLayer vesicleLayer = new VesicleLayer(simulation);
        simulation.setVesicleLayer(vesicleLayer);

        ComparableQuantity<Length> radius = Quantities.getQuantity(20, NANO(METRE));

        // vesicle contained
        Vesicle vesicle = new Vesicle("Vesicle", new Vector2D(25.0, 25.0), radius);
        vesicle.getConcentrationContainer().initialize(MEMBRANE, binder, Quantities.getQuantity(0.1, MOLE_PER_LITRE));
        vesicle.getConcentrationContainer().initialize(MEMBRANE, binding.getComplex(), Quantities.getQuantity(0.0, MOLE_PER_LITRE));
        vesicleLayer.addVesicle(vesicle);

        // concentrations
        AutomatonNode node = graph.getNode(0, 0);
        node.getConcentrationContainer().initialize(INNER, bindee, Quantities.getQuantity(1.0, MOLE_PER_LITRE));

        double previousNodeConcentration = 1.0;
        double previousVesicleConcentration = 0.0;
        for (int i = 0; i < 10; i++) {
            simulation.nextEpoch();
            // node assertion
            double currentNodeConcentration = node.getConcentrationContainer().get(INNER, bindee);
            assertTrue(currentNodeConcentration < previousNodeConcentration);
            previousNodeConcentration = currentNodeConcentration;
            // vesicle assertion
            double currentVesicleConcentration = vesicle.getConcentrationContainer().get(MEMBRANE, binding.getComplex());
            assertTrue(currentVesicleConcentration > previousVesicleConcentration);
            previousVesicleConcentration = currentVesicleConcentration;
        }
    }


    @Test
    @DisplayName("complex building reaction - section changing binding with partially contained vesicle")
    void testComplexBuildingWithPartialVesicle() {
        double simulationExtend = 150;
        int nodesHorizontal = 3;
        int nodesVertical = 3;

        Rectangle rectangle = new Rectangle(simulationExtend, simulationExtend);
        Simulation simulation = new Simulation();
        simulation.setSimulationRegion(rectangle);

        // setup scaling
        ComparableQuantity<Length> systemExtend = Quantities.getQuantity(1, MICRO(METRE));
        Environment.setSystemExtend(systemExtend);
        Environment.setSimulationExtend(simulationExtend);
        Environment.setNodeSpacingToDiameter(systemExtend, nodesHorizontal);

        // setup graph and assign regions
        AutomatonGraph graph = AutomatonGraphs.createRectangularAutomatonGraph(nodesHorizontal, nodesVertical);
        simulation.setGraph(graph);

        // the rate constants
        RateConstant forwardRate = RateConstant.create(1.0e6)
                .forward().secondOrder()
                .concentrationUnit(MOLE_PER_LITRE)
                .timeUnit(MINUTE)
                .build();

        RateConstant backwardRate = RateConstant.create(0.01)
                .backward().firstOrder()
                .timeUnit(MINUTE)
                .build();

        // the ligand
        ChemicalEntity bindee = new SmallMolecule.Builder("bindee")
                .name("bindee")
                .assignFeature(new MolarMass(10, Evidence.NO_EVIDENCE))
                .build();

        // the receptor
        Protein binder = new Protein.Builder("binder")
                .name("binder")
                .assignFeature(new MolarMass(100, Evidence.NO_EVIDENCE))
                .build();

        // create and add module
        ComplexBuildingReaction binding = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("binding")
                .of(bindee, forwardRate)
                .in(INNER)
                .by(binder, backwardRate)
                .to(MEMBRANE)
                .build();

        // initialize vesicle layer
        VesicleLayer vesicleLayer = new VesicleLayer(simulation);
        simulation.setVesicleLayer(vesicleLayer);

        ComparableQuantity<Length> radius = Quantities.getQuantity(20, NANO(METRE));

        // vesicle contained
        Vesicle vesicle = new Vesicle("Vesicle", new Vector2D(25.0, 50.0), radius);
        vesicle.getConcentrationContainer().initialize(MEMBRANE, binder, Quantities.getQuantity(0.1, MOLE_PER_LITRE));
        vesicle.getConcentrationContainer().initialize(MEMBRANE, binding.getComplex(), Quantities.getQuantity(0.0, MOLE_PER_LITRE));
        vesicleLayer.addVesicle(vesicle);

        // concentrations
        AutomatonNode first = graph.getNode(0, 0);
        first.getConcentrationContainer().initialize(INNER, bindee, Quantities.getQuantity(1.0, MOLE_PER_LITRE));
        // concentrations
        AutomatonNode second = graph.getNode(0, 1);
        second.getConcentrationContainer().initialize(INNER, bindee, Quantities.getQuantity(0.5, MOLE_PER_LITRE));

        double previousFirstConcentration = 1.0;
        double previousSecondConcentration = 0.5;
        double previousVesicleConcentration = 0.0;
        for (int i = 0; i < 10; i++) {
            simulation.nextEpoch();
            // first node assertions
            double currentFirstConcentration = first.getConcentrationContainer().get(INNER, bindee);
            assertTrue(currentFirstConcentration < previousFirstConcentration);
            previousFirstConcentration = currentFirstConcentration;
            // first node assertions
            double currentSecondConcentration = second.getConcentrationContainer().get(INNER, bindee);
            assertTrue(currentSecondConcentration < previousSecondConcentration);
            previousSecondConcentration = currentSecondConcentration;
            // outer assertions
            double currentVesicleConcentration = vesicle.getConcentrationContainer().get(MEMBRANE, binding.getComplex());
            assertTrue(currentVesicleConcentration > previousVesicleConcentration);
            previousVesicleConcentration = currentVesicleConcentration;
        }
    }

}
