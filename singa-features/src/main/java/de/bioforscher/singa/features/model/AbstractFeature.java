package de.bioforscher.singa.features.model;

/**
 * @author cl
 */
public abstract class AbstractFeature<FeatureContent> implements Feature<FeatureContent> {

    private final FeatureContent featureContent;
    private final FeatureOrigin featureOrigin;

    public AbstractFeature(FeatureContent featureContent, FeatureOrigin featureOrigin) {
        this.featureContent = featureContent;
        this.featureOrigin = featureOrigin;
    }

    @Override
    public FeatureOrigin getFeatureOrigin() {
        return featureOrigin;
    }

    @Override
    public FeatureContent getFeatureContent() {
        return featureContent;
    }

    @Override
    public String toString() {
        return getSymbol() +"("+featureOrigin.getName()+") = " + featureContent;
    }

}
