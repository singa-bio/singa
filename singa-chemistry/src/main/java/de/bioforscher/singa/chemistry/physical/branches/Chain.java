package de.bioforscher.singa.chemistry.physical.branches;

import de.bioforscher.singa.chemistry.physical.atoms.Atom;
import de.bioforscher.singa.chemistry.physical.atoms.AtomName;
import de.bioforscher.singa.chemistry.physical.leaves.AminoAcid;
import de.bioforscher.singa.chemistry.physical.leaves.LeafSubstructure;
import de.bioforscher.singa.chemistry.physical.leaves.Nucleotide;
import de.bioforscher.singa.chemistry.physical.model.Bond;
import de.bioforscher.singa.chemistry.physical.model.Substructure;
import de.bioforscher.singa.core.utility.Nameable;
import de.bioforscher.singa.mathematics.vectors.Vector3D;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The chainIdentifier is one of the grouping elements that should contain primarily residues and are connected to form a single
 * molecule. This model is adopted from the classical PDB structure files. Since this also implements the nameable
 * interface, the name of a chin is its chainIdentifier pdbIdentifier (a single letter).
 *
 * @author cl
 * @see AminoAcid
 */
public class Chain extends BranchSubstructure<Chain> implements Nameable {

    private Set<Integer> consecutiveIdentifier;

    /**
     * The chainIdentifier identifier of this chainIdentifier.
     */
    private String chainIdentifier;

    /**
     * Creates a new Chain with the given graph pdbIdentifier. This is not the single character chainIdentifier identifier, but the
     * reference for the placement in the graph.
     *
     * @param graphIdentifier The identifier in the graph.
     */
    public Chain(int graphIdentifier) {
        super(graphIdentifier);
        this.consecutiveIdentifier = new HashSet<>();
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
     * Creates a new Chain with the graph identifier 0. <b>Use this method only if there is only one chainIdentifier and nothing
     * more on this level in a structure.</b>
     */
    public Chain() {
        super(0);
    }

    /**
     * Returns the chainIdentifier identifier (the single character identifier).
     *
     * @return The chainIdentifier identifier.
     */
    public String getChainIdentifier() {
        return this.chainIdentifier;
    }

    /**
     * Sets the chainIdentifier identifier (the single letter identifier).
     *
     * @param chainIdentifier The chainIdentifier identifier.
     */
    public void setChainIdentifier(String chainIdentifier) {
        this.chainIdentifier = chainIdentifier;
    }

    /**
     * Connects the all residues, that are currently in the chainIdentifier, in order of their appearance in the
     * List of AminoAcids ({@link BranchSubstructure#getAminoAcids()}).
     */
    public void connectChainBackbone() {
        LeafSubstructure<?, ?> lastSubstructure = null;
        for (LeafSubstructure<?, ?> currentSubstructure : getLeafSubstructures()) {
            if (lastSubstructure != null) {
                if (lastSubstructure instanceof AminoAcid && currentSubstructure instanceof AminoAcid) {
                    connectPeptideBonds((AminoAcid) lastSubstructure, (AminoAcid) currentSubstructure);
                } else if (lastSubstructure instanceof Nucleotide && currentSubstructure instanceof Nucleotide) {
                    connectNucleotideBonds((Nucleotide) lastSubstructure, (Nucleotide) currentSubstructure);
                }
            }
            lastSubstructure = currentSubstructure;
        }
    }

    /**
     * Connects two residues, using the Backbone Carbon ({@link AtomName#C C})
     * of the source residue and the Backbone Nitrogen ({@link AtomName#N N})
     * of the target residue.
     *
     * @param source AminoAcid with Backbone Carbon.
     * @param target AminoAcid with Backbone Nitrogen.
     */
    public void connectPeptideBonds(AminoAcid source, AminoAcid target) {
        // creates the peptide backbone
        Bond bond = new Bond(nextEdgeIdentifier());
        if (source.containsAtomWithName(AtomName.C) && target.containsAtomWithName(AtomName.N)) {
            addEdgeBetween(bond, source.getBackboneCarbon(), target.getBackboneNitrogen());
        }
    }

    public void connectNucleotideBonds(Nucleotide source, Nucleotide target) {
        Bond bond = new Bond(nextEdgeIdentifier());
        if (source.containsAtomWithName(AtomName.O3Pr) && target.containsAtomWithName(AtomName.P)) {
            addEdgeBetween(bond, source.getAtomByName(AtomName.O3Pr), target.getAtomByName(AtomName.P));
        }
    }

    public void addToConsecutivePart(LeafSubstructure<?, ?> leafSubstructure) {
        this.consecutiveIdentifier.add(leafSubstructure.getIdentifier());
        this.addSubstructure(leafSubstructure);
    }

    public List<LeafSubstructure<?, ?>> getConsecutivePart() {
        return this.consecutiveIdentifier.stream()
                .map(this::getLeafSubstructure)
                .collect(Collectors.toList());
    }

    public List<LeafSubstructure<?, ?>> getNonConsecutivePart() {
        return this.getLeafSubstructures().stream()
                .filter(leafSubstructure -> !this.consecutiveIdentifier.contains(leafSubstructure.getIdentifier()))
                .map(substructure -> (LeafSubstructure<?, ?>) substructure)
                .collect(Collectors.toList());
    }

    public List<LeafSubstructure<?, ?>> getLeafSubstructures() {
        return this.substructures.values().stream()
                .map(substructure -> (LeafSubstructure<?, ?>) substructure)
                .collect(Collectors.toList());
    }

    public LeafSubstructure<?, ?> getLeafSubstructure(int identifier) {
        return (LeafSubstructure<?, ?>) this.substructures.get(identifier);
    }

    /**
     * Gets the name (i.e. the single letter chainIdentifier identifier) of this chainIdentifier.
     *
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

    @Override
    public String toString() {
        return getLeafSubstructures().stream()
                .findAny()
                .map(LeafSubstructure::getPdbIdentifier)
                .orElse("") + "_" + this.chainIdentifier;
    }

    @Override
    public void setPosition(Vector3D position) {
        //FIXME not yet implemented
        throw new UnsupportedOperationException();
    }
}
