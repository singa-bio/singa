package de.bioforscher.singa.features.model;

import java.util.Collection;
import java.util.Set;

/**
 * @author cl
 */
public interface Featureable {

    Collection<Feature<?>> getFeatures();

    <FeatureType extends Feature> FeatureType getFeature(Class<FeatureType> featureTypeClass);

    <FeatureType extends Feature> void setFeature(Class<FeatureType> featureTypeClass);

    <FeatureType extends Feature> void setFeature(FeatureType feature);

    <FeatureType extends Feature> boolean hasFeature(Class<FeatureType> featureTypeClass);

    Set<Class<? extends Feature>> getAvailableFeatures();

    default boolean meetsAllRequirements(Set<Class<? extends Feature>> featureTypeClass) {
        for (Class<? extends Feature> typeClass : featureTypeClass) {
            if (!hasFeature(typeClass)) {
                return false;
            }
        }
        return true;
    }

    default <FeatureType extends Feature> boolean canBeFeaturedWith(Class<FeatureType> featureTypeClass) {
        return getAvailableFeatures().contains(featureTypeClass);
    }

    default void scaleScalableFeatures() {
        getFeatures().stream()
                .filter(feature -> feature instanceof ScalableFeature)
                .map(feature -> (ScalableFeature) feature)
                .forEach(ScalableFeature::scale);
    }

}
