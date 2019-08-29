package bio.singa.chemistry.reactions.conditions;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.complex.GraphComplex;

public class HasNumberOfEntities implements CandidateCondition {

    private final ChemicalEntity chemicalEntity;
    private final int numberOfParts;

    public HasNumberOfEntities(ChemicalEntity chemicalEntity, int numberOfParts) {
        this.chemicalEntity = chemicalEntity;
        this.numberOfParts = numberOfParts;
    }

    @Override
    public boolean test(GraphComplex graphComplex) {
        return graphComplex.countParts(chemicalEntity) == numberOfParts;
    }

}
