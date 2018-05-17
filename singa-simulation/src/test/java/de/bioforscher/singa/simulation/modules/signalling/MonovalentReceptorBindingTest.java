package de.bioforscher.singa.simulation.modules.signalling;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.entities.Receptor;
import de.bioforscher.singa.chemistry.descriptive.entities.SmallMolecule;
import de.bioforscher.singa.chemistry.descriptive.features.reactions.RateConstant;
import de.bioforscher.singa.features.identifiers.ChEBIIdentifier;
import de.bioforscher.singa.features.identifiers.UniProtIdentifier;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.compartments.CellSectionState;
import de.bioforscher.singa.simulation.model.compartments.EnclosedCompartment;
import de.bioforscher.singa.simulation.model.compartments.Membrane;
import de.bioforscher.singa.simulation.model.concentrations.MembraneContainer;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraphs;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.uom.se.quantity.Quantities;

import java.util.HashSet;
import java.util.Set;

import static de.bioforscher.singa.features.parameters.EnvironmentalParameters.getTransformedMolarConcentration;
import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static tec.uom.se.unit.Units.MINUTE;

/**
 * @author cl
 */
public class MonovalentReceptorBindingTest {

    private static final Logger logger = LoggerFactory.getLogger(MonovalentReceptorBindingTest.class);

    @Test
    public void shouldInitializeCorrectly() {

        logger.info("Testing Monovalent Receptor Binding.");

        // see Receptors (Lauffenburger) p. 30
        // prazosin, CHEBI:8364
        ChemicalEntity ligand = new SmallMolecule.Builder("ligand")
                .name("prazosin")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:8364"))
                .build();

        // the corresponding rate constants
        RateConstant forwardsRate = RateConstant.create(2.4e8).forward().firstOrder().timeUnit(MINUTE).build();
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
        // compartments
        EnclosedCompartment left = new EnclosedCompartment("LC", "Left");
        EnclosedCompartment right = new EnclosedCompartment("RC", "Right");
        Membrane membrane = Membrane.forCompartment(right);
        // concentrations
        AutomatonNode membraneNode = automatonGraph.getNode(0, 0);
        membraneNode.setState(CellSectionState.MEMBRANE);
        MembraneContainer concentrationContainer = new MembraneContainer(left, right, membrane);
        membraneNode.setConcentrationContainer(concentrationContainer);
        membraneNode.setAvailableConcentration(ligand, left, Quantities.getQuantity(0.1, MOLE_PER_LITRE).to(getTransformedMolarConcentration()));
        membraneNode.setAvailableConcentration(receptor, membrane.getOuterLayer(), Quantities.getQuantity(0.1, MOLE_PER_LITRE).to(getTransformedMolarConcentration()));

        // define sections
        Set<CellSection> sections = new HashSet<>();
        sections.add(left);
        sections.add(membrane.getOuterLayer());
        sections.add(membrane.getInnerLayer());
        sections.add(right);
        concentrationContainer.setReferencedSections(sections);

        // create and add module
        ComplexBuildingReaction.inSimulation(simulation)
                .of(ligand, forwardsRate)
                .in(CellSectionState.NON_MEMBRANE)
                .by(receptor, backwardsRate)
                .to(CellSectionState.MEMBRANE);

        for (int i = 0; i < 10; i++) {
            simulation.nextEpoch();
        }

    }

}