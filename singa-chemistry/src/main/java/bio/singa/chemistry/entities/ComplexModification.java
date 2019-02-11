package bio.singa.chemistry.entities;

import bio.singa.mathematics.graphs.trees.BinaryTreeNode;

import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

/**
 * @author cl
 */
public class ComplexModification {

    public enum Operation {
        ADD(" to "),
        REMOVE(" from "),
        REPLACE(" with ");

        private String text;

        Operation(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    private Operation operation;
    private ChemicalEntity modificator;
    private ChemicalEntity modificationPosition;

    public static ComplexEntity apply(ComplexEntity entity, ComplexModification modification) {
        switch (modification.getOperation()) {
            case ADD: {
                ComplexEntity replacement = ComplexEntity.from(modification.getModificationPosition(), modification.getModificator());
                ComplexEntity modifiedEntity = entity.copy();
                List<BinaryTreeNode<ChemicalEntity>> path = modifiedEntity.pathTo(modification.getModificationPosition());
                ListIterator<BinaryTreeNode<ChemicalEntity>> iterator = path.listIterator(path.size());
                while (iterator.hasPrevious()) {
                    BinaryTreeNode<ChemicalEntity> current = iterator.previous();
                    if (current.getData().equals(modification.getModificationPosition())) {
                        continue;
                    }
                    if (current.getRight().getData().equals(modification.getModificationPosition())) {
                        current.setRight(replacement);
                    }
                    if (current.getLeft().getData().equals(modification.getModificationPosition())) {
                        current.setLeft(replacement);
                    }
                    ((ComplexEntity) current).setIdentifier(current.toNewickString(t -> t.getIdentifier().getContent()));
                }
                return modifiedEntity;
            }
            case REMOVE: {
                ComplexEntity modifiedEntity = entity.copy();
                List<BinaryTreeNode<ChemicalEntity>> path = modifiedEntity.pathTo(modification.getModificationPosition());
                BinaryTreeNode<ChemicalEntity> node = path.get(path.size() - 1);
                ChemicalEntity retainedEntity = null;
                if (node.hasLeft()) {
                    if (node.getLeft().getData().equals(modification.getModificator())) {
                        retainedEntity = node.getRight().getData();
                    }
                    if (node.getRight().getData().equals(modification.getModificator())) {
                        retainedEntity = node.getLeft().getData();
                    }
                }
                Objects.requireNonNull(retainedEntity);
                modifiedEntity.substitute(modification.getModificationPosition(), retainedEntity);
                for (BinaryTreeNode<ChemicalEntity> current : path) {
                    ((ComplexEntity) current).setIdentifier(current.toNewickString(t -> t.getIdentifier().getContent()));
                }
                return modifiedEntity;
            }
            case REPLACE:
                ComplexEntity modifiedEntity = entity.copy();
                List<BinaryTreeNode<ChemicalEntity>> path = modifiedEntity.pathTo(modification.getModificationPosition());
                modifiedEntity.substitute(modification.getModificationPosition(), modification.getModificator());
                for (BinaryTreeNode<ChemicalEntity> current : path) {
                    if (current instanceof ComplexEntity) {
                        ((ComplexEntity) current).setIdentifier(current.toNewickString(t -> t.getIdentifier().getContent()));
                    }
                }
                return modifiedEntity;
        }
        throw new IllegalStateException("The requested modification was impossible");
    }

    public ComplexModification(Operation operation, ChemicalEntity modificator, ChemicalEntity modificationPosition) {
        this.operation = operation;
        this.modificator = modificator;
        this.modificationPosition = modificationPosition;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public ChemicalEntity getModificator() {
        return modificator;
    }

    public void setModificator(ChemicalEntity modificator) {
        this.modificator = modificator;
    }

    public ChemicalEntity getModificationPosition() {
        return modificationPosition;
    }

    public void setModificationPosition(ChemicalEntity modificationPosition) {
        this.modificationPosition = modificationPosition;
    }

    @Override
    public String toString() {
        if (operation.equals(Operation.REPLACE)) {
            return operation + " " + modificationPosition + operation.getText() + modificator;
        }
        return operation + " " + modificator + operation.getText() + modificationPosition;
    }
}
