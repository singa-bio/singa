package de.bioforscher.singa.chemistry.descriptive.entities;


import de.bioforscher.singa.chemistry.descriptive.features.databases.uniprot.UniProtParserService;
import de.bioforscher.singa.chemistry.descriptive.features.molarmass.MolarMass;
import de.bioforscher.singa.chemistry.descriptive.features.reactions.MichaelisConstant;
import de.bioforscher.singa.chemistry.descriptive.features.reactions.TurnoverNumber;
import de.bioforscher.singa.core.identifier.SimpleStringIdentifier;
import de.bioforscher.singa.core.identifier.UniProtIdentifier;

import java.util.ArrayList;
import java.util.List;

/**
 * An Enzyme is a Protein, that is associated with a catalytic function. For the usage in reactions this chemical
 * entity can be supplied with a Michaelis constant (usually abbreviated with km), an turnover number (abbreviated
 * with kcat), a List of possible substrates and a critical substrate that is rate determining. Additionally multiple
 * predefined Annotations can be set (additional names, organisms, amino acid sequences, ...). Enzymes may be parsed
 * from the UniProt Database using the
 * {@link UniProtParserService UniProtParserService}.
 *
 * @author cl
 * @see <a href="https://en.wikipedia.org/wiki/Michaelis%E2%80%93Menten_kinetics">Wikipedia: Michaelis–Menten
 * kinetics</a>
 */
public class Enzyme extends Protein {

    /**
     * A list of possible substrate.
     */
    private List<Species> substrates;

    /**
     * Creates a new Enzyme with the given {@link UniProtIdentifier}.
     *
     * @param identifier The {@link UniProtIdentifier}.
     */
    protected Enzyme(SimpleStringIdentifier identifier) {
        super(identifier);
        availableFeatures.add(MichaelisConstant.class);
        availableFeatures.add(TurnoverNumber.class);
    }

    /**
     * Returns possible substrates of this enzyme.
     *
     * @return The possible substrates
     */
    public List<Species> getSubstrates() {
        return substrates;
    }

    /**
     * Sets possible substrates of this enzyme.
     *
     * @param substrates The possible substrates
     */
    public void setSubstrates(List<Species> substrates) {
        this.substrates = substrates;
    }

    @Override
    public String toString() {
        return "Enzyme: " + getIdentifier() + " " + getName() + " weight: " + getFeature(MolarMass.class);
    }

    public static class Builder extends ChemicalEntity.Builder<Enzyme, Builder, SimpleStringIdentifier> {

        public Builder(SimpleStringIdentifier identifier) {
            super(identifier);
            topLevelObject.setSubstrates(new ArrayList<>());
        }

        public Builder(String identifier) {
            this(new SimpleStringIdentifier(identifier));
        }

        @Override
        protected Enzyme createObject(SimpleStringIdentifier primaryIdentifer) {
            return new Enzyme(primaryIdentifer);
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }

        public Builder substrates(List<Species> substrates) {
            topLevelObject.setSubstrates(substrates);
            return this;
        }

        public Builder addSubstrate(Species substrate) {
            topLevelObject.getSubstrates().add(substrate);
            return this;
        }

    }

}
