package de.bioforscher.chemistry.physical.proteins;

import de.bioforscher.chemistry.physical.model.SubStructure;
import de.bioforscher.chemistry.physical.bonds.Bond;
import de.bioforscher.core.utility.Nameable;

/**
 * The chain is one of the grouping elements that should contain primarily residues and are connected to form a single
 * molecule. This model is adopted from the classical PDB structure files. Since this also implements the nameable
 * interface, the name of a chin is its chain identifier (a single letter).
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

    /**
     * Returns the chain identifier (the single letter identifier).
     * @return The chain identifier.
     */
    public char getChainIdentifier() {
        return this.chainIdentifier;
    }

    /**
     * Sets the chain identifier (the single letter identifier).
     * @param chainIdentifier The chain identifier.
     */
    public void setChainIdentifier(char chainIdentifier) {
        this.chainIdentifier = chainIdentifier;
    }

    /**
     * Connects the all residues, that are currently in the chain, in order of their appearance in the
     * List of Residues ({@link SubStructure#getResidues()}).
     */
    public void connectChainBackbone() {
        Residue lastResidue = null;
        for (Residue currentResidue : getResidues()) {
            if (lastResidue != null) {
                connectPeptideBonds(lastResidue, currentResidue);
            }
            lastResidue = currentResidue;
        }
    }

    /**
     * Connects two residues, using the Backbone Carbon ({@link de.bioforscher.chemistry.physical.atoms.AtomName#C C})
     * from the source residue and the Backbone Nitrogen ({@link de.bioforscher.chemistry.physical.atoms.AtomName#N N})
     * from the target residue.
     * @param source Residue with Backbone Carbon.
     * @param target Residue with Backbone Nitrogen.
     */
    public void connectPeptideBonds(Residue source, Residue target) {
        // creates the peptide backbone
        Bond bond = new Bond();
        bond.setIdentifier(nextEdgeIdentifier());
        bond.setSource(source.getBackboneCarbon());
        bond.setTarget(target.getBackboneNitrogen());
        addEdge(bond.getIdentifier(), bond);
        source.addNeighbour(target);
        target.addNeighbour(source);
    }

    /**
     * Gets the name (i.e. the single letter chain identifier) of this chain.
     * @return The name.
     */
    @Override
    public String getName() {
        return String.valueOf(this.chainIdentifier);
    }


}
