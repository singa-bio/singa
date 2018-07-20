package de.bioforscher.singa.simulation.parser.sbml.converter;

import de.bioforscher.singa.chemistry.entities.ChemicalEntity;
import de.bioforscher.singa.simulation.model.modules.concentration.imlementations.DynamicReaction;
import de.bioforscher.singa.simulation.model.modules.concentration.reactants.CatalyticReactant;
import de.bioforscher.singa.simulation.model.modules.concentration.reactants.KineticLaw;
import de.bioforscher.singa.simulation.model.modules.concentration.reactants.ReactantRole;
import de.bioforscher.singa.simulation.model.modules.concentration.reactants.StoichiometricReactant;
import de.bioforscher.singa.simulation.model.parameters.SimulationParameter;
import de.bioforscher.singa.simulation.model.simulation.Simulation;
import de.bioforscher.singa.simulation.parser.sbml.FunctionReference;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.ModifierSpeciesReference;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SpeciesReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.Unit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Converts JSBML Reactions to SiNGA Reactions
 *
 * @author cl
 */
public class SBMLReactionConverter {

    private static final Logger logger = LoggerFactory.getLogger(SBMLReactionConverter.class);

    private final Map<String, ChemicalEntity> entities;

    private final SBMLKineticLawConverter kineticLawConverter;
    private DynamicReaction currentReaction;

    public SBMLReactionConverter(Map<String, Unit<?>> units, Map<String, ChemicalEntity> entities, Map<String, FunctionReference> functions, Map<String, SimulationParameter<?>> globalParameters) {
        this.entities = entities;
        kineticLawConverter = new SBMLKineticLawConverter(units, functions, globalParameters);
    }

    public List<DynamicReaction> convertReactions(ListOf<Reaction> sbmlReactions) {
        List<DynamicReaction> reactions = new ArrayList<>();
        for (Reaction reaction : sbmlReactions) {
            reactions.add(convertReaction(reaction));
        }
        return reactions;
    }

    public DynamicReaction convertReaction(Reaction reaction) {
        Simulation simulation = new Simulation();
        logger.debug("Parsing Reaction {} ...", reaction.getName());
        KineticLaw kineticLaw = kineticLawConverter.convertKineticLaw(reaction.getKineticLaw());
        currentReaction = new DynamicReaction();
        currentReaction.setSimulation(simulation);
        currentReaction.setKineticLaw(kineticLaw);
        assignSubstrates(reaction.getListOfReactants());
        assignProducts(reaction.getListOfProducts());
        assignModifiers(reaction.getListOfModifiers());
        logger.debug("Parsed Reaction:{}", currentReaction.getReactionString());
        return currentReaction;
    }

    private void assignSubstrates(ListOf<SpeciesReference> substrates) {
        for (SpeciesReference reference : substrates) {
            logger.debug("Assigning Chemical Entity {} as substrate.", reference.getSpecies());
            String identifier = reference.getSpecies();
            currentReaction.getKineticLaw().referenceChemicalEntityToParameter(identifier, entities.get(identifier));
            currentReaction.getStoichiometricReactants().add(new StoichiometricReactant(entities.get(identifier), ReactantRole.DECREASING, reference.getStoichiometry()));
        }
    }

    private void assignProducts(ListOf<SpeciesReference> products) {
        for (SpeciesReference reference : products) {
            logger.debug("Assigning Chemical Entity {} as product.", reference.getSpecies());
            String identifier = reference.getSpecies();
            currentReaction.getKineticLaw().referenceChemicalEntityToParameter(identifier, entities.get(identifier));
            currentReaction.getStoichiometricReactants().add(new StoichiometricReactant(entities.get(identifier), ReactantRole.INCREASING, reference.getStoichiometry()));
        }
    }

    private void assignModifiers(ListOf<ModifierSpeciesReference> modifiers) {
        for (ModifierSpeciesReference reference : modifiers) {
            logger.debug("Assigning Chemical Entity {} as catalytic modifier.", reference.getSpecies());
            String identifier = reference.getSpecies();
            currentReaction.getKineticLaw().referenceChemicalEntityToParameter(identifier, entities.get(identifier));
            currentReaction.getCatalyticReactants().add(new CatalyticReactant(entities.get(identifier), ReactantRole.INCREASING));
        }
    }


}
