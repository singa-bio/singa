package de.bioforscher.singa.chemistry.descriptive.entities;


import de.bioforscher.singa.chemistry.descriptive.features.databases.uniprot.UniProtParserService;
import de.bioforscher.singa.chemistry.descriptive.features.molarmass.MolarMass;
import de.bioforscher.singa.core.identifier.SimpleStringIdentifier;
import de.bioforscher.singa.core.identifier.UniProtIdentifier;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.features.quantities.ReactionRate;
import de.bioforscher.singa.features.units.UnitProvider;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import java.util.ArrayList;
import java.util.List;

import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static de.bioforscher.singa.features.units.UnitProvider.PER_SECOND;

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
     * Creates a new Enzyme with the given {@link UniProtIdentifier}.
     *
     * @param identifier The {@link UniProtIdentifier}.
     */
    protected Enzyme(SimpleStringIdentifier identifier) {
        super(identifier);
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
     * Sets the turnover number in {@link UnitProvider#PER_SECOND 1/s}.
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
     * Sets the michaelis constant in {@link UnitProvider#MOLE_PER_LITRE mol/l}.
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



    @Override
    public String toString() {
        return "Enzyme: " + getIdentifier() + " " + getName() + " weight: " + getFeature(MolarMass.class);
    }

    public static class Builder extends ChemicalEntity.Builder<Enzyme, Builder, SimpleStringIdentifier> {

        public Builder(SimpleStringIdentifier identifier) {
            super(identifier);
            this.topLevelObject.setSubstrates(new ArrayList<>());
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
            this.topLevelObject.setSubstrates(substrates);
            return this;
        }

        public Builder addSubstrate(Species substrate) {
            this.topLevelObject.getSubstrates().add(substrate);
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
