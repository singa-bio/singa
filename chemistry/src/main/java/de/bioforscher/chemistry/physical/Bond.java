package de.bioforscher.chemistry.physical;

import de.bioforscher.mathematics.graphs.model.AbstractEdge;

public class Bond extends AbstractEdge<Atom> {

    private BondType type;

    public Bond(int identifier, Atom source, Atom target, BondType type) {
        super(identifier, source, target);
        this.type = type;
    }

    public Bond(Atom source, Atom target, BondType type) {
        super(source, target);
        this.type = type;
    }

    public BondType getType() {
        return this.type;
    }

    public void setType(BondType type) {
        this.type = type;
    }

}
