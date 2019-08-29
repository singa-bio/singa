package bio.singa.chemistry.reactions.conditions;

import bio.singa.chemistry.entities.complex.BindingSite;
import bio.singa.chemistry.entities.complex.GraphComplex;

/**
 * @author cl
 */
public class IsOnlyBoundAt implements CandidateCondition {

    private BindingSite bindingSite;

    public IsOnlyBoundAt(BindingSite bindingSite) {
        this.bindingSite = bindingSite;
    }

    @Override
    public boolean test(GraphComplex graphComplex) {
        return graphComplex.getEdges().size() == 1 && graphComplex.getEdges().iterator().next().getConnectedSite().equals(bindingSite);
    }

}
