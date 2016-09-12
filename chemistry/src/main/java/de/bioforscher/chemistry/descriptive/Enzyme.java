package de.bioforscher.chemistry.descriptive;


import de.bioforscher.chemistry.descriptive.annotations.Annotation;
import de.bioforscher.core.biology.Organism;
import de.bioforscher.core.identifier.UniProtIdentifier;
import de.bioforscher.units.quantities.MolarConcentration;
import de.bioforscher.units.quantities.ReactionRate;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import java.util.ArrayList;
import java.util.List;

import static de.bioforscher.chemistry.descriptive.annotations.AnnotationType.SOURCE_ORGANISM;
import static de.bioforscher.units.UnitDictionary.MOLE_PER_LITRE;
import static de.bioforscher.units.UnitDictionary.PER_SECOND;

public class Enzyme extends ChemicalEntity<UniProtIdentifier> {

    private Quantity<MolarConcentration> michaelisConstant;
    private Quantity<ReactionRate> turnoverNumber;
    private List<Species> substrates;
    private Species criticalSubstrate;

    protected Enzyme(UniProtIdentifier identifier) {
        super(identifier);
    }

    public Species getCriticalSubstrate() {
        return criticalSubstrate;
    }

    public Quantity<ReactionRate> getTurnoverNumber() {
        return turnoverNumber;
    }

    public Quantity<MolarConcentration> getMichaelisConstant() {
        return michaelisConstant;
    }

    public List<Species> getSubstrates() {
        return substrates;
    }

    public void setCriticalSubstrate(Species criticalSubstrate) {
        this.criticalSubstrate = criticalSubstrate;
        if (!substrates.contains(criticalSubstrate)) {
            addSubstrate(criticalSubstrate);
        }
    }

    public void setTurnoverNumber(double turnoverNumber) {
        this.turnoverNumber = Quantities.getQuantity(turnoverNumber, PER_SECOND);
    }

    public void setTurnoverNumber(Quantity<ReactionRate> turnoverNumber) {
        this.turnoverNumber = turnoverNumber;
    }

    public void setMichaelisConstant(double michaelisConstant) {
        this.michaelisConstant = Quantities.getQuantity(michaelisConstant, MOLE_PER_LITRE);
    }

    public void setMichaelisConstant(Quantity<MolarConcentration> michaelisConstant) {
        this.michaelisConstant = michaelisConstant;
    }

    public void setSubstrates(List<Species> substrates) {
        this.substrates = substrates;
    }

    public void addSubstrate(Species substrate) {
        this.substrates.add(substrate);
    }

    public void setSourceOrganism(Organism organism) {
        addAnnotation(new Annotation<>(SOURCE_ORGANISM, organism));
    }

    public Organism getSourceOrganism() {
        return getAnnotation(Organism.class, SOURCE_ORGANISM);
    }

    @Override
    public String toString() {
        return "Enzyme: " + getIdentifier() + " " + Character.toUpperCase(getName().charAt(0)) + getName().substring(1)
                + " weight: " + getMolarMass() ;
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
            this.topLevelObject.addSubstrate(substrate);
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
