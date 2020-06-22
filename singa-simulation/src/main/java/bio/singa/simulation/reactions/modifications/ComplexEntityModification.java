package bio.singa.simulation.reactions.modifications;

import bio.singa.simulation.entities.ChemicalEntity;
import bio.singa.simulation.entities.BindingSite;
import bio.singa.simulation.entities.ComplexEntity;

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
