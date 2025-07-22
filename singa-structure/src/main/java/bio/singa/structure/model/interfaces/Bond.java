package bio.singa.structure.model.interfaces;

import bio.singa.chemistry.model.CovalentBondType;

public interface Bond<A extends Atom> {
    int getIdentifier();

    CovalentBondType getBondType();

    void setBondType(CovalentBondType bondType);

    A getSource();

    void setSource(A source);

    A getTarget();

    void setTarget(A target);

    boolean connectsAtom(A atom);

    Bond<A> getCopy();
}
