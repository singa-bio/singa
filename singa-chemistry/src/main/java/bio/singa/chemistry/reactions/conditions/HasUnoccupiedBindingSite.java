package bio.singa.chemistry.reactions.conditions;

import bio.singa.chemistry.entities.complex.BindingSite;
import bio.singa.chemistry.entities.complex.ComplexEntity;

public class HasUnoccupiedBindingSite implements CandidateCondition {

    private BindingSite bindingSite;

    public HasUnoccupiedBindingSite(BindingSite bindingSite) {
        this.bindingSite = bindingSite;
    }

    @Override
    public boolean test(ComplexEntity graphComplex) {
        return graphComplex.getNodeWithUnoccupiedBindingSite(bindingSite).isPresent();
    }

}
