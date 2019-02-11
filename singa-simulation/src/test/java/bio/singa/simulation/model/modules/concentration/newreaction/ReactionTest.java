package bio.singa.simulation.model.modules.concentration.newreaction;

import bio.singa.chemistry.entities.*;
import bio.singa.chemistry.entities.ComplexEntity;
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
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.sections.concentration.ConcentrationInitializer;
import bio.singa.simulation.model.simulation.Simulation;
import bio.singa.structure.features.molarmass.MolarMass;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import static bio.singa.features.units.UnitProvider.MICRO_MOLE_PER_LITRE;
import static bio.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static bio.singa.simulation.model.sections.CellRegions.CELL_OUTER_MEMBRANE_REGION;
import static bio.singa.simulation.model.sections.CellRegions.CYTOPLASM_REGION;
import static bio.singa.simulation.model.sections.CellSubsection.SECTION_A;
import static bio.singa.simulation.model.sections.CellSubsections.CELL_OUTER_MEMBRANE;
import static bio.singa.simulation.model.sections.CellSubsections.CYTOPLASM;
import static bio.singa.simulation.model.sections.CellTopology.INNER;
import static bio.singa.simulation.model.sections.CellTopology.MEMBRANE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static tec.uom.se.unit.MetricPrefix.*;
import static tec.uom.se.unit.Units.*;

/**
 * @author cl
 */
class ReactionTest {


    @Test
    void irreversibleReactionCytoplasm() {

        Simulation simulation = new Simulation();

        AutomatonGraph automatonGraph = AutomatonGraphs.singularGraph(CYTOPLASM_REGION);
        simulation.setGraph(automatonGraph);

        ChemicalEntity a = SmallMolecule.create("A").build();
        ChemicalEntity b = SmallMolecule.create("B").build();
        ChemicalEntity c = SmallMolecule.create("C").build();

        ConcentrationInitializer ci = new ConcentrationInitializer();
        ci.addInitialConcentration(CYTOPLASM, a, Quantities.getQuantity(1, MICRO_MOLE_PER_LITRE));
        ci.addInitialConcentration(CYTOPLASM, b, Quantities.getQuantity(1, MICRO_MOLE_PER_LITRE));
        simulation.setConcentrationInitializer(ci);

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
            System.out.println(simulation.getGraph().getNode(0,0).getConcentrationContainer().get(CYTOPLASM, a)+" "+simulation.getGraph().getNode(0,0).getConcentrationContainer().get(CYTOPLASM, b)+" "+simulation.getGraph().getNode(0,0).getConcentrationContainer().get(CYTOPLASM, c));
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

        ConcentrationInitializer ci = new ConcentrationInitializer();
        ci.addInitialConcentration(CYTOPLASM, a, Quantities.getQuantity(1, MICRO_MOLE_PER_LITRE));
        ci.addInitialConcentration(CELL_OUTER_MEMBRANE, b, Quantities.getQuantity(1, MICRO_MOLE_PER_LITRE));
        simulation.setConcentrationInitializer(ci);

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
            System.out.println(simulation.getGraph().getNode(0,0).getConcentrationContainer().get(CYTOPLASM, a)+" "+simulation.getGraph().getNode(0,0).getConcentrationContainer().get(MEMBRANE, b)+" "+simulation.getGraph().getNode(0,0).getConcentrationContainer().get(MEMBRANE, c));
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

        ConcentrationInitializer ci = new ConcentrationInitializer();
        ci.addInitialConcentration(CYTOPLASM, a, Quantities.getQuantity(1, MICRO_MOLE_PER_LITRE));
        ci.addInitialConcentration(CYTOPLASM, b, Quantities.getQuantity(1, MICRO_MOLE_PER_LITRE));
        simulation.setConcentrationInitializer(ci);

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
            System.out.println(simulation.getGraph().getNode(0,0).getConcentrationContainer().get(CYTOPLASM, a)+" "+simulation.getGraph().getNode(0,0).getConcentrationContainer().get(CYTOPLASM, b)+" "+simulation.getGraph().getNode(0,0).getConcentrationContainer().get(CYTOPLASM, c));
        }
    }

    @Test
    void reversibleReactionMembrane() {
        UnitRegistry.setSpace(Quantities.getQuantity(1.0, MILLI(METRE)));

        // see Receptors (Lauffenburger) p. 30
        // prazosin, CHEBI:8364
        ChemicalEntity ligand = new SmallMolecule.Builder("ligand")
                .name("prazosin")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:8364"))
                .build();

        // alpha-1 adrenergic receptor, P35348
        Protein receptor = new Protein.Builder("receptor")
                .name("alpha-1 adrenergic receptor")
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
        ChemicalEntity bindee = new SmallMolecule.Builder("bindee")
                .name("bindee")
                .assignFeature(new MolarMass(10, Evidence.NO_EVIDENCE))
                .build();

        // the receptor
        Protein binder = new Protein.Builder("binder")
                .name("binder")
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

        ComparableQuantity<Length> radius = Quantities.getQuantity(20, NANO(METRE));

        // vesicle contained
        Vesicle vesicle = new Vesicle("Vesicle", new Vector2D(25.0, 25.0), radius);
        vesicle.getConcentrationContainer().initialize(MEMBRANE, binder, Quantities.getQuantity(0.1, MOLE_PER_LITRE));
        vesicleLayer.addVesicle(vesicle);

        // concentrations
        AutomatonNode node = graph.getNode(0, 0);
        node.getConcentrationContainer().initialize(INNER, bindee, Quantities.getQuantity(1.0, MOLE_PER_LITRE));

        // checkpoints
        Quantity<Time> firstCheckpoint = Quantities.getQuantity(50, MICRO(SECOND));
        boolean firstCheckpointPassed = false;
        Quantity<Time> secondCheckpoint = Quantities.getQuantity(500, MICRO(SECOND));
        // run simulation
        while (simulation.getElapsedTime().isLessThanOrEqualTo(secondCheckpoint)) {
            simulation.nextEpoch();
            if (!firstCheckpointPassed && simulation.getElapsedTime().isGreaterThanOrEqualTo(firstCheckpoint)) {
                assertEquals(9.446E-7, node.getConcentrationContainer().get(INNER, bindee), 1e-10);
                assertEquals(5.537E-8, vesicle.getConcentrationContainer().get(MEMBRANE, complex), 1e-10);
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
        ChemicalEntity bindee = new SmallMolecule.Builder("bindee")
                .name("bindee")
                .assignFeature(new MolarMass(10, Evidence.NO_EVIDENCE))
                .build();

        // the receptor
        Protein binder = new Protein.Builder("binder")
                .name("binder")
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

        ComparableQuantity<Length> radius = Quantities.getQuantity(20, NANO(METRE));

        // vesicle contained
        Vesicle vesicle = new Vesicle("Vesicle", new Vector2D(25.0, 50.0), radius);
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
                assertEquals(9.693E-7, first.getConcentrationContainer().get(INNER, bindee), 1e-10);
                assertEquals(4.846E-7, second.getConcentrationContainer().get(INNER, bindee), 1e-10);
                assertEquals(4.603E-8, vesicle.getConcentrationContainer().get(MEMBRANE, complex), 1e-10);
                firstCheckpointPassed = true;
            }
        }

        // check final values
        assertEquals(9.335E-7, first.getConcentrationContainer().get(INNER, bindee), 1e-10);
        assertEquals(4.667E-7, second.getConcentrationContainer().get(INNER, bindee), 1e-10);
        assertEquals(9.972E-8, vesicle.getConcentrationContainer().get(MEMBRANE, complex), 1e-10);

    }


}