package bio.singa.features.model;

/**
 * @author cl
 */
public abstract class AbstractFeature<FeatureContent> implements Feature<FeatureContent> {

    private final FeatureContent featureContent;
    private final Evidence featureOrigin;

    public AbstractFeature(FeatureContent featureContent, Evidence featureOrigin) {
        this.featureContent = featureContent;
        this.featureOrigin = featureOrigin;
    }

    @Override
    public Evidence getFeatureOrigin() {
        return featureOrigin;
    }

    @Override
    public FeatureContent getFeatureContent() {
        return featureContent;
    }

    @Override
    public String toString() {
        return getSymbol() + " = " + featureContent;
    }

}
