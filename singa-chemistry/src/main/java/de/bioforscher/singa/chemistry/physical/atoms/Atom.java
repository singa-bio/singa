package de.bioforscher.singa.chemistry.physical.atoms;

import de.bioforscher.singa.chemistry.descriptive.elements.Element;
import de.bioforscher.singa.chemistry.physical.model.StructuralEntity;
import de.bioforscher.singa.mathematics.vectors.Vector3D;

/**
 * @author cl
 */
public interface Atom extends StructuralEntity<Atom, Integer> {

    Element getElement();

    void setElement(Element element);

    void setIdentifier(int identifier);

    void setPosition(Vector3D position);

    String getAtomNameString();

    void setAtomNameString(String atomNameString);

    default boolean isHydrogen() {
        return this.getElement().getProtonNumber() == 1;
    }

}
