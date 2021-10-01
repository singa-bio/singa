package bio.singa.structure.model.cif;

import bio.singa.chemistry.model.elements.Element;
import bio.singa.mathematics.vectors.Vector3D;
import bio.singa.structure.model.interfaces.Atom;

public class CifAtom implements Atom {

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
    public double getBFactor() {
        return 0;
    }

    @Override
    public void setBFactor(double bFactor) {

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
