package bio.singa.chemistry.entities.modification;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.ComplexEntity;

/**
 * @author cl
 */
public class RemoveModificiation extends AbstractModification {

    public RemoveModificiation(ChemicalEntity modificator) {
        super(modificator, "remove");
    }

    public RemoveModificiation(ChemicalEntity modificator, ChemicalEntity modificationPosition) {
        super(modificator, modificationPosition, "remove");
    }

    @Override
    public ComplexEntity apply(ComplexEntity target) {
        // copy original target
        ComplexEntity modifiedEntity = target.copy();
        if (getModificationPosition() != null) {
            modifiedEntity.remove(getModificator(), getModificationPosition());
        } else {
            modifiedEntity.remove(getModificator());
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
