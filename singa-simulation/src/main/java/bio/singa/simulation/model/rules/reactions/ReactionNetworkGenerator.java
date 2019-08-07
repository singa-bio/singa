package bio.singa.simulation.model.rules.reactions;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.ComplexEntity;
import bio.singa.chemistry.entities.ModificationSite;
import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.core.utility.ListHelper;
import bio.singa.mathematics.combinatorics.StreamPermutations;
import bio.singa.mathematics.graphs.trees.BinaryTreeNode;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.Reactant;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.ReactantSet;
import bio.singa.simulation.model.sections.CellTopology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.ReactantRole.PRODUCT;
import static bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.ReactantRole.SUBSTRATE;
import static bio.singa.simulation.model.rules.reactions.ModificationOperation.*;

/**
 * @author cl
 */
public class ReactionNetworkGenerator {

    private static final Logger logger = LoggerFactory.getLogger(ReactionNetworkGenerator.class);

    private List<ChemicalEntity> possibleEntities;

    private HashMap<ReactionRule, List<ReactantSet>> reactions;
    private Map<ChemicalEntity, Set<ModificationSite>> modifications;

    private List<ReactionRule> productOnlyRules;

    public ReactionNetworkGenerator() {
        possibleEntities = new ArrayList<>();
        reactions = new LinkedHashMap<>();
        productOnlyRules = new ArrayList<>();
    }

    public List<ReactantSet> addRule(ReactionRule rule) {
        if (rule.isProductsOnly()) {
            productOnlyRules.add(rule);
        }
        List<ReactantSet> setList = new ArrayList<>();
        reactions.put(rule, setList);
        determineEntites(rule);
        return setList;
    }

    public void generateNetwork() {
        determineBindingSites();
        prereact();
        logger.info("Generating reaction network.");
        boolean reactionsUnstable;
        do {
            reactionsUnstable = false;
            for (ReactionRule innerRule : reactions.keySet()) {
                // determine candidates for each reactant
                List<List<Reactant>> reactionCandidates = determineReactionCandidates(innerRule);
                // determine possible modifications
                List<ReactantSet> newReactantSets = generateReactantSets(innerRule, reactionCandidates);
                removeEntityDuplicates();

                List<ReactantSet> existingReactantSets = reactions.get(innerRule);
                int previousNumberOfReactions = existingReactantSets.size();
                for (ReactantSet currentReactantSet : newReactantSets) {
                    if (!existingReactantSets.contains(currentReactantSet)) {
                        existingReactantSets.add(currentReactantSet);
                    }
                }
                int updatedNumberOfReactions = existingReactantSets.size();
                if (updatedNumberOfReactions != previousNumberOfReactions) {
                    reactionsUnstable = true;
                }
            }
        } while (reactionsUnstable);
        logReactantSets();
    }

    private void logReactantSets() {
        for (Map.Entry<ReactionRule, List<ReactantSet>> entry : reactions.entrySet()) {
            logger.info("rule {} produced the following reactions: ", entry.getKey());
            for (ReactantSet reactantSet : entry.getValue()) {
                List<String> substrates = new ArrayList<>();
                for (Reactant substrate : reactantSet.getSubstrates()) {
                    if (substrate.getEntity() instanceof ComplexEntity) {
                        substrates.add(((ComplexEntity) substrate.getEntity()).getReferenceIdentifier());
                    } else {
                        substrates.add(substrate.getEntity().getIdentifier());
                    }
                }
                List<String> products = new ArrayList<>();
                for (Reactant product : reactantSet.getProducts()) {
                    if (product.getEntity() instanceof ComplexEntity) {
                        products.add(((ComplexEntity) product.getEntity()).getReferenceIdentifier());
                    } else {
                        products.add(product.getEntity().getIdentifier());
                    }
                }
                String reactionString = String.join(" + ", substrates) + " -> " + String.join(" + ", products);
                logger.info(reactionString);
            }
        }
    }

    private void prereact() {
        for (ReactionRule productOnlyRule : productOnlyRules) {
            // determine candidates for each reactant
            List<List<Reactant>> reactionCandidates = determineReactionCandidates(productOnlyRule);
            // determine possible modifications
            List<ReactantSet> newReactantSets = generateReactantSets(productOnlyRule, reactionCandidates);

            for (List<Reactant> reactionCandidateList : reactionCandidates) {
                for (Reactant reactant : reactionCandidateList) {
                    possibleEntities.remove(reactant.getEntity());
                }
            }

            for (ReactantSet newReactantSet : newReactantSets) {
                for (Reactant product : newReactantSet.getProducts()) {
                    possibleEntities.add(product.getEntity());
                }
            }
        }
        // remove duplicates
        possibleEntities = ListHelper.removeDuplicates(possibleEntities);
    }

    private void determineEntites(ReactionRule rule) {
        // append new modification sites
        addEntitiesFromReactants(rule);
        // remove duplicates
        possibleEntities = ListHelper.removeDuplicates(possibleEntities);
    }

