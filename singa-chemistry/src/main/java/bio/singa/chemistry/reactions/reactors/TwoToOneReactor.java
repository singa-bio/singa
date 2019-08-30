package bio.singa.chemistry.reactions.reactors;

import bio.singa.chemistry.entities.complex.ComplexEntity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import static bio.singa.chemistry.reactions.conditions.CandidateConditionBuilder.*;

/**
 * @author cl
 */
public class TwoToOneReactor extends AbstractGraphComplexReactor {

    private List<Predicate<ComplexEntity>> secondCandidateConditions;
    private List<ComplexEntity> secondarySubstrates;

    public TwoToOneReactor() {
        secondCandidateConditions = new ArrayList<>();
        secondarySubstrates = new ArrayList<>();
    }

    public List<Predicate<ComplexEntity>> getSecondCandidateConditions() {
        return secondCandidateConditions;
    }

    public void setSecondCandidateConditions(List<Predicate<ComplexEntity>> secondCandidateConditions) {
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
        invertedReactor.setModification(getModification().invert());
        invertedReactor.getPrimaryCandidateConditions().add(hasOccupiedBindingSite(getModification().getBindingSite()));
        invertedReactor.getPrimaryCandidateConditions().add(hasAnyOfEntity(getModification().getPrimaryEntity()));
        invertedReactor.getPrimaryCandidateConditions().add(hasAnyOfEntity(getModification().getSecondaryEntity()));

        return invertedReactor;
    }

    @Override
    public void clear() {
        super.clear();
        secondarySubstrates.clear();
    }

}
