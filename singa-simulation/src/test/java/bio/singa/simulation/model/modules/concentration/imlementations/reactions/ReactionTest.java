package bio.singa.simulation.model.modules.concentration.imlementations.reactions;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.EntityRegistry;
import bio.singa.chemistry.entities.complex.BindingSite;
import bio.singa.chemistry.entities.complex.ComplexEntity;
import bio.singa.chemistry.entities.simple.Protein;
import bio.singa.chemistry.entities.simple.SmallMolecule;
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
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.sections.ConcentrationContainer;
import bio.singa.simulation.model.simulation.Simulation;
import bio.singa.structure.features.molarmass.MolarMass;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import static bio.singa.chemistry.reactions.conditions.CandidateConditionBuilder.hasOneOfEntity;
import static bio.singa.chemistry.reactions.conditions.CandidateConditionBuilder.hasUnoccupiedBindingSite;
import static bio.singa.chemistry.reactions.reactors.ReactionChainBuilder.add;
import static bio.singa.chemistry.reactions.reactors.ReactionChainBuilder.bind;
import static bio.singa.features.units.UnitProvider.MICRO_MOLE_PER_LITRE;
import static bio.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static bio.singa.simulation.model.sections.CellRegions.CELL_OUTER_MEMBRANE_REGION;
import static bio.singa.simulation.model.sections.CellRegions.CYTOPLASM_REGION;
import static bio.singa.simulation.model.sections.CellSubsections.CELL_OUTER_MEMBRANE;
import static bio.singa.simulation.model.sections.CellSubsections.CYTOPLASM;
import static bio.singa.simulation.model.sections.CellTopology.INNER;
import static bio.singa.simulation.model.sections.CellTopology.MEMBRANE;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void irreversibleReactionCytoplasm() {

        Simulation simulation = new Simulation();

        AutomatonGraph automatonGraph = AutomatonGraphs.singularGraph(CYTOPLASM_REGION);
        simulation.setGraph(automatonGraph);

        ChemicalEntity a = SmallMolecule.create("A").build();
        ChemicalEntity b = SmallMolecule.create("B").build();
        ChemicalEntity c = SmallMolecule.create("C").build();

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
    void irreversibleReactionMembrane() {
        Simulation simulation = new Simulation();

        AutomatonGraph automatonGraph = AutomatonGraphs.singularGraph(CELL_OUTER_MEMBRANE_REGION);
        simulation.setGraph(automatonGraph);

        ChemicalEntity a = SmallMolecule.create("A").build();
        ChemicalEntity b = SmallMolecule.create("B").build();
        ChemicalEntity c = SmallMolecule.create("C").build();

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
    void irreversibleReactionStoichiometry() {
        Simulation simulation = new Simulation();

        AutomatonGraph automatonGraph = AutomatonGraphs.singularGraph(CYTOPLASM_REGION);
        simulation.setGraph(automatonGraph);

        ChemicalEntity a = SmallMolecule.create("A").build();
        ChemicalEntity b = SmallMolecule.create("B").build();
        ChemicalEntity c = SmallMolecule.create("C").build();

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
    void reversibleReactionMembrane() {
        UnitRegistry.setSpace(Quantities.getQuantity(1.0, MILLI(METRE)));

        // see Receptors (Lauffenburger) p. 30
        // prazosin, CHEBI:8364
        ChemicalEntity ligand = SmallMolecule.create("ligand")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:8364"))
                .build();

        // alpha-1 adrenergic receptor, P35348
        Protein receptor = new Protein.Builder("receptor")
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
                .subsection(CYTOPLASM)
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
        while ((currentTime = simulation.getElapsedTime().to(MILLI(SECOND))).getValue().doubleValue() < secondCheckpoint.getValue().doubleValue()) {
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
                .assignFeature(new MolarMass(10))
                .build();

        // the receptor
        Protein binder = new Protein.Builder("binder")
                .assignFeature(new MolarMass(100))
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
        while (simulation.getElapsedTime().isLessThanOrEqualTo(secondCheckpoint)) {
            simulation.nextEpoch();
            if (!firstCheckpointPassed && simulation.getElapsedTime().isGreaterThanOrEqualTo(firstCheckpoint)) {
                assertEquals(9.442E-7, node.getConcentrationContainer().get(INNER, bindee), 1e-10);
                assertEquals(5.546E-8, vesicle.getConcentrationContainer().get(MEMBRANE, complex), 1e-10);
                firstCheckpointPassed = true;
            }
        }

        // check final values
        assertEquals(9.000E-7, node.getConcentrationContainer().get(INNER, bindee), 1e-10);
        assertEquals(1.000E-7, vesicle.getConcentrationContainer().get(MEMBRANE, complex), 1e-10);
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
        while (simulation.getElapsedTime().isLessThanOrEqualTo(secondCheckpoint)) {
            simulation.nextEpoch();
            if (!firstCheckpointPassed && simulation.getElapsedTime().isGreaterThanOrEqualTo(firstCheckpoint)) {
                assertEquals(9.695E-7, first.getConcentrationContainer().get(INNER, bindee), 1e-10);
                assertEquals(4.847E-7, second.getConcentrationContainer().get(INNER, bindee), 1e-10);
                assertEquals(4.576E-8, vesicle.getConcentrationContainer().get(MEMBRANE, complex), 1e-10);
                firstCheckpointPassed = true;
            }
        }

        // check final values
        assertEquals(9.335E-7, first.getConcentrationContainer().get(INNER, bindee), 1e-10);
        assertEquals(4.667E-7, second.getConcentrationContainer().get(INNER, bindee), 1e-10);
        assertEquals(9.972E-8, vesicle.getConcentrationContainer().get(MEMBRANE, complex), 1e-10);

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

        while (simulation.getElapsedTime().isLessThan(Quantities.getQuantity(0.1, SECOND))) {
            simulation.nextEpoch();
        }

        ConcentrationContainer container = simulation.getGraph().getNode(0, 0).getConcentrationContainer();
        assertEquals(9.965E-11, container.get(INNER, EntityRegistry.matchExactly("PP1")), 1E-6);
        assertEquals(3.144E-13, container.get(INNER, EntityRegistry.matchExactly("PP1", "P")), 1E-6);
        assertEquals(3.144E-13, container.get(MEMBRANE, EntityRegistry.matchExactly("AQP2", "P")), 1E-6);
    }

}