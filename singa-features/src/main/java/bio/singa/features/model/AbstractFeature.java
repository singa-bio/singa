package bio.singa.features.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author cl
 */
public abstract class AbstractFeature<FeatureContent> implements Feature<FeatureContent> {

    private int identifier;
    protected FeatureContent baseContent;
    protected FeatureContent featureContent;
    private List<FeatureContent> alternativeContents;
    private List<Evidence> evidence;

    public AbstractFeature(FeatureContent featureContent, List<Evidence> evidence) {
        this.featureContent = featureContent;
        baseContent = featureContent;
        alternativeContents = new ArrayList<>();
        this.evidence = evidence;
    }

    public AbstractFeature(FeatureContent featureContent, Evidence evidence) {
        this.featureContent = featureContent;
        baseContent = featureContent;
        alternativeContents = new ArrayList<>();
        this.evidence = new ArrayList<>();
        if (evidence != null) {
            this.evidence.add(evidence);
        }
    }

    public AbstractFeature(FeatureContent featureContent) {
        this.featureContent = featureContent;
        baseContent = featureContent;
        alternativeContents = new ArrayList<>();
        evidence = new ArrayList<>();
    }

    public AbstractFeature() {
        alternativeContents = new ArrayList<>();
        evidence = new ArrayList<>();
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public int getIdentifier() {
        return identifier;
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
    public void addEvidence(Evidence evidence) {
        this.evidence.add(evidence);
    }

    @Override
    public String getDescriptor() {
        return getClass().getSimpleName();
    }

    @Override
    public FeatureContent getContent() {
        return featureContent;
    }

    public List<FeatureContent> getAlternativeContents() {
        return alternativeContents;
    }

    public void setAlternativeContents(List<FeatureContent> alternativeContents) {
        this.alternativeContents = alternativeContents;
    }

    public void addAlternativeContent(FeatureContent alternativeContent) {
        alternativeContents.add(alternativeContent);
    }

    @Override
    public void setAlternativeContent(int index) {
        featureContent = alternativeContents.get(index);
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
