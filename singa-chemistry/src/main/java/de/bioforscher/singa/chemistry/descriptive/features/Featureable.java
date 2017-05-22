package de.bioforscher.singa.chemistry.descriptive.features;

/**
 * Any entity that is able to be annotated with {@link Feature}s must implement this interface. This allows
 * {@link FeatureProvider}s to annotate this entity.
 *
 * @author cl
 */
public interface Featureable {

    /***
     * Returns the {@link Feature} of the given kind.
     * @param kind The kind of feature.
     * @return The Feature.
     */
    Feature getFeature(FeatureKind kind);

    /**
     * Assigns a predefined {@link Feature} and overrides any that my already be present for this entity.
     * @param feature The Feature.
     */
    void assignFeature(Feature<?> feature);

    /**
     * Delegates the assignment of this Feature to the responsible {@link FeatureProvider}. Any Features that
     * might be required for the determination of this Feature are also assigned.
     * @param featureKind The kind of Feature to annotate.
     */
    void assignFeature(FeatureKind featureKind);

    /**
     * Returns {@code true} if this kind of feature is already annotated.
     * @param kind The kind to check.
     * @return {@code true} if this kind of feature is already annotated.
     */
    boolean hasFeature(FeatureKind kind);


}
