package bio.singa.chemistry.entities.graphcomplex.conditions;

import bio.singa.chemistry.entities.graphcomplex.BindingSite;
import bio.singa.chemistry.entities.graphcomplex.GraphComplex;

public class HasUnoccupiedBindingSite implements CandidateCondition {

    private BindingSite bindingSite;

    public HasUnoccupiedBindingSite(BindingSite bindingSite) {
        this.bindingSite = bindingSite;
    }

    @Override
    public boolean test(GraphComplex graphComplex) {
        return graphComplex.getNodeWithUnoccupiedBindingSite(bindingSite).isPresent();
    }

}
