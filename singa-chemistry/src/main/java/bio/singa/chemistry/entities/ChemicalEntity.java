package bio.singa.chemistry.entities;

import bio.singa.chemistry.annotations.Annotatable;
import bio.singa.chemistry.annotations.Annotation;
import bio.singa.chemistry.annotations.AnnotationType;
import bio.singa.chemistry.features.ChemistryFeatureContainer;
import bio.singa.chemistry.features.diffusivity.Diffusivity;
import bio.singa.chemistry.features.permeability.MembranePermeability;
import bio.singa.chemistry.features.structure3d.Structure3D;
import bio.singa.core.utility.Nameable;
import bio.singa.features.identifiers.*;
import bio.singa.features.identifiers.model.Identifiable;
import bio.singa.features.identifiers.model.Identifier;
import bio.singa.features.identifiers.model.IdentifierPatternRegistry;
import bio.singa.features.model.Feature;
import bio.singa.features.model.FeatureContainer;
import bio.singa.features.model.Featureable;
import bio.singa.features.quantities.MolarVolume;
import bio.singa.structure.features.molarmass.MolarMass;

import java.util.*;

/**
 * Chemical Entity is an abstract class that provides the common features of all chemical substances on a descriptive
 * level. Each chemical entity should be identifiable by an
 * {@link Identifier}. Chemical entities can be annotated, posses a {@link MolarMass} and a name.
 *
 * @author cl
 * @see <a href="https://de.wikipedia.org/wiki/Simplified_Molecular_Input_Line_Entry_Specification">Wikipedia:
 * SMILES</a>
 */
public abstract class ChemicalEntity implements Identifiable<SimpleStringIdentifier>, Nameable, Annotatable, Featureable {

    public static final Set<Class<? extends Feature>> availableFeatures = new HashSet<>();

    static {
        availableFeatures.add(InChIKey.class);
        availableFeatures.add(ChEBIIdentifier.class);
        availableFeatures.add(PubChemIdentifier.class);
        availableFeatures.add(PDBLigandIdentifier.class);
        availableFeatures.add(Diffusivity.class);
        availableFeatures.add(MembranePermeability.class);
        availableFeatures.add(MolarMass.class);
        availableFeatures.add(MolarVolume.class);
        availableFeatures.add(Structure3D.class);
    }

    /**
     * The distinct {@link Identifier} by which this entity is identified.
     */
    protected final SimpleStringIdentifier identifier;

    /**
     * The name by which this entity is referenced.
     */
    protected String name = "Unnamed chemical entity";

    /**
     * All annotations of this entity.
     */
    protected List<Annotation> annotations;

    protected FeatureContainer features;

    private boolean membraneAnchored;

    /**
     * Creates a new Chemical Entity with the given identifier.
     *
     * @param identifier The pdbIdentifier.
     */
    protected ChemicalEntity(SimpleStringIdentifier identifier) {
        this.identifier = identifier;
        membraneAnchored = false;
        annotations = new ArrayList<>();
        features = new ChemistryFeatureContainer();
        IdentifierPatternRegistry.instantiate(identifier.getContent()).ifPresent(this::setFeature);
    }

    @Override
    public SimpleStringIdentifier getIdentifier() {
        return identifier;
    }

    @Override
    public String getName() {
        return name.equals("Unnamed chemical entity") ? identifier.getContent() : name;
    }

    /**
     * Sets the name.
     *
     * @param name The name.
     */
    public void setName(String name) {
        this.name = name;
    }

    public boolean isMembraneAnchored() {
        return membraneAnchored;
    }

    public void setMembraneAnchored(boolean membraneAnchored) {
        this.membraneAnchored = membraneAnchored;
    }

    @Override
    public List<Annotation> getAnnotations() {
        return annotations;
    }

    /**
     * Adds an additional name as an annotation to this chemical entity.
     *
     * @param additionalName An alternative name.
     */
    public void addAdditionalName(String additionalName) {
        addAnnotation(new Annotation<>(AnnotationType.ADDITIONAL_NAME, additionalName));
    }

