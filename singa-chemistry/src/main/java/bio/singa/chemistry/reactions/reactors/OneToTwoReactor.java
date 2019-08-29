package bio.singa.chemistry.reactions.reactors;

import bio.singa.chemistry.entities.complex.GraphComplex;

import java.util.ArrayList;
import java.util.List;

/**
 * This is for release modifications.
 */
public class OneToTwoReactor extends AbstractGraphComplexReactor {

    private List<GraphComplex> secondaryProducts;

    public OneToTwoReactor() {
        secondaryProducts = new ArrayList<>();
    }

    public List<GraphComplex> getSecondaryProducts() {
        return secondaryProducts;
    }

    public void setSecondaryProducts(List<GraphComplex> secondaryProducts) {
        this.secondaryProducts = secondaryProducts;
    }

    @Override
    public void collectCandidates(List<GraphComplex> substrateCandidates) {
        setPrimarySubstrates(filterCandidates(substrateCandidates, getPrimaryCandidateConditions()));
    }

    @Override
    public List<ReactionElement> getProducts() {
        if (getPrimarySubstrates().size() != getPrimaryProducts().size() || getPrimarySubstrates().size() != getSecondaryProducts().size()) {
            logger.warn("Substrate and product lists should have the same size.");
        }
        List<ReactionElement> elements = new ArrayList<>();
        for (int i = 0; i < getPrimarySubstrates().size(); i++) {
            GraphComplex substrate = getPrimarySubstrates().get(i);
            GraphComplex primaryProduct = getPrimaryProducts().get(i);
            GraphComplex secondaryProduct = getSecondaryProducts().get(i);
            elements.add(ReactionElement.createOneToTwo(substrate, primaryProduct, secondaryProduct));
        }
        return elements;
    }

    @Override
    public void apply() {
        for (GraphComplex complex : getPrimarySubstrates()) {
            getModification().addCandidate(complex);
            getModification().apply();
            List<GraphComplex> results = getModification().getResults();
            if (results.size() != 2) {
                logger.warn("One to two modifications should only have one product per modification");
            }
            getPrimaryProducts().add(results.get(0));
            getSecondaryProducts().add(results.get(1));
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
