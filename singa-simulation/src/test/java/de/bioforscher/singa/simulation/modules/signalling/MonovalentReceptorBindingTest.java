package de.bioforscher.singa.simulation.modules.signalling;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.entities.ComplexedChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.entities.Receptor;
import de.bioforscher.singa.chemistry.descriptive.entities.SmallMolecule;
import de.bioforscher.singa.chemistry.descriptive.features.reactions.BackwardsRateConstant;
import de.bioforscher.singa.chemistry.descriptive.features.reactions.ForwardsRateConstant;
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

import static de.bioforscher.singa.chemistry.descriptive.features.reactions.TurnoverNumber.PER_MINUTE;
import static de.bioforscher.singa.features.model.FeatureOrigin.MANUALLY_ANNOTATED;
import static de.bioforscher.singa.features.parameters.EnvironmentalParameters.getTransformedMolarConcentration;
import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;

/**
 * @author cl
 */
public class MonovalentReceptorBindingTest {

    private static final Logger logger = LoggerFactory.getLogger(MonovalentReceptorBindingTest.class);

    @Test
    public void shouldInitializeCorrectly() {

        logger.info("Testing Monovalent Receptor Binding Module.");

        // see Receptors (Lauffenburger) p. 30
        // prazosin, CHEBI:8364
        ChemicalEntity ligand = new SmallMolecule.Builder("ligand")
                .name("prazosin")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:8364"))
                .build();
        // the corresponding rate constants
        ForwardsRateConstant forwardsRateConstant = new ForwardsRateConstant(Quantities.getQuantity(2.4e8, PER_MINUTE), MANUALLY_ANNOTATED);
        BackwardsRateConstant backwardsRateConstant = new BackwardsRateConstant(Quantities.getQuantity(0.018, PER_MINUTE), MANUALLY_ANNOTATED);
        // alpha-1 adrenergic receptor, P35348
        Receptor receptor = new Receptor.Builder("receptor")
                .name("alpha-1 adrenergic receptor")
                .additionalIdentifier(new UniProtIdentifier("P35348"))
                .addLigand(ligand, forwardsRateConstant, backwardsRateConstant)
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
        for (ComplexedChemicalEntity boundReceptor : receptor.getBoundReceptorStates()) {
            membraneNode.setAvailableConcentration(boundReceptor, membrane.getOuterLayer(), Quantities.getQuantity(0.1, MOLE_PER_LITRE).to(getTransformedMolarConcentration()));
        }
        // define chemical entities
        simulation.addReferencedEntity(ligand);
        simulation.addReferencedEntity(receptor);
        simulation.addReferencedEntities(receptor.getBoundReceptorStates());
        // define sections
        Set<CellSection> sections = new HashSet<>();
        sections.add(left);
        sections.add(membrane.getOuterLayer());
        sections.add(membrane.getInnerLayer());
        sections.add(right);
        concentrationContainer.setReferencedSections(sections);

        // create and add module
        MonovalentReceptorBinding.inSimulation(simulation)
                .receptor(receptor)
                .build();

        for (int i = 0; i < 10; i++) {
            simulation.nextEpoch();
        }

    }

}