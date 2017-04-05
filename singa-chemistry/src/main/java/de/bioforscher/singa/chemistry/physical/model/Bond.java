package de.bioforscher.singa.chemistry.physical.model;

import de.bioforscher.singa.chemistry.physical.atoms.Atom;
import de.bioforscher.singa.mathematics.graphs.model.AbstractEdge;

public class Bond extends AbstractEdge<Atom> {

    private BondType bondType;

    public Bond() {
        this(BondType.SINGLE_BOND);
    }

    public Bond(int identifier) {
        super(identifier);
    }

    public Bond(int identifier, BondType bondType) {
        super(identifier);
        this.bondType = bondType;
    }

    public Bond(BondType bondType) {
        this.bondType = bondType;
    }

    /**
     * This is a copy constructor. Creates a new bond with the same attributes as the given bond. The source and target
     * atoms of this bond are NOT copied. Due to the nature of this operation it would be bad to keep a part of
     * the relations to the lifecycle of the bond to copy. If you want to keep the neighbouring atoms, copy the
     * superordinate substructure that contains this bond and it will also traverse and copy the connected atoms.
     *
     * @param bond The bond to copy.
     */
    public Bond(Bond bond) {
        this.identifier = bond.getIdentifier();
        this.bondType = bond.getBondType();
    }

    public BondType getBondType() {
        return this.bondType;
    }

    public void setBondType(BondType bondType) {
        this.bondType = bondType;
    }

    public Bond getCopy() {
        return new Bond(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bond bond = (Bond) o;
        if (this.identifier != bond.getIdentifier()) return false;
        return this.bondType == bond.bondType;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.bondType != null ? this.bondType.hashCode() : 0);
        return result;
    }
}
