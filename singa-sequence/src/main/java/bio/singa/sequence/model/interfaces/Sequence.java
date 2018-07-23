package bio.singa.sequence.model.interfaces;

import bio.singa.structure.model.families.StructuralFamily;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fk
 */
public interface Sequence<FamilyType extends StructuralFamily> {

    List<FamilyType> getSequence();

    default int getLength() {
        return getSequence().size();
    }

    default FamilyType getLetter(int position) {
        return getSequence().get(position);
    }

    default String getSequenceAsString() {
        return getSequence().stream()
                .map(StructuralFamily::getOneLetterCode)
                .collect(Collectors.joining(""));
    }
}
