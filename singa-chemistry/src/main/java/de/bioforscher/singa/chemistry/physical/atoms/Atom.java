package de.bioforscher.singa.chemistry.physical.atoms;

import de.bioforscher.singa.chemistry.descriptive.elements.Element;
import de.bioforscher.singa.chemistry.physical.model.StructuralEntity;
import de.bioforscher.singa.mathematics.vectors.Vector3D;

import java.lang.reflect.InvocationTargetException;

/**
 * @author cl
 */
public interface Atom extends StructuralEntity<Atom> {

    Element getElement();

    void setElement(Element element);

    // getIdentifier from StructuralEntity

    void setIdentifier(int identifier);

    // getPosition from StructuralEntity

    void setPosition(Vector3D position);

    String getAtomNameString();

    void setAtomNameString(String atomNameString);

    default boolean isHydrogen() {
        return this.getElement().getProtonNumber() == 1;
    }

    default Atom getCopy() {
        try {
            return getClass().getConstructor(getClass()).newInstance(this);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
            throw new UnsupportedOperationException("Instance types must match to copy successfully.");
        }
    }
}
