package bio.singa.chemistry.entities.graphcomplex.reactors;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.graphcomplex.BindingSite;
import bio.singa.chemistry.entities.graphcomplex.GraphComplex;
import bio.singa.chemistry.entities.graphcomplex.GraphComplexNode;
import bio.singa.core.utility.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author cl
 */
public class ReactionNetworkGenerator {

    private static final Logger logger = LoggerFactory.getLogger(ReactionNetworkGenerator.class);

    private Set<GraphComplex> possibleEntities;
    private List<ReactionChain> reactionChains;

    public ReactionNetworkGenerator() {
        possibleEntities = new HashSet<>();
        reactionChains = new ArrayList<>();
    }

    public void add(ReactionChain reactors) {
        reactionChains.add(reactors);
    }

    public void generate() {
        determineBindingSites();
        logBindingSites();
        boolean reactionsUnstable;
        do {
            reactionsUnstable = false;
            for (ReactionChain reactionChain : reactionChains) {
                int previousNumberOfReactions = reactionChain.getReactantElements().size();
                reactionChain.process(possibleEntities);
                Set<ReactionElement> reactantElements = reactionChain.getReactantElements();
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
    }

    private void logBindingSites() {
        logger.debug("assigned binding sites:");
        for (GraphComplex possibleEntity : possibleEntities) {
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

    public void determineBindingSites() {
        Map<ChemicalEntity, Set<BindingSite>> bindingSiteMapping = new HashMap<>();
        for (ReactionChain reactionChain : reactionChains) {
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
        // create base graphs
        for (Map.Entry<ChemicalEntity, Set<BindingSite>> entry : bindingSiteMapping.entrySet()) {
            ChemicalEntity entity = entry.getKey();
            Set<BindingSite> bindingSites = entry.getValue();
            possibleEntities.add(GraphComplex.from(entity, bindingSites));
        }
    }

}
