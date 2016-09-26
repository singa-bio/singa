package de.bioforscher.chemistry.physical;

import de.bioforscher.mathematics.graphs.model.AbstractEdge;

public class Bond extends AbstractEdge<Atom> {

    private BondType bondType;

    public Bond(BondType bondType) {
        this.bondType = bondType;
    }

    public Bond() {
        this.bondType = BondType.COVALENT_BOND;
    }


}
