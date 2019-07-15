package bio.singa.chemistry.features;

import bio.singa.features.model.Feature;
import bio.singa.features.model.FeatureContainer;
import bio.singa.features.model.Featureable;

/**
 * @author cl
 */
public class ChemistryFeatureContainer extends FeatureContainer {

    @Override
    public <FeatureableType extends Featureable, FeatureType extends Feature<?>> void setFeature(Class<FeatureType> featureTypeClass, FeatureableType featureable) {
        FeatureProviderRegistry.getProvider(featureTypeClass).assign(featureable);
    }

}
