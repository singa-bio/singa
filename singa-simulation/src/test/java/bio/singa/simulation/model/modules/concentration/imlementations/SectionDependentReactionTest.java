package bio.singa.simulation.model.modules.concentration.imlementations;

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
import bio.singa.simulation.model.simulation.Simulation;
import bio.singa.structure.features.molarmass.MolarMass;
import org.junit.jupiter.api.Test;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import static bio.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static bio.singa.simulation.model.sections.CellSubsection.SECTION_A;
import static bio.singa.simulation.model.sections.CellTopology.INNER;
import static bio.singa.simulation.model.sections.CellTopology.MEMBRANE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tec.uom.se.unit.MetricPrefix.*;
import static tec.uom.se.unit.Units.*;

/**
 * @author cl
 */
class SectionDependentReactionTest {

    @Test
    void testSame() {
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
        SectionDependentReaction.inSimulation(simulation)
                .identifier("section dependent")
                .addSubstrate(bindee, INNER)
                .addSubstrate(binder, MEMBRANE)
                .addProduct(complex, MEMBRANE)
                .forwardsRate(forwardRate)
                .backwardsRate(backwardRate)
                .build();

        ComplexBuildingReaction binding = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("complex building")
                .of(bindee, forwardRate)
                .in(INNER)
                .by(binder, backwardRate)
                .to(MEMBRANE)
                .formingComplex(complex)
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
            double currentVesicleConcentration = vesicle.getConcentrationContainer().get(MEMBRANE, complex);
            assertTrue(currentVesicleConcentration > previousVesicleConcentration);
            previousVesicleConcentration = currentVesicleConcentration;
        }

    }

    @Test
    void testTransformedComplexBuilding() {
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
        RateConstant kf = RateConstant.create(2.4e8)
                .forward().secondOrder()
                .concentrationUnit(MOLE_PER_LITRE)
                .timeUnit(MINUTE)
                .build();

        RateConstant kb = RateConstant.create(0.018)
                .backward().firstOrder()
                .timeUnit(MINUTE)
                .build();

        // create and add module
        SectionDependentReaction.inSimulation(simulation)
                .identifier("binding_reaction")
                .addSubstrate(ligand, INNER)
                .addSubstrate(receptor, MEMBRANE)
                .addProduct(complex, MEMBRANE)
                .forwardsRate(kf)
                .backwardsRate(kb)
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

}