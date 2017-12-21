package de.bioforscher.singa.features.model;

import java.util.Collection;
import java.util.HashMap;

/**
 * @author cl
 */
public abstract class FeatureContainer {

    private final HashMap<Class<? extends Feature>, Feature<?>> content;

    public FeatureContainer() {
        content = new HashMap<>();
    }

    public <FeatureType extends Feature<?>> FeatureType getFeature(Class<FeatureType> featureTypeClass) {
        return featureTypeClass.cast(content.get(featureTypeClass));
    }

    public abstract <FeatureableType extends Featureable, FeatureType extends Feature<?>> void setFeature(Class<FeatureType> featureTypeClass, FeatureableType featureable);

    public <FeatureType extends Feature<?>> void setFeature(FeatureType feature) {
        content.put(feature.getClass(), feature);
    }

    public <FeatureType extends Feature<?>> boolean hasFeature(Class<FeatureType> featureTypeClass) {
        return content.containsKey(featureTypeClass);
    }

    public Collection<Feature<?>> getAllFeatures() {
        return content.values();
    }

}
