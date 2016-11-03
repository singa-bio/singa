package de.bioforscher.chemistry.physical;

import de.bioforscher.core.utility.Nameable;

/**
 * Created by Christoph on 30/10/2016.
 */
public class Chain extends SubStructure implements Nameable {

    private char chainIdentifier;

    public Chain(int identifier) {
        super(identifier);
    }

    public Chain() {
        super(0);
    }

    public char getChainIdentifier() {
        return this.chainIdentifier;
    }

    public void setChainIdentifier(char chainIdentifier) {
        this.chainIdentifier = chainIdentifier;
    }

    public void connectChainBackbone() {
        Residue lastResidue = null;
        for (Residue currentResidue : getResidues()) {
            if (lastResidue != null) {
                connectPeptideBonds(lastResidue, currentResidue);
            }
            lastResidue = currentResidue;
        }
    }

    public void connectPeptideBonds(Residue source, Residue target) {
        // creates the peptide backbone
        Bond bond = new Bond();
        bond.setIdentifier(nextEdgeIdentifier());
        bond.setSource(source.getBackboneCarbon().orElseThrow(IllegalStateException::new));
        bond.setTarget(target.getBackboneNitrogen().orElseThrow(IllegalArgumentException::new));
        addEdge(bond.getIdentifier(), bond);
        source.addNeighbour(target);
        target.addNeighbour(source);
    }

    @Override
    public String getName() {
        return String.valueOf(this.chainIdentifier);
    }


}
