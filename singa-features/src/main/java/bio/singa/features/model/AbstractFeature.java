package bio.singa.features.model;

/**
 * @author cl
 */
public abstract class AbstractFeature<FeatureContent> implements Feature<FeatureContent> {

    private final FeatureContent featureContent;
    private final Evidence evidence;

    public AbstractFeature(FeatureContent featureContent, Evidence evidence) {
        this.featureContent = featureContent;
        this.evidence = evidence;
    }

    @Override
    public Evidence getEvidence() {
        return evidence;
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
