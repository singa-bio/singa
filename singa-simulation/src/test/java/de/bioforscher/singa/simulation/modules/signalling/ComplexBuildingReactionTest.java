package de.bioforscher.singa.simulation.modules.signalling;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.entities.Protein;
import de.bioforscher.singa.chemistry.descriptive.entities.SmallMolecule;
import de.bioforscher.singa.chemistry.descriptive.features.reactions.RateConstant;
import de.bioforscher.singa.features.model.FeatureOrigin;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.compartments.CellSectionState;
import de.bioforscher.singa.simulation.model.compartments.EnclosedCompartment;
import de.bioforscher.singa.simulation.model.compartments.Membrane;
import de.bioforscher.singa.simulation.model.concentrations.MembraneContainer;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraphs;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import de.bioforscher.singa.structure.features.molarmass.MolarMass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import java.util.HashSet;
import java.util.Set;

import static de.bioforscher.singa.features.parameters.Environment.getTransformedMolarConcentration;
import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;
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
                .in(CellSectionState.NON_MEMBRANE)
                .by(binder, backwardRate)
                .to(CellSectionState.MEMBRANE)
                .build();

        // setup graph
        final AutomatonGraph automatonGraph = AutomatonGraphs.singularGraph();
        simulation.setGraph(automatonGraph);
        // compartments
        EnclosedCompartment left = new EnclosedCompartment("LC", "Left");
        EnclosedCompartment right = new EnclosedCompartment("RC", "Right");
        Membrane membrane = Membrane.forCompartment(right);
        // concentrations
        AutomatonNode membraneNode = automatonGraph.getNode(0, 0);
        membraneNode.setState(CellSectionState.MEMBRANE);
        MembraneContainer concentrationContainer = new MembraneContainer(left, right, membrane);
        membraneNode.setConcentrationContainer(concentrationContainer);
        membraneNode.setAvailableConcentration(bindee, left, Quantities.getQuantity(0.1, MOLE_PER_LITRE).to(getTransformedMolarConcentration()));
        membraneNode.setAvailableConcentration(binder, membrane.getOuterLayer(), Quantities.getQuantity(0.1, MOLE_PER_LITRE).to(getTransformedMolarConcentration()));
        membraneNode.setAvailableConcentration(binding.getComplex(), membrane.getOuterLayer(), Quantities.getQuantity(0.0, MOLE_PER_LITRE).to(getTransformedMolarConcentration()));

        // define sections
        Set<CellSection> sections = new HashSet<>();
        sections.add(left);
        sections.add(membrane.getOuterLayer());
        sections.add(membrane.getInnerLayer());
        sections.add(right);
        concentrationContainer.setReferencedSections(sections);

        Quantity<MolarConcentration> previousConcentration = null;
        for (int i = 0; i < 10; i++) {
            simulation.nextEpoch();
            Quantity<MolarConcentration> currentConcentration = membraneNode.getAvailableConcentration(binding.getComplex(), membrane.getOuterLayer());
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
                .in(CellSectionState.NON_MEMBRANE)
                .by(binder, innerBackwardsRateConstant)
                .to(CellSectionState.MEMBRANE)
                .build();

        // create and add outer module
        ComplexBuildingReaction outerBinding = ComplexBuildingReaction.inSimulation(simulation)
                .of(outerBindee, outerForwardsRateConstant)
                .in(CellSectionState.NON_MEMBRANE)
                .by(binder, outerBackwardsRateConstant)
                .to(CellSectionState.MEMBRANE)
                .build();

        // setup graph
        final AutomatonGraph automatonGraph = AutomatonGraphs.singularGraph();
        simulation.setGraph(automatonGraph);
        // compartments
        EnclosedCompartment outer = new EnclosedCompartment("Outer", "Outer");
        EnclosedCompartment inner = new EnclosedCompartment("Inner", "Inner");
        Membrane membrane = Membrane.forCompartment(inner);
        // concentrations
        AutomatonNode membraneNode = automatonGraph.getNode(0, 0);
        membraneNode.setState(CellSectionState.MEMBRANE);
        MembraneContainer concentrationContainer = new MembraneContainer(outer, inner, membrane);
        membraneNode.setConcentrationContainer(concentrationContainer);
        membraneNode.setAvailableConcentration(outerBindee, outer, Quantities.getQuantity(0.1, MOLE_PER_LITRE).to(getTransformedMolarConcentration()));
        membraneNode.setAvailableConcentration(innerBindee, inner, Quantities.getQuantity(0.1, MOLE_PER_LITRE).to(getTransformedMolarConcentration()));
        membraneNode.setAvailableConcentration(binder, membrane.getOuterLayer(), Quantities.getQuantity(0.1, MOLE_PER_LITRE).to(getTransformedMolarConcentration()));
        membraneNode.setAvailableConcentration(innerBinding.getComplex(), membrane.getOuterLayer(), Quantities.getQuantity(0.0, MOLE_PER_LITRE).to(getTransformedMolarConcentration()));
        membraneNode.setAvailableConcentration(outerBinding.getComplex(), membrane.getOuterLayer(), Quantities.getQuantity(0.0, MOLE_PER_LITRE).to(getTransformedMolarConcentration()));

        // define sections
        Set<CellSection> sections = new HashSet<>();
        sections.add(outer);
        sections.add(membrane.getOuterLayer());
        sections.add(membrane.getInnerLayer());
        sections.add(inner);
        concentrationContainer.setReferencedSections(sections);

        Quantity<MolarConcentration> previousInnerConcentration = null;
        Quantity<MolarConcentration> previousOuterConcentration = null;
        for (int i = 0; i < 10; i++) {
            simulation.nextEpoch();
            // inner assertions
            Quantity<MolarConcentration> currentInnerConcentration = membraneNode.getAvailableConcentration(innerBinding.getComplex(), membrane.getOuterLayer());
            if (previousInnerConcentration != null) {
                assertTrue(currentInnerConcentration.getValue().doubleValue() > previousInnerConcentration.getValue().doubleValue());
            }
            previousInnerConcentration = currentInnerConcentration;
            // outer assertions
            Quantity<MolarConcentration> currentOuterConcentration = membraneNode.getAvailableConcentration(outerBinding.getComplex(), membrane.getOuterLayer());
            if (previousOuterConcentration != null) {
                assertTrue(currentOuterConcentration.getValue().doubleValue() > previousOuterConcentration.getValue().doubleValue());
            }
            previousOuterConcentration = currentOuterConcentration;
        }

    }



}