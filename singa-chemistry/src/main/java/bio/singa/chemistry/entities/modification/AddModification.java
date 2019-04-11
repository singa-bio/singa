package bio.singa.chemistry.entities.modification;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.ComplexEntity;

/**
 * @author cl
 */
public class AddModification extends AbstractModification {

    public AddModification(ChemicalEntity modificator, ChemicalEntity modificationPosition) {
        super(modificator, modificationPosition, "add");
    }

    @Override
    public ComplexEntity apply(ComplexEntity target) {
        // copy original target
        ComplexEntity resultingEntity = target.copy();
        if (getModificationPosition() != null) {
            // create replacement complex of modification position and modificator
            ComplexEntity replacement = ComplexEntity.from(getModificationPosition(), getModificator());
            // replace modifiaction position with replacement
            resultingEntity.replace(replacement, getModificationPosition());
        } else {
            throw new IllegalStateException("The requested modification " + this + " was impossible");
        }
        // return new resulting entity
        return resultingEntity;
    }

    @Override
    public String toString() {
        return getOperation() + " " + getModificator() + " at " + getModificationPosition();
    }

}
