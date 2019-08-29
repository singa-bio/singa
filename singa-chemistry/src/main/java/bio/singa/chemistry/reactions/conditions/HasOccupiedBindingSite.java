package bio.singa.chemistry.reactions.conditions;

import bio.singa.chemistry.entities.complex.BindingSite;
import bio.singa.chemistry.entities.complex.GraphComplex;

public class HasOccupiedBindingSite implements CandidateCondition {

    private BindingSite bindingSite;

    public HasOccupiedBindingSite(BindingSite bindingSite) {
        this.bindingSite = bindingSite;
    }

    @Override
    public boolean test(GraphComplex graphComplex) {
        return graphComplex.containsEdge(edge -> edge.getConnectedSite().equals(bindingSite));
    }

}
