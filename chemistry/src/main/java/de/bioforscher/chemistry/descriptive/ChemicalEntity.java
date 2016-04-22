package de.bioforscher.chemistry.descriptive;

import de.bioforscher.core.identifier.model.Identifiable;
import de.bioforscher.core.identifier.model.Identifier;
import de.bioforscher.core.parameters.Nameable;
import de.bioforscher.units.quantities.MolarMass;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;

import static de.bioforscher.units.UnitDictionary.GRAM_PER_MOLE;

/**
 * Created by Christoph on 18.04.2016.
 */
public abstract class ChemicalEntity<IdentifierType extends Identifier> implements Identifiable<IdentifierType>, Nameable {

    private final IdentifierType identifier;
    private String name = "Unnamed chemical Entity";
    private Quantity<MolarMass> molarMass;

    @Override
    public IdentifierType getIdentifier() {
        return this.identifier;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Quantity<MolarMass> getMolarMass() {
        return this.molarMass;
    }

    public void setMolarMass(Quantity<MolarMass> molarMass) {
        this.molarMass = molarMass;
    }

    public void setMolarMass(double molarMass) {
        this.molarMass = Quantities.getQuantity(molarMass, GRAM_PER_MOLE);
    }

    protected ChemicalEntity(IdentifierType identifier) {
        this.identifier = identifier;
    }

    public static abstract class Builder<TopLevelType extends ChemicalEntity, BuilderType extends Builder, IdentifierType extends Identifier> {

        protected TopLevelType topLevelObject;
        protected BuilderType builderObject;

        protected abstract TopLevelType createObject(IdentifierType identifier);

        protected abstract BuilderType getBuilder();

        public Builder(IdentifierType identifier) {
            topLevelObject = createObject(identifier);
            builderObject = getBuilder();
        }

        public BuilderType name(String name) {
            topLevelObject.setName(name);
            return builderObject;
        }

        public BuilderType molarMass(Quantity<MolarMass> molarMass) {
            topLevelObject.setMolarMass(molarMass);
            return builderObject;
        }

        public BuilderType molarMass(double molarMass) {
            topLevelObject.setMolarMass(molarMass);
            return builderObject;
        }

        public TopLevelType build() {
            return this.topLevelObject;
        }
    }

}