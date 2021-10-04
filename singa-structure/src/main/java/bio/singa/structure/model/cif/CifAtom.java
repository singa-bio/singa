package bio.singa.structure.model.cif;

import bio.singa.chemistry.model.elements.Element;
import bio.singa.mathematics.vectors.Vector3D;
import bio.singa.structure.model.interfaces.Atom;

public class CifAtom implements Atom {

    private int atomIdentifier;
    private String atomName;
    private Vector3D position;
    private double bFactor;
    private Element element;

    public CifAtom(CifAtom cifAtom) {
        atomIdentifier = cifAtom.atomIdentifier;
        atomName = cifAtom.atomName;
        position = cifAtom.position;
        bFactor = cifAtom.bFactor;
        element = cifAtom.element;
    }

    @Override
    public int getAtomIdentifier() {
        return atomIdentifier;
    }

    public void setAtomIdentifier(int atomIdentifier) {
        this.atomIdentifier = atomIdentifier;
    }

    @Override
    public String getAtomName() {
        return atomName;
    }

    public void setAtomName(String atomName) {
        this.atomName = atomName;
    }

    @Override
    public Vector3D getPosition() {
        return position;
    }

    @Override
    public void setPosition(Vector3D position) {
        this.position = position;
    }

    @Override
    public double getBFactor() {
        return bFactor;
    }

    @Override
    public void setBFactor(double bFactor) {
        this.bFactor = bFactor;
    }

    @Override
    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    @Override
    public Atom getCopy() {
        return new CifAtom(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CifAtom cifAtom = (CifAtom) o;

        return atomIdentifier == cifAtom.atomIdentifier;
    }

    @Override
    public int hashCode() {
        return atomIdentifier;
    }

}
