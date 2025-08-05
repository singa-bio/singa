package bio.singa.structure.model.cif;

import bio.singa.chemistry.model.CovalentBondType;
import bio.singa.structure.model.interfaces.Bond;

public class CifBond implements Bond<CifAtom> {

    private final int identifier;

    protected CifAtom source;

    protected CifAtom target;

    private CovalentBondType bondType;

    public CifBond(int identifier) {
        this.identifier = identifier;
    }

    public CifBond(int identifier, CovalentBondType bondType) {
        this(identifier);
        this.bondType = bondType;
    }

    /**
     * This is a copy constructor. Creates a new bond with the same attributes as the given bond. The source and target
     * atoms of this bond are NOT copied. Due to the nature of this operation it would be bad to keep a part of the
     * relations to the lifecycle of the bond to copy. If you want to keep the neighbouring atoms, copy the
     * superordinate substructure that contains this bond and it will also traverse and copy the connected atoms.
     *
     * @param bond The bond to copy.
     */
    public CifBond(CifBond bond) {
        identifier = bond.identifier;
        bondType = bond.bondType;
    }

    @Override
    public int getIdentifier() {
        return identifier;
    }

    @Override
    public CovalentBondType getBondType() {
        return bondType;
    }

    @Override
    public void setBondType(CovalentBondType bondType) {
        this.bondType = bondType;
    }

    @Override
    public CifAtom getSource() {
        return source;
    }

    @Override
    public void setSource(CifAtom source) {
        this.source = source;
    }

    @Override
    public CifAtom getTarget() {
        return target;
    }

    @Override
    public void setTarget(CifAtom target) {
        this.target = target;
    }

    @Override
    public boolean connectsAtom(CifAtom atom) {
        return source.equals(atom) || target.equals(atom);
    }

    @Override
    public CifBond getCopy() {
        return new CifBond(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CifBond bond = (CifBond) o;
        if (identifier != bond.getIdentifier()) return false;
        return bondType == bond.bondType;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (bondType != null ? bondType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return identifier+": "+source.getAtomName()+"-"+source.getAtomIdentifier()+" -- "+target.getAtomName()+"-"+target.getAtomIdentifier();
    }
}
