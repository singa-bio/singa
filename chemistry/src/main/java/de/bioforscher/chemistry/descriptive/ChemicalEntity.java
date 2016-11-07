package de.bioforscher.chemistry.descriptive;

import de.bioforscher.chemistry.descriptive.annotations.Annotatable;
import de.bioforscher.chemistry.descriptive.annotations.Annotation;
import de.bioforscher.core.identifier.model.Identifiable;
import de.bioforscher.core.identifier.model.Identifier;
import de.bioforscher.core.utility.Nameable;
import de.bioforscher.units.UnitProvider;
import de.bioforscher.units.quantities.MolarMass;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import java.util.ArrayList;
import java.util.List;

import static de.bioforscher.chemistry.descriptive.annotations.AnnotationType.ADDITIONAL_NAME;
import static de.bioforscher.units.UnitProvider.GRAM_PER_MOLE;

/**
 * Chemical Entity is an abstract class that provides the common features of all chemical substances on a descriptive
 * level. It does not contain the exact chemical structure, to handle chemical structures have a look at
 * {@link de.bioforscher.chemistry.physical.Structure Structure}. Each chemical entity should be identifiable by an
 * {@link Identifier}. Chemical entities can be annotated, posses a {@link MolarMass} and a name.
 *
 * @param <IdentifierType> The Type of the {@link Identifier}, that identifies this entity.
 * @author cl
 * @see <a href="https://de.wikipedia.org/wiki/Simplified_Molecular_Input_Line_Entry_Specification">Wikipedia:
 * SMILES</a>
 */
public abstract class ChemicalEntity<IdentifierType extends Identifier> implements Identifiable<IdentifierType>,
        Nameable, Annotatable {

    /**
     * The distinct {@link Identifier} by which this entity is identified.
     */
    private final IdentifierType identifier;

    /**
     * The name by which this entity is referenced.
     */
    private String name = "Unnamed chemical entity";

    /**
     * The molar mass of this entity.
     */
    private Quantity<MolarMass> molarMass;

    /**
     * All annotations of this entity.
     */
    private List<Annotation> annotations;

    /**
     * Creates a new Chemical Entity with the given identifier.
     *
     * @param identifier The identifier.
     */
    protected ChemicalEntity(IdentifierType identifier) {
        this.identifier = identifier;
        this.annotations = new ArrayList<>();
    }

    @Override
    public IdentifierType getIdentifier() {
        return this.identifier;
    }

    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name.
     *
     * @param name The name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the {@link MolarMass}.
     *
     * @return The {@link MolarMass}.
     */
    public Quantity<MolarMass> getMolarMass() {
        return this.molarMass;
    }

    /**
     * Sets The {@link MolarMass} in {@link UnitProvider#GRAM_PER_MOLE g/mol}.
     *
     * @param molarMass The {@link MolarMass} in {@link UnitProvider#GRAM_PER_MOLE g/mol}.
     */
    public void setMolarMass(double molarMass) {
        this.molarMass = Quantities.getQuantity(molarMass, GRAM_PER_MOLE);
    }

    /**
     * Sets the {@link MolarMass}.
     *
     * @param molarMass The {@link MolarMass}.
     */
    public void setMolarMass(Quantity<MolarMass> molarMass) {
        this.molarMass = molarMass;
    }

    @Override
    public List<Annotation> getAnnotations() {
        return this.annotations;
    }

    /**
     * Adds an additional name as an annotation to this chemical entity.
     *
     * @param additionalName An alternative name.
     */
    public void addAdditionalName(String additionalName) {
        addAnnotation(new Annotation<>(ADDITIONAL_NAME, additionalName));
    }

    /**
     * Gets all additional names for the Annotations as a List of Strings.
     *
     * @return All alternative names.
     */
    public List<String> getAdditionalNames() {
        return getContentOfAnnotations(String.class, ADDITIONAL_NAME);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChemicalEntity<?> that = (ChemicalEntity<?>) o;
        return this.identifier != null ? this.identifier.equals(that.identifier) : that.identifier == null;
    }

    @Override
    public int hashCode() {
        return this.identifier != null ? this.identifier.hashCode() : 0;
    }

    public static abstract class Builder<TopLevelType extends ChemicalEntity, BuilderType extends Builder, IdentifierType extends Identifier> {

        TopLevelType topLevelObject;
        BuilderType builderObject;

        public Builder(IdentifierType identifier) {
            this.topLevelObject = createObject(identifier);
            this.builderObject = getBuilder();
        }

        protected abstract TopLevelType createObject(IdentifierType identifier);

        protected abstract BuilderType getBuilder();

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