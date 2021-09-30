package bio.singa.structure.model.oak;

import bio.singa.structure.model.families.StructuralFamily;
import bio.singa.structure.model.interfaces.*;
import bio.singa.structure.parser.pfam.PfamParser;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class OakChain implements Chain {

    private final String identifier;

    private final TreeMap<PdbLeafIdentifier, OakLeafSubstructure> leafSubstructures;

    private final Set<PdbLeafIdentifier> consecutiveIdentifiers;

    public OakChain(String chainIdentifier) {
        identifier = chainIdentifier;
        leafSubstructures = new TreeMap<>();
        consecutiveIdentifiers = new HashSet<>();
    }

    public OakChain(OakChain chain) {
        identifier = chain.identifier;
        leafSubstructures = new TreeMap<>();
        for (OakLeafSubstructure leafSubstructure : chain.leafSubstructures.values()) {
            leafSubstructures.put(leafSubstructure.getIdentifier(), leafSubstructure.getCopy());
        }
        consecutiveIdentifiers = new HashSet<>(chain.consecutiveIdentifiers);
    }

    @Override
    public String getChainIdentifier() {
        return identifier;
    }

    @Override
    public List<LeafSubstructure> getAllLeafSubstructures() {
        return new ArrayList<>(leafSubstructures.values());
    }

    @Override
    public Optional<LeafSubstructure> getLeafSubstructure(LeafIdentifier leafIdentifier) {
        if (leafSubstructures.containsKey(leafIdentifier)) {
            return Optional.of(leafSubstructures.get(leafIdentifier));
        }
        return Optional.empty();

    }

    @Override
    public LeafSubstructure getFirstLeafSubstructure() {
        return leafSubstructures.values().iterator().next();
    }

    public void addLeafSubstructure(OakLeafSubstructure leafSubstructure, boolean consecutivePart) {
        if (consecutivePart) {
            consecutiveIdentifiers.add(leafSubstructure.getIdentifier());
        }
        addLeafSubstructure(leafSubstructure);
    }

    public void addLeafSubstructure(OakLeafSubstructure leafSubstructure) {
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
    public Optional<Atom> getAtom(Integer atomIdentifier) {
        for (LeafSubstructure leafSubstructure : leafSubstructures.values()) {
            final Optional<Atom> optionalAtom = leafSubstructure.getAtom(atomIdentifier);
            if (optionalAtom.isPresent()) {
                return optionalAtom;
            }
        }
        return Optional.empty();
    }

    @Override
    public void removeAtom(Integer atomIdentifier) {
        for (LeafSubstructure leafSubstructure : leafSubstructures.values()) {
            final Optional<Atom> optionalAtom = leafSubstructure.getAtom(atomIdentifier);
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
            connectPeptideBonds((OakAminoAcid) previousAminoAcid, (OakAminoAcid) aminoAcid);
            previousAminoAcid = aminoAcid;
        }
        Nucleotide previousNucleotide = null;
        for (Nucleotide nucleotide : getAllNucleotides()) {
            if (previousNucleotide == null) {
                previousNucleotide = nucleotide;
                continue;
            }
            connectNucleotideBonds((OakNucleotide) previousNucleotide, (OakNucleotide) nucleotide);
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
    public static void connectPeptideBonds(OakAminoAcid source, OakAminoAcid target) {
        // creates the peptide backbone
        Optional<Atom> sourceAtomOptional = source.getAtomByName("C");
        Optional<Atom> targetAtomOptional = target.getAtomByName("N");
        if (sourceAtomOptional.isPresent() && targetAtomOptional.isPresent()) {
            source.addBondBetween((OakAtom) sourceAtomOptional.get(), (OakAtom) targetAtomOptional.get());
        }
    }

    public void connectNucleotideBonds(OakNucleotide source, OakNucleotide target) {
        // creates the peptide backbone
        Optional<Atom> sourceAtomOptional = source.getAtomByName("O3'");
        Optional<Atom> targetAtomOptional = target.getAtomByName("P");
        if (sourceAtomOptional.isPresent() && targetAtomOptional.isPresent()) {
            source.addBondBetween((OakAtom) sourceAtomOptional.get(), (OakAtom) targetAtomOptional.get());
        }
    }

    public List<OakLeafSubstructure> getConsecutivePart() {
        List<OakLeafSubstructure> consecutivePart = new ArrayList<>();
        for (OakLeafSubstructure leafSubstructure : leafSubstructures.values()) {
            if (consecutiveIdentifiers.contains(leafSubstructure.getIdentifier())) {
                consecutivePart.add(leafSubstructure);
            }
        }
        return consecutivePart;
    }

    public List<OakLeafSubstructure> getNonConsecutivePart() {
        List<OakLeafSubstructure> consecutivePart = new ArrayList<>();
        for (OakLeafSubstructure leafSubstructure : leafSubstructures.values()) {
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
    public OakChain getCopy() {
        return new OakChain(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OakChain oakChain = (OakChain) o;

        return identifier != null ? identifier.equals(oakChain.identifier) : oakChain.identifier == null;
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
