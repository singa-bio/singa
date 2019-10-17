package bio.singa.chemistry.reactions.reactors;

import bio.singa.chemistry.entities.complex.ComplexEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * This is for release modifications.
 */
public class OneToTwoReactor extends AbstractGraphComplexReactor {

    private List<ComplexEntity> secondaryProducts;

    public OneToTwoReactor() {
        secondaryProducts = new ArrayList<>();
    }

    public List<ComplexEntity> getSecondaryProducts() {
        return secondaryProducts;
    }

    public void setSecondaryProducts(List<ComplexEntity> secondaryProducts) {
        this.secondaryProducts = secondaryProducts;
    }

    @Override
    public void collectCandidates(List<ComplexEntity> substrateCandidates) {
        setPrimarySubstrates(filterCandidates(substrateCandidates, getPrimaryCandidateConditions()));
    }

    @Override
    public List<ReactionElement> getProducts() {
        if (getPrimarySubstrates().size() != getPrimaryProducts().size() || getPrimarySubstrates().size() != getSecondaryProducts().size()) {
            logger.warn("Substrate and product lists should have the same size.");
        }
        List<ReactionElement> elements = new ArrayList<>();
        for (int i = 0; i < getPrimarySubstrates().size(); i++) {
            ComplexEntity substrate = getPrimarySubstrates().get(i);
            ComplexEntity primaryProduct = getPrimaryProducts().get(i);
            ComplexEntity secondaryProduct = getSecondaryProducts().get(i);
            elements.add(ReactionElement.createOneToTwo(substrate, primaryProduct, secondaryProduct));
        }
        return elements;
    }

    @Override
    public void apply() {
        for (ComplexEntity complex : getPrimarySubstrates()) {
            getModification().addCandidate(complex);
            getModification().apply();
            List<ComplexEntity> results = getModification().getResults();
            if (results.size() != 2) {
                logger.warn("One to two modifications should only have one product per modification");
            }
            ComplexEntity first = results.get(0);
            ComplexEntity second = results.get(1);
            if (first.containsEntity(getModification().getPrimaryEntity())) {
                getPrimaryProducts().add(first);
                getSecondaryProducts().add(second);
            } else {
                getPrimaryProducts().add(second);
                getSecondaryProducts().add(first);
            }
            getModification().clear();
        }
    }

    @Override
    public ComplexReactor invert() {
        throw new IllegalStateException("Not yet implemented");
    }

    @Override
    public void clear() {
        super.clear();
        secondaryProducts.clear();
    }
}
