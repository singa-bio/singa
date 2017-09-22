package de.bioforscher.singa.chemistry.physical.interfaces;

import de.bioforscher.singa.chemistry.physical.model.LeafIdentifier;

import java.util.List;

/**
 * @author cl
 */
public interface LeafSubstructure<LeafSubstructureType extends LeafSubstructure> {

    LeafIdentifier getIdentifier();

    String getThreeLetterCode();

    List<Atom> getAllAtoms();

    LeafSubstructureType getCopy();

    default String flatToString() {
            return getClass().getSimpleName()+" ("+getThreeLetterCode()+") "+getIdentifier();
    }

}
