package de.bioforscher.singa.structure.model.oak;

import de.bioforscher.singa.structure.model.identifiers.LeafIdentifier;
import de.bioforscher.singa.structure.model.interfaces.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class OakChain implements Chain {

    private final String identifier;

    private TreeMap<LeafIdentifier, OakLeafSubstructure<?>> leafSubstructures;

    private Set<LeafIdentifier> consecutiveIdentifiers;

    public OakChain(String chainIdentifier) {
        this.identifier = chainIdentifier;
        this.leafSubstructures = new TreeMap<>();
        this.consecutiveIdentifiers = new HashSet<>();
    }

    public OakChain(OakChain chain) {
        this.identifier = chain.identifier;
        this.leafSubstructures = new TreeMap<>();
        for (OakLeafSubstructure<?> leafSubstructure : chain.leafSubstructures.values()) {
            this.leafSubstructures.put(leafSubstructure.getIdentifier(), leafSubstructure.getCopy());
        }
        this.consecutiveIdentifiers = new HashSet<>(chain.consecutiveIdentifiers);
    }

    @Override
    public String getIdentifier() {
        return this.identifier;
    }

    @Override
    public List<LeafSubstructure<?>> getAllLeafSubstructures() {
        return new ArrayList<>(this.leafSubstructures.values());
    }

    @Override
    public Optional<LeafSubstructure<?>> getLeafSubstructure(LeafIdentifier leafIdentifier) {
        if (this.leafSubstructures.containsKey(leafIdentifier)) {
            return Optional.of(this.leafSubstructures.get(leafIdentifier));
        }
        return Optional.empty();

    }

    public void addLeafSubstructure(OakLeafSubstructure leafSubstructure, boolean consecutivePart) {
        if (consecutivePart) {
            this.consecutiveIdentifiers.add(leafSubstructure.getIdentifier());
        }
        addLeafSubstructure(leafSubstructure);
    }

    public void addLeafSubstructure(OakLeafSubstructure leafSubstructure) {
        this.leafSubstructures.put(leafSubstructure.getIdentifier(), leafSubstructure);
    }

    @Override
    public boolean removeLeafSubstructure(LeafIdentifier leafIdentifier) {
        if (this.leafSubstructures.containsKey(leafIdentifier)) {
            // collect all atoms that should be removed
            List<Integer> atomsToBeRemoved = this.leafSubstructures.get(leafIdentifier).getAllAtoms().stream()
                    .map(Atom::getIdentifier)
                    .collect(Collectors.toList());
            // remove them
            atomsToBeRemoved.forEach(this::removeAtom);
            // remove the leaf
            this.leafSubstructures.remove(leafIdentifier);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Optional<Atom> getAtom(Integer atomIdentifier) {
        for (LeafSubstructure<?> leafSubstructure : this.leafSubstructures.values()) {
            final Optional<Atom> optionalAtom = leafSubstructure.getAtom(atomIdentifier);
            if (optionalAtom.isPresent()) {
                return optionalAtom;
            }
        }
        return Optional.empty();
    }

    @Override
    public void removeAtom(Integer atomIdentifier) {
        for (LeafSubstructure<?> leafSubstructure : this.leafSubstructures.values()) {
            final Optional<Atom> optionalAtom = leafSubstructure.getAtom(atomIdentifier);
            if (optionalAtom.isPresent()) {
                leafSubstructure.removeAtom(optionalAtom.get().getIdentifier());
                return;
            }
        }
    }

    public void connectChainBackbone() {
        OakLeafSubstructure<?> lastSubstructure = null;
        for (OakLeafSubstructure<?> currentSubstructure : leafSubstructures.values()) {
            if (lastSubstructure != null) {
                if (lastSubstructure instanceof AminoAcid && currentSubstructure instanceof AminoAcid) {
                    connectPeptideBonds((OakAminoAcid) lastSubstructure, (OakAminoAcid) currentSubstructure);
                } else if (lastSubstructure instanceof Nucleotide && currentSubstructure instanceof Nucleotide) {
                    connectNucleotideBonds((OakNucleotide) lastSubstructure, (OakNucleotide) currentSubstructure);
                }
            }
            lastSubstructure = currentSubstructure;
        }
    }

    /**
     * Connects two residues, using the Backbone Carbon (C) of the source residue and the Backbone Nitrogen (N) of the
     * target residue.
     *
     * @param source AminoAcid with Backbone Carbon.
     * @param target AminoAcid with Backbone Nitrogen.
     */
    public void connectPeptideBonds(OakAminoAcid source, OakAminoAcid target) {
        // creates the peptide backbone
        if (source.containsAtomWithName("C") && target.containsAtomWithName("N")) {
            source.addBondBetween((OakAtom) source.getAtomByName("C").get(), (OakAtom) target.getAtomByName("N").get());
        }
    }

    public void connectNucleotideBonds(OakNucleotide source, OakNucleotide target) {
        // creates the peptide backbone
        if (source.containsAtomWithName("O3'") && target.containsAtomWithName("P")) {
            source.addBondBetween((OakAtom) source.getAtomByName("O3'").get(), (OakAtom) target.getAtomByName("P").get());
        }
    }

    public List<LeafSubstructure<?>> getConsecutivePart() {
        List<LeafSubstructure<?>> consecutivePart = new ArrayList<>();
        for (LeafSubstructure<?> leafSubstructure : this.leafSubstructures.values()) {
            if (consecutiveIdentifiers.contains(leafSubstructure.getIdentifier())) {
                consecutivePart.add(leafSubstructure);
            }
        }
        return consecutivePart;
    }

    public List<LeafSubstructure<?>> getNonConsecutivePart() {
        List<LeafSubstructure<?>> consecutivePart = new ArrayList<>();
        for (LeafSubstructure<?> leafSubstructure : this.leafSubstructures.values()) {
            if (!consecutiveIdentifiers.contains(leafSubstructure.getIdentifier())) {
                consecutivePart.add(leafSubstructure);
            }
        }
        return consecutivePart;
    }

    LeafIdentifier getNextLeafIdentifier() {
        LeafIdentifier lastLeafIdentifier = this.leafSubstructures.lastEntry().getKey();
        return new LeafIdentifier(lastLeafIdentifier.getPdbIdentifier(), lastLeafIdentifier.getModelIdentifier(),
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

        if (!identifier.equals(oakChain.identifier)) return false;
        return leafSubstructures != null ? leafSubstructures.equals(oakChain.leafSubstructures) : oakChain.leafSubstructures == null;
    }

    @Override
    public int hashCode() {
        int result = identifier.hashCode();
        result = 31 * result + (leafSubstructures != null ? leafSubstructures.hashCode() : 0);
        return result;
    }
}
