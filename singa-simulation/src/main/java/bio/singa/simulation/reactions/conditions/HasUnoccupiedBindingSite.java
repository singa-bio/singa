package bio.singa.simulation.reactions.conditions;

import bio.singa.simulation.entities.ChemicalEntity;
import bio.singa.simulation.entities.BindingSite;
import bio.singa.simulation.entities.ComplexEntity;

public class HasUnoccupiedBindingSite implements CandidateCondition {

    private BindingSite bindingSite;

    public HasUnoccupiedBindingSite(BindingSite bindingSite) {
        this.bindingSite = bindingSite;
    }

    @Override
    public boolean test(ComplexEntity graphComplex) {
        return graphComplex.getNodeWithUnoccupiedBindingSite(bindingSite).isPresent();
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
