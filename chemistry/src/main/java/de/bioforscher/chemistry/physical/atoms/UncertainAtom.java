package de.bioforscher.chemistry.physical.atoms;

import de.bioforscher.chemistry.descriptive.elements.Element;
import de.bioforscher.mathematics.vectors.Vector3D;

/**
 * Created by Christoph on 09/11/2016.
 */
public class UncertainAtom extends RegularAtom {

    /**
     * Creates a new atom with the given identifier, element, name and position.
     *
     * @param identifier The identifier.
     * @param element The element.
     * @param atomNameString The name.
     * @param position The position.
     */
    public UncertainAtom(int identifier, Element element, String atomNameString, Vector3D position) {
        super(identifier, element, atomNameString, position);
    }
}
