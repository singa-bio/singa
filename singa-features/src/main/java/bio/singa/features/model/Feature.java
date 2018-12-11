package bio.singa.features.model;

/**
 * A Feature is a kind of annotation that represents a information required by any algorithm. Each feature can
 * only ba annotated once to each {@link Featureable}. The origin of the feature is annotated in the as a
 * {@link Evidence}.
 *
 * @author cl
 */
public interface Feature<FeatureContent> {

    /**
     * Returns the information bearing content of the feature.
     *
     * @return The information bearing content of the feature.
     */
    FeatureContent getFeatureContent();

    /**
     * Returns the evidence for the feature.
     *
     * @return The evidence for the feature.
     */
    Evidence getEvidence();

    /**
     * Returns the symbol associated to this feature.
     * @return The symbol associated to this feature.
     */
    String getSymbol();

}
