package bio.singa.simulation.reactions.conditions;


import bio.singa.simulation.entities.ChemicalEntity;
import bio.singa.simulation.entities.BindingSite;
import bio.singa.simulation.entities.ComplexEntity;

public interface CandidateCondition {

    boolean test(ComplexEntity graphComplex);
    boolean concerns(ChemicalEntity chemicalEntity);
    boolean concerns(BindingSite bindingSite);

}
