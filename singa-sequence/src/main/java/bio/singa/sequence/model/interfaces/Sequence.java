package bio.singa.sequence.model.interfaces;

import bio.singa.structure.model.families.StructuralFamily;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fk
 */
public interface Sequence {

    String getSequence();

    default int getLength() {
        return getSequence().length();
    }

    default char getLetter(int position) {
        return getSequence().charAt(position);
    }

}
