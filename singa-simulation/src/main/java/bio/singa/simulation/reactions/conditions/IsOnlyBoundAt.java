package bio.singa.simulation.reactions.conditions;

import bio.singa.simulation.entities.ChemicalEntity;
import bio.singa.simulation.entities.BindingSite;
import bio.singa.simulation.entities.ComplexEntity;

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

    @Override
    public boolean concerns(ChemicalEntity chemicalEntity) {
        return false;
    }

    @Override
    public boolean concerns(BindingSite bindingSite) {
        return bindingSite.equals(this.bindingSite);
    }
}
