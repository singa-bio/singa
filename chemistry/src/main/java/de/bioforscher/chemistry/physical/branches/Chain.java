package de.bioforscher.chemistry.physical.branches;

import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;
import de.bioforscher.chemistry.physical.leafes.Nucleotide;
import de.bioforscher.chemistry.physical.leafes.Residue;
import de.bioforscher.chemistry.physical.model.Bond;
import de.bioforscher.chemistry.physical.model.Substructure;
import de.bioforscher.core.utility.Nameable;

import java.util.Map;
import java.util.TreeMap;

import static de.bioforscher.chemistry.physical.atoms.AtomName.O3Pr;
import static de.bioforscher.chemistry.physical.atoms.AtomName.OP3;
import static de.bioforscher.chemistry.physical.atoms.AtomName.P;

/**
 * The chain is one of the grouping elements that should contain primarily residues and are connected to form a single
 * molecule. This model is adopted from the classical PDB structure files. Since this also implements the nameable
 * interface, the name of a chin is its chain identifier (a single letter).
 *
 * @author cl
 *
 * @see Residue
 */
public class Chain extends BranchSubstructure<Chain> implements Nameable {

    /**
     * The identifier of this chain.
     */
    private String chainIdentifier;

    /**
     * Creates a new Chain with the given graph identifier. This is not the single letter chain identifier, but the
     * reference for the placement in the graph.
     *
     * @param graphIdentifier The identifier in the graph.
     */
    public Chain(int graphIdentifier) {
        super(graphIdentifier);
    }

    public Chain(Chain chain) {
        this(chain.getIdentifier());
        this.chainIdentifier = chain.chainIdentifier;
        for (Substructure<?> structure : chain.getSubstructures()) {
            this.addSubstructure(structure.getCopy());
        }
        Map<Integer, Atom> atoms = new TreeMap<>();
        for (Atom atom : this.getAllAtoms()) {
            atoms.put(atom.getIdentifier(), atom);
        }
        for (Bond bond : chain.getEdges()) {
            Bond edgeCopy = bond.getCopy();
            Atom sourceCopy = atoms.get(bond.getSource().getIdentifier());
            Atom targetCopy = atoms.get(bond.getTarget().getIdentifier());
            addEdgeBetween(edgeCopy, sourceCopy, targetCopy);
        }
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
    public String getChainIdentifier() {
        return this.chainIdentifier;
    }

    /**
     * Sets the chain identifier (the single letter identifier).
     * @param chainIdentifier The chain identifier.
     */
    public void setChainIdentifier(String chainIdentifier) {
        this.chainIdentifier = chainIdentifier;
    }

    /**
     * Connects the all residues, that are currently in the chain, in order of their appearance in the
     * List of Residues ({@link BranchSubstructure#getResidues()}).
     */
    public void connectChainBackbone() {
        LeafSubstructure<?,?> lastSubstructure = null;
        for (LeafSubstructure<?,?> currentSubstructure: getLeafSubstructures()) {
            if (lastSubstructure != null) {
                if (lastSubstructure instanceof Residue && currentSubstructure instanceof Residue) {
                    connectPeptideBonds((Residue) lastSubstructure, (Residue) currentSubstructure);
                } else if (lastSubstructure instanceof Nucleotide && currentSubstructure instanceof Nucleotide) {
                    connectNucleotideBonds((Nucleotide) lastSubstructure, (Nucleotide) currentSubstructure);
                }
            }
            lastSubstructure = currentSubstructure;
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
        Bond bond = new Bond(nextEdgeIdentifier());
        addEdgeBetween(bond, source.getBackboneCarbon(), target.getBackboneNitrogen());
    }

    public void connectNucleotideBonds(Nucleotide source, Nucleotide target) {
        Bond bond = new Bond(nextEdgeIdentifier());
        addEdgeBetween(bond, source.getAtomByName(O3Pr), target.getAtomByName(P));
    }

    /**
     * Gets the name (i.e. the single letter chain identifier) of this chain.
     * @return The name.
     */
    @Override
    public String getName() {
        return String.valueOf(this.chainIdentifier);
    }

    @Override
    public Chain getCopy() {
        return new Chain(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Chain chain = (Chain) o;

        return this.chainIdentifier != null ? this.chainIdentifier.equals(chain.chainIdentifier) : chain.chainIdentifier == null;
    }

    @Override
    public int hashCode() {
        return this.chainIdentifier != null ? this.chainIdentifier.hashCode() : 0;
    }

}
