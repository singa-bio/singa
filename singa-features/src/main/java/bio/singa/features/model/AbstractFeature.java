package bio.singa.features.model;

import java.util.ArrayList;
import java.util.Arrays;
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

    private String comment;

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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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
        return identifier == that.identifier &&
                Objects.equals(featureContent, that.featureContent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, featureContent);
    }

    public static abstract class Builder<FeatureContent, TopLevelType extends Feature<FeatureContent>, BuilderType extends Builder> {

        protected final TopLevelType topLevelObject;
        protected final BuilderType builderObject;

        public Builder(FeatureContent content) {
            topLevelObject = createObject(content);
            builderObject = getBuilder();
        }

        protected abstract TopLevelType createObject(FeatureContent content);

        protected abstract BuilderType getBuilder();

        public BuilderType comment(String comment) {
            topLevelObject.setComment(comment);
            return builderObject;
        }

        public BuilderType evidence(Evidence evidence) {
            topLevelObject.addEvidence(evidence);
            return builderObject;
        }

        public BuilderType evidence(Evidence... evidences) {
            Arrays.stream(evidences).forEach(topLevelObject::addEvidence);
            return builderObject;
        }

        public BuilderType evidence(List<Evidence> evidences) {
            evidences.forEach(topLevelObject::addEvidence);
            return builderObject;
        }


        public TopLevelType build() {
            return topLevelObject;
        }

    }

}
