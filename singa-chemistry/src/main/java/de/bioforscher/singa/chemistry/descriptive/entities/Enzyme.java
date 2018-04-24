package de.bioforscher.singa.chemistry.descriptive.entities;


import de.bioforscher.singa.chemistry.descriptive.features.databases.uniprot.UniProtParserService;
import de.bioforscher.singa.chemistry.descriptive.features.reactions.MichaelisConstant;
import de.bioforscher.singa.chemistry.descriptive.features.reactions.TurnoverNumber;
import de.bioforscher.singa.features.identifiers.SimpleStringIdentifier;
import de.bioforscher.singa.features.identifiers.UniProtIdentifier;
import de.bioforscher.singa.features.model.Feature;
import de.bioforscher.singa.structure.features.molarmass.MolarMass;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    private static final Set<Class<? extends Feature>> availableFeatures = new HashSet<>();

    static {
        Enzyme.availableFeatures.addAll(ChemicalEntity.availableFeatures);
        availableFeatures.add(MichaelisConstant.class);
        availableFeatures.add(TurnoverNumber.class);
    }

    /**
     * A list of possible substrate.
     */
    private List<SmallMolecule> substrates;

    /**
     * Creates a new Enzyme with the given {@link UniProtIdentifier}.
     *
     * @param identifier The {@link UniProtIdentifier}.
     */
    protected Enzyme(SimpleStringIdentifier identifier) {
        super(identifier);
    }

    /**
     * Returns possible substrates of this enzyme.
     *
     * @return The possible substrates
     */
    public List<SmallMolecule> getSubstrates() {
        return substrates;
    }

    /**
     * Sets possible substrates of this enzyme.
     *
     * @param substrates The possible substrates
     */
    public void setSubstrates(List<SmallMolecule> substrates) {
        this.substrates = substrates;
    }

    @Override
    public Set<Class<? extends Feature>> getAvailableFeatures() {
        return availableFeatures;
    }

    @Override
    public String toString() {
        return "Enzyme: " + getIdentifier() + " " + getName() + " weight: " + getFeature(MolarMass.class);
    }

    public static class Builder extends ChemicalEntity.Builder<Enzyme, Builder> {

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

        public Builder substrates(List<SmallMolecule> substrates) {
            topLevelObject.setSubstrates(substrates);
            return this;
        }

        public Builder addSubstrate(SmallMolecule substrate) {
            topLevelObject.getSubstrates().add(substrate);
            return this;
        }

    }

}
