package bio.singa.chemistry.entities.modification;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.ComplexEntity;

/**
 * @author cl
 */
public class ReplaceModification extends AbstractModification {

    public ReplaceModification(ChemicalEntity modificator, ChemicalEntity modificationPosition) {
        super(modificator, modificationPosition, "replace");
    }

    @Override
    public ComplexEntity apply(ComplexEntity target) {
        // copy original target
        ComplexEntity resultingEntity = target.copy();
        if (getModificationPosition() != null) {
            // replace modifiaction position with replacement
            resultingEntity.replace(getModificator(), getModificationPosition());
        } else {
            throw new IllegalStateException("The requested modification " + this + " was impossible");
        }
        // return new resulting entity
        return resultingEntity;
    }

    @Override
    public String toString() {
        return  getOperation() +" "+ getModificationPosition() + " with " + getModificator();
    }
}
