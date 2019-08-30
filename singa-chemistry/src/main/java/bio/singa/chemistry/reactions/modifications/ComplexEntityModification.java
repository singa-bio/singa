package bio.singa.chemistry.reactions.modifications;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.complex.BindingSite;
import bio.singa.chemistry.entities.complex.ComplexEntity;

import java.util.List;

public interface ComplexEntityModification {

    void addCandidate(ComplexEntity candidate);
    List<ComplexEntity> getResults();

    BindingSite getBindingSite();

    void setPrimaryEntity(ChemicalEntity chemicalEntity);
    ChemicalEntity getPrimaryEntity();

    void setSecondaryEntity(ChemicalEntity chemicalEntity);
    ChemicalEntity getSecondaryEntity();

    void apply();
    ComplexEntityModification invert();
    void clear();

}
