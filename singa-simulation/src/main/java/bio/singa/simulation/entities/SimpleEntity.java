package bio.singa.simulation.entities;

import bio.singa.chemistry.features.ChemistryFeatureContainer;
import bio.singa.chemistry.features.permeability.MembranePermeability;
import bio.singa.features.identifiers.*;
import bio.singa.features.identifiers.model.Identifier;
import bio.singa.features.model.Feature;
import bio.singa.features.model.FeatureContainer;
import bio.singa.features.quantities.MolarVolume;
import bio.singa.features.quantities.MolarMass;

import java.util.*;

/**
 * @author cl
 */
public class SimpleEntity implements ChemicalEntity {

    public static final Set<Class<? extends Feature>> availableFeatures = new HashSet<>();

    static {
        availableFeatures.add(InChIKey.class);
        availableFeatures.add(ChEBIIdentifier.class);
        availableFeatures.add(PubChemIdentifier.class);
        availableFeatures.add(PDBLigandIdentifier.class);
        availableFeatures.add(MembranePermeability.class);
        availableFeatures.add(MolarMass.class);
        availableFeatures.add(MolarVolume.class);
    }

    public static SimpleEntityBuilder create(String identifier) {
        return new SimpleEntityBuilder(identifier);
    }

    /**
     * The distinct {@link Identifier} by which this entity is identified and registered in the {@link EntityRegistry}.
     */
    private final String identifier;

    /**
     * Determines whether this entity is a bound to the membrane in its native state.
     */
    private boolean membraneBound;

    /**
     * Determines whether this entity is considered small in the context of binding site assignment.
     */
    private boolean isSmall;

    private FeatureContainer features;

    /**
     * Creates a new chemical entity with the given identifier.
     *
     * @param identifier The identifier.
     */
    protected SimpleEntity(String identifier) {
        this.identifier = identifier;
        features = new ChemistryFeatureContainer();
        EntityRegistry.put(identifier, this);
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public boolean isMembraneBound() {
        return membraneBound;
    }

    @Override
    public void setMembraneBound(boolean membraneBound) {
        this.membraneBound = membraneBound;
    }

    @Override
    public boolean isSmall() {
        return isSmall;
    }

    public void setSmall(boolean small) {
        isSmall = small;
    }

    public void addAdditionalIdentifiers(Collection<Identifier> identifiers) {
        identifiers.forEach(this::addAdditionalIdentifier);
    }

    public void addAdditionalIdentifier(Identifier identifier) {
        setFeature(identifier);
    }

    @Override
    public List<Identifier> getAllIdentifiers() {
        List<Identifier> identifiers = features.getAdditionalIdentifiers();
        identifiers.add(new SimpleStringIdentifier(identifier));
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
        return getIdentifier();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleEntity that = (SimpleEntity) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    public static class SimpleEntityBuilder {

        private final SimpleEntity simpleEntity;

        public SimpleEntityBuilder(String identifier) {
            simpleEntity = new SimpleEntity(identifier);
        }

        public SimpleEntityBuilder membraneBound() {
            simpleEntity.setMembraneBound(true);
            return this;
        }

        public SimpleEntityBuilder small() {
            simpleEntity.setSmall(true);
            return this;
        }

        public SimpleEntityBuilder assignFeature(Feature feature) {
            simpleEntity.setFeature(feature);
            return this;
        }

        public SimpleEntityBuilder assignFeature(Class<? extends Feature> feature) {
            simpleEntity.setFeature(feature);
            return this;
        }

        public SimpleEntityBuilder additionalIdentifier(Identifier identifier) {
            simpleEntity.setFeature(identifier);
            return this;
        }

        public SimpleEntityBuilder additionalIdentifiers(Collection<Identifier> identifiers) {
            for (Identifier identifier : identifiers) {
                assignFeature(identifier);
            }
            return this;
        }

        public SimpleEntity build() {
            return simpleEntity;
        }

    }

}
