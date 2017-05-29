package de.bioforscher.singa.units.features.playground;

import de.bioforscher.singa.units.features.model.Feature;
import de.bioforscher.singa.units.features.model.FeatureContainer;
import de.bioforscher.singa.units.features.model.Featureable;

import java.util.HashSet;
import java.util.Set;

/**
 * @author cl
 */
public class SomethingFeatureable implements Featureable {

    private FeatureContainer container;
    private final Set<Class<? extends Feature>> availableFeatures;

    public SomethingFeatureable() {
        this.container = new FeatureContainer();
        this.availableFeatures = new HashSet<>();
        // add available features
    }

    @Override
    public <FeatureType extends Feature> FeatureType getFeature(Class<FeatureType> featureTypeClass) {
        return this.container.getFeature(featureTypeClass);
    }

    @Override
    public <FeatureType extends Feature> void setFeature(Class<FeatureType> featureTypeClass) {
        this.container.setFeature(featureTypeClass, this);
    }

    @Override
    public <FeatureType extends Feature> void setFeature(FeatureType feature) {
        this.container.setFeature(feature);
    }

    @Override
    public <FeatureType extends Feature> boolean hasFeature(Class<FeatureType> featureTypeClass) {
        return this.container.hasFeature(featureTypeClass);
    }

    @Override
    public Set<Class<? extends Feature>> getAvailableFeatures() {
        return availableFeatures;
    }


}
