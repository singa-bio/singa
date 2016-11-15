package de.bioforscher.chemistry.physical.bonds;

import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.mathematics.graphs.model.AbstractEdge;

public class Bond extends AbstractEdge<Atom> {

    private BondType bondType;

    public Bond(BondType bondType) {
        this.bondType = bondType;
    }

    public Bond() {
        this.bondType = BondType.COVALENT_BOND;
    }

    public Bond getCopy(){
        Bond copy = new Bond(bondType);
        copy.setSource(source.getCopy());
        copy.setTarget(source.getCopy());
    }
}
