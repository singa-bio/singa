package de.bioforscher.singa.chemistry.physical.interfaces;

import de.bioforscher.singa.chemistry.physical.model.LeafIdentifier;

/**
 * @author cl
 */
public interface LeafSubstructure<LeafSubstructureType extends LeafSubstructure> extends AtomContainer {

    LeafIdentifier getIdentifier();

    String getThreeLetterCode();

    LeafSubstructureType getCopy();

    default String flatToString() {
            return getClass().getSimpleName()+" ("+getThreeLetterCode()+") "+getIdentifier();
    }

}
