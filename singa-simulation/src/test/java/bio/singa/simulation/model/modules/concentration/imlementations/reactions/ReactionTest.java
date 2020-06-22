package bio.singa.simulation.model.modules.concentration.imlementations.reactions;

import bio.singa.chemistry.features.reactions.MichaelisConstant;
import bio.singa.chemistry.features.reactions.TurnoverNumber;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.simulation.entities.*;
import bio.singa.simulation.entities.simple.Protein;
import bio.singa.simulation.entities.simple.SmallMolecule;
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
import bio.singa.simulation.model.concentrations.ConcentrationBuilder;
import bio.singa.simulation.model.concentrations.TimedCondition;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.Reactant;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.ReactantRole;
import bio.singa.simulation.model.sections.*;
import bio.singa.simulation.model.simulation.Simulation;
import bio.singa.simulation.model.simulation.error.TimeStepManager;
import bio.singa.structure.features.molarmass.MolarMass;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.ProductUnit;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import static bio.singa.simulation.model.sections.CellRegions.EXTRACELLULAR_REGION;
import static bio.singa.simulation.reactions.conditions.CandidateConditionBuilder.*;
import static bio.singa.simulation.reactions.reactors.ReactionChainBuilder.add;
import static bio.singa.simulation.reactions.reactors.ReactionChainBuilder.bind;
import static bio.singa.features.units.UnitProvider.*;
import static bio.singa.simulation.model.sections.CellRegions.CELL_OUTER_MEMBRANE_REGION;
import static bio.singa.simulation.model.sections.CellRegions.CYTOPLASM_REGION;
import static bio.singa.simulation.model.sections.CellSubsections.*;
import static bio.singa.simulation.model.sections.CellTopology.INNER;
import static bio.singa.simulation.model.sections.CellTopology.MEMBRANE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.units.indriya.AbstractUnit.ONE;
import static tech.units.indriya.unit.MetricPrefix.*;
import static tech.units.indriya.unit.Units.*;

/**
 * @author cl
 */
class ReactionTest {

    @BeforeAll
    static void initialize() {
        UnitRegistry.reinitialize();
        Environment.reset();
    }

    @AfterEach
    void cleanUp() {
        UnitRegistry.reinitialize();
        Environment.reset();
    }

    @Test
    @DisplayName("irreversible reaction, static reactants, non-membrane")
    void irreversibleReactionCytoplasm() {

        Simulation simulation = new Simulation();

        AutomatonGraph automatonGraph = AutomatonGraphs.singularGraph(CYTOPLASM_REGION);
        simulation.setGraph(automatonGraph);

        ChemicalEntity a = SimpleEntity.create("A").build();
        ChemicalEntity b = SimpleEntity.create("B").build();
        ChemicalEntity c = SimpleEntity.create("C").build();

        // set concentrations
        ConcentrationBuilder.create(simulation)
                .entity(a)
                .subsection(CYTOPLASM)
                .concentrationValue(1.0)
                .microMolar()
                .build();

        // set concentration
        ConcentrationBuilder.create(simulation)
                .entity(b)
                .subsection(CYTOPLASM)
                .concentrationValue(1.0)
                .microMolar()
                .build();

        RateConstant rate = RateConstant.create(1.0)
                .forward().secondOrder()
                .concentrationUnit(MICRO_MOLE_PER_LITRE)
                .timeUnit(SECOND)
                .build();

        ReactionBuilder.staticReactants(simulation)
                .addSubstrate(a)
                .addSubstrate(b)
                .addProduct(c)
                .irreversible()
                .rate(rate)
                .identifier("irreversible test reaction")
                .build();

        for (int i = 0; i < 100; i++) {
            simulation.nextEpoch();
            // System.out.println(simulation.getGraph().getNode(0, 0).getConcentrationContainer().get(CYTOPLASM, a) + " " + simulation.getGraph().getNode(0, 0).getConcentrationContainer().get(CYTOPLASM, b) + " " + simulation.getGraph().getNode(0, 0).getConcentrationContainer().get(CYTOPLASM, c));
        }

    }

    @Test
    @DisplayName("irreversible reaction, static reactants, membrane")
    void irreversibleReactionMembrane() {
        Simulation simulation = new Simulation();

        AutomatonGraph automatonGraph = AutomatonGraphs.singularGraph(CELL_OUTER_MEMBRANE_REGION);
        simulation.setGraph(automatonGraph);

        ChemicalEntity a = SimpleEntity.create("A").build();
        ChemicalEntity b = SimpleEntity.create("B").build();
        ChemicalEntity c = SimpleEntity.create("C").build();

        ConcentrationBuilder.create(simulation)
                .entity(a)
                .subsection(CYTOPLASM)
                .concentrationValue(1.0)
                .microMolar()
                .build();

        ConcentrationBuilder.create(simulation)
                .entity(b)
                .subsection(CELL_OUTER_MEMBRANE)
                .concentrationValue(1.0)
                .microMolar()
                .build();

        RateConstant rate = RateConstant.create(1.0)
                .forward().secondOrder()
                .concentrationUnit(MICRO_MOLE_PER_LITRE)
                .timeUnit(SECOND)
                .build();

        ReactionBuilder.staticReactants(simulation)
                .addSubstrate(a)
                .addSubstrate(b, MEMBRANE)
                .addProduct(c, MEMBRANE)
                .irreversible()
                .rate(rate)
                .identifier("irreversible test reaction")
                .build();

        for (int i = 0; i < 100; i++) {
            simulation.nextEpoch();
            // System.out.println(simulation.getGraph().getNode(0, 0).getConcentrationContainer().get(CYTOPLASM, a) + " " + simulation.getGraph().getNode(0, 0).getConcentrationContainer().get(MEMBRANE, b) + " " + simulation.getGraph().getNode(0, 0).getConcentrationContainer().get(MEMBRANE, c));
        }
    }

