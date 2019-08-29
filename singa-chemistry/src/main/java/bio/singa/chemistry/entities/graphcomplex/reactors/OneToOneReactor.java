package bio.singa.chemistry.entities.graphcomplex.reactors;

import bio.singa.chemistry.entities.graphcomplex.GraphComplex;

import java.util.ArrayList;
import java.util.List;

/**
 * This is for add and remove modifications.
 */
public class OneToOneReactor extends AbstractGraphComplexReactor {

    @Override
    public void collectCandidates(List<GraphComplex> substrateCandidates) {
        setPrimarySubstrates(filterCandidates(substrateCandidates, getPrimaryCandidateConditions()));
    }

    @Override
    public List<ReactionElement> getProducts() {
        if (getPrimarySubstrates().size() != getPrimaryProducts().size()) {
            logger.warn("Substrate and product lists should have the same size.");
        }
        List<ReactionElement> elements = new ArrayList<>();
        for (int i = 0; i < getPrimarySubstrates().size(); i++) {
            GraphComplex substrate = getPrimarySubstrates().get(i);
            GraphComplex product = getPrimaryProducts().get(i);
            elements.add(ReactionElement.createOneToOne(substrate, product));
        }
        return elements;
    }

    public void apply() {
        for (GraphComplex complex : getPrimarySubstrates()) {
            getModification().addCandidate(complex);
            getModification().apply();
            List<GraphComplex> results = getModification().getResults();
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
