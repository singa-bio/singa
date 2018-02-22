package de.bioforscher.singa.sequence.model;

import de.bioforscher.singa.sequence.model.interfaces.Sequence;
import de.bioforscher.singa.structure.model.families.StructuralFamily;

import java.util.List;

/**
 * @author fk
 */
public class AbstractSequence<FamilyType extends StructuralFamily> implements Sequence<FamilyType> {

    private List<FamilyType> sequence;

    public AbstractSequence(List<FamilyType> sequence) {
        this.sequence = sequence;
    }

    @Override
    public List<FamilyType> getSequence() {
        return sequence;
    }
}
