package de.bioforscher.chemistry.descriptive;

import de.bioforscher.chemistry.descriptive.annotations.Annotatable;
import de.bioforscher.chemistry.descriptive.annotations.Annotation;
import de.bioforscher.core.identifier.model.Identifiable;
import de.bioforscher.core.identifier.model.Identifier;
import de.bioforscher.core.utility.Nameable;
import de.bioforscher.units.quantities.MolarMass;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import java.util.ArrayList;
import java.util.List;

import static de.bioforscher.chemistry.descriptive.annotations.AnnotationType.ADDITIONAL_NAME;
import static de.bioforscher.units.UnitDictionary.GRAM_PER_MOLE;

/**
 * Created by Christoph on 18.04.2016.
 */
public abstract class ChemicalEntity<IdentifierType extends Identifier> implements Identifiable<IdentifierType>,
        Nameable, Annotatable {

    private final IdentifierType identifier;
    private String name = "Unnamed chemical Entity";
    private Quantity<MolarMass> molarMass;

    private List<Annotation> annotations;

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
        this.annotations = new ArrayList<>();
    }

    @Override
    public List<Annotation> getAnnotations() {
        return this.annotations;
    }

    public void addAdditionalName(String additionalName) {
        addAnnotation(new Annotation<>(ADDITIONAL_NAME, additionalName));
    }

    public List<String> getAdditionalNames() {
        return getContentOfAnnotations(String.class, ADDITIONAL_NAME);
    }

    public static abstract class Builder<TopLevelType extends ChemicalEntity, BuilderType extends Builder, IdentifierType extends Identifier> {

        protected TopLevelType topLevelObject;
        protected BuilderType builderObject;

        protected abstract TopLevelType createObject(IdentifierType identifier);

        protected abstract BuilderType getBuilder();

        public Builder(IdentifierType identifier) {
            this.topLevelObject = createObject(identifier);
            this.builderObject = getBuilder();
        }

        public BuilderType name(String name) {
            this.topLevelObject.setName(name);
            return this.builderObject;
        }

        public BuilderType molarMass(Quantity<MolarMass> molarMass) {
            this.topLevelObject.setMolarMass(molarMass);
            return this.builderObject;
        }

        public BuilderType molarMass(double molarMass) {
            this.topLevelObject.setMolarMass(molarMass);
            return this.builderObject;
        }

        public BuilderType annotation(Annotation annotation) {
            this.topLevelObject.addAnnotation(annotation);
            return this.builderObject;
        }

        public TopLevelType build() {
            return this.topLevelObject;
        }
    }

}