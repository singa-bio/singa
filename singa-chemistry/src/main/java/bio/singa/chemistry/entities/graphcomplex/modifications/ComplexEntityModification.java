package bio.singa.chemistry.entities.graphcomplex.modifications;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.graphcomplex.BindingSite;
import bio.singa.chemistry.entities.graphcomplex.GraphComplex;

import java.util.List;

public interface ComplexEntityModification {

    void addCandidate(GraphComplex candidate);
    List<GraphComplex> getResults();

    BindingSite getBindingSite();

    void setPrimaryEntity(ChemicalEntity chemicalEntity);
    ChemicalEntity getPrimaryEntity();

    void setSecondaryEntity(ChemicalEntity chemicalEntity);
    ChemicalEntity getSecondaryEntity();

    void apply();
    ComplexEntityModification invert();
    void clear();

}
