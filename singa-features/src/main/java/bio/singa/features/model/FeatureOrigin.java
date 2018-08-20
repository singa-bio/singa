package bio.singa.features.model;

import static bio.singa.features.model.FeatureOrigin.OriginType.MANUAL_ANNOTATION;

/**
 * @author cl
 */
public class FeatureOrigin {

    public static final FeatureOrigin MANUALLY_ANNOTATED = new FeatureOrigin(MANUAL_ANNOTATION, "manually assigned", "none");
    private final OriginType originType;
    private final String name;
    private final String publication;

    public FeatureOrigin(OriginType originType, String name, String publication) {
        this.originType = originType;
        this.name = name;
        this.publication = publication;
    }

    public OriginType getOriginType() {
        return originType;
    }

    public String getName() {
        return name;
    }

    public String getPublication() {
        return publication;
    }

    @Override
    public String toString() {
        return originType == MANUAL_ANNOTATION ? "manual annotation" : originType + " " + name;
    }

    public enum OriginType {
        PREDICTION, DATABASE, LITERATURE, MANUAL_ANNOTATION
    }
}
