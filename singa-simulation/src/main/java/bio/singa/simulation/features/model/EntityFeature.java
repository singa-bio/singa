package bio.singa.simulation.features.model;

import bio.singa.simulation.entities.ChemicalEntity;
import bio.singa.features.model.Evidence;
import bio.singa.features.model.QualitativeFeature;

import java.util.List;

/**
 * @author cl
 */
public abstract class EntityFeature extends QualitativeFeature<ChemicalEntity> {

    public EntityFeature(ChemicalEntity entity, List<Evidence> evidence) {
        super(entity, evidence);
    }

    public EntityFeature(ChemicalEntity entity, Evidence evidence) {
        super(entity, evidence);
    }

    public EntityFeature(ChemicalEntity entity) {
        super(entity);
    }

}
