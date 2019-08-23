package bio.singa.simulation.model.rules.reactions;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.ComplexEntity;
import bio.singa.chemistry.entities.ModificationSite;
import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.core.utility.ListHelper;
import bio.singa.core.utility.Pair;
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
import static bio.singa.chemistry.entities.graphcomplex.ModificationOperation.*;

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
                removeDuplicatesFromPossibleEntities();

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
        addPossibleEntitiesFromReactants(rule);
        // remove duplicates
        possibleEntities = ListHelper.removeDuplicates(possibleEntities);
    }

    private List<ReactantSet> generateReactantSets(ReactionRule rule, List<List<Reactant>> reactionCandidates) {
        List<ReactantSet> reactantSets = new ArrayList<>();
        List<List<Reactant>> validSubstrateCombinations = StreamPermutations.permutations(reactionCandidates);
        for (List<Reactant> substrates : validSubstrateCombinations) {
            List<Reactant> products = new ArrayList<>();
            products.addAll(determineProductsForSubstrateCombination(substrates, rule));
            products.addAll(collectSimpleProducts(rule));
            addPossibleEntitiesFromProducts(products);
            ReactantSet reactantSet = new ReactantSet(substrates, products, Collections.emptyList());
            reactantSets.add(reactantSet);
        }
        return reactantSets;
    }

    private void determineBindingSites() {
        modifications = new HashMap<>();
        // determine entities which require binding sites
        for (ReactionRule reactionRule : reactions.keySet()) {
            for (ReactantInformation reactantInformation : reactionRule.getReactantInformation()) {
                for (ReactantModification modification : reactantInformation.getModifications()) {
                    if (modification.getOperationType().equals(BIND) || modification.getOperationType().equals(ADD)) {
                        markForModification(modification.getTarget(), modification.getSite());
                        if (!(modification.getModificator() instanceof SmallMolecule)) {
                            markForModification(modification.getModificator(), modification.getSite());
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

    private List<Reactant> determineProductsForSubstrateCombination(List<Reactant> reactionCandidates, ReactionRule rule) {
        if (reactionCandidates.size() > 2) {
            logger.warn("Passed more than two reaction candidates to modification. This might result in unexpected modification.");
        }
        if (reactionCandidates.size() < 1) {
            throw new IllegalStateException("Modification require at least one substrates, but none was passed after substrate validation.");
        }
        ComplexEntity mainProductEntity = ((ComplexEntity) reactionCandidates.get(0).getEntity());
        ComplexEntity byproductEntity = null;
        for (ReactantInformation reactantInformation : rule.getReactantInformation()) {
            for (ReactantModification modification : reactantInformation.getModifications()) {
                switch (modification.getOperationType()) {
                    case BIND: {
                        if (reactionCandidates.size() != 2) {
                            throw new IllegalStateException("Binding modification require two substrates, but only one was passed after substrate validation.");
                        }
                        ChemicalEntity secondEntity = reactionCandidates.get(1).getEntity();
                        mainProductEntity = modification.apply(mainProductEntity, secondEntity);
                        break;
                    }
                    case RELEASE: {
                        // the first element is the original entity
                        Pair<ComplexEntity> complexPair = modification.release(mainProductEntity);
                        mainProductEntity = complexPair.getFirst();
                        // the second part is split of
                        byproductEntity = complexPair.getSecond();
                        break;
                    }
                    case ADD:
                    case REMOVE: {
                        // if target does not contain specified modification site try the other
                        if (byproductEntity != null) {
                            if (modification.getOperationType().equals(REMOVE) && mainProductEntity.find(modification.getModificator()) == null) {
                                byproductEntity = modification.apply(byproductEntity);
                            }
                        } else {
                            mainProductEntity = modification.apply(mainProductEntity);
                        }
                        break;
                    }
                    default:
                        throw new IllegalStateException("Unknown modification operation.");
                }
            }
        }
        if (mainProductEntity == null) {
            throw new IllegalStateException("No product could be created.");
        }
        Reactant mainProduct = new Reactant(mainProductEntity, PRODUCT, fromBoundStatus(mainProductEntity.isMembraneBound()));
        if (byproductEntity == null) {
            return Collections.singletonList(mainProduct);
        } else {
            List<Reactant> reactants = new ArrayList<>();
            reactants.add(mainProduct);
            reactants.add(new Reactant(byproductEntity, PRODUCT, fromBoundStatus(byproductEntity.isMembraneBound())));
            return reactants;
        }
    }

    private List<Reactant> collectSimpleProducts(ReactionRule rule) {
        return rule.getReactantInformation().stream()
                .map(ReactantInformation::getReactant)
                .filter(reactant -> reactant.getRole().equals(PRODUCT))
                .collect(Collectors.toList());
    }

    private void addPossibleEntitiesFromReactants(ReactionRule rule) {
        rule.getReactantInformation().stream()
                .map(information -> information.getReactant().getEntity())
                .forEach(possibleEntities::add);
    }

    private void addPossibleEntitiesFromProducts(List<Reactant> products) {
        products.stream()
                .map(Reactant::getEntity)
                .forEach(possibleEntities::add);
    }

    private void removeDuplicatesFromPossibleEntities() {
        possibleEntities = possibleEntities.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    private void markForModification(ChemicalEntity entity, ModificationSite modification) {
        if (!modifications.containsKey(entity)) {
            modifications.put(entity, new LinkedHashSet<>());
        }
        modifications.get(entity).add(modification);
    }

    public void setPrereaction(ReactionRule rule) {
        rule.setProductsOnly(true);
        productOnlyRules.add(rule);
    }

    public static CellTopology fromBoundStatus(boolean membraneBound) {
        if (membraneBound) {
            return CellTopology.MEMBRANE;
        } else {
            return CellTopology.INNER;
        }
    }

}
