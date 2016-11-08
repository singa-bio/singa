package de.bioforscher.chemistry.physical.proteins;

import de.bioforscher.chemistry.physical.model.SubStructure;
import de.bioforscher.chemistry.physical.bonds.Bond;
import de.bioforscher.core.utility.Nameable;

/**
 * The chain is one of the grouping elements that should contain primarily residues and are connected to form a single
 * molecule. This model is adopted from the classical PDB structure files. Since this also implements the nameable
 * interface, the name of a chin is its chain identifier (a single letter code).
 *
 * @author cl
 *
 * @see Residue
 */
public class Chain extends SubStructure implements Nameable {

    /**
     * The identifier of this chain.
     */
    private char chainIdentifier;

    /**
     * Creates a new Chain with the given graph identifier. This is not the single letter chain identifier, but the
     * reference for the placement in the graph.
     *
     * @param graphIdentifier The identifier in the graph.
     */
    public Chain(int graphIdentifier) {
        super(graphIdentifier);
    }

    /**
     * Creates a new Chain with the graph identifier 0. Use this method only if there is only one chain and nothing
     * more on this level in a structure.
     */
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
