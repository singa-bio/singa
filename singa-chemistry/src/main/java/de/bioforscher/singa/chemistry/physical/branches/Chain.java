package de.bioforscher.singa.chemistry.physical.branches;

import de.bioforscher.singa.chemistry.physical.atoms.Atom;
import de.bioforscher.singa.chemistry.physical.atoms.AtomName;
import de.bioforscher.singa.chemistry.physical.leaves.AminoAcid;
import de.bioforscher.singa.chemistry.physical.leaves.LeafSubstructure;
import de.bioforscher.singa.chemistry.physical.leaves.Nucleotide;
import de.bioforscher.singa.chemistry.physical.model.Bond;
import de.bioforscher.singa.chemistry.physical.model.LeafIdentifier;
import de.bioforscher.singa.chemistry.physical.model.Substructure;

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
public class Chain extends BranchSubstructure<Chain, String> {

    private Set<LeafIdentifier> consecutiveIdentifiers;

    /**
     * Creates a new Chain with the given graph pdbIdentifier. This is not the single character chainIdentifier identifier, but the
     * reference for the placement in the graph.
     *
     * @param identifier The identifier in the graph.
     */
    public Chain(String identifier) {
        super(identifier);
        this.consecutiveIdentifiers = new TreeSet<>();
    }

    public Chain(Chain chain) {
        this(chain.getIdentifier());
        for (Substructure<?, ?> structure : chain.getSubstructures()) {
            this.addSubstructure(structure.getCopy());
        }
        Map<Integer, Atom> atoms = new TreeMap<>();
        for (Atom atom : getAllAtoms()) {
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
        super(LeafIdentifier.DEFAULT_CHAIN_IDENTIFER);
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
        this.consecutiveIdentifiers.add(leafSubstructure.getIdentifier());
        this.addSubstructure(leafSubstructure);
    }

    public List<LeafSubstructure<?, ?>> getConsecutivePart() {
        return this.consecutiveIdentifiers.stream()
                .map(this::getLeafSubstructure)
                .collect(Collectors.toList());
    }

    public List<LeafSubstructure<?, ?>> getNonConsecutivePart() {
        return this.getLeafSubstructures().stream()
                .filter(leafSubstructure -> !this.consecutiveIdentifiers.contains(leafSubstructure.getIdentifier()))
                .map(substructure -> (LeafSubstructure<?, ?>) substructure)
                .collect(Collectors.toList());
    }

    public List<LeafSubstructure<?, ?>> getLeafSubstructures() {
        return this.substructures.values().stream()
                .map(substructure -> (LeafSubstructure<?, ?>) substructure)
                .collect(Collectors.toList());
    }

    public LeafSubstructure<?, ?> getLeafSubstructure(LeafIdentifier identifier) {
        return (LeafSubstructure<?, ?>) this.substructures.get(identifier);
    }

    @Override
    public String flatToString() {
        return "Chain " + identifier + " containing "+this.getSubstructures().size()+" LeafSubstructures";
    }

    @Override
    public String deepToString() {
        return "Chain " + identifier + ", with Leaves: {" + getLeafSubstructures().stream()
                .map(leaf -> leaf.getFamily().getThreeLetterCode()+"-"+ leaf.getIdentifier().getSerial())
                .collect(Collectors.joining(", ")) + "}";
    }

    @Override
    public Chain getCopy() {
        return new Chain(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Chain that = (Chain) o;

        return identifier != null ? identifier.equals(that.identifier) : that.identifier == null;
    }

    @Override
    public int hashCode() {
        return identifier != null ? identifier.hashCode() : 0;
    }



}
