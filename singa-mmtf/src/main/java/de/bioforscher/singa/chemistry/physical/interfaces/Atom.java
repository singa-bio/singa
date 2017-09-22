package de.bioforscher.singa.chemistry.physical.interfaces;

import de.bioforscher.singa.chemistry.descriptive.elements.Element;
import de.bioforscher.singa.mathematics.vectors.Vector3D;

/**
 * The identifier of the atom is not unique. Depending on the structure format you are reading there might be different
 * atom identifiers.
 *
 * @author cl
 */
public interface Atom {

    int getIdentifier();

    Vector3D getPosition();

    Element getElement();

    String getAtomName();

    default String flatToString() {
        return getIdentifier() + ": " + getAtomName() + " at " + getPosition();
    }

}
