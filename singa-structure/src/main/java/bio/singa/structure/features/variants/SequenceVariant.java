package bio.singa.structure.features.variants;

import bio.singa.features.model.Evidence;
import bio.singa.structure.model.families.AminoAcidFamily;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class SequenceVariant {

    private final String identifier;
    private String description;
    private List<Evidence> evidences;
    private AminoAcidFamily original;
    private AminoAcidFamily variation;
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

    public AminoAcidFamily getOriginal() {
        return original;
    }

    public void setOriginal(AminoAcidFamily original) {
        this.original = original;
    }

    public AminoAcidFamily getVariation() {
        return variation;
    }

    public void setVariation(AminoAcidFamily variation) {
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
