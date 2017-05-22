package de.bioforscher.singa.units.features.model;

/**
 * A Feature is a kind of annotation that represents a information required by any algorithm. Each feature can
 * only ba annotated once to each {@link Featureable}. The origin of the feature is annotated in the as a
 * {@link FeatureOrigin}.
 *
 * @author cl
 */
public interface Feature<FeatureContent> {

    /**
     * Returns the information bearing content of the feature.
     * @return The information bearing content of the feature.
     */
    FeatureContent getFeatureContent();

    /**
     * Returns the origin of the feature.
     * @return The origin of the feature.
     */
    FeatureOrigin getFeatureOrigin();

}
