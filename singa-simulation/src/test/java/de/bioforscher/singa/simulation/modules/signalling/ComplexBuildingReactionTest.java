package de.bioforscher.singa.simulation.modules.signalling;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.entities.Protein;
import de.bioforscher.singa.chemistry.descriptive.entities.SmallMolecule;
import de.bioforscher.singa.chemistry.descriptive.features.reactions.RateConstant;
import de.bioforscher.singa.features.model.FeatureOrigin;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraphs;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import de.bioforscher.singa.simulation.model.newsections.CellRegion;
import de.bioforscher.singa.simulation.model.newsections.CellSubsection;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import de.bioforscher.singa.structure.features.molarmass.MolarMass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.Quantity;

import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static de.bioforscher.singa.simulation.model.newsections.CellTopology.*;
import static org.junit.Assert.assertTrue;
import static tec.uom.se.unit.Units.MINUTE;

/**
 * @author cl
 */
public class ComplexBuildingReactionTest {

    private static final Logger logger = LoggerFactory.getLogger(ComplexBuildingReactionTest.class);

    @Test
    public void testMembraneAbsorption() {

        logger.info("Testing Section Changing Binding (Membrane Absorption).");
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
        membraneNode.getConcentrationContainer().set(CellSubsection.SECTION_A, bindee, 1.0);
        membraneNode.getConcentrationContainer().set(CellSubsection.MEMBRANE, binder, 0.1);
        membraneNode.getConcentrationContainer().set(CellSubsection.MEMBRANE, binding.getComplex(), 0.0);

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

        logger.info("Testing Section Changing Binding (Membrane Absorption).");
        // the rate constants
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
                .of(innerBindee, innerForwardsRateConstant)
                .in(INNER)
                .by(binder, innerBackwardsRateConstant)
                .to(MEMBRANE)
                .build();

        // create and add outer module
        ComplexBuildingReaction outerBinding = ComplexBuildingReaction.inSimulation(simulation)
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

        membraneNode.getConcentrationContainer().set(CellSubsection.SECTION_B, outerBindee, 0.1);
        membraneNode.getConcentrationContainer().set(CellSubsection.SECTION_A, innerBindee, 0.1);
        membraneNode.getConcentrationContainer().set(CellSubsection.MEMBRANE, binder, 0.1);
        membraneNode.getConcentrationContainer().set(CellSubsection.MEMBRANE, innerBinding.getComplex(), 0.0);
        membraneNode.getConcentrationContainer().set(CellSubsection.MEMBRANE, outerBinding.getComplex(), 0.0);

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