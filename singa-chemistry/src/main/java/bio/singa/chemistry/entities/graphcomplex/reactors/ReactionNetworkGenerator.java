package bio.singa.chemistry.entities.graphcomplex.reactors;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.graphcomplex.BindingSite;
import bio.singa.chemistry.entities.graphcomplex.GraphComplex;
import bio.singa.core.utility.Pair;

import java.util.*;

/**
 * @author cl
 */
public class ReactionNetworkGenerator {

    private List<GraphComplex> possibleEntities;
    private List<ReactionChain> reactionChains;

    public ReactionNetworkGenerator() {
        possibleEntities = new ArrayList<>();
        reactionChains = new ArrayList<>();
    }

    public void add(ReactionChain reactors) {
        reactionChains.add(reactors);
    }

    public void generate() {
        determineBindingSites();
        for (ReactionChain reactionChain : reactionChains) {
            reactionChain.process(possibleEntities);
            List<ReactionElement> reactantElements = reactionChain.getReactantElements();
            reactantElements.stream()
                    .map(ReactionElement::getProducts)
                    .forEach(possibleEntities::addAll);
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
