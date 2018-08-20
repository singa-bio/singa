package bio.singa.chemistry.features;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.model.AbstractFeature;
import bio.singa.features.model.FeatureOrigin;

/**
 * @author cl
 */
public abstract class EntityFeature extends AbstractFeature<ChemicalEntity> {

    public EntityFeature(ChemicalEntity entity, FeatureOrigin featureOrigin) {
        super(entity, featureOrigin);
    }

}
