package bio.singa.simulation.features;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.model.Evidence;
import bio.singa.features.model.QualitativeFeature;

import java.util.List;
import java.util.Set;

/**
 * @author cl
 */
public abstract class MultiEntityFeature extends QualitativeFeature<Set<ChemicalEntity>> {

    public MultiEntityFeature(Set<ChemicalEntity> chemicalEntities, List<Evidence> evidence) {
        super(chemicalEntities, evidence);
    }

    public MultiEntityFeature(Set<ChemicalEntity> chemicalEntities, Evidence evidence) {
        super(chemicalEntities, evidence);
    }

    public MultiEntityFeature(Set<ChemicalEntity> chemicalEntities) {
        super(chemicalEntities);
    }
}
