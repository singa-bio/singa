package bio.singa.chemistry.reactions.reactors;

import bio.singa.chemistry.entities.complex.ComplexEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * This is for add and remove modifications.
 */
public class OneToOneReactor extends AbstractGraphComplexReactor {

    @Override
    public void collectCandidates(List<ComplexEntity> substrateCandidates) {
        setPrimarySubstrates(filterCandidates(substrateCandidates, getPrimaryCandidateConditions()));
    }

    @Override
    public List<ReactionElement> getProducts() {
        if (getPrimarySubstrates().size() != getPrimaryProducts().size()) {
            logger.warn("Substrate and product lists should have the same size.");
        }
        List<ReactionElement> elements = new ArrayList<>();
        for (int i = 0; i < getPrimarySubstrates().size(); i++) {
            ComplexEntity substrate = getPrimarySubstrates().get(i);
            ComplexEntity product = getPrimaryProducts().get(i);
            elements.add(ReactionElement.createOneToOne(substrate, product));
        }
        return elements;
    }

    public void apply() {
        for (ComplexEntity complex : getPrimarySubstrates()) {
            getModification().addCandidate(complex);
            getModification().apply();
            List<ComplexEntity> results = getModification().getResults();
            if (results.size() != 1) {
                logger.warn("One to one modifications should only have one product per modification");
            }
            getPrimaryProducts().add(results.get(0));
            getModification().clear();
        }
    }

    @Override
    public ComplexReactor invert() {
        throw new IllegalStateException("Not yet implemented");
    }
}
