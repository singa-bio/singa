package bio.singa.structure.algorithms.interaction;

import bio.singa.mathematics.vectors.Vector3D;
import bio.singa.structure.elements.Element;
import bio.singa.structure.model.interfaces.Atom;
import bio.singa.structure.parser.plip.Interaction;

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
