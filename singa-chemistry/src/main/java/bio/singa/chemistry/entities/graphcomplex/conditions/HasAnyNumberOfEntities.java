package bio.singa.chemistry.entities.graphcomplex.conditions;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.graphcomplex.GraphComplex;

public class HasAnyNumberOfEntities implements CandidateCondition {

    private final ChemicalEntity chemicalEntity;

    public HasAnyNumberOfEntities(ChemicalEntity chemicalEntity) {
        this.chemicalEntity = chemicalEntity;
    }

    @Override
    public boolean test(GraphComplex graphComplex) {
        return graphComplex.countParts(chemicalEntity) != 0;
    }

}
