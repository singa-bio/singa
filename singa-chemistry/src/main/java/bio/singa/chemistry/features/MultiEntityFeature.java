package bio.singa.chemistry.features;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.model.AbstractFeature;
import bio.singa.features.model.Evidence;

import java.util.Set;

/**
 * @author cl
 */
public abstract class MultiEntityFeature extends AbstractFeature<Set<ChemicalEntity>> {

    public MultiEntityFeature(Set<ChemicalEntity> chemicalEntities, Evidence featureOrigin) {
        super(chemicalEntities, featureOrigin);
    }

}
