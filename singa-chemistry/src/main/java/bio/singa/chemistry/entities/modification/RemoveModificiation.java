package bio.singa.chemistry.entities.modification;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.ComplexEntity;

/**
 * @author cl
 */
public class RemoveModificiation extends AbstractModification {

    public RemoveModificiation(ChemicalEntity modificator) {
        super(modificator, "splitOfComplex");
    }

    public RemoveModificiation(ChemicalEntity modificator, ChemicalEntity modificationPosition) {
        super(modificator, modificationPosition, "splitOfComplex");
    }

    @Override
    public ComplexEntity apply(ComplexEntity target) {
        // copy original target
        ComplexEntity modifiedEntity = target.copy();
        if (getModificationPosition() != null) {
            modifiedEntity.removeFromPosition(getModificator(), getModificationPosition());
        } else {
            // modifiedEntity.splitOfComplex(getModificator());
        }
        return modifiedEntity;
    }

    @Override
    public String toString() {
        if (getModificationPosition() == null) {
            return getOperation() + " " + getModificator();
        }
        return getOperation() + " " + getModificator() + " from " + getModificationPosition();
    }
}
