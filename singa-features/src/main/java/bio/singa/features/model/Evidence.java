package bio.singa.features.model;

import java.util.Objects;

import static bio.singa.features.model.Evidence.SourceType.GUESS;

/**
 * @author cl
 */
public class Evidence {

    public static final Evidence NO_EVIDENCE = new Evidence(GUESS, "no evidence", "no information about evidence provided");

    private SourceType type;
    private String identifier;
    private String description;
    private String comment;

    public Evidence(SourceType type) {
        this.type = type;
    }

    public Evidence(SourceType type, String identifier, String description) {
        this.type = type;
        this.identifier = identifier;
        this.description = description;
    }

    public Evidence(SourceType type, String identifier, String description, String comment) {
        this(type, identifier, description);
        this.comment = comment;
    }

    public SourceType getType() {
        return type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return type + " " + identifier + " - " + description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Evidence evidence = (Evidence) o;
        return type == evidence.type &&
                Objects.equals(identifier, evidence.identifier) &&
                Objects.equals(description, evidence.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, identifier, description);
    }

    public enum SourceType {
        GUESS, ESTIMATION, PREDICTION, DATABASE, LITERATURE
    }
}
