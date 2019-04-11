package bio.singa.chemistry.entities;

import bio.singa.chemistry.annotations.Annotation;
import bio.singa.chemistry.features.ChemistryFeatureContainer;
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
     * All annotations of this entity.
     */
    private List<Annotation> annotations;

    private FeatureContainer features;

    private boolean membraneAnchored;

    public ComplexEntity() {
        annotations = new ArrayList<>();
        features = new ChemistryFeatureContainer();
    }

    private ComplexEntity(SimpleStringIdentifier identifier) {
        this.identifier = identifier;
    }

    public static ComplexEntity from(ChemicalEntity... entities) {
        if (entities.length < 2) {
            throw new IllegalArgumentException("At least two entities are required to create a complex.");
        }
        ChemicalEntity currentEntity = entities[0];
        for (int i = 1; i < entities.length; i++) {
            currentEntity = from(currentEntity, entities[i]);
        }
        return (ComplexEntity) currentEntity;
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
        complexEntity.setIdentifier(complexEntity.toNewickString(t -> t.getIdentifier().getContent(), ":"));
        return complexEntity;
    }


    public void appendEntity(ChemicalEntity data) {
        if (!hasLeft()) {
            if (data instanceof ComplexEntity) {
                setLeft((ComplexEntity) data);
            } else {
                addLeft(data);
            }
        } else if (!hasRight()) {
            if (data instanceof ComplexEntity) {
                setRight((ComplexEntity) data);
            } else {
                addRight(data);
            }
        }
        setData(this);
    }

    public void replace(ChemicalEntity replacement, ChemicalEntity replacementPosition) {
        // find path to modification position
        List<BinaryTreeNode<ChemicalEntity>> path = pathTo(replacementPosition);
        if (path == null || path.isEmpty()) {
            throw new IllegalStateException("Replacement position" + replacementPosition + " could not be fund in " + this);
        }
        // perform addition and rename path to addition
        ListIterator<BinaryTreeNode<ChemicalEntity>> iterator = path.listIterator(path.size());
        while (iterator.hasPrevious()) {
            BinaryTreeNode<ChemicalEntity> current = iterator.previous();
            if (current.getData().equals(replacementPosition)) {
                continue;
            }
            if (current.getLeft().getData().equals(replacementPosition)) {
                if (replacement instanceof ComplexEntity) {
                    current.setLeft(((ComplexEntity) replacement));
                } else {
                    current.addLeft(replacement);
                }
            }
            if (current.getRight().getData().equals(replacementPosition)) {
                if (replacement instanceof ComplexEntity) {
                    current.setRight(((ComplexEntity) replacement));
                } else {
                    current.addLeft(replacement);
                }
            }
            ((ComplexEntity) current).setIdentifier(current.toNewickString(t -> t.getIdentifier().getContent(), ":"));
        }
    }

    /**
     * Removes the given entity, but only at the specified position and renames all affected inner nodes.
     *
     * @param toBeRemoved The entity to be removed.
     * @param removalPosition The removal position.
     */
    public void remove(ChemicalEntity toBeRemoved, ChemicalEntity removalPosition) {
        // find path to modification position
        List<BinaryTreeNode<ChemicalEntity>> path = pathTo(removalPosition);
        if (path == null || path.isEmpty()) {
            throw new IllegalStateException("Removal position" + removalPosition + " could not be fund in " + this);
        }
        // find part of the tree that will be maintained
        BinaryTreeNode<ChemicalEntity> node = path.get(path.size() - 1);
        ChemicalEntity retainedEntity = null;
        if (node.hasLeft()) {
            if (node.getLeft().getData().equals(toBeRemoved)) {
                retainedEntity = node.getRight().getData();
            }
            if (node.getRight().getData().equals(toBeRemoved)) {
                retainedEntity = node.getLeft().getData();
            }
        }
        if (retainedEntity == null) {
            throw new IllegalStateException("Entity to remove" + toBeRemoved + " could not be fund in " + this);
        }
        // substitute what remains to the modification position
        substitute(removalPosition, retainedEntity);
        // rename updated path to the root
        for (BinaryTreeNode<ChemicalEntity> current : path) {
            ((ComplexEntity) current).setIdentifier(current.toNewickString(t -> t.getIdentifier().getContent(), ":"));
        }
    }

    /**
     * Removes the given entity from the tree and renames all affected inner nodes.
     *
     * @param toBeRemoved The entity to be removed.
     */
    public void remove(ChemicalEntity toBeRemoved) {
        // find path to modification position
        List<BinaryTreeNode<ChemicalEntity>> path = pathTo(toBeRemoved);
        if (path == null || path.isEmpty()) {
            throw new IllegalStateException("Entity to remove" + toBeRemoved + " could not be fund in " + this);
        }
        // find part of the tree that will be maintained
        BinaryTreeNode<ChemicalEntity> parent = path.get(path.size() - 1);
        BinaryTreeNode<ChemicalEntity> retainedEntity = null;
        if (parent.hasLeft()) {
            if (parent.getLeft().getData().equals(toBeRemoved)) {
                retainedEntity = parent.getRight();
            }
        } else if (parent.hasRight()) {
            if (parent.getRight().getData().equals(toBeRemoved)) {
                retainedEntity = parent.getLeft();
            }
        }
        if (retainedEntity == null) {
            throw new IllegalStateException("Entity to remove" + toBeRemoved + " could not be fund in " + this);
        }
        // substitute what remains to the modification position
        substitute(parent, retainedEntity);
        // rename updated path to the root
        for (BinaryTreeNode<ChemicalEntity> current : path) {
            ((ComplexEntity) current).setIdentifier(current.toNewickString(t -> t.getIdentifier().getContent(), ":"));
        }
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
        return "Complex " + identifier;
    }

}
