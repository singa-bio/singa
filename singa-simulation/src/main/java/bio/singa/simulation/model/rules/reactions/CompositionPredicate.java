package bio.singa.simulation.model.rules.reactions;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.ComplexEntity;
import bio.singa.chemistry.entities.ModificationSite;
import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.mathematics.graphs.trees.BinaryTreeNode;

import java.util.Objects;
import java.util.function.BiPredicate;

/**
 * @author cl
 */
public enum CompositionPredicate {

    HAS_PART("has part %s",
            (condition, entityToTest) ->
                    Objects.nonNull(((ComplexEntity) entityToTest).find(condition.getEntity()))),

    HAS_N_PART("has exactly %s of %s",
            (condition, entityToTest) ->
                    ((ComplexEntity) entityToTest).countParts(condition.getEntity()) == condition.getNumber()),

    HAS_NOT_PART("has not %s",
            (condition, entityToTest) ->
                    Objects.isNull(((ComplexEntity) entityToTest).find(condition.getEntity()))),

    IS_UNOCCUPIED("has unoccupied %s",
            (condition, entityToTest) -> {
                BinaryTreeNode<ChemicalEntity> binaryTreeNode = ((ComplexEntity) entityToTest).find(condition.getEntity());
                if (binaryTreeNode != null) {
                    ModificationSite bindingSite = ((ModificationSite) binaryTreeNode.getData());
                    return !bindingSite.isOccupied();
                }
                return false;
            }),

    IS_OCCUPIED("has occupied %s",
            (condition, entityToTest) -> {
                BinaryTreeNode<ChemicalEntity> binaryTreeNode = ((ComplexEntity) entityToTest).find(condition.getEntity());
                if (binaryTreeNode != null) {
                    ModificationSite bindingSite = ((ModificationSite) binaryTreeNode.getData());
                    return bindingSite.isOccupied();
                }
                return false;
            }),

    SOLITARY_BINDING("has no occupied sites",
            (condition, entityToTest) -> {
                for (ModificationSite site : ((ComplexEntity) entityToTest).getSites()) {
                    if (site.isOccupied()) {
                        return false;
                    }
                }
                return true;
            }),

    IS_SMALL_MOLECULE("is small molecule",
            (condition, entityToTest) -> entityToTest instanceof SmallMolecule);


    private final String descriptor;
    private final BiPredicate<ReactantCondition, ChemicalEntity> predicate;

    CompositionPredicate(String descriptor, BiPredicate<ReactantCondition, ChemicalEntity> predicate) {
        this.descriptor = descriptor;
        this.predicate = predicate;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public BiPredicate<ReactantCondition, ChemicalEntity> getPredicate() {
        return predicate;
    }

}
