package de.bioforscher.singa.simulation.model.parameters;

import de.bioforscher.singa.chemistry.descriptive.features.ChemistryFeatureContainer;
import de.bioforscher.singa.features.model.Feature;
import de.bioforscher.singa.features.model.FeatureContainer;
import de.bioforscher.singa.features.model.Featureable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author cl
 */
public class FeatureManager implements Featureable {

    /**
     * The features available for automatic annotation and assignment.
     */
    private Set<Class<? extends Feature>> availableFeatures = new HashSet<>();

    private Set<Class<? extends Feature>> requiredFeatures = new HashSet<>();

    /**
     * The features of the reaction.
     */
    private FeatureContainer features;

    public FeatureManager() {
        features = new ChemistryFeatureContainer();
    }

    @Override
    public Collection<Feature<?>> getFeatures() {
        return features.getAllFeatures();
    }

    @Override
    public <FeatureType extends Feature> FeatureType getFeature(Class<FeatureType> featureTypeClass) {
        return features.getFeature(featureTypeClass);
    }

    @Override
    public <FeatureType extends Feature> void setFeature(Class<FeatureType> featureTypeClass) {
        features.setFeature(featureTypeClass, this);
    }

    @Override
    public <FeatureType extends Feature> void setFeature(FeatureType feature) {
        features.setFeature(feature);
    }

    @Override
    public <FeatureType extends Feature> boolean hasFeature(Class<FeatureType> featureTypeClass) {
        return features.hasFeature(featureTypeClass);
    }

    public Collection<Feature<?>> getAllFeatures() {
        return features.getAllFeatures();
    }

    public String listFeatures(String precedingSpaces) {
        return features.listFeatures(precedingSpaces);
    }

    @Override
    public Set<Class<? extends Feature>> getAvailableFeatures() {
        return availableFeatures;
    }

    public Set<Class<? extends Feature>> getRequiredFeatures() {
        return requiredFeatures;
    }
}
