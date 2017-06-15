package de.bioforscher.singa.features.model;

import java.util.HashMap;

/**
 * @author cl
 */
public class FeatureContainer {

    private HashMap<Class<? extends Feature>, Feature> content;

    public FeatureContainer() {
        this.content = new HashMap<>();
    }

    public <FeatureType extends Feature> FeatureType getFeature(Class<FeatureType> featureTypeClass) {
        return featureTypeClass.cast(this.content.get(featureTypeClass));
    }

    public <FeatureableType extends Featureable, FeatureType extends Feature> void setFeature(Class<FeatureType> featureTypeClass, FeatureableType featureable) {
        FeatureRegistry.getProvider(featureTypeClass).assign(featureable);
    }

    public <FeatureType extends Feature> void setFeature(FeatureType feature) {
        this.content.put(feature.getClass(), feature);
    }

    public <FeatureType extends Feature> boolean hasFeature(Class<FeatureType> featureTypeClass) {
        return this.content.containsKey(featureTypeClass);
    }

}