    @Test
    @DisplayName("irreversible reaction, static reactants, non-membrane, stochiomenty")
    void irreversibleReactionStoichiometry() {
        Simulation simulation = new Simulation();

        AutomatonGraph automatonGraph = AutomatonGraphs.singularGraph(CYTOPLASM_REGION);
        simulation.setGraph(automatonGraph);

        ChemicalEntity a = SimpleEntity.create("A").build();
        ChemicalEntity b = SimpleEntity.create("B").build();
        ChemicalEntity c = SimpleEntity.create("C").build();

        ConcentrationBuilder.create(simulation)
                .entity(a)
                .subsection(CYTOPLASM)
                .concentrationValue(1.0)
                .microMolar()
                .build();

        ConcentrationBuilder.create(simulation)
                .entity(b)
                .subsection(CYTOPLASM)
                .concentrationValue(1.0)
                .microMolar()
                .build();

        RateConstant rate = RateConstant.create(1.0)
                .forward().secondOrder()
                .concentrationUnit(MICRO_MOLE_PER_LITRE)
                .timeUnit(SECOND)
                .build();

        ReactionBuilder.staticReactants(simulation)
                .addSubstrate(a)
                .addSubstrate(b, 2)
                .addProduct(c)
                .irreversible()
                .rate(rate)
                .identifier("irreversible test reaction")
                .build();

        for (int i = 0; i < 100; i++) {
            simulation.nextEpoch();
            // System.out.println(simulation.getGraph().getNode(0, 0).getConcentrationContainer().get(CYTOPLASM, a) + " " + simulation.getGraph().getNode(0, 0).getConcentrationContainer().get(CYTOPLASM, b) + " " + simulation.getGraph().getNode(0, 0).getConcentrationContainer().get(CYTOPLASM, c));
        }
    }

