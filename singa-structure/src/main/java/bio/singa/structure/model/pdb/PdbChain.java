package bio.singa.structure.model.pdb;

import bio.singa.structure.model.interfaces.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class PdbChain implements Chain {

    private final String identifier;

    private final TreeMap<PdbLeafIdentifier, PdbLeafSubstructure> leafSubstructures;

    private final Set<PdbLeafIdentifier> consecutiveIdentifiers;

    public PdbChain(String chainIdentifier) {
        identifier = chainIdentifier;
        leafSubstructures = new TreeMap<>();
        consecutiveIdentifiers = new HashSet<>();
    }

    public PdbChain(PdbChain chain) {
        identifier = chain.identifier;
        leafSubstructures = new TreeMap<>();
        for (PdbLeafSubstructure leafSubstructure : chain.leafSubstructures.values()) {
            leafSubstructures.put(leafSubstructure.getIdentifier(), leafSubstructure.getCopy());
        }
        consecutiveIdentifiers = new HashSet<>(chain.consecutiveIdentifiers);
    }

    @Override
    public String getChainIdentifier() {
        return identifier;
    }

    @Override
    public Collection<PdbLeafSubstructure> getAllLeafSubstructures() {
        return leafSubstructures.values();
    }

    @Override
    public Optional<PdbLeafSubstructure> getLeafSubstructure(LeafIdentifier leafIdentifier) {
        if (leafSubstructures.containsKey(leafIdentifier)) {
            return Optional.of(leafSubstructures.get(leafIdentifier));
        }
        return Optional.empty();

    }

    @Override
    public LeafSubstructure getFirstLeafSubstructure() {
        return leafSubstructures.values().iterator().next();
    }

    public void addLeafSubstructure(PdbLeafSubstructure leafSubstructure, boolean consecutivePart) {
        if (consecutivePart) {
            consecutiveIdentifiers.add(leafSubstructure.getIdentifier());
        }
        addLeafSubstructure(leafSubstructure);
    }

    public void addLeafSubstructure(PdbLeafSubstructure leafSubstructure) {
        leafSubstructures.put(leafSubstructure.getIdentifier(), leafSubstructure);
    }

    @Override
    public boolean removeLeafSubstructure(LeafIdentifier leafIdentifier) {
        if (leafSubstructures.containsKey(leafIdentifier)) {
            // collect all atoms that should be removed
            List<Integer> atomsToBeRemoved = leafSubstructures.get(leafIdentifier).getAllAtoms().stream()
                    .map(Atom::getAtomIdentifier)
                    .collect(Collectors.toList());
            // remove them
            atomsToBeRemoved.forEach(this::removeAtom);
            // remove the leaf
            leafSubstructures.remove(leafIdentifier);
            consecutiveIdentifiers.remove(leafIdentifier);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Optional<PdbAtom> getAtom(Integer atomIdentifier) {
        for (PdbLeafSubstructure leafSubstructure : leafSubstructures.values()) {
            final Optional<PdbAtom> optionalAtom = leafSubstructure.getAtom(atomIdentifier);
            if (optionalAtom.isPresent()) {
                return optionalAtom;
            }
        }
        return Optional.empty();
    }

    @Override
    public void removeAtom(Integer atomIdentifier) {
        for (PdbLeafSubstructure leafSubstructure : leafSubstructures.values()) {
            final Optional<PdbAtom> optionalAtom = leafSubstructure.getAtom(atomIdentifier);
            if (optionalAtom.isPresent()) {
                leafSubstructure.removeAtom(optionalAtom.get().getAtomIdentifier());
                return;
            }
        }
    }

    public void connectChainBackbone() {
        AminoAcid previousAminoAcid = null;
        for (AminoAcid aminoAcid : getAllAminoAcids()) {
            if (previousAminoAcid == null) {
                previousAminoAcid = aminoAcid;
                continue;
            }
            connectPeptideBonds((PdbAminoAcid) previousAminoAcid, (PdbAminoAcid) aminoAcid);
            previousAminoAcid = aminoAcid;
        }
        Nucleotide previousNucleotide = null;
        for (Nucleotide nucleotide : getAllNucleotides()) {
            if (previousNucleotide == null) {
                previousNucleotide = nucleotide;
                continue;
            }
            connectNucleotideBonds((PdbNucleotide) previousNucleotide, (PdbNucleotide) nucleotide);
            previousNucleotide = nucleotide;
        }
    }

    /**
     * Connects two residues, using the Backbone Carbon (C) of the source residue and the Backbone Nitrogen (N) of the
     * target residue.
     *
     * @param source AminoAcid with Backbone Carbon.
     * @param target AminoAcid with Backbone Nitrogen.
     */
    public static void connectPeptideBonds(PdbAminoAcid source, PdbAminoAcid target) {
        // creates the peptide backbone
        Optional<Atom> sourceAtomOptional = source.getAtomByName("C");
        Optional<Atom> targetAtomOptional = target.getAtomByName("N");
        if (sourceAtomOptional.isPresent() && targetAtomOptional.isPresent()) {
            source.addBondBetween((PdbAtom) sourceAtomOptional.get(), (PdbAtom) targetAtomOptional.get());
        }
    }

    public void connectNucleotideBonds(PdbNucleotide source, PdbNucleotide target) {
        // creates the peptide backbone
        Optional<Atom> sourceAtomOptional = source.getAtomByName("O3'");
        Optional<Atom> targetAtomOptional = target.getAtomByName("P");
        if (sourceAtomOptional.isPresent() && targetAtomOptional.isPresent()) {
            source.addBondBetween((PdbAtom) sourceAtomOptional.get(), (PdbAtom) targetAtomOptional.get());
        }
    }

    public List<PdbLeafSubstructure> getConsecutivePart() {
        List<PdbLeafSubstructure> consecutivePart = new ArrayList<>();
        for (PdbLeafSubstructure leafSubstructure : leafSubstructures.values()) {
            if (consecutiveIdentifiers.contains(leafSubstructure.getIdentifier())) {
                consecutivePart.add(leafSubstructure);
            }
        }
        return consecutivePart;
    }

    public List<PdbLeafSubstructure> getNonConsecutivePart() {
        List<PdbLeafSubstructure> consecutivePart = new ArrayList<>();
        for (PdbLeafSubstructure leafSubstructure : leafSubstructures.values()) {
            if (!consecutiveIdentifiers.contains(leafSubstructure.getIdentifier())) {
                consecutivePart.add(leafSubstructure);
            }
        }
        return consecutivePart;
    }

    public PdbLeafIdentifier getNextLeafIdentifier() {
        PdbLeafIdentifier lastLeafIdentifier = leafSubstructures.lastEntry().getKey();
        return new PdbLeafIdentifier(lastLeafIdentifier.getStructureIdentifier(), lastLeafIdentifier.getModelIdentifier(),
                lastLeafIdentifier.getChainIdentifier(), lastLeafIdentifier.getSerial() + 1);
    }

    @Override
    public PdbChain getCopy() {
        return new PdbChain(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PdbChain pdbChain = (PdbChain) o;

        return identifier != null ? identifier.equals(pdbChain.identifier) : pdbChain.identifier == null;
    }

    @Override
    public int hashCode() {
        return identifier != null ? identifier.hashCode() : 0;
    }

    @Override
    public String toString() {
        return flatToString();
    }

}