    /**
     * Gets all additional names for the Annotations as a List of Strings.
     *
     * @return All alternative names.
     */
    public List<String> getAdditionalNames() {
        return getContentOfAnnotations(String.class, AnnotationType.ADDITIONAL_NAME);
    }

    public void addAdditionalIdentifiers(Collection<Identifier> identifiers) {
        identifiers.forEach(this::addAdditionalIdentifier);
    }

    public void addAdditionalIdentifier(Identifier identifier) {
        setFeature(identifier);
    }

    public List<Identifier> getAdditionalIdentifiers() {
        List<Identifier> identifiers = new ArrayList<>();
        for (Feature<?> feature : getFeatures()) {
            if (feature instanceof Identifier) {
                identifiers.add((Identifier) feature);
            }
        }
        identifiers.addAll(getContentOfAnnotations(Identifier.class, AnnotationType.ADDITIONAL_IDENTIFIER));
        return identifiers;
    }

    @Override
    public Collection<Feature<?>> getFeatures() {
        return features.getAllFeatures();
    }

    @Override
    public <FeatureType extends Feature> FeatureType getFeature(Class<FeatureType> featureTypeClass) {
        if (!features.hasFeature(featureTypeClass)) {
            setFeature(featureTypeClass);
        }
        return features.getFeature(featureTypeClass);
    }

    @Override
    public <FeatureType extends Feature> void setFeature(Class<FeatureType> featureTypeClass) {
        features.setFeature(featureTypeClass, this);
    }

    @Override
    public <FeatureType extends Feature> void setFeature(FeatureType feature) {
        features.setFeature(feature);
    }

    @Override
    public <FeatureType extends Feature> boolean hasFeature(Class<FeatureType> featureTypeClass) {
        return features.hasFeature(featureTypeClass);
    }

    @Override
    public Set<Class<? extends Feature>> getAvailableFeatures() {
        return availableFeatures;
    }

    public List<Identifier> getAllIdentifiers() {
        List<Identifier> identifiers = getAdditionalIdentifiers();
        identifiers.add(identifier);
        return identifiers;
    }

    @Override
    public String toString() {
        return "Entity " + getIdentifier().getContent();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChemicalEntity that = (ChemicalEntity) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    public static abstract class Builder<TopLevelType extends ChemicalEntity, BuilderType extends Builder> {

        final TopLevelType topLevelObject;
        final BuilderType builderObject;

        public Builder(SimpleStringIdentifier identifier) {
            topLevelObject = createObject(identifier);
            builderObject = getBuilder();
        }

        public Builder(String identifier) {
            topLevelObject = createObject(new SimpleStringIdentifier(identifier));
            builderObject = getBuilder();
        }

        protected abstract TopLevelType createObject(SimpleStringIdentifier primaryIdentifer);

        protected abstract BuilderType getBuilder();

        public BuilderType name(String name) {
            topLevelObject.setName(name);
            return builderObject;
        }

        public BuilderType setMembraneAnchored(boolean membraneAnchored) {
            topLevelObject.setMembraneAnchored(membraneAnchored);
            return builderObject;
        }

        public BuilderType assignFeature(Feature feature) {
            topLevelObject.setFeature(feature);
            return builderObject;
        }

        public BuilderType assignFeature(Class<? extends Feature> feature) {
            topLevelObject.setFeature(feature);
            return builderObject;
        }

        public BuilderType additionalIdentifier(Identifier identifier) {
            topLevelObject.addAdditionalIdentifier(identifier);
            return builderObject;
        }

        public BuilderType additionalIdentifiers(Collection<Identifier> identifiers) {
            topLevelObject.addAdditionalIdentifiers(identifiers);
            return builderObject;
        }

        public BuilderType annotation(Annotation annotation) {
            topLevelObject.addAnnotation(annotation);
            return builderObject;
        }

        public TopLevelType build() {
            return topLevelObject;
        }

    }

}