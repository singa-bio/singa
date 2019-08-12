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
    private String identifier;

    private String referenceIdentifier;

    private boolean membraneBound;

    /**
     * All annotations of this entity.
     */
    private List<Annotation> annotations;

    private FeatureContainer features;

    public ComplexEntity() {
        annotations = new ArrayList<>();
        features = new ChemistryFeatureContainer();
    }

    private ComplexEntity(String identifier) {
        this();
        this.identifier = identifier;
    }

    private void update() {
        setIdentifier(toNewickString(ChemicalEntity::getIdentifier, ":"));
        membraneBound = false;
        for (ChemicalEntity entity : getAllData()) {
            if (entity.isMembraneBound()) {
                setMembraneBound(true);
                break;
            }
        }
        referenceIdentifier = nonSiteString();
    }

    private void updateGlobal() {
        inOrderTraversal(node -> {
            if (!node.isLeaf()) {
                ComplexEntity entity = (ComplexEntity) node;
                entity.update();
            }
        });
    }

    private void updatePath(List<BinaryTreeNode<ChemicalEntity>> path) {
        ListIterator<BinaryTreeNode<ChemicalEntity>> iterator = path.listIterator(path.size());
        while (iterator.hasPrevious()) {
            // rename if complex entity
            BinaryTreeNode<ChemicalEntity> complexToUpdate = iterator.previous();
            if (complexToUpdate instanceof ComplexEntity) {
                ((ComplexEntity) complexToUpdate).update();
            }
        }
    }

    public String getReferenceIdentifier() {
        return referenceIdentifier;
    }

    private String nonSiteString() {
        List<String> sites = new ArrayList<>();
        inOrderTraversal(node -> {
            if (node.isLeaf()) {
                if (!(node.getData() instanceof ModificationSite)) {
                    sites.add((node.getData().getIdentifier()));
                }
            }
        });
        return String.join("-", sites);
    }

    @Override
    public boolean isMembraneBound() {
        return membraneBound;
    }

    @Override
    public void setMembraneBound(boolean membraneBound) {
        this.membraneBound = membraneBound;
    }

    public static ComplexEntity from(ChemicalEntity first, ChemicalEntity... entities) {
        if (entities.length < 1) {
            throw new IllegalArgumentException("At least two entities are required to create a complex.");
        }
        ChemicalEntity currentEntity = first;
        for (ChemicalEntity entity : entities) {
            currentEntity = combine(currentEntity, entity);
        }
        ComplexEntity complexEntity = (ComplexEntity) currentEntity;
        complexEntity.update();
        EntityRegistry.put(complexEntity.getReferenceIdentifier(), complexEntity);
        return complexEntity;
    }

    public static ComplexEntity from(ChemicalEntity first, ChemicalEntity second) {
        ComplexEntity complexEntity = combine(first, second);
        complexEntity.update();
        EntityRegistry.put(complexEntity.getReferenceIdentifier(), complexEntity);
        return complexEntity;
    }

    private static ComplexEntity combine(ChemicalEntity first, ChemicalEntity second) {
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
            return;
        }
        // get parent
        BinaryTreeNode<ChemicalEntity> parentNode = path.get(path.size() - 2);
        // replace with replacement
        if (parentNode.hasLeft()) {
            if (parentNode.getLeft().getData().equals(replacementPosition)) {
                if (replacement instanceof ComplexEntity) {
                    parentNode.setLeft(((ComplexEntity) replacement));
                } else {
                    parentNode.addLeft(replacement);
                }
            }
        }
        if (parentNode.hasRight()) {
            if (parentNode.getRight().getData().equals(replacementPosition)) {
                if (replacement instanceof ComplexEntity) {
                    parentNode.setRight(((ComplexEntity) replacement));
                } else {
                    parentNode.addRight(replacement);
                }
            }
        }
        updatePath(path);
        EntityRegistry.put(referenceIdentifier, this);
    }

    public boolean attach(ChemicalEntity attachment, ModificationSite attachmentPosition) {
        // find path to modification position
        List<BinaryTreeNode<ChemicalEntity>> path = pathTo(attachmentPosition);
        // return if attachment position cannot be found
        if (path == null || path.isEmpty()) {
            return false;
        }
        // return if attachment position is not occupied
        ModificationSite originalSite = (ModificationSite) path.get(path.size() - 1).getData();
        if (originalSite.isOccupied()) {
            return false;
        }
        // set occupied
        ModificationSite modifiedSite = originalSite.copy();
        modifiedSite.setOccupied(true);
        // create modified attachment
        ComplexEntity modifiedAttachment;
        if (attachment instanceof ComplexEntity) {
            modifiedAttachment = ((ComplexEntity) attachment).copy();
            modifiedAttachment.replace(modifiedSite, originalSite);
        } else {
            modifiedAttachment = ComplexEntity.from(modifiedSite, attachment);
        }
        // perform addition at parent
        BinaryTreeNode<ChemicalEntity> current = path.get(path.size() - 2);
        if (current.hasLeft() && current.getLeft().getData().equals(attachmentPosition)) {
            current.setLeft(modifiedAttachment);
        } else if (current.hasRight() && current.getRight().getData().equals(attachmentPosition)) {
            current.setRight(modifiedAttachment);
        }
        // rename path to addition
        updatePath(path);
        EntityRegistry.put(referenceIdentifier, this);
        return true;
    }

    public void removeFromPosition(ChemicalEntity toBeRemoved, ChemicalEntity removalPosition) {
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
            throw new IllegalStateException("Entity to remove " + toBeRemoved + " could not be fund in " + this);
        }
        if (retainedEntity instanceof ModificationSite) {
            // set occupied
            ModificationSite modificationSite = ((ModificationSite) retainedEntity).copy();
            modificationSite.setOccupied(false);
            retainedEntity = modificationSite;
        }
        // substitute what remains to the modification position
        substitute(removalPosition, retainedEntity);
        // rename updated path to the root
        updatePath(path);
        EntityRegistry.put(referenceIdentifier, this);
    }

    /**
     * Searches for the modification part at the specified modification site, removes it from this tree and returns
     * it. This tries to maintain all parts that are associated to the split of complex.
     *
     * @param target The binding partner that will be maintained.
     * @param modification The binding partner that will be removed.
     * @param site The binding site where both parts bind.
     * @return The split of complex.
     */
    public ComplexEntity splitOfComplex(ChemicalEntity target, ChemicalEntity modification, ModificationSite site) {
        // find path to modification position
        List<BinaryTreeNode<ChemicalEntity>> path = pathTo(site);
        if (path == null || path.isEmpty()) {
            throw new IllegalStateException("Site to split of from " + site + " could not be fund in " + this);
        }
        // find part of the tree that will be removed
        ListIterator<BinaryTreeNode<ChemicalEntity>> iterator = path.listIterator(path.size());
        BinaryTreeNode<ChemicalEntity> previous = null;
        while (iterator.hasPrevious()) {
            BinaryTreeNode<ChemicalEntity> current = iterator.previous();
            // if there is the target part in the tree we have traversed one too far
            if (current.find(target) != null) {
                //
                if (current.getLeft().find(target) != null) {
                    current.setRight(new BinaryTreeNode<>(site));
                } else if (current.getRight().find(target) != null) {
                    current.setLeft(new BinaryTreeNode<>(site));
                }
                break;
            }
            // the previous part contains the part that contains the complex that is split off
            previous = current;
        }
        // rename updated path to the root
        updateGlobal();
        EntityRegistry.put(referenceIdentifier, this);
        // also for the split of part
        if (previous.getData() instanceof ComplexEntity) {
            ComplexEntity splitOf = (ComplexEntity) previous.getData();
            splitOf.update();
            EntityRegistry.put(splitOf.getReferenceIdentifier(), splitOf);
            return splitOf;
        } else {
            return ComplexEntity.from(previous.getData(), site);
        }
    }

    public List<ModificationSite> getSites() {
        List<ModificationSite> sites = new ArrayList<>();
        inOrderTraversal(node -> {
            if (node.getData() instanceof ModificationSite) {
                sites.add(((ModificationSite) node.getData()));
            }
        });
        return sites;
    }

    public List<Protein> getProteins() {
        List<Protein> sites = new ArrayList<>();
        inOrderTraversal(node -> {
            if (node.getData() instanceof Protein) {
                sites.add(((Protein) node.getData()));
            }
        });
        return sites;
    }


    public int countParts(ChemicalEntity entity) {
        List<ChemicalEntity> sites = new ArrayList<>();
        inOrderTraversal(node -> {
            if (node.getData().equals(entity)) {
                sites.add((node.getData()));
            }
        });
        return sites.size();
    }

    @Override
    public List<Annotation> getAnnotations() {
        return annotations;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }


    @Override
    public String getIdentifier() {
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
        identifiers.add(new SimpleStringIdentifier(identifier));
        return identifiers;
    }

    public ComplexEntity copy() {
        ComplexEntity copy = new ComplexEntity(identifier);
        copy.membraneBound = isMembraneBound();
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
