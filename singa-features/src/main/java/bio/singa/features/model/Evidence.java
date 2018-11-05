package bio.singa.features.model;

import java.util.Objects;

import static bio.singa.features.model.Evidence.OriginType.MANUAL_ANNOTATION;

/**
 * @author cl
 */
public class Evidence {

    public static final Evidence MANUALLY_ANNOTATED = new Evidence(MANUAL_ANNOTATION, "manually assigned", "none");
    private final OriginType originType;
    private String name;
    private String publication;

    public Evidence(OriginType originType, String name, String publication) {
        this.originType = originType;
        this.name = name;
        this.publication = publication;
    }

    public Evidence(OriginType originType) {
        this.originType = originType;
    }

    public OriginType getOriginType() {
        return originType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPublication() {
        return publication;
    }

    public void setPublication(String publication) {
        this.publication = publication;
    }

    @Override
    public String toString() {
        return originType == MANUAL_ANNOTATION ? "manual annotation" : originType + " " + name;
    }

    public String full() {
        return originType + " " + name + " - " + (getPublication() == null ? "no further description" : getPublication());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Evidence evidence = (Evidence) o;
        return originType == evidence.originType &&
                Objects.equals(name, evidence.name) &&
                Objects.equals(publication, evidence.publication);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originType, name, publication);
    }

    public enum OriginType {
        PREDICTION, DATABASE, LITERATURE, MANUAL_ANNOTATION
    }
}
