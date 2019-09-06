package bio.singa.chemistry.reactions;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.EntityRegistry;
import bio.singa.chemistry.entities.complex.BindingSite;
import bio.singa.chemistry.entities.complex.ComplexEntity;
import bio.singa.chemistry.entities.complex.GraphComplexNode;
import bio.singa.chemistry.reactions.reactors.ComplexReactor;
import bio.singa.chemistry.reactions.reactors.ReactionChain;
import bio.singa.chemistry.reactions.reactors.ReactionElement;
import bio.singa.core.utility.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author cl
 */
public class ReactionNetworkGenerator {

    private static final Logger logger = LoggerFactory.getLogger(ReactionNetworkGenerator.class);

    private Set<ComplexEntity> possibleEntities;
    private List<ReactionChain> reactionChains;
    private List<ReactionChain> preReactions;

    public ReactionNetworkGenerator() {
        possibleEntities = new HashSet<>();
        reactionChains = new ArrayList<>();
        preReactions = new ArrayList<>();
    }

    public void add(ReactionChain reactionChain) {
        reactionChains.add(reactionChain);
    }

    public void addPreReaction(ReactionChain reactionChain) {
        preReactions.add(reactionChain);
    }

    public void generate() {
        Map<ChemicalEntity, Set<BindingSite>> bindingSites = determineBindingSites();
        possibleEntities = createInitialEntities(bindingSites);
        performPrereactions(bindingSites);
        logBindingSites();
        boolean reactionsUnstable;
        do {
            reactionsUnstable = false;
            for (ReactionChain reactionChain : reactionChains) {
                int previousNumberOfReactions = reactionChain.getReactantElements().size();
                reactionChain.process(possibleEntities);
                Set<ReactionElement> reactantElements = reactionChain.getReactantElements();
                reactantElements.stream()
                        .map(ReactionElement::getSubstrates)
                        .forEach(possibleEntities::addAll);
                reactantElements.stream()
                        .map(ReactionElement::getProducts)
                        .forEach(possibleEntities::addAll);
                int updatedNumberOfReactions = reactantElements.size();
                if (updatedNumberOfReactions != previousNumberOfReactions) {
                    reactionsUnstable = true;
                }
            }
            if (reactionsUnstable) {
                debugLogCreatedReactions();
                logger.debug("repeating since reactions were unstable");
            }
        } while (reactionsUnstable);
        intoLogCreatedReactions();
        registerEntities();
    }

    private void performPrereactions(Map<ChemicalEntity, Set<BindingSite>> bindingSites) {
        Set<ComplexEntity> prereactionEntities = createInitialEntities(bindingSites);
        Set<ComplexEntity> prereactionSubstrates = new HashSet<>();
        Set<ComplexEntity> prereactionProducts = new HashSet<>();
        boolean reactionsUnstable;
        do {
            reactionsUnstable = false;
            for (ReactionChain reactionChain : preReactions) {
                int previousNumberOfReactions = reactionChain.getReactantElements().size();
                reactionChain.process(prereactionEntities);
                Set<ReactionElement> reactantElements = reactionChain.getReactantElements();
                reactantElements.stream()
                        .map(ReactionElement::getSubstrates)
                        .forEach(substrates -> {
                            prereactionEntities.addAll(substrates);
                            prereactionSubstrates.addAll(substrates);
                        });
                reactantElements.stream()
                        .map(ReactionElement::getProducts)
                        .forEach(products -> {
                            prereactionEntities.addAll(products);
                            prereactionProducts.addAll(products);
                        });
                int updatedNumberOfReactions = reactantElements.size();
                if (updatedNumberOfReactions != previousNumberOfReactions) {
                    reactionsUnstable = true;
                }
            }
            if (reactionsUnstable) {
                debugLogCreatedReactions();
                logger.debug("repeating since reactions were unstable");
            }
        } while (reactionsUnstable);
        possibleEntities.removeAll(prereactionSubstrates);
        possibleEntities.addAll(prereactionProducts);
    }

    private void registerEntities() {
            possibleEntities.forEach(EntityRegistry::put);
    }

    private void logBindingSites() {
        logger.debug("assigned binding sites:");
        for (ComplexEntity possibleEntity : possibleEntities) {
            for (GraphComplexNode node : possibleEntity.getNodes()) {
                logger.debug("  {}: {}", node.getEntity().getIdentifier(), node.getBindingSites());
            }
        }
    }

    private void debugLogCreatedReactions() {
        for (ReactionChain reactionChain : reactionChains) {
            logger.debug("rule {} produced the following reactions: ", reactionChain.getIdentifier());
            for (ReactionElement reactantElement : reactionChain.getReactantElements()) {
                logger.debug("  {}", reactantElement);
            }
        }
    }

    private void intoLogCreatedReactions() {
        for (ReactionChain reactionChain : reactionChains) {
            logger.info("rule {} produced the following reactions: ", reactionChain.getIdentifier());
            for (ReactionElement reactantElement : reactionChain.getReactantElements()) {
                logger.info("  {}", reactantElement);
            }
        }
    }

    public Map<ChemicalEntity, Set<BindingSite>> determineBindingSites() {
        List<ReactionChain> allChains = new ArrayList<>(reactionChains);
        allChains.addAll(preReactions);
        Map<ChemicalEntity, Set<BindingSite>> bindingSiteMapping = new HashMap<>();
        for (ReactionChain reactionChain : allChains) {
            for (ComplexReactor reactor : reactionChain.getReactors()) {

                Map.Entry<BindingSite, Pair<ChemicalEntity>> siteEntry = reactor.getBindingSite();
                ChemicalEntity primaryEntity = siteEntry.getValue().getFirst();
                ChemicalEntity secondaryEntity = siteEntry.getValue().getSecond();
                BindingSite bindingSite = siteEntry.getKey();

                if (!bindingSiteMapping.containsKey(primaryEntity)) {
                    bindingSiteMapping.put(primaryEntity, new HashSet<>());
                }
                bindingSiteMapping.get(primaryEntity).add(bindingSite);

                if (!bindingSiteMapping.containsKey(secondaryEntity)) {
                    bindingSiteMapping.put(secondaryEntity, new HashSet<>());
                }
                bindingSiteMapping.get(secondaryEntity).add(bindingSite);

            }
        }
        return bindingSiteMapping;
    }

    public Set<ComplexEntity> createInitialEntities(Map<ChemicalEntity, Set<BindingSite>> bindingSiteMapping) {
        Set<ComplexEntity> possibleEntities = new HashSet<>();
        // create base graphs
        for (Map.Entry<ChemicalEntity, Set<BindingSite>> entry : bindingSiteMapping.entrySet()) {
            ChemicalEntity entity = entry.getKey();
            Set<BindingSite> bindingSites = entry.getValue();
            possibleEntities.add(ComplexEntity.from(entity, bindingSites));
        }
        return possibleEntities;
    }

}
