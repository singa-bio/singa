package de.bioforscher.chemistry.descriptive;

import de.bioforscher.chemistry.descriptive.annotations.Annotation;
import de.bioforscher.core.biology.Organism;
import de.bioforscher.core.identifier.SimpleStringIdentifier;

import java.util.List;

import static de.bioforscher.chemistry.descriptive.annotations.AnnotationType.AMINO_ACID_SEQUENCE;
import static de.bioforscher.chemistry.descriptive.annotations.AnnotationType.ORGANISM;

/**
 * @author cl
 */
public class Protein extends ChemicalEntity<SimpleStringIdentifier> {

    /**
     * Creates a new Chemical Entity with the given identifier.
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
        addAnnotation(new Annotation<>(ORGANISM, organism));
    }

    /**
     * Adds an organism with a description as an annotation to
     *
     * @param organism    The organism.
     * @param description The description.
     */
    public void addOrganism(Organism organism, String description) {
        addAnnotation(new Annotation<>(ORGANISM, description, organism));
    }

    /**
     * Retrieves all Organisms annotated.
     *
     * @return All Organisms annotated.
     */
    public List<Organism> getAllOrganisms() {
        return getContentOfAnnotations(Organism.class, ORGANISM);
    }

    /**
     * Tries to retrieve organisms with a certain description.
     *
     * @param description The description.
     * @return The Organisms.
     */
    public List<Organism> getOrganismsWith(String description) {
        return getContentOfAnnotations(Organism.class, description, ORGANISM);
    }

    /**
     * Adds an amino acid sequence as an annotation.
     *
     * @param sequence The amino acid sequence.
     */
    public void addAminoAcidSequence(String sequence) {
        addAnnotation(new Annotation<>(AMINO_ACID_SEQUENCE, sequence));
    }

    /**
     * Gets all amino acid sequences annotated.
     *
     * @return The amino acid sequences.
     */
    public List<String> getAllAminoAcidSequences() {
        return getContentOfAnnotations(String.class, AMINO_ACID_SEQUENCE);
    }

    /**
     * Gets all amino acid sequences annotated with a certain description.
     *
     * @param description The description
     * @return The amino acid sequences.
     */
    public List<String> getAllAminoAcidSequencesWith(String description) {
        return getContentOfAnnotations(String.class, description, AMINO_ACID_SEQUENCE);
    }

    public static class Builder extends ChemicalEntity.Builder<Protein, Builder, SimpleStringIdentifier> {
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
