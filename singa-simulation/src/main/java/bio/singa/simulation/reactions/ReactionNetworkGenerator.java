package bio.singa.simulation.reactions;

import bio.singa.simulation.entities.ChemicalEntity;
import bio.singa.simulation.entities.EntityRegistry;
import bio.singa.simulation.entities.BindingSite;
import bio.singa.simulation.entities.ComplexEntity;
import bio.singa.simulation.entities.GraphComplexNode;
import bio.singa.simulation.reactions.reactors.ComplexReactor;
import bio.singa.simulation.reactions.reactors.ReactionChain;
import bio.singa.simulation.reactions.reactors.ReactionElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static bio.singa.simulation.entities.ComplexEntityBuilder.attachBindingSites;

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
                debugLogCreatedReaction(reactionChain);
            }
            if (reactionsUnstable) {
                logger.debug("repeating since reactions were unstable");
            }
        } while (reactionsUnstable);
        infoLogCreatedReactions();
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
                debugLogCreatedReaction(reactionChain);
            }
            if (reactionsUnstable) {
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

    private void debugLogCreatedReaction(ReactionChain reactionChain) {
        logger.debug("rule {} produced the following reactions: ", reactionChain.getIdentifier());
        for (ReactionElement reactantElement : reactionChain.getReactantElements()) {
            logger.debug("  {}", reactantElement);
        }
    }

    private void infoLogCreatedReactions() {
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
                attachBindingSites(bindingSiteMapping, reactor.getBindingSite());
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
