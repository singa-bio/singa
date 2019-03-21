package bio.singa.structure.model.molecules;

import bio.singa.mathematics.graphs.model.AbstractEdge;

import java.util.Objects;

/**
 * @author cl
 */
public class MoleculeBond extends AbstractEdge<MoleculeAtom> {

    private MoleculeBondType type;

    public MoleculeBond(int identifier) {
        super(identifier);
    }

    public MoleculeBond(int identifier, char smilesSymbol) {
        super(identifier);
        type = MoleculeBondType.getBondForSMILESSymbol(smilesSymbol);
    }

    public MoleculeBond(int identifier, MoleculeAtom source, MoleculeAtom target, MoleculeBondType type) {
        super(identifier, source, target);
        this.type = type;
    }

    public MoleculeBond(MoleculeBond moleculeBond) {
        super(moleculeBond);
        type = moleculeBond.type;
    }

    public MoleculeBondType getType() {
        return type;
    }

    public void setType(MoleculeBondType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MoleculeBond)) return false;
        if (!super.equals(o)) return false;
        MoleculeBond that = (MoleculeBond) o;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), type);
    }

    @Override
    public MoleculeBond getCopy() {
        return new MoleculeBond(this);
    }
}
