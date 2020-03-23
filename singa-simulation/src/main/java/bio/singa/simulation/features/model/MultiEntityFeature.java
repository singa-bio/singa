package bio.singa.simulation.features.model;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.model.Evidence;
import bio.singa.features.model.QualitativeFeature;

import java.util.List;

/**
 * @author cl
 */
public abstract class MultiEntityFeature extends QualitativeFeature<List<ChemicalEntity>> {

    public MultiEntityFeature(List<ChemicalEntity> chemicalEntities, List<Evidence> evidence) {
        super(chemicalEntities, evidence);
    }

    public MultiEntityFeature(List<ChemicalEntity> chemicalEntities, Evidence evidence) {
        super(chemicalEntities, evidence);
    }

    public MultiEntityFeature(List<ChemicalEntity> chemicalEntities) {
        super(chemicalEntities);
    }
}
