package bio.singa.simulation.reactions.reactors;

import bio.singa.simulation.entities.ComplexEntity;
import bio.singa.simulation.reactions.conditions.CandidateCondition;
import bio.singa.simulation.reactions.modifications.ComplexEntityModification;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static bio.singa.simulation.reactions.conditions.CandidateConditionBuilder.hasAnyOfEntity;
import static bio.singa.simulation.reactions.conditions.CandidateConditionBuilder.hasOccupiedBindingSite;

/**
 * @author cl
 */
public class TwoToOneReactor extends AbstractGraphComplexReactor {

    private List<CandidateCondition> secondCandidateConditions;
    private List<ComplexEntity> secondarySubstrates;

    public TwoToOneReactor() {
        secondCandidateConditions = new ArrayList<>();
        secondarySubstrates = new ArrayList<>();
    }

    public List<CandidateCondition> getSecondCandidateConditions() {
        return secondCandidateConditions;
    }

    public void setSecondCandidateConditions(List<CandidateCondition> secondCandidateConditions) {
        this.secondCandidateConditions = secondCandidateConditions;
    }

    public List<ComplexEntity> getSecondarySubstrates() {
        return secondarySubstrates;
    }

    public void setSecondarySubstrates(List<ComplexEntity> secondarySubstrates) {
        this.secondarySubstrates = secondarySubstrates;
    }

    @Override
    public void collectCandidates(List<ComplexEntity> substrateCandidates) {
        setPrimarySubstrates(filterCandidates(substrateCandidates, getPrimaryCandidateConditions()));
        setSecondarySubstrates(filterCandidates(substrateCandidates, getSecondCandidateConditions()));
    }

    @Override
    public List<ReactionElement> getProducts() {
        List<ReactionElement> elements = new ArrayList<>();
        Iterator<ComplexEntity> productIterator = getPrimaryProducts().iterator();
        for (int primaryIndex = 0; primaryIndex < getPrimarySubstrates().size(); primaryIndex++) {
            ComplexEntity primarySubstrate = getPrimarySubstrates().get(primaryIndex);
            for (int secondaryIndex = 0; secondaryIndex < getSecondarySubstrates().size(); secondaryIndex++) {
                ComplexEntity secondarySubstrate = getSecondarySubstrates().get(secondaryIndex);
                ComplexEntity product = productIterator.next();
                elements.add(ReactionElement.createTwoToOne(primarySubstrate, secondarySubstrate, product));
            }
        }
        return elements;
    }

    @Override
    public void apply() {
        for (int primaryIndex = 0; primaryIndex < getPrimarySubstrates().size(); primaryIndex++) {
            for (int secondaryIndex = 0; secondaryIndex < getSecondarySubstrates().size(); secondaryIndex++) {
                getModification().addCandidate(getPrimarySubstrates().get(primaryIndex));
                getModification().addCandidate(getSecondarySubstrates().get(secondaryIndex));
                getModification().apply();
                List<ComplexEntity> results = getModification().getResults();
                if (results.size() != 1) {
                    logger.warn("Two to one modifications should only have one product per modification");
                }
                getPrimaryProducts().add(results.get(0));
                getModification().clear();
            }
        }
    }

    @Override
    public ComplexReactor invert() {
        OneToTwoReactor invertedReactor = new OneToTwoReactor();

        ComplexEntityModification invertedModification = getModification().invert();
        invertedReactor.setModification(invertedModification);
        invertedReactor.getPrimaryCandidateConditions().add(hasOccupiedBindingSite(getModification().getBindingSite()));
        invertedReactor.getPrimaryCandidateConditions().add(hasAnyOfEntity(getModification().getPrimaryEntity()));
        invertedReactor.getPrimaryCandidateConditions().add(hasAnyOfEntity(getModification().getSecondaryEntity()));

        // if condition does not consider the implicit conditions reuse it
        for (CandidateCondition condition : getPrimaryCandidateConditions()) {
            if (condition.concerns(getModification().getBindingSite())) {
                continue;
            }
            if (condition.concerns(getModification().getPrimaryEntity())) {
                continue;
            }
            invertedReactor.getPrimaryCandidateConditions().add(condition);
        }
        for (CandidateCondition condition : getSecondCandidateConditions()) {
            if (condition.concerns(getModification().getBindingSite())) {
                continue;
            }
            if (condition.concerns(getModification().getSecondaryEntity())) {
                continue;
            }
            invertedReactor.getPrimaryCandidateConditions().add(condition);
        }
        return invertedReactor;
    }

    @Override
    public void clear() {
        super.clear();
        secondarySubstrates.clear();
    }

}