    private List<ReactantSet> generateReactantSets(ReactionRule rule, List<List<Reactant>> reactionCandidates) {
        List<ReactantSet> reactantSets = new ArrayList<>();
        List<List<Reactant>> validSubstrateCombinations = StreamPermutations.permutations(reactionCandidates);
        for (List<Reactant> substrates : validSubstrateCombinations) {
            Reactant product = determineProductForSubstrateCombination(substrates, rule);
            possibleEntities.add(product.getEntity());
            List<Reactant> products = collectProducts(rule, product);
            products.addAll(collectReleasedEntities(rule, substrates));
            ReactantSet reactantSet = new ReactantSet(substrates, products, Collections.emptyList());
            reactantSets.add(reactantSet);
        }
        return reactantSets;
    }

    private List<Reactant> collectReleasedEntities(ReactionRule rule, List<Reactant> substrates) {
        List<Reactant> products = new ArrayList<>();
        for (ReactantInformation reactantInformation : rule.getReactantInformation()) {
            for (ReactantModification modification : reactantInformation.getModifications()) {
                if (modification.getOperationType().equals(RELEASE)) {
                    for (Reactant substrate : substrates) {
                        ComplexEntity complexEntity = (ComplexEntity) substrate.getEntity();
                        List<BinaryTreeNode<ChemicalEntity>> path = complexEntity.pathTo(modification.getModificator());
                        BinaryTreeNode<ChemicalEntity> parent = path.get(path.size() - 2);
                        ComplexEntity resultingEntity = ComplexEntity.from(modification.getSite(), parent.getData());
                        Reactant reactant = determineProductForSubstrateCombination(Collections.singletonList(new Reactant(resultingEntity, SUBSTRATE, fromBoundStatus(resultingEntity.isMembraneBound()))), rule);
                        products.add(reactant);
                    }
                }
            }
        }
        return products;
    }

    private List<Reactant> collectProducts(ReactionRule rule, Reactant product) {
        List<Reactant> products = new ArrayList<>();
        products.add(product);
        for (ReactantInformation reactantInformation : rule.getReactantInformation()) {
            Reactant candidate = reactantInformation.getReactant();
            if (candidate.getRole().equals(PRODUCT)) {
                products.add(candidate);
            }
        }
        return products;
    }

    private void determineBindingSites() {
        modifications = new HashMap<>();
        // determine entities which require binding sites
        for (ReactionRule reactionRule : reactions.keySet()) {
            for (ReactantInformation reactantInformation : reactionRule.getReactantInformation()) {
                for (ReactantModification modification : reactantInformation.getModifications()) {
                    if (modification.getOperationType().equals(BIND) || modification.getOperationType().equals(ADD)) {
                        markModification(modification.getTarget(), modification.getSite());
                        if (!(modification.getModificator() instanceof SmallMolecule)) {
                            markModification(modification.getModificator(), modification.getSite());
                        }
                    }
                }
            }
        }
        // replace vanilla versions
        for (Map.Entry<ChemicalEntity, Set<ModificationSite>> entry : modifications.entrySet()) {
            ModificationSite[] modificationSites = entry.getValue().toArray(new ModificationSite[0]);
            ComplexEntity complexEntity = ComplexEntity.from(entry.getKey(), modificationSites);
            possibleEntities.remove(entry.getKey());
            possibleEntities.add(complexEntity);
        }
    }

    private void markModification(ChemicalEntity entity, ModificationSite modification) {
        if (!modifications.containsKey(entity)) {
            modifications.put(entity, new LinkedHashSet<>());
        }
        modifications.get(entity).add(modification);
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
            if (information.getReactant().getRole() != SUBSTRATE) {
                continue;
            }

            // collect the matches for each reactant
            List<Reactant> matches = new ArrayList<>();
            // is this is a small molecule
            if (information.getReactant().getEntity() instanceof SmallMolecule) {
                SmallMolecule smallMoleculeReactant = (SmallMolecule) information.getReactant().getEntity();
                for (ChemicalEntity possibleEntity : possibleEntities) {
                    if (possibleEntity.equals(smallMoleculeReactant)) {
                        matches.add(information.getReactant());
                        break;
                    }
                }
            } else {
                matchLoop:
                for (ChemicalEntity possibleEntity : possibleEntities) {
                    // no complex molecule
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

                        if (modification.getOperationType().equals(REMOVE) || modification.getOperationType().equals(RELEASE)) {
                            // if removing something skip entities whose required binding sites are not occupied
                            ModificationSite targetSite = (ModificationSite) bindingSite.getData();
                            if (!targetSite.isOccupied()) {
                                continue matchLoop;
                            }
                        }
                    }
                    // skip entities that fail any condition
                    if (!ReactantCondition.testAll(information.getConditions(), complexEntity)) {
                        continue;
                    }
                    matches.add(new Reactant(complexEntity, SUBSTRATE, fromBoundStatus(complexEntity.isMembraneBound())));
                }
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
                        ChemicalEntity secondEntity = reactionCandidates.get(1).getEntity();
                        product = modification.apply(product, secondEntity);
                        break;
                    }
                    case ADD:
                    case RELEASE:
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
        return new Reactant(product, PRODUCT, fromBoundStatus(product.isMembraneBound()));
    }


    private void addEntitiesFromReactants(ReactionRule rule) {
        rule.getReactantInformation().stream()
                .map(information -> information.getReactant().getEntity())
                .forEach(entity -> possibleEntities.add(entity));
    }


    public static CellTopology fromBoundStatus(boolean membraneBound) {
        if (membraneBound) {
            return CellTopology.MEMBRANE;
        } else {
            return CellTopology.INNER;
        }
    }

}
