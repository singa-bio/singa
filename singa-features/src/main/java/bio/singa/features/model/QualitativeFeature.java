package bio.singa.features.model;

import java.util.List;

/**
 * @author cl
 */
public abstract class QualitativeFeature<FeatureContent> extends AbstractFeature<FeatureContent> {

    public QualitativeFeature(FeatureContent featureContent, List<Evidence> evidence) {
        super(featureContent, evidence);
        FeatureRegistry.addQualitativeFeature(this);
    }

    public QualitativeFeature(FeatureContent featureContent, Evidence evidence) {
        super(featureContent, evidence);
        FeatureRegistry.addQualitativeFeature(this);
    }

    public QualitativeFeature(FeatureContent featureContent) {
        super(featureContent);
        FeatureRegistry.addQualitativeFeature(this);
    }

}
