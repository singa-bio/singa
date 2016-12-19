package de.bioforscher.chemistry.descriptive;


import de.bioforscher.chemistry.descriptive.annotations.Annotation;
import de.bioforscher.chemistry.parser.uniprot.UniProtParserService;
import de.bioforscher.core.biology.Organism;
import de.bioforscher.core.identifier.UniProtIdentifier;
import de.bioforscher.units.quantities.MolarConcentration;
import de.bioforscher.units.quantities.ReactionRate;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import java.util.ArrayList;
import java.util.List;

import static de.bioforscher.chemistry.descriptive.annotations.AnnotationType.AMINO_ACID_SEQUENCE;
import static de.bioforscher.chemistry.descriptive.annotations.AnnotationType.ORGANISM;
import static de.bioforscher.units.UnitProvider.MOLE_PER_LITRE;
import static de.bioforscher.units.UnitProvider.PER_SECOND;

/**
 * An Enzyme is a Protein, that is associated with a catalytic function. For the usage in reactions this chemical
 * entity can be supplied with a Michaelis constant (usually abbreviated with km), an turnover number (abbriviated
 * with kcat), a List of possible substrates and a critical substrate that is rate determining. Additionally multiple
 * predefined Annotations can be set (additional names, organisms, amino acid sequences, ...). Enzymes may be parsed
 * from the UniProt Database using the
 * {@link UniProtParserService UniProtParserService}.
 *
 * @author cl
 * @see <a href="https://en.wikipedia.org/wiki/Michaelis%E2%80%93Menten_kinetics">Wikipedia: Michaelis–Menten
 * kinetics</a>
 */
public class Enzyme extends ChemicalEntity<UniProtIdentifier> {

    /**
     * The michaelis constant is an inverse measure of the substrate's affinity to the enzyme.
     */
    private Quantity<MolarConcentration> michaelisConstant;

    /**
     * The turnover number is the maximal number of substrate molecules converted to product by enzyme and second.
     */
    private Quantity<ReactionRate> turnoverNumber;

    /**
     * A list of possible substrate.
     */
    private List<Species> substrates;

    /**
     * The primary or critical substrate used in a reaction.
     */
    private Species criticalSubstrate;

    /**
     * Creates a new Enzyme with the given {@link UniProtIdentifier}.
     *
     * @param identifier The {@link UniProtIdentifier}.
     */
    protected Enzyme(UniProtIdentifier identifier) {
        super(identifier);
    }

    /**
     * Returns the critical Substrate.
     *
     * @return The critical Substrate.
     */
    public Species getCriticalSubstrate() {
        return this.criticalSubstrate;
    }

    /**
     * Sets the critical Substrate.
     *
     * @param criticalSubstrate The critical substrate.
     */
    public void setCriticalSubstrate(Species criticalSubstrate) {
        this.criticalSubstrate = criticalSubstrate;
        if (!this.substrates.contains(criticalSubstrate)) {
            this.substrates.add(criticalSubstrate);
        }
    }

    /**
     * Returns the turnover number.
     *
     * @return The turnover number.
     */
    public Quantity<ReactionRate> getTurnoverNumber() {
        return this.turnoverNumber;
    }

    /**
     * Sets the turnover number in {@link de.bioforscher.units.UnitProvider#PER_SECOND 1/s}.
     *
     * @param turnoverNumber The turnover number.
     */
    public void setTurnoverNumber(double turnoverNumber) {
        this.turnoverNumber = Quantities.getQuantity(turnoverNumber, PER_SECOND);
    }

    /**
     * Sets the turnover number.
     *
     * @param turnoverNumber The turnover number.
     */
    public void setTurnoverNumber(Quantity<ReactionRate> turnoverNumber) {
        this.turnoverNumber = turnoverNumber;
    }

    /**
     * Returns the michaelis constant.
     *
     * @return The michaelis constant.
     */
    public Quantity<MolarConcentration> getMichaelisConstant() {
        return this.michaelisConstant;
    }

    /**
     * Sets the michaelis constant in {@link de.bioforscher.units.UnitProvider#MOLE_PER_LITRE mol/l}.
     *
     * @param michaelisConstant The michaelis constant.
     */
    public void setMichaelisConstant(double michaelisConstant) {
        this.michaelisConstant = Quantities.getQuantity(michaelisConstant, MOLE_PER_LITRE);
    }

    /**
     * Sets the michaelis constant.
     *
     * @param michaelisConstant The michaelis constant.
     */
    public void setMichaelisConstant(Quantity<MolarConcentration> michaelisConstant) {
        this.michaelisConstant = michaelisConstant;
    }

    /**
     * Returns possible substrates of this enzyme.
     *
     * @return The possible substrates
     */
    public List<Species> getSubstrates() {
        return this.substrates;
    }

    /**
     * Sets possible substrates of this enzyme.
     *
     * @param substrates The possible substrates
     */
    public void setSubstrates(List<Species> substrates) {
        this.substrates = substrates;
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
     * @param organism The organism.
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
    public List<String> getAllAminoAcidSequenceWith(String description) {
        return getContentOfAnnotations(String.class, description, AMINO_ACID_SEQUENCE);
    }

    @Override
    public String toString() {
        return "Enzyme: " + getIdentifier() + " " + getName() + " weight: " + getMolarMass();
    }

    public static class Builder extends ChemicalEntity.Builder<Enzyme, Builder, UniProtIdentifier> {

        public Builder(UniProtIdentifier identifier) {
            super(identifier);
            this.topLevelObject.setSubstrates(new ArrayList<>());
        }

        public Builder(String identifier) {
            this(new UniProtIdentifier(identifier));
        }

        @Override
        protected Enzyme createObject(UniProtIdentifier identifier) {
            return new Enzyme(identifier);
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }

        public Builder substrates(List<Species> substrates) {
            this.topLevelObject.setSubstrates(substrates);
            return this;
        }

        public Builder addSubstrate(Species substrate) {
            this.topLevelObject.getSubstrates().add(substrate);
            return this;
        }

        public Builder criticalSubstrate(Species criticalSubstrate) {
            this.topLevelObject.setCriticalSubstrate(criticalSubstrate);
            return this;
        }

        public Builder turnoverNumber(Quantity<ReactionRate> turnoverNumber) {
            this.topLevelObject.setTurnoverNumber(turnoverNumber);
            return this;
        }

        public Builder turnoverNumber(double turnoverNumber) {
            this.topLevelObject.setTurnoverNumber(turnoverNumber);
            return this;
        }

        public Builder michaelisConstant(double michaelisConstant) {
            this.topLevelObject.setMichaelisConstant(michaelisConstant);
            return this;
        }

        public Builder michaelisConstant(Quantity<MolarConcentration> michaelisConstant) {
            this.topLevelObject.setMichaelisConstant(michaelisConstant);
            return this;
        }

    }

}
