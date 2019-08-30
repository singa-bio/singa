package bio.singa.chemistry.reactions.conditions;

import bio.singa.chemistry.entities.complex.BindingSite;
import bio.singa.chemistry.entities.complex.ComplexEntity;

public class HasOccupiedBindingSite implements CandidateCondition {

    private BindingSite bindingSite;

    public HasOccupiedBindingSite(BindingSite bindingSite) {
        this.bindingSite = bindingSite;
    }

    @Override
    public boolean test(ComplexEntity graphComplex) {
        return graphComplex.containsEdge(edge -> edge.getConnectedSite().equals(bindingSite));
    }

}
