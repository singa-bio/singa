package de.bioforscher.singa.chemistry.descriptive.features;

/**
 * @author cl
 */
public interface Featureable {

    Feature getFeature(FeatureKind kind);

    void assignFeature(Feature<?> feature);

    void assignFeature(FeatureKind featureKind);

    boolean hasFeature(FeatureKind kind);

}
