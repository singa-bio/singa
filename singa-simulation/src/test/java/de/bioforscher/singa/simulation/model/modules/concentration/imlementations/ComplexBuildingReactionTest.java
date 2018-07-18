package de.bioforscher.singa.simulation.model.modules.concentration.imlementations;

import de.bioforscher.singa.chemistry.entities.*;
import de.bioforscher.singa.chemistry.features.reactions.RateConstant;
import de.bioforscher.singa.features.identifiers.ChEBIIdentifier;
import de.bioforscher.singa.features.identifiers.UniProtIdentifier;
import de.bioforscher.singa.features.model.FeatureOrigin;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraphs;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import de.bioforscher.singa.simulation.model.sections.CellRegion;
import de.bioforscher.singa.simulation.model.sections.CellSubsection;
import de.bioforscher.singa.simulation.model.sections.CellTopology;
import de.bioforscher.singa.simulation.model.sections.ConcentrationContainer;
import de.bioforscher.singa.simulation.model.simulation.Simulation;
import de.bioforscher.singa.structure.features.molarmass.MolarMass;
import org.junit.After;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Time;

import static de.bioforscher.singa.features.parameters.Environment.getConcentrationUnit;
import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static de.bioforscher.singa.simulation.model.sections.CellSubsection.SECTION_A;
import static de.bioforscher.singa.simulation.model.sections.CellTopology.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static tec.uom.se.unit.MetricPrefix.MILLI;
import static tec.uom.se.unit.Units.*;

/**
 * @author cl
 */
public class ComplexBuildingReactionTest {

    private static final Logger logger = LoggerFactory.getLogger(ComplexBuildingReactionTest.class);

    @After
    public void cleanUp() {
        Environment.reset();
    }

    @Test
    public void minimalSetUpTest() {
        Environment.reset();
        logger.info("Testing section changing binding (minimal setup).");
        // the rate constants
        RateConstant forwardRate = RateConstant.create(1).forward().secondOder().concentrationUnit(MOLE_PER_LITRE).timeUnit(SECOND).build();
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
        membraneNode.getConcentrationContainer().set(OUTER, bindee, 1.0);
        membraneNode.getConcentrationContainer().set(MEMBRANE, binder, 1.0);
        membraneNode.getConcentrationContainer().set(MEMBRANE, complex, 1.0);

        // forewared and backward reactions should cancel each other out
        Quantity<MolarConcentration> empty = Environment.emptyConcentration();
        Quantity<MolarConcentration> one = Quantities.getQuantity(1.0, MOLE_PER_LITRE).to(Environment.getConcentrationUnit());
        for (int i = 0; i < 10; i++) {
            ConcentrationContainer container = membraneNode.getConcentrationContainer();

            assertEquals(container.get(CellTopology.INNER, bindee), empty);
            assertEquals(container.get(CellTopology.INNER, binder), empty);
            assertEquals(container.get(CellTopology.INNER, complex), empty);

            assertEquals(container.get(CellTopology.MEMBRANE, bindee), empty);
            assertEquals(container.get(CellTopology.MEMBRANE, binder), one);
            assertEquals(container.get(CellTopology.MEMBRANE, complex), one);

            assertEquals(container.get(CellTopology.OUTER, bindee), one);
            assertEquals(container.get(CellTopology.OUTER, binder), empty);
            assertEquals(container.get(CellTopology.OUTER, complex), empty);

            simulation.nextEpoch();
        }
    }

