package de.bioforscher.singa.chemistry.descriptive.features;

import de.bioforscher.singa.features.model.Feature;
import de.bioforscher.singa.features.model.FeatureContainer;
import de.bioforscher.singa.features.model.Featureable;

/**
 * @author cl
 */
public class ChemistryFeatureContainer extends FeatureContainer {

    @Override
    public <FeatureableType extends Featureable, FeatureType extends Feature<?>> void setFeature(Class<FeatureType> featureTypeClass, FeatureableType featureable) {
        FeatureRegistry.getProvider(featureTypeClass).assign(featureable);
    }
}
