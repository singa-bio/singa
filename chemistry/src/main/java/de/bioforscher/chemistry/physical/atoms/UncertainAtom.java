package de.bioforscher.chemistry.physical.atoms;

import de.bioforscher.chemistry.descriptive.elements.Element;
import de.bioforscher.mathematics.vectors.Vector3D;

/**
 * @author cl
 */
public class UncertainAtom extends RegularAtom {

    /**
     * Creates a new atom with the given pdbIdentifier, element, name and position.
     *
     * @param identifier The pdbIdentifier.
     * @param element The element.
     * @param atomNameString The name.
     * @param position The position.
     */
    public UncertainAtom(int identifier, Element element, String atomNameString, Vector3D position) {
        super(identifier, element, atomNameString, position);
    }

}
