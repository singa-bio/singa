package bio.singa.simulation.model.rules.reactions;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.ComplexEntity;
import bio.singa.chemistry.entities.ModificationSite;
import bio.singa.mathematics.combinatorics.StreamPermutations;
import bio.singa.mathematics.graphs.trees.BinaryTreeNode;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.Reactant;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.ReactantRole;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.ReactantSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class ReactionRuleAggregator {

    private static final Logger logger = LoggerFactory.getLogger(ReactionRuleAggregator.class);

    private List<ReactionRule> rules;

    private List<ChemicalEntity> possibleEntities;

    private List<ReactantSet> reactantSets;

    public ReactionRuleAggregator() {
        rules = new ArrayList<>();
        possibleEntities = new ArrayList<>();
        reactantSets = new ArrayList<>();
    }

    public void addRule(ReactionRule rule) {
        rules.add(rule);
        aggregate(rule);
    }

    private void aggregate(ReactionRule rule) {
        // append new modification sites
        addEntitiesFromReactants(rule);
        // append modifications defined in the rule
        assignBindingSites();
        removeEntityDuplicates();
        // determine candidates for eac reactant
        List<List<Reactant>> reactionCandidates = determineReactionCandidates(rule);
        // determine possible modifications
        List<List<Reactant>> validSubstrateCombinations = StreamPermutations.permutations(reactionCandidates);
        for (List<Reactant> substrates : validSubstrateCombinations) {
            Reactant product = determineProductForSubstrateCombination(substrates, rule);
            possibleEntities.add(product.getEntity());
            ReactantSet reactantSet = new ReactantSet(substrates, Collections.singletonList(product), Collections.emptyList());
            System.out.println(reactantSet);
            System.out.println(((ComplexEntity) product.getEntity()).nonSiteString());
        }
        System.out.println();

    }

    private void assignBindingSites() {
        ListIterator<ChemicalEntity> iterator = possibleEntities.listIterator();
        // iterate possible entities
        outer:
        while (iterator.hasNext()) {
            ChemicalEntity currentEntity = iterator.next();
            // iterate rules
            for (ReactionRule rule : rules) {
                // iterate reactants
                for (ReactantInformation information : rule.getReactantInformation()) {
                    if (information.getModifications() == null) {
                        continue;
                    }
                    // iterate modifications
                    for (ReactantModification modification : information.getModifications()) {
                        if (modification.getOperationType().equals(ModificationOperation.REMOVE)) {
                            continue;
                        }
                        ComplexEntity complexEntity;
                        if (currentEntity instanceof ComplexEntity) {
                            complexEntity = (ComplexEntity) currentEntity;
                        } else {
                            // non complexes are replaced with complex versions
                            if (currentEntity.equals(modification.getTarget())) {
                                iterator.remove();
                                iterator.add(modification.getTargetSite());
                                continue outer;
                            } else if (currentEntity.equals(modification.getModificator())) {
                                iterator.remove();
                                iterator.add(modification.getModificationSite());
                                continue outer;
                            }
                            continue;
                        }
                        // skip entities which already have the current site
                        if (complexEntity.find(modification.getSite()) != null) {
                            continue;
                        }
                        // get the target of the modification
                        BinaryTreeNode<ChemicalEntity> targetNode = complexEntity.find(modification.getTarget());
                        // if the target is available
                        if (targetNode != null) {
                            // replace it with the combination of target and binding site
                            complexEntity.replace(modification.getTargetSite(), modification.getTarget());
                            continue;
                        }
                        // if this is no binding modification
                        if (!modification.getOperationType().equals(ModificationOperation.BIND)) {
                            continue;
                        }
                        // get the target of a binding modification
                        BinaryTreeNode<ChemicalEntity> modificatorNode = complexEntity.find(modification.getModificator());
                        // if the target is available
                        if (modificatorNode != null) {
                            // replace it with the combination of target and binding site
                            complexEntity.replace(modification.getModificationSite(), modification.getModificator());
                        }


                    }
                }
            }
        }
    }

    private void removeEntityDuplicates() {
        possibleEntities = possibleEntities.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    private List<List<Reactant>> determineReactionCandidates(ReactionRule rule) {
        List<List<Reactant>> candiadates = new ArrayList<>();
        // have a look at all entities
        for (ReactantInformation information : rule.getReactantInformation()) {
            // skip non substrates
            if (information.getReactant().getRole() != ReactantRole.SUBSTRATE) {
                continue;
            }
            // collect the matches for each reactant
            List<Reactant> matches = new ArrayList<>();
            matchLoop:
            for (ChemicalEntity possibleEntity : possibleEntities) {
                if (!(possibleEntity instanceof ComplexEntity)) {
                    continue;
                }
                ComplexEntity complexEntity = (ComplexEntity) possibleEntity;
                // skip entities that do not contain the target
                if (complexEntity.find(information.getReactant().getEntity()) == null) {
                    continue;
                }
                // filter based on modifications
                for (ReactantModification modification : information.getModifications()) {
                    BinaryTreeNode<ChemicalEntity> bindingSite = complexEntity.find(modification.getSite());
                    // skip if the required site is not present
                    if (bindingSite == null) {
                        continue;
                    }

                    if (modification.getOperationType().equals(ModificationOperation.REMOVE)) {
                        // if removing something skip entities whose required binding sites are not occupied
                        ModificationSite targetSite = (ModificationSite) bindingSite.getData();
                        if (!targetSite.isOccupied()) {
                            continue matchLoop;
                        }
                    } else {
                        // if adding something skip entities whose required binding sites would already be occupied
                        ModificationSite targetSite = (ModificationSite) bindingSite.getData();
                        if (targetSite.isOccupied()) {
                            continue matchLoop;
                        }
                    }
                }
                // skip entities that fail any condition
                if (!ReactantCondition.testAll(information.getConditions(), complexEntity)) {
                    continue;
                }
                matches.add(new Reactant(complexEntity, ReactantRole.SUBSTRATE));
            }
            candiadates.add(matches);
        }
        return candiadates;
    }

    private Reactant determineProductForSubstrateCombination(List<Reactant> reactionCandidates, ReactionRule rule) {
        if (reactionCandidates.size() > 2) {
            logger.warn("Passed more than two reaction candidates to modification. This might result in unexpected modification.");
        }
        if (reactionCandidates.size() < 1) {
            throw new IllegalStateException("Modification require at least one substrates, but none was passed after substrate validation.");
        }
        ComplexEntity product = ((ComplexEntity) reactionCandidates.get(0).getEntity());
        for (ReactantInformation reactantInformation : rule.getReactantInformation()) {
            for (ReactantModification modification : reactantInformation.getModifications()) {
                switch (modification.getOperationType()) {
                    case BIND: {
                        if (reactionCandidates.size() != 2) {
                            throw new IllegalStateException("Binding modification require two substrates, but only one was passed after substrate validation.");
                        }
                        ComplexEntity secondEntity = (ComplexEntity) reactionCandidates.get(1).getEntity();
                        product = modification.apply(product, secondEntity);
                        break;
                    }
                    case ADD:
                    case REMOVE: {
                        product = modification.apply(product);
                        break;
                    }
                    default:
                        throw new IllegalStateException("Unknown modification operation.");
                }
            }
        }
        if (product == null) {
            throw new IllegalStateException("No product could be created.");
        }
        return new Reactant(product, ReactantRole.PRODUCT);
    }


    private void addEntitiesFromReactants(ReactionRule rule) {
        rule.getReactantInformation().stream()
                .map(information -> information.getReactant().getEntity())
                .forEach(entity -> possibleEntities.add(entity));
    }


}
