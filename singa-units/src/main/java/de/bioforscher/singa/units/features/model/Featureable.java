package de.bioforscher.singa.units.features.model;

import java.util.Set;

/**
 * @author cl
 */
public interface Featureable {

    <FeatureType extends Feature> FeatureType getFeature(Class<FeatureType> featureTypeClass);

    <FeatureType extends Feature> void setFeature(Class<FeatureType> featureTypeClass);

    <FeatureType extends Feature> void setFeature(FeatureType feature);

    <FeatureType extends Feature> boolean hasFeature(Class<FeatureType> featureTypeClass);

    Set<Class<? extends Feature>> getAvailableFeatures();

    default <FeatureType extends Feature> boolean canBeFeaturedWith(Class<FeatureType> featureTypeClass) {
        return getAvailableFeatures().contains(featureTypeClass);
    }

}
