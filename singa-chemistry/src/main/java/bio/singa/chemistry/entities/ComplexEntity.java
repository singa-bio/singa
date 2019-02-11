package bio.singa.chemistry.entities;

import bio.singa.chemistry.annotations.Annotation;
import bio.singa.chemistry.features.diffusivity.Diffusivity;
import bio.singa.chemistry.features.permeability.MembranePermeability;
import bio.singa.chemistry.features.structure3d.Structure3D;
import bio.singa.features.identifiers.*;
import bio.singa.features.identifiers.model.Identifier;
import bio.singa.features.model.Feature;
import bio.singa.features.model.FeatureContainer;
import bio.singa.features.quantities.MolarVolume;
import bio.singa.mathematics.graphs.trees.BinaryTreeNode;
import bio.singa.structure.features.molarmass.MolarMass;

import java.util.*;

/**
 * @author cl
 */
public class ComplexEntity extends BinaryTreeNode<ChemicalEntity> implements ChemicalEntity {

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
    private SimpleStringIdentifier identifier;

    /**
     * The name by which this entity is referenced.
     */
    private String name = "Unnamed chemical entity";

    /**
     * All annotations of this entity.
     */
    private List<Annotation> annotations;

    private FeatureContainer features;

    private boolean membraneAnchored;

    private ComplexEntity() {
    }

    private ComplexEntity(SimpleStringIdentifier identifier) {
        this.identifier = identifier;
    }

    public static ComplexEntity from(ChemicalEntity first, ChemicalEntity second) {
        ComplexEntity complexEntity = new ComplexEntity();
        if (first instanceof ComplexEntity) {
            complexEntity.setLeft((ComplexEntity) first);
        } else {
            complexEntity.addLeft(first);
        }
        if (second instanceof ComplexEntity) {
            complexEntity.setRight((ComplexEntity) second);
        } else {
            complexEntity.addRight(second);
        }
        complexEntity.setData(complexEntity);
        complexEntity.setIdentifier(complexEntity.toNewickString(t -> t.getIdentifier().getContent()));
        return complexEntity;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setMembraneAnchored(boolean membraneAnchored) {
        this.membraneAnchored = membraneAnchored;
    }

    @Override
    public boolean isMembraneAnchored() {
        return membraneAnchored;
    }

    @Override
    public List<Annotation> getAnnotations() {
        return annotations;
    }

    public void setIdentifier(String identifier) {
        this.identifier = new SimpleStringIdentifier(identifier);
    }

    public void setIdentifier(SimpleStringIdentifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public SimpleStringIdentifier getIdentifier() {
        return identifier;
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
        List<Identifier> identifiers = features.getAdditionalIdentifiers();
        identifiers.add(identifier);
        return identifiers;
    }

    public ComplexEntity copy() {
        ComplexEntity copy = new ComplexEntity(identifier);
        if (hasLeft()) {
            copy.setLeft(getLeft().copy());
        }
        if (hasRight()) {
            copy.setRight(getRight().copy());
        }
        copy.setData(copy);
        return copy;
    }


    public ComplexEntity apply(ComplexModification modification) {
        return ComplexModification.apply(this, modification);
    }

    public String complexNotation() {
        String leftString = "";
        String rightString = "";
        if (hasLeft()) {
            leftString = complexNotation();
        }
        if (hasRight()) {
            rightString = complexNotation();
        }
        if (!hasLeft() && !hasRight()) {
            return leftString + getIdentifier().toString() + rightString;
        } else {
            return leftString + ":" + rightString;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComplexEntity that = (ComplexEntity) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    @Override
    public String toString() {
        return "Complex "+identifier;
    }

}
