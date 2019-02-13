package bio.singa.chemistry.entities;

import bio.singa.chemistry.annotations.Annotation;
import bio.singa.chemistry.annotations.AnnotationType;
import bio.singa.chemistry.annotations.taxonomy.Organism;
import bio.singa.features.identifiers.SimpleStringIdentifier;
import bio.singa.features.model.Feature;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author cl
 */
public class Protein extends AbstractChemicalEntity {

    private static final Set<Class<? extends Feature>> availableFeatures = new HashSet<>();

    public static Builder create(String identifier) {
        return new Builder(identifier);
    }

    public static Builder create(SimpleStringIdentifier identifier) {
        return new Builder(identifier);
    }

    static {
        Protein.availableFeatures.addAll(AbstractChemicalEntity.availableFeatures);
    }

    /**
     * Creates a new Protein with the given identifier.
     *
     * @param identifier The identifier.
     */
    protected Protein(SimpleStringIdentifier identifier) {
        super(identifier);
    }

    /**
     * Adds an organism as an annotation.
     *
     * @param organism The organism.
     */
    public void addOrganism(Organism organism) {
        addAnnotation(new Annotation<>(AnnotationType.ORGANISM, organism));
    }

    /**
     * Adds an organism with a description as an annotation to
     *
     * @param organism The organism.
     * @param description The description.
     */
    public void addOrganism(Organism organism, String description) {
        addAnnotation(new Annotation<>(AnnotationType.ORGANISM, description, organism));
    }

    /**
     * Retrieves all Organisms annotated.
     *
     * @return All Organisms annotated.
     */
    public List<Organism> getAllOrganisms() {
        return getContentOfAnnotations(Organism.class, AnnotationType.ORGANISM);
    }

    /**
     * Tries to retrieve organisms with a certain description.
     *
     * @param description The description.
     * @return The Organisms.
     */
    public List<Organism> getOrganismsWith(String description) {
        return getContentOfAnnotations(Organism.class, description, AnnotationType.ORGANISM);
    }

    /**
     * Adds an amino acid sequence as an annotation.
     *
     * @param sequence The amino acid sequence.
     */
    public void addAminoAcidSequence(String sequence) {
        addAnnotation(new Annotation<>(AnnotationType.AMINO_ACID_SEQUENCE, sequence));
    }

    /**
     * Gets all amino acid sequences annotated.
     *
     * @return The amino acid sequences.
     */
    public List<String> getAllAminoAcidSequences() {
        return getContentOfAnnotations(String.class, AnnotationType.AMINO_ACID_SEQUENCE);
    }

    /**
     * Gets all amino acid sequences annotated with a certain description.
     *
     * @param description The description
     * @return The amino acid sequences.
     */
    public List<String> getAllAminoAcidSequencesWith(String description) {
        return getContentOfAnnotations(String.class, description, AnnotationType.AMINO_ACID_SEQUENCE);
    }

    @Override
    public Set<Class<? extends Feature>> getAvailableFeatures() {
        return availableFeatures;
    }

    public static class Builder extends AbstractChemicalEntity.Builder<Protein, Builder> {

        public Builder(SimpleStringIdentifier identifier) {
            super(identifier);
        }

        public Builder(String identifier) {
            this(new SimpleStringIdentifier(identifier));
        }

        @Override
        protected Protein createObject(SimpleStringIdentifier primaryIdentifer) {
            return new Protein(primaryIdentifer);
        }

        @Override
        protected Protein.Builder getBuilder() {
            return this;
        }

    }

}
