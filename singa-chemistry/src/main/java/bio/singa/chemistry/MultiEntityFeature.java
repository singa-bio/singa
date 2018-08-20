package bio.singa.chemistry;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.model.AbstractFeature;
import bio.singa.features.model.FeatureOrigin;

import java.util.Set;

/**
 * @author cl
 */
public abstract class MultiEntityFeature extends AbstractFeature<Set<ChemicalEntity>> {

    public MultiEntityFeature(Set<ChemicalEntity> chemicalEntities, FeatureOrigin featureOrigin) {
        super(chemicalEntities, featureOrigin);
    }

}
