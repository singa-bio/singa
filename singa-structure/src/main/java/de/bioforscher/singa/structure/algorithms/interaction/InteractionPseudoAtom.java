package de.bioforscher.singa.structure.algorithms.interaction;

import de.bioforscher.singa.mathematics.vectors.Vector3D;
import de.bioforscher.singa.structure.elements.Element;
import de.bioforscher.singa.structure.model.interfaces.Atom;
import de.bioforscher.singa.structure.parser.plip.Interaction;

/**
 * @author fk
 */
public class InteractionPseudoAtom implements Atom {

    // TODO implement here
    public static InteractionPseudoAtom of(Interaction interaction) {
        return null;
    }

    @Override
    public Integer getAtomIdentifier() {
        return null;
    }

    @Override
    public Vector3D getPosition() {
        return null;
    }

    @Override
    public void setPosition(Vector3D position) {

    }

    @Override
    public Element getElement() {
        return null;
    }

    @Override
    public String getAtomName() {
        return null;
    }

    @Override
    public Atom getCopy() {
        return null;
    }
}
