package bio.singa.chemistry.entities;

import bio.singa.chemistry.annotations.Annotation;
import bio.singa.chemistry.annotations.AnnotationType;
import bio.singa.chemistry.features.ChemistryFeatureContainer;
import bio.singa.chemistry.features.diffusivity.Diffusivity;
import bio.singa.chemistry.features.permeability.MembranePermeability;
import bio.singa.chemistry.features.structure3d.Structure3D;
import bio.singa.features.identifiers.*;
import bio.singa.features.identifiers.model.Identifier;
import bio.singa.features.model.Feature;
import bio.singa.features.model.FeatureContainer;
import bio.singa.features.quantities.MolarVolume;
import bio.singa.structure.features.molarmass.MolarMass;

import java.util.*;

/**
 * @author cl
 */
public abstract class AbstractChemicalEntity implements ChemicalEntity {

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
     * Creates a new chemical entity with the given identifier.
     *
     * @param identifier The identifier.
     */
    protected AbstractChemicalEntity(SimpleStringIdentifier identifier) {
        this.identifier = identifier;
        membraneAnchored = false;
        annotations = new ArrayList<>();
        features = new ChemistryFeatureContainer();
        // IdentifierPatternRegistry.instantiate(identifier.getContent()).ifPresent(this::setFeature);
    }

    @Override
    public SimpleStringIdentifier getIdentifier() {
        return identifier;
    }

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

    public List<Identifier> getAllIdentifiers() {
        List<Identifier> identifiers = features.getAdditionalIdentifiers();
        identifiers.add(identifier);
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



    @Override
    public String toString() {
        return "Entity " + getIdentifier().getContent();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractChemicalEntity that = (AbstractChemicalEntity) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    public static abstract class Builder<TopLevelType extends ChemicalEntity, BuilderType extends Builder> {

        protected final TopLevelType topLevelObject;
        protected final BuilderType builderObject;

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

        public BuilderType assignFeature(Feature feature) {
            topLevelObject.setFeature(feature);
            return builderObject;
        }

        public BuilderType assignFeature(Class<? extends Feature> feature) {
            topLevelObject.setFeature(feature);
            return builderObject;
        }

        public BuilderType additionalIdentifier(Identifier identifier) {
            topLevelObject.setFeature(identifier);
            return builderObject;
        }

        public BuilderType additionalIdentifiers(Collection<Identifier> identifiers) {
            for (Identifier identifier : identifiers) {
                topLevelObject.setFeature(identifier);
            }
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
