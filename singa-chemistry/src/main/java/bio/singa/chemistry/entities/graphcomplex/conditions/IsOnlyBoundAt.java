package bio.singa.chemistry.entities.graphcomplex.conditions;

import bio.singa.chemistry.entities.graphcomplex.BindingSite;
import bio.singa.chemistry.entities.graphcomplex.GraphComplex;

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
