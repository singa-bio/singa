package bio.singa.chemistry.reactions.conditions;

import bio.singa.chemistry.entities.complex.BindingSite;
import bio.singa.chemistry.entities.complex.ComplexEntity;

/**
 * @author cl
 */
public class IsOnlyBoundAt implements CandidateCondition {

    private BindingSite bindingSite;

    public IsOnlyBoundAt(BindingSite bindingSite) {
        this.bindingSite = bindingSite;
    }

    @Override
    public boolean test(ComplexEntity graphComplex) {
        return graphComplex.getEdges().size() == 1 && graphComplex.getEdges().iterator().next().getConnectedSite().equals(bindingSite);
    }

}
