package bio.singa.chemistry.features.databases.sequencevariants;

import bio.singa.features.model.Evidence;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class SequenceVariant {

    private final String identifier;
    private String description;
    private List<Evidence> evidences;
    private String original;
    private String variation;
    private int location;

    public SequenceVariant(String identifier) {
        this.identifier = identifier;
        evidences = new ArrayList<>();
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Evidence> getEvidences() {
        return evidences;
    }

    public void setEvidences(List<Evidence> evidences) {
        this.evidences = evidences;
    }

    public void addEvidence(Evidence evidence) {
        evidences.add(evidence);
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getVariation() {
        return variation;
    }

    public void setVariation(String variation) {
        this.variation = variation;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "SequenceVariant{" +
                "identifier='" + identifier + '\'' +
                ", description='" + description + '\'' +
                ", evidences=" + evidences +
                ", original=" + original +
                ", variation=" + variation +
                ", location=" + location +
                '}';
    }
}