    @Test
    @DisplayName("reversible reaction, static reactants, non-membrane")
    void testReversibleReaction() {
        // create simulation
        Simulation simulation = new Simulation();

        // setup graph
        AutomatonGraph graph = AutomatonGraphs.singularGraph();

        // prepare species
        ChemicalEntity speciesA = SimpleEntity.create("A").build();
        ChemicalEntity speciesB = SimpleEntity.create("B").build();

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
        ReactionBuilder.staticReactants(simulation)
                .addSubstrate(speciesA)
                .addProduct(speciesB)
                .reversible()
                .forwardReactionRate(forwardsRate)
                .backwardReactionRate(backwardsRate)
                .build();

        // add graph
        simulation.setGraph(graph);

        AutomatonNode node = graph.getNode(0, 0);
        Quantity<Time> currentTime;
        Quantity<Time> firstCheckpoint = Quantities.getQuantity(25.0, MILLI(SECOND));
        boolean firstCheckpointPassed = false;
        Quantity<Time> secondCheckpoint = Quantities.getQuantity(800.0, MILLI(SECOND));
        // run simulation
        while ((currentTime = TimeStepManager.getElapsedTime().to(MILLI(SECOND))).getValue().doubleValue() < secondCheckpoint.getValue().doubleValue()) {
            simulation.nextEpoch();
            if (!firstCheckpointPassed && currentTime.getValue().doubleValue() > firstCheckpoint.getValue().doubleValue()) {
                assertEquals(0.8906, UnitRegistry.concentration(node.getConcentrationContainer().get(subsection, speciesA)).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
                assertEquals(0.1093, UnitRegistry.concentration(node.getConcentrationContainer().get(subsection, speciesB)).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
                firstCheckpointPassed = true;
            }
        }

        // check final values
        assertEquals(0.66666, UnitRegistry.concentration(node.getConcentrationContainer().get(subsection, speciesA)).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-5);
        assertEquals(0.33333, UnitRegistry.concentration(node.getConcentrationContainer().get(subsection, speciesB)).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-5);

    }

    @Test
    @DisplayName("reversible reaction, static reactants, real example")
    void reversibleReactionMembrane() {
        UnitRegistry.setSpace(Quantities.getQuantity(1.0, MILLI(METRE)));

        // see Receptors (Lauffenburger) p. 30
        // prazosin, CHEBI:8364
        ChemicalEntity ligand = SimpleEntity.create("ligand")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:8364"))
                .small()
                .build();

        // alpha-1 adrenergic receptor, P35348
        ChemicalEntity receptor = SimpleEntity.create("receptor")
                .additionalIdentifier(new UniProtIdentifier("P35348"))
                .build();

        ComplexEntity complex = ComplexEntity.from(receptor, ligand);

        // create simulation
        Simulation simulation = new Simulation();

        // setup graph
        final AutomatonGraph automatonGraph = AutomatonGraphs.singularGraph();
        simulation.setGraph(automatonGraph);
        // concentrations
        AutomatonNode membraneNode = automatonGraph.getNode(0, 0);
        membraneNode.setCellRegion(CELL_OUTER_MEMBRANE_REGION);

        ConcentrationBuilder.create(simulation)
                .entity(ligand)
                .subsection(EXTRACELLULAR_REGION)
                .concentrationValue(0.1)
                .unit(MOLE_PER_LITRE)
                .build();

        ConcentrationBuilder.create(simulation)
                .entity(receptor)
                .subsection(CELL_OUTER_MEMBRANE)
                .concentrationValue(0.1)
                .unit(MOLE_PER_LITRE)
                .build();

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
        ReactionBuilder.staticReactants(simulation)
                .addSubstrate(ligand)
                .addSubstrate(receptor, CellTopology.MEMBRANE)
                .addProduct(complex, CellTopology.MEMBRANE)
                .reversible()
                .forwardReactionRate(forwardsRate)
                .backwardReactionRate(backwardsRate)
                .build();

        // checkpoints
        Quantity<Time> currentTime;
        Quantity<Time> firstCheckpoint = Quantities.getQuantity(0.05, MILLI(SECOND));
        boolean firstCheckpointPassed = false;
        Quantity<Time> secondCheckpoint = Quantities.getQuantity(2.0, MILLI(SECOND));
        // run simulation
        while ((currentTime = TimeStepManager.getElapsedTime().to(MILLI(SECOND))).getValue().doubleValue() < secondCheckpoint.getValue().doubleValue()) {
            simulation.nextEpoch();
            if (!firstCheckpointPassed && currentTime.getValue().doubleValue() > firstCheckpoint.getValue().doubleValue()) {
                assertEquals(0.00476, UnitRegistry.concentration(membraneNode.getConcentrationContainer().get(CELL_OUTER_MEMBRANE, receptor)).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
                assertEquals(0.00476, UnitRegistry.concentration(membraneNode.getConcentrationContainer().get(INNER, ligand)).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
                assertEquals(0.09523, UnitRegistry.concentration(membraneNode.getConcentrationContainer().get(CELL_OUTER_MEMBRANE, complex)).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
                firstCheckpointPassed = true;
            }
        }

        // check final values
        assertEquals(0.0001, UnitRegistry.concentration(membraneNode.getConcentrationContainer().get(CELL_OUTER_MEMBRANE, receptor)).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
        assertEquals(0.0001, UnitRegistry.concentration(membraneNode.getConcentrationContainer().get(INNER, ligand)).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
        assertEquals(0.0998, UnitRegistry.concentration(membraneNode.getConcentrationContainer().get(CELL_OUTER_MEMBRANE, complex)).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
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
        ReactionBuilder.staticReactants(simulation)
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
        while ((currentTime = TimeStepManager.getElapsedTime().to(MILLI(SECOND))).getValue().doubleValue() < secondCheckpoint.getValue().doubleValue()) {
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
                .build();

        ReactionBuilder.staticReactants(simulation)
                .addSubstrate(sm, MEMBRANE)
                .irreversible()
                .rate(rateConstant)
                .build();

        Quantity<Dimensionless> molecules = MolarConcentration.concentrationToMolecules(vesicle.getConcentrationContainer().get(MEMBRANE, sm));
        assertEquals(60, molecules.getValue().intValue());
        while (TimeStepManager.getElapsedTime().isLessThanOrEqualTo(Quantities.getQuantity(13, SECOND))) {
            simulation.nextEpoch();
            System.out.println(UnitRegistry.getTime());
            molecules = MolarConcentration.concentrationToMolecules(vesicle.getConcentrationContainer().get(MEMBRANE, sm));
        }
        assertEquals(0.0, molecules.getValue().doubleValue());
    }

    @Test
    @DisplayName("example reaction - with enzyme fructose bisphosphate aldolase")
    void testMichaelisMentenReaction() {
        // SABIO Entry ID: 28851
        // Kinetic properties of fructose bisphosphate aldolase from Trypanosoma brucei compared to aldolase from rabbit
        // muscle and Staphylococcus aureus.
        // create simulation
        Simulation simulation = new Simulation();
        simulation.setMaximalTimeStep(Quantities.getQuantity(0.1, SECOND));
        // setup graph
        AutomatonGraph graph = AutomatonGraphs.singularGraph();

        // get required species
        SmallMolecule fructosePhosphate = SmallMolecule.create("FP").build();
        SmallMolecule glyceronePhosphate = SmallMolecule.create("GP").build();
        SmallMolecule glyceraldehyde = SmallMolecule.create("GA").build();
        Protein aldolase = Protein.create("P07752").build();

        // rates
        MichaelisConstant michaelisConstant = new MichaelisConstant(Quantities.getQuantity(9.0e-3, MOLE_PER_LITRE), Evidence.NO_EVIDENCE);
        TurnoverNumber turnoverNumber = new TurnoverNumber(76, new ProductUnit<>(ONE.divide(MINUTE)), Evidence.NO_EVIDENCE);

        // set concentrations
        CellSubsection subsection = EXTRACELLULAR_REGION.getInnerSubsection();
        for (AutomatonNode node : graph.getNodes()) {
            node.getConcentrationContainer().initialize(subsection, fructosePhosphate, Quantities.getQuantity(1.0, MOLE_PER_LITRE));
            node.getConcentrationContainer().initialize(subsection, aldolase, Quantities.getQuantity(0.01, MOLE_PER_LITRE));
        }

        // setup reaction
        // create reaction using the properties of the enzyme
        ReactionBuilder.staticReactants(simulation)
                .addSubstrate(fructosePhosphate)
                .addCatalyst(aldolase)
                .addProduct(glyceraldehyde)
                .addProduct(glyceronePhosphate)
                .michaelisMenten()
                .michaelisConstant(michaelisConstant)
                .turnover(turnoverNumber)
                .build();

        // add graph
        simulation.setGraph(graph);

        AutomatonNode node = graph.getNode(0, 0);
        Quantity<Time> currentTime;
        Quantity<Time> firstCheckpoint = Quantities.getQuantity(39.2, SECOND);
        boolean firstCheckpointPassed = false;
        Quantity<Time> secondCheckpoint = Quantities.getQuantity(90, SECOND);
        // run simulation
        while ((currentTime = TimeStepManager.getElapsedTime().to(SECOND)).getValue().doubleValue() < secondCheckpoint.getValue().doubleValue()) {
            simulation.nextEpoch();
            if (!firstCheckpointPassed && currentTime.getValue().doubleValue() > firstCheckpoint.getValue().doubleValue()) {
                assertEquals(0.50, UnitRegistry.concentration(node.getConcentrationContainer().get(subsection, fructosePhosphate)).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-2);
                assertEquals(0.49, UnitRegistry.concentration(node.getConcentrationContainer().get(subsection, glyceronePhosphate)).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-2);
                assertEquals(0.49, UnitRegistry.concentration(node.getConcentrationContainer().get(subsection, glyceraldehyde)).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-2);
                assertEquals(0.01, UnitRegistry.concentration(node.getConcentrationContainer().get(subsection, aldolase)).to(MOLE_PER_LITRE).getValue().doubleValue());
                firstCheckpointPassed = true;
            }
        }
        // check final values
        assertEquals(0.0, UnitRegistry.concentration(node.getConcentrationContainer().get(subsection, fructosePhosphate)).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
        assertEquals(1.0, UnitRegistry.concentration(node.getConcentrationContainer().get(subsection, glyceronePhosphate)).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
        assertEquals(1.0, UnitRegistry.concentration(node.getConcentrationContainer().get(subsection, glyceraldehyde)).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
        assertEquals(0.01, UnitRegistry.concentration(node.getConcentrationContainer().get(subsection, aldolase)).to(MOLE_PER_LITRE).getValue().doubleValue());
    }

    @Test
    @DisplayName("example reaction - arbitrary law")
    void shouldPerformDynamicReaction() {

        // reactant
        ChemicalEntity substrate = SmallMolecule.create("substrate").build();
        ChemicalEntity product = SmallMolecule.create("product").build();
        ChemicalEntity catalyst = SmallMolecule.create("catalyst").build();

        // create simulation
        Simulation simulation = new Simulation();

        RateConstant rateConstant = RateConstant.create(0.1)
                .forward().firstOrder()
                .timeUnit(SECOND)
                .build();

        // create and add module
        ReactionBuilder.staticReactants(simulation)
                .kineticLaw("substrate*sin(catalyst)*k/product")
                .referenceParameter(new Reactant(substrate, ReactantRole.SUBSTRATE, INNER))
                .referenceParameter(new Reactant(product, ReactantRole.PRODUCT, INNER))
                .referenceParameter(new Reactant(catalyst, ReactantRole.CATALYTIC, INNER))
                .referenceParameter("k", rateConstant)
                .build();

        // setup graph
        final AutomatonGraph automatonGraph = AutomatonGraphs.singularGraph();
        simulation.setGraph(automatonGraph);
        // set concentrations
        AutomatonNode node = automatonGraph.getNode(0, 0);
        node.setCellRegion(CellRegions.CYTOPLASM_REGION);

        ConcentrationBuilder.create(simulation)
                .entity(substrate)
                .subsection(CYTOPLASM)
                .concentrationValue(200)
                .nanoMolar()
                .build();

        ConcentrationBuilder.create(simulation)
                .entity(product)
                .subsection(CYTOPLASM)
                .concentrationValue(100)
                .nanoMolar()
                .build();

        ConcentrationBuilder.create(simulation)
                .entity(catalyst)
                .subsection(CYTOPLASM)
                .concentrationValue(30)
                .nanoMolar()
                .build();

        for (int i = 0; i < 10; i++) {
            simulation.nextEpoch();
            System.out.println(node.getConcentrationContainer().get(CYTOPLASM, substrate));
            System.out.println(node.getConcentrationContainer().get(CYTOPLASM, product));
            System.out.println(node.getConcentrationContainer().get(CYTOPLASM, catalyst));
        }
    }

    @Test
    @DisplayName("reaction - section changing binding with fully contained vesicle")
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
        ChemicalEntity bindee = SmallMolecule.create("bindee")
                .build();

        // the receptor
        Protein binder = new Protein.Builder("binder")
                .build();

        ComplexEntity complex = ComplexEntity.from(binder, bindee);

        // create and add module
        ReactionBuilder.staticReactants(simulation)
                .addSubstrate(bindee)
                .addSubstrate(binder, CellTopology.MEMBRANE)
                .addProduct(complex, CellTopology.MEMBRANE)
                .reversible()
                .forwardReactionRate(forwardRate)
                .backwardReactionRate(backwardRate)
                .build();

        // initialize vesicle layer
        VesicleLayer vesicleLayer = new VesicleLayer(simulation);
        simulation.setVesicleLayer(vesicleLayer);

        // vesicle contained
        Vesicle vesicle = new Vesicle(new Vector2D(25.0, 25.0), Quantities.getQuantity(20, NANO(METRE)));
        vesicleLayer.addVesicle(vesicle);

        // concentrations
        ConcentrationBuilder.create(simulation)
                .entity(binder)
                .topology(MEMBRANE)
                .concentrationValue(0.1)
                .unit(MOLE_PER_LITRE)
                .onlyVesicles()
                .build();

        ConcentrationBuilder.create(simulation)
                .entity(bindee)
                .topology(INNER)
                .concentrationValue(1.0)
                .unit(MOLE_PER_LITRE)
                .onlyNodes()
                .build();

        AutomatonNode node = graph.getNode(0, 0);

        // checkpoints
        Quantity<Time> firstCheckpoint = Quantities.getQuantity(50, MICRO(SECOND));
        boolean firstCheckpointPassed = false;
        Quantity<Time> secondCheckpoint = Quantities.getQuantity(500, MICRO(SECOND));
        // run simulation
        while (TimeStepManager.getElapsedTime().isLessThanOrEqualTo(secondCheckpoint)) {
            simulation.nextEpoch();
            if (!firstCheckpointPassed && TimeStepManager.getElapsedTime().isGreaterThanOrEqualTo(firstCheckpoint)) {
                assertEquals(9.447E-7, node.getConcentrationContainer().get(INNER, bindee), 1e-10);
                assertEquals(5.522E-8, vesicle.getConcentrationContainer().get(MEMBRANE, complex), 1e-10);
                firstCheckpointPassed = true;
            }
        }

        // check final values
        assertEquals(9.000E-7, node.getConcentrationContainer().get(INNER, bindee), 1e-10);
        assertEquals(1.000E-7, vesicle.getConcentrationContainer().get(MEMBRANE, complex), 1e-10);
    }

    @Test
    @DisplayName("reaction - membrane and vesicle should give the same results")
    void reversibleReactionMembraneVesicle() {
        // create simulation
        Simulation simulation = new Simulation();

        // setup scaling
        ComparableQuantity<Length> systemExtend = Quantities.getQuantity(1, MICRO(METRE));
        Environment.setSystemExtend(systemExtend);

        // setup graph
        final AutomatonGraph graph = AutomatonGraphs.singularGraph();
        simulation.setGraph(graph);

        // concentrations
        AutomatonNode node = graph.getNode(0, 0);
        node.setCellRegion(CELL_OUTER_MEMBRANE_REGION);

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
        ChemicalEntity bindee = SmallMolecule.create("bindee")
                .build();

        // the receptor
        Protein binder = new Protein.Builder("binder")
                .build();

        ComplexEntity complex = ComplexEntity.from(binder, bindee);

        // create and add module
        ReactionBuilder.staticReactants(simulation)
                .addSubstrate(bindee)
                .addSubstrate(binder, CellTopology.MEMBRANE)
                .addProduct(complex, CellTopology.MEMBRANE)
                .reversible()
                .forwardReactionRate(forwardRate)
                .backwardReactionRate(backwardRate)
                .build();

        // concentrations
        ConcentrationBuilder.create(simulation)
                .entity(binder)
                .topology(MEMBRANE)
                .concentrationValue(0.1)
                .unit(MOLE_PER_LITRE)
                .build();

        ConcentrationBuilder.create(simulation)
                .entity(bindee)
                .topology(INNER)
                .concentrationValue(1.0)
                .unit(MOLE_PER_LITRE)
                .build();

        // checkpoints
        Quantity<Time> firstCheckpoint = Quantities.getQuantity(50, MICRO(SECOND));
        boolean firstCheckpointPassed = false;
        Quantity<Time> secondCheckpoint = Quantities.getQuantity(500, MICRO(SECOND));
        // run simulation
        while (TimeStepManager.getElapsedTime().isLessThanOrEqualTo(secondCheckpoint)) {
            simulation.nextEpoch();
            if (!firstCheckpointPassed && TimeStepManager.getElapsedTime().isGreaterThanOrEqualTo(firstCheckpoint)) {
                assertEquals(9.447E-7, node.getConcentrationContainer().get(INNER, bindee), 1e-10);
                assertEquals(5.522E-8, node.getConcentrationContainer().get(MEMBRANE, complex), 1e-10);
                firstCheckpointPassed = true;
            }
        }

        // check final values
        assertEquals(9.000E-7, node.getConcentrationContainer().get(INNER, bindee), 1e-10);
        assertEquals(1.000E-7, node.getConcentrationContainer().get(MEMBRANE, complex), 1e-10);
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
        ChemicalEntity bindee = SmallMolecule.create("bindee")
                .assignFeature(new MolarMass(10, Evidence.NO_EVIDENCE))
                .build();

        // the receptor
        Protein binder = new Protein.Builder("binder")
                .assignFeature(new MolarMass(100, Evidence.NO_EVIDENCE))
                .build();

        ComplexEntity complex = ComplexEntity.from(binder, bindee);

        // create and add module
        ReactionBuilder.staticReactants(simulation)
                .addSubstrate(bindee)
                .addSubstrate(binder, CellTopology.MEMBRANE)
                .addProduct(complex, CellTopology.MEMBRANE)
                .reversible()
                .forwardReactionRate(forwardRate)
                .backwardReactionRate(backwardRate)
                .build();

        // initialize vesicle layer
        VesicleLayer vesicleLayer = new VesicleLayer(simulation);
        simulation.setVesicleLayer(vesicleLayer);

        // vesicle contained
        Vesicle vesicle = new Vesicle(new Vector2D(25.0, 50.0), Quantities.getQuantity(20, NANO(METRE)));
        vesicle.getConcentrationContainer().initialize(MEMBRANE, binder, Quantities.getQuantity(0.1, MOLE_PER_LITRE));
        vesicle.getConcentrationContainer().initialize(MEMBRANE, complex, Quantities.getQuantity(0.0, MOLE_PER_LITRE));
        vesicleLayer.addVesicle(vesicle);

        // concentrations
        AutomatonNode first = graph.getNode(0, 0);
        first.getConcentrationContainer().initialize(INNER, bindee, Quantities.getQuantity(1.0, MOLE_PER_LITRE));
        AutomatonNode second = graph.getNode(0, 1);
        second.getConcentrationContainer().initialize(INNER, bindee, Quantities.getQuantity(0.5, MOLE_PER_LITRE));

        // checkpoints
        Quantity<Time> firstCheckpoint = Quantities.getQuantity(50, MICRO(SECOND));
        boolean firstCheckpointPassed = false;
        Quantity<Time> secondCheckpoint = Quantities.getQuantity(500, MICRO(SECOND));
        // run simulation
        while (TimeStepManager.getElapsedTime().isLessThanOrEqualTo(secondCheckpoint)) {
            simulation.nextEpoch();
            if (!firstCheckpointPassed && TimeStepManager.getElapsedTime().isGreaterThanOrEqualTo(firstCheckpoint)) {
                assertEquals(9.691E-7, first.getConcentrationContainer().get(INNER, bindee), 1e-10);
                assertEquals(4.845E-7, second.getConcentrationContainer().get(INNER, bindee), 1e-10);
                assertEquals(4.630E-8, vesicle.getConcentrationContainer().get(MEMBRANE, complex), 1e-10);
                firstCheckpointPassed = true;
            }
        }

        // check final values
        assertEquals(9.335E-7, first.getConcentrationContainer().get(INNER, bindee), 1e-10);
        assertEquals(4.667E-7, second.getConcentrationContainer().get(INNER, bindee), 1e-10);
        assertEquals(9.972E-8, vesicle.getConcentrationContainer().get(MEMBRANE, complex), 1e-10);

    }

    @Test
    @DisplayName("complex building reaction - minimal setup")
    void minimalSetUpTest() {
        // create simulation
        Simulation simulation = new Simulation();

        // setup graph
        AutomatonGraph automatonGraph = AutomatonGraphs.singularGraph();
        simulation.setGraph(automatonGraph);

        // rate constants
        RateConstant forwardRate = RateConstant.create(1)
                .forward().secondOrder()
                .concentrationUnit(MOLE_PER_LITRE)
                .timeUnit(SECOND)
                .build();

        RateConstant backwardRate = RateConstant.create(1)
                .backward().firstOrder()
                .timeUnit(SECOND)
                .build();

        // reactants
        ChemicalEntity bindee = SimpleEntity.create("bindee").build();
        ChemicalEntity binder = SimpleEntity.create("binder").build();
        ChemicalEntity complex = ComplexEntity.from(binder, bindee);

        // create and add module
        ReactionBuilder.staticReactants(simulation)
                .addSubstrate(binder, MEMBRANE)
                .addSubstrate(bindee, INNER)
                .addProduct(complex, MEMBRANE)
                .complexBuilding()
                .associationRate(forwardRate)
                .dissociationRate(backwardRate)
                .build();

        // set concentrations
        AutomatonNode membraneNode = automatonGraph.getNode(0, 0);
        membraneNode.setCellRegion(CellRegions.CELL_OUTER_MEMBRANE_REGION);
        membraneNode.getConcentrationContainer().initialize(INNER, bindee, Quantities.getQuantity(1.0, MOLE_PER_LITRE));
        membraneNode.getConcentrationContainer().initialize(MEMBRANE, binder, Quantities.getQuantity(1.0, MOLE_PER_LITRE));
        membraneNode.getConcentrationContainer().initialize(MEMBRANE, complex, Quantities.getQuantity(1.0, MOLE_PER_LITRE));

        // forward and backward reactions should cancel each other out
        ConcentrationContainer container = membraneNode.getConcentrationContainer();
        for (int i = 0; i < 10; i++) {
            assertEquals(0.0, container.get(MEMBRANE, bindee));
            assertEquals(1.0, UnitRegistry.concentration(container.get(MEMBRANE, binder)).to(MOLE_PER_LITRE).getValue().doubleValue());
            assertEquals(1.0, UnitRegistry.concentration(container.get(MEMBRANE, complex)).to(MOLE_PER_LITRE).getValue().doubleValue());
            assertEquals(1.0, UnitRegistry.concentration(container.get(INNER, bindee)).to(MOLE_PER_LITRE).getValue().doubleValue());
            assertEquals(0, container.get(INNER, binder));
            assertEquals(0, container.get(INNER, complex));
            simulation.nextEpoch();
        }
    }

    @Test
    @DisplayName("complex building reaction - simple section changing binding")
    void testMembraneAbsorption() {
        // create simulation
        Simulation simulation = new Simulation();

        // setup graph
        final AutomatonGraph automatonGraph = AutomatonGraphs.singularGraph();
        simulation.setGraph(automatonGraph);

        // rate constants
        RateConstant<?> forwardRate = RateConstant.create(1.0e6)
                .forward().secondOrder()
                .concentrationUnit(MOLE_PER_LITRE)
                .timeUnit(MINUTE)
                .build();

        RateConstant<?> backwardRate = RateConstant.create(0.01)
                .backward().firstOrder()
                .timeUnit(MINUTE)
                .build();

        // reactants
        ChemicalEntity bindee = SimpleEntity.create("bindee").build();
        ChemicalEntity binder = SimpleEntity.create("binder").build();
        ChemicalEntity complex = ComplexEntity.from(binder, bindee);

        // create and add module
        ReactionBuilder.staticReactants(simulation)
                .addSubstrate(binder, MEMBRANE)
                .addSubstrate(bindee, INNER)
                .addProduct(complex, MEMBRANE)
                .complexBuilding()
                .associationRate(forwardRate)
                .dissociationRate(backwardRate)
                .build();

        // concentrations
        AutomatonNode membraneNode = automatonGraph.getNode(0, 0);
        membraneNode.setCellRegion(CellRegions.CELL_OUTER_MEMBRANE_REGION);
        membraneNode.getConcentrationContainer().set(INNER, bindee, 1.0);
        membraneNode.getConcentrationContainer().set(MEMBRANE, binder, 0.1);
        membraneNode.getConcentrationContainer().set(MEMBRANE, complex, 0.0);

        double previousConcentration = 0.0;
        for (int i = 0; i < 10; i++) {
            simulation.nextEpoch();
            double currentConcentration = membraneNode.getConcentrationContainer().get(CellSubsections.CELL_OUTER_MEMBRANE, complex);
            assertTrue(currentConcentration > previousConcentration);
            previousConcentration = currentConcentration;
        }
    }

    @Test
    void complexBuildingRuleBased() {
        Simulation simulation = new Simulation();
        AutomatonGraph automatonGraph = AutomatonGraphs.singularGraph(CELL_OUTER_MEMBRANE_REGION);
        simulation.setGraph(automatonGraph);

        ChemicalEntity aqp2 = Protein.create("AQP2").membraneBound().build();
        BindingSite aqpSite = BindingSite.createNamed("ser265");

        ChemicalEntity pp1 = Protein.create("PP1").build();
        BindingSite pp1Site = BindingSite.createNamed("thr38");

        ChemicalEntity p = SmallMolecule.create("P").build();

        ChemicalEntity pka = Protein.create("PKA").build();
        BindingSite substrateSite = BindingSite.createNamed("sub");

        RateConstant forwardRate = RateConstant.create(200)
                .forward().secondOrder()
                .concentrationUnit(MOLE_PER_LITRE)
                .timeUnit(SECOND)
                .build();

        RateConstant backwardRate = RateConstant.create(8)
                .backward().firstOrder()
                .timeUnit(SECOND)
                .build();

        ReactionBuilder.FinalStep first = ReactionBuilder.ruleBased(simulation)
                .rule(bind(substrateSite, pka)
                        .to(aqp2)
                        .secondaryCondition(hasUnoccupiedBindingSite(aqpSite))
                        .identifier("pka aqp2 complex building")
                        .build())
                .complexBuilding()
                .associationRate(forwardRate)
                .dissociationRate(backwardRate);

        ReactionBuilder.FinalStep second = ReactionBuilder.ruleBased(simulation)
                .rule(bind(substrateSite, pka)
                        .to(pp1)
                        .secondaryCondition(hasUnoccupiedBindingSite(pp1Site))
                        .identifier("pka pp1 complex building")
                        .build())
                .complexBuilding()
                .associationRate(forwardRate)
                .dissociationRate(backwardRate);

        // phosphorylation of aqp2 to aqp2p effective catalytic constant
        RateConstant kCat = RateConstant.create(50)
                .forward().firstOrder()
                .timeUnit(SECOND)
                .build();

        ReactionBuilder.FinalStep third = ReactionBuilder.ruleBased(simulation)
                .rule(add(aqpSite, p)
                        .to(aqp2)
                        .condition(hasOneOfEntity(pka))
                        .and()
                        .release(substrateSite, aqp2)
                        .from(pka)
                        .identifier("aqp2 phosphorylation")
                        .build())
                .irreversible()
                .rate(kCat);

        ReactionBuilder.FinalStep fourth = ReactionBuilder.ruleBased(simulation)
                .rule(add(pp1Site, p)
                        .to(pp1)
                        .condition(hasOneOfEntity(pka))
                        .and()
                        .release(substrateSite, pp1)
                        .from(pka)
                        .identifier("pp1 phosphorylation")
                        .build())
                .irreversible()
                .rate(kCat);

        ReactionBuilder.generateNetwork();
        first.build();
        second.build();
        third.build();
        fourth.build();

        // set concentrations
        ConcentrationBuilder.create(simulation)
                .entity(EntityRegistry.matchExactly("AQP2"))
                .subsection(CELL_OUTER_MEMBRANE)
                .concentrationValue(100)
                .microMolar()
                .build();

        ConcentrationBuilder.create(simulation)
                .entity(EntityRegistry.matchExactly("PP1"))
                .subsection(CYTOPLASM)
                .concentrationValue(100)
                .microMolar()
                .build();

        ConcentrationBuilder.create(simulation)
                .entity(EntityRegistry.matchExactly("PKA"))
                .subsection(CYTOPLASM)
                .concentrationValue(100)
                .microMolar()
                .build();

        while (TimeStepManager.getElapsedTime().isLessThan(Quantities.getQuantity(0.1, SECOND))) {
            simulation.nextEpoch();
        }

        ConcentrationContainer container = simulation.getGraph().getNode(0, 0).getConcentrationContainer();
        assertEquals(9.965E-11, container.get(INNER, EntityRegistry.matchExactly("PP1")), 1E-6);
        assertEquals(3.144E-13, container.get(INNER, EntityRegistry.matchExactly("PP1", "P")), 1E-6);
        assertEquals(3.144E-13, container.get(MEMBRANE, EntityRegistry.matchExactly("AQP2", "P")), 1E-6);
    }

    @Test
    void testKinase() {

        Simulation simulation = new Simulation();
        AutomatonGraph automatonGraph = AutomatonGraphs.singularGraph(CELL_OUTER_MEMBRANE_REGION);
        simulation.setGraph(automatonGraph);

        Protein enzyme = Protein.create("Enzyme")
                .membraneBound()
                .build();

        SmallMolecule ligand = SmallMolecule.create("Ligand").build();
        Protein kinase = Protein.create("Kinase").build();
        SmallMolecule phosphate = SmallMolecule.create("P").build();

        RateConstant kFE = RateConstant.create(0.5)
                .forward().secondOrder()
                .concentrationUnit(MICRO_MOLE_PER_LITRE)
                .timeUnit(MINUTE)
                .build();

        RateConstant kBE = RateConstant.create(0.01)
                .backward().firstOrder()
                .timeUnit(SECOND)
                .build();

        ReactionBuilder.ruleBased(simulation)
                .rule(add(ligand).to(enzyme)
                        .build())
                .reversible()
                .forwardReactionRate(kFE)
                .backwardReactionRate(kBE)
                .build();


        RateConstant kFB = RateConstant.create(0.2)
                .forward().secondOrder()
                .concentrationUnit(MICRO_MOLE_PER_LITRE)
                .timeUnit(SECOND)
                .build();

        RateConstant kBB = RateConstant.create(0.1)
                .backward().firstOrder()
                .timeUnit(SECOND)
                .build();

        ReactionBuilder.ruleBased(simulation)
                .rule(bind(kinase).to(enzyme)
                        .secondaryCondition(hasOneOfEntity(ligand))
                        .secondaryCondition(hasNoneOfEntity(phosphate))
                        .build())
                .reversible()
                .forwardReactionRate(kFB)
                .backwardReactionRate(kBB)
                .build();

        ReactionBuilder.ruleBased(simulation)
                .rule(add(phosphate).to(enzyme)
                        .condition(hasOneOfEntity(kinase))
                        .and()
                        .release(kinase).from(enzyme)
                        .build())
                .reversible()
                .forwardReactionRate(kFB)
                .backwardReactionRate(kBB)
                .build();

        ReactionBuilder.generateNetwork();

    }

    @Test
    void timedReactions() {

        Simulation simulation = new Simulation();
        simulation.setMaximalTimeStep(Quantities.getQuantity(0.1, SECOND));
        AutomatonGraph automatonGraph = AutomatonGraphs.singularGraph(CYTOPLASM_REGION);
        simulation.setGraph(automatonGraph);

        SmallMolecule camp = SmallMolecule.create("camp").build();

        RateConstant<?> camp_influx = RateConstant.create(10)
                .forward().zeroOrder()
                .concentrationUnit(NANO_MOLE_PER_LITRE)
                .timeUnit(SECOND)
                .build();

        ReactionBuilder.staticReactants(simulation)
                .addProduct(camp, INNER)
                .irreversible()
                .rate(camp_influx)
                .condition(TimedCondition.of(TimedCondition.Relation.LESS, Quantities.getQuantity(5, SECOND)))
                .build();

        double previous = 0.0;
        while (TimeStepManager.getElapsedTime().isLessThan(Quantities.getQuantity(10, SECOND))) {
            simulation.nextEpoch();
            double current = automatonGraph.getNode(0, 0).getConcentrationContainer().get(INNER, camp);
            if (TimeStepManager.getElapsedTime().isLessThan(Quantities.getQuantity(5, SECOND))) {
                assertTrue(current > previous);
            }
            if (TimeStepManager.getElapsedTime().isGreaterThan(Quantities.getQuantity(5.1, SECOND))) {
                assertEquals(current, previous);
            }
            System.out.println(UnitRegistry.humanReadable(TimeStepManager.getElapsedTime())+" : "+UnitRegistry.concentration(current).to(NANO_MOLE_PER_LITRE));
            previous= current;
        }

    }
}