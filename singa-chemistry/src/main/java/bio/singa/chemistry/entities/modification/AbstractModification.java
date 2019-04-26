package bio.singa.chemistry.entities.modification;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.ComplexEntity;

/**
 * @author cl
 */
public abstract class AbstractModification {

    private final String operation;
    private ChemicalEntity modificator;
    private ChemicalEntity modificationPosition;

    public AbstractModification(ChemicalEntity modificator, String operation) {
        this.modificator = modificator;
        this.operation = operation;
    }

    public AbstractModification(ChemicalEntity modificator, ChemicalEntity modificationPosition, String operation) {
        this.modificator = modificator;
        this.modificationPosition = modificationPosition;
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

    public String getOperation() {
        return operation;
    }

    public abstract ComplexEntity apply(ComplexEntity target);

}
