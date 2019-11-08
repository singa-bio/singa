package bio.singa.chemistry.reactions.conditions;


import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.complex.BindingSite;
import bio.singa.chemistry.entities.complex.ComplexEntity;

public interface CandidateCondition {

    boolean test(ComplexEntity graphComplex);
    boolean concerns(ChemicalEntity chemicalEntity);
    boolean concerns(BindingSite bindingSite);

}