    @Test
    public void testPrazosinExample() {
        Environment.reset();
        Environment.setNodeDistance(Quantities.getQuantity(1.0, MILLI(METRE)));
        logger.info("Testing Monovalent Receptor Binding.");

        // see Receptors (Lauffenburger) p. 30
        // prazosin, CHEBI:8364
        ChemicalEntity ligand = new SmallMolecule.Builder("ligand")
                .name("prazosin")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:8364"))
                .build();

        // the corresponding rate constants
        RateConstant forwardsRate = RateConstant.create(2.4e8).forward().secondOder().concentrationUnit(MOLE_PER_LITRE).timeUnit(MINUTE).build();
        RateConstant backwardsRate = RateConstant.create(0.018).backward().firstOrder().timeUnit(MINUTE).build();
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
        membraneNode.getConcentrationContainer().set(SECTION_A, ligand, Quantities.getQuantity(0.1, MOLE_PER_LITRE).to(getConcentrationUnit()));
        membraneNode.getConcentrationContainer().set(CellSubsection.MEMBRANE, receptor, Quantities.getQuantity(0.1, MOLE_PER_LITRE).to(getConcentrationUnit()));

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
                logger.info("First checkpoint reached at {}.", simulation.getElapsedTime().to(MILLI(SECOND)));
                assertEquals(0.00476, membraneNode.getConcentrationContainer().get(CellSubsection.MEMBRANE, receptor).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
                assertEquals(0.00476, membraneNode.getConcentrationContainer().get(INNER, ligand).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
                assertEquals(0.09523, membraneNode.getConcentrationContainer().get(CellSubsection.MEMBRANE, complex).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
                firstCheckpointPassed = true;
            }
        }

        // check final values
        assertEquals(0.0001, membraneNode.getConcentrationContainer().get(CellSubsection.MEMBRANE, receptor).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
        assertEquals(0.0001, membraneNode.getConcentrationContainer().get(INNER, ligand).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
        assertEquals(0.0998, membraneNode.getConcentrationContainer().get(CellSubsection.MEMBRANE, complex).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
        logger.info("Second and final checkpoint (at {}) reached successfully.", simulation.getElapsedTime().to(MILLI(SECOND)));
        Environment.reset();
    }

    @Test
    public void testMembraneAbsorption() {
        Environment.reset();
        logger.info("Testing section changing binding (membrane absorption).");
        // the rate constants
        RateConstant forwardRate = RateConstant.create(1.0e6).forward().secondOder().concentrationUnit(MOLE_PER_LITRE).timeUnit(MINUTE).build();
        RateConstant backwardRate = RateConstant.create(0.01).backward().firstOrder().timeUnit(MINUTE).build();

        // the ligand
        ChemicalEntity bindee = new SmallMolecule.Builder("bindee")
                .name("bindee")
                .assignFeature(new MolarMass(10, FeatureOrigin.MANUALLY_ANNOTATED))
                .build();

        // the receptor
        Protein binder = new Protein.Builder("binder")
                .name("binder")
                .assignFeature(new MolarMass(100, FeatureOrigin.MANUALLY_ANNOTATED))
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

        Quantity<MolarConcentration> previousConcentration = null;
        for (int i = 0; i < 10; i++) {
            simulation.nextEpoch();
            Quantity<MolarConcentration> currentConcentration = membraneNode.getConcentrationContainer().get(CellSubsection.MEMBRANE, binding.getComplex());
            if (previousConcentration != null) {
                assertTrue(currentConcentration.getValue().doubleValue() > previousConcentration.getValue().doubleValue());
            }
            previousConcentration = currentConcentration;
        }
    }

    @Test
    public void shouldReactInsideAndOutside() {
        Environment.reset();
        logger.info("Testing section changing binding (inside and outside reactions).");

        // the rate constants
        RateConstant innerForwardsRateConstant = RateConstant.create(1.0e6).forward().secondOder().concentrationUnit(MOLE_PER_LITRE).timeUnit(MINUTE).build();
        RateConstant innerBackwardsRateConstant = RateConstant.create(0.01).backward().firstOrder().timeUnit(MINUTE).build();

        // the rate constants
        RateConstant outerForwardsRateConstant = RateConstant.create(1.0e6).forward().secondOder().concentrationUnit(MOLE_PER_LITRE).timeUnit(MINUTE).build();
        RateConstant outerBackwardsRateConstant = RateConstant.create(0.01).backward().firstOrder().timeUnit(MINUTE).build();

        // the inner ligand
        ChemicalEntity innerBindee = new SmallMolecule.Builder("inner bindee")
                .name("inner bindee")
                .assignFeature(new MolarMass(10, FeatureOrigin.MANUALLY_ANNOTATED))
                .build();

        // the outer ligand
        ChemicalEntity outerBindee = new SmallMolecule.Builder("outer bindee")
                .name("outer bindee")
                .assignFeature(new MolarMass(10, FeatureOrigin.MANUALLY_ANNOTATED))
                .build();

        // the receptor
        Protein binder = new Protein.Builder("binder")
                .name("binder")
                .assignFeature(new MolarMass(100, FeatureOrigin.MANUALLY_ANNOTATED))
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

        membraneNode.getConcentrationContainer().set(INNER, innerBindee, 0.1);
        membraneNode.getConcentrationContainer().set(OUTER, outerBindee, 0.1);
        membraneNode.getConcentrationContainer().set(MEMBRANE, binder, 0.1);
        membraneNode.getConcentrationContainer().set(MEMBRANE, innerBinding.getComplex(), 0.0);
        membraneNode.getConcentrationContainer().set(MEMBRANE, outerBinding.getComplex(), 0.0);

        Quantity<MolarConcentration> previousInnerConcentration = null;
        Quantity<MolarConcentration> previousOuterConcentration = null;
        for (int i = 0; i < 10; i++) {
            simulation.nextEpoch();
            // inner assertions
            Quantity<MolarConcentration> currentInnerConcentration = membraneNode.getConcentrationContainer().get(CellSubsection.MEMBRANE, innerBinding.getComplex());
            if (previousInnerConcentration != null) {
                assertTrue(currentInnerConcentration.getValue().doubleValue() > previousInnerConcentration.getValue().doubleValue());
            }
            previousInnerConcentration = currentInnerConcentration;
            // outer assertions
            Quantity<MolarConcentration> currentOuterConcentration = membraneNode.getConcentrationContainer().get(CellSubsection.MEMBRANE, outerBinding.getComplex());
            if (previousOuterConcentration != null) {
                assertTrue(currentOuterConcentration.getValue().doubleValue() > previousOuterConcentration.getValue().doubleValue());
            }
            previousOuterConcentration = currentOuterConcentration;
        }
    }

}
