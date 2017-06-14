package de.bioforscher.singa.features.model;

import static de.bioforscher.singa.features.model.FeatureOrigin.OriginType.MANUAL_ANNOTATION;

/**
 * @author cl
 */
public class FeatureOrigin {

    public static FeatureOrigin MANUALLY_ANNOTATED = new FeatureOrigin(MANUAL_ANNOTATION);

    public enum OriginType {
        PREDICTION, DATABASE, MANUAL_ANNOTATION
    }

    private final OriginType originType;
    private final String name;
    private final String publication;

    public FeatureOrigin(OriginType originType, String name, String publication) {
        this.originType = originType;
        this.name = name;
        this.publication = publication;
    }

    public FeatureOrigin(OriginType originType) {
        this.originType = originType;
        this.name = "Undefined";
        this.publication = "Undefined ";
    }

    public OriginType getOriginType() {
        return this.originType;
    }

    public String getName() {
        return this.name;
    }

    public String getPublication() {
        return this.publication;
    }

    @Override
    public String toString() {
        return this.originType == MANUAL_ANNOTATION ? "manual annotation" : this.name + " ("+ this.publication +")";
    }
}
