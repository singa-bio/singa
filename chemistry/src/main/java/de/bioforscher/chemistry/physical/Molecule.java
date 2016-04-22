package de.bioforscher.chemistry.physical;

import de.bioforscher.mathematics.graphs.model.AbstractGraph;

public class Molecule extends AbstractGraph<Atom, Bond> {

    public void connect(int identifier, Atom source, Atom target, BondType bondType) {
        Bond bond = new Bond(identifier, source, target, bondType);
        connectWithEdge(identifier, source, target, bond);
    }

}
