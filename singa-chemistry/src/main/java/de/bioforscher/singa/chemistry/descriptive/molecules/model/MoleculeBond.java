package de.bioforscher.singa.chemistry.descriptive.molecules.model;

import de.bioforscher.singa.mathematics.graphs.model.AbstractEdge;

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

    public MoleculeBondType getType() {
        return type;
    }

    public void setType(MoleculeBondType type) {
        this.type = type;
    }


}
