package bio.singa.simulation.reactions.conditions;

import bio.singa.simulation.entities.ChemicalEntity;
import bio.singa.simulation.entities.BindingSite;
import bio.singa.simulation.entities.ComplexEntity;

public class HasNumberOfEntities implements CandidateCondition {

    private final ChemicalEntity chemicalEntity;
    private final int numberOfParts;

    public HasNumberOfEntities(ChemicalEntity chemicalEntity, int numberOfParts) {
        this.chemicalEntity = chemicalEntity;
        this.numberOfParts = numberOfParts;
    }

    @Override
    public boolean test(ComplexEntity graphComplex) {
        return graphComplex.countParts(chemicalEntity) == numberOfParts;
    }

    @Override
    public boolean concerns(ChemicalEntity chemicalEntity) {
        return chemicalEntity.equals(this.chemicalEntity);
    }

    @Override
    public boolean concerns(BindingSite bindingSite) {
        return false;
    }
}
