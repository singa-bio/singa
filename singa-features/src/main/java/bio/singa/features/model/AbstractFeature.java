package bio.singa.features.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author cl
 */
public abstract class AbstractFeature<FeatureContent> implements Feature<FeatureContent> {

    protected FeatureContent featureContent;
    private List<Evidence> evidence;

    public AbstractFeature(FeatureContent featureContent, List<Evidence> evidence) {
        this.featureContent = featureContent;
        this.evidence = evidence;
    }

    public AbstractFeature(FeatureContent featureContent, Evidence evidence) {
        this.featureContent = featureContent;
        if (evidence != null) {
            this.evidence = new ArrayList<>();
            this.evidence.add(evidence);
        }
    }

    public AbstractFeature(FeatureContent featureContent) {
        this.featureContent = featureContent;
    }

    @Override
    public Evidence getPrimaryEvidence() {
        if (evidence.isEmpty()) {
            return Evidence.NO_EVIDENCE;
        }
        return evidence.iterator().next();
    }

    @Override
    public List<Evidence> getAllEvidence() {
        return evidence;
    }

    @Override
    public String getDescriptor() {
        return getClass().getSimpleName();
    }

    @Override
    public FeatureContent getContent() {
        return featureContent;
    }

    @Override
    public String toString() {
        return getDescriptor() + " = " + featureContent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractFeature<?> that = (AbstractFeature<?>) o;
        return Objects.equals(featureContent, that.featureContent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(featureContent);
    }

}
