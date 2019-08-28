package bio.singa.chemistry.entities.graphcomplex.conditions;

import bio.singa.chemistry.entities.graphcomplex.BindingSite;
import bio.singa.chemistry.entities.graphcomplex.GraphComplex;

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
