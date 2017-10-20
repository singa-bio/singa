package de.bioforscher.singa.structure.model.graph.branches;

import de.bioforscher.singa.structure.algorithms.superimposition.fit3d.Fit3DBuilder;
import de.bioforscher.singa.structure.model.graph.families.AminoAcidFamily;
import de.bioforscher.singa.structure.model.graph.families.MatcherFamily;
import de.bioforscher.singa.structure.model.graph.families.NucleotideFamily;
import de.bioforscher.singa.structure.model.graph.model.LeafIdentifier;
import de.bioforscher.singa.structure.model.graph.model.StructuralEntityFilter;
import de.bioforscher.singa.structure.model.interfaces.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class StructuralMotif implements LeafSubstructureContainer {

    private String identifier;

    public LinkedHashMap<LeafIdentifier, LeafSubstructure> leafSubstructures;

    private StructuralMotif(String identifier) {
        this.identifier = identifier;
        this.leafSubstructures = new LinkedHashMap<>();
    }

    private StructuralMotif(String identifier, List<LeafSubstructure> leafSubstructures) {
        this(identifier);
        leafSubstructures.forEach(this::addSubstructure);
    }

    private StructuralMotif(StructuralMotif structuralMotif) {
        this.identifier = structuralMotif.identifier;
        this.leafSubstructures = new LinkedHashMap<>();
        for (LeafSubstructure leafSubstructure : structuralMotif.leafSubstructures.values()) {
            this.leafSubstructures.put(leafSubstructure.getIdentifier(), leafSubstructure.getCopy());
        }
    }

    /**
     * Creates a {@link StructuralMotif} by extracting the given residues identified by a list of {@link
     * LeafIdentifier}s.
     *
     * @param structure The {@link Structure} from which the {@link StructuralMotif} should be extracted.
     * @param leafIdentifiers The {@link LeafIdentifier}s of the residues that should compose the {@link
     * StructuralMotif}.
     * @return A new {@link StructuralMotif}.
     */
    public static StructuralMotif fromLeafIdentifiers(Structure structure, List<LeafIdentifier> leafIdentifiers) {
        List<LeafSubstructure> leafSubstructures = new ArrayList<>();
        for (LeafIdentifier leafIdentifier : leafIdentifiers) {
            final Optional<LeafSubstructure> leafSubstructure = structure.getLeafSubstructure(leafIdentifier);
            if (leafSubstructure.isPresent()) {
                leafSubstructures.add(leafSubstructure.get());
            } else {
                throw new NoSuchElementException("Unable to add leaf substructure with identifier" + leafIdentifier + " from structure " + structure.getPdbIdentifier());
            }
        }
        return new StructuralMotif(generateMotifIdentifier(leafSubstructures), leafSubstructures);
    }

    /**
     * Forms a {@link StructuralMotif} out of the given {@link LeafSubstructure}s.
     *
     * @param leafSubstructures The {@link LeafSubstructure}s that should compose the {@link StructuralMotif}.
     * @return A new {@link StructuralMotif}.
     */
    public static StructuralMotif fromLeafSubstructures(List<LeafSubstructure> leafSubstructures) {
        return new StructuralMotif(generateMotifIdentifier(leafSubstructures), leafSubstructures);
    }

    private static String generateMotifIdentifier(List<LeafSubstructure> substructures) {
        String pdbIdentifier = substructures.iterator().next().getIdentifier().getPdbIdentifier();
        return substructures.stream()
                .map(leafSubstructure -> leafSubstructure.getIdentifier().getChainIdentifier() + "-" + leafSubstructure.getIdentifier().getSerial())
                .collect(Collectors.joining("_", pdbIdentifier + "_", ""));
    }

    @Override
    public List<LeafSubstructure> getAllLeafSubstructures() {
        return new ArrayList<>(leafSubstructures.values());
    }

    @Override
    public Optional<LeafSubstructure> getLeafSubstructure(LeafIdentifier leafIdentifier) {
        final LeafSubstructure leafSubstructure = leafSubstructures.get(leafIdentifier);
        if (leafSubstructure != null) {
            return Optional.of(leafSubstructure);
        }
        return Optional.empty();
    }

    @Override
    public boolean removeLeafSubstructure(LeafIdentifier leafIdentifier) {
        final LeafSubstructure leafSubstructure = this.leafSubstructures.remove(leafIdentifier);
        return leafSubstructure != null;
    }


    public void addSubstructure(LeafSubstructure leafSubstructure) {
        this.leafSubstructures.put(leafSubstructure.getIdentifier(), leafSubstructure);
        this.leafSubstructures.put(leafSubstructure.getIdentifier(), (LeafSubstructure) leafSubstructure);
    }


    /**
     * Returns the size of the structural motif (the number of contained {@link LeafSubstructure}s).
     *
     * @return The size of the motif.
     */
    public int size() {
        return this.leafSubstructures.size();
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
                leafSubstructure.removeAtom(atomIdentifier);
                return;
            }
        }
    }

    /**
     * For a given {@link LeafIdentifier} a possible exchange is added. This exchange is considered when searching with
     * {@link Fit3DBuilder Fit3D}. The {@link MatcherFamily} allows exchanges for groups of {@link LeafSubstructure}s
     * with similar physicochemical attributes.
     *
     * @param leafIdentifier The leaf to add the exchange to.
     * @param matcherFamily The {@link MatcherFamily}.
     */
    public void addExchangeableFamily(LeafIdentifier leafIdentifier, MatcherFamily matcherFamily) {
        matcherFamily.getMembers()
                .forEach(matcherFamilyMember -> addExchangeableFamily(leafIdentifier, matcherFamilyMember));
    }

    /**
     * For a given {@link LeafIdentifier} a possible exchange is added. This exchange is considered when searching with
     * {@link Fit3DBuilder Fit3D}.
     *
     * @param leafIdentifier The leaf to add the exchange to.
     * @param nucleotideFamily The {@link Nucleotide} that can be exchanged.
     */
    public void addExchangeableFamily(LeafIdentifier leafIdentifier, NucleotideFamily nucleotideFamily) {
        leafSubstructures.values().stream()
                .filter(StructuralEntityFilter.LeafFilter.isNucleotide())
                .map(Nucleotide.class::cast)
                .filter(nucleotide -> nucleotide.getIdentifier().getChainIdentifier().equals(leafIdentifier.getChainIdentifier()))
                .filter(nucleotide -> nucleotide.getIdentifier().getSerial() == leafIdentifier.getSerial())
                .findFirst()
                .orElseThrow(NoSuchElementException::new)
                .addExchangeableFamily(nucleotideFamily);
    }

    /**
     * For a given {@link LeafIdentifier} a possible exchange is added. This exchange is considered when searching with
     * {@link Fit3DBuilder Fit3D}.
     *
     * @param leafIdentifier The leaf to add the exchange to.
     * @param aminoAcidFamily The {@link AminoAcid} that can be exchanged.
     */
    public void addExchangeableFamily(LeafIdentifier leafIdentifier, AminoAcidFamily aminoAcidFamily) {
        leafSubstructures.values().stream()
                .filter(StructuralEntityFilter.LeafFilter.isAminoAcid())
                .map(AminoAcid.class::cast)
                .filter(aminoAcid -> aminoAcid.getIdentifier().getChainIdentifier().equals(leafIdentifier.getChainIdentifier()))
                .filter(aminoAcid -> aminoAcid.getIdentifier().getSerial() == leafIdentifier.getSerial())
                .findFirst()
                .orElseThrow(NoSuchElementException::new)
                .addExchangeableFamily(aminoAcidFamily);
    }

    /**
     * Assigns an {@link AminoAcidFamily} to all members of this motif as exchange.
     *
     * @param aminoAcidFamily The {@link AminoAcidFamily} which should be assigned as exchangeable type.
     */
    public void addExchangeableFamilyToAll(AminoAcidFamily aminoAcidFamily) {
        leafSubstructures.values().stream()
                .filter(StructuralEntityFilter.LeafFilter.isAminoAcid())
                .map(AminoAcid.class::cast)
                .forEach(exchangeable -> exchangeable.addExchangeableFamily(aminoAcidFamily));

    }

    /**
     * Assigns an {@link NucleotideFamily} to all members of this motif as exchange.
     *
     * @param nucleotideFamily The {@link NucleotideFamily} which should be assigned as exchangeable type.
     */
    public void addExchangeableFamilyToAll(NucleotideFamily nucleotideFamily) {
        leafSubstructures.values().stream()
                .filter(StructuralEntityFilter.LeafFilter.isNucleotide())
                .map(Nucleotide.class::cast)
                .forEach(exchangeable -> exchangeable.addExchangeableFamily(nucleotideFamily));
    }

    /**
     * Assigns each member of a {@link MatcherFamily} to all members of this motif as exchange.
     *
     * @param matcherFamily The {@link MatcherFamily} that bundles all members which should be assigned as exchangeable
     * type.
     */
    public void addExchangeableFamilyToAll(MatcherFamily matcherFamily) {
        leafSubstructures.values().forEach(leafSubstructure -> matcherFamily.getMembers()
                .forEach(matcherFamilyMember -> addExchangeableFamily(leafSubstructure.getIdentifier(), matcherFamilyMember)));
    }


    @Override
    public String toString() {
        return this.identifier;
    }

    @Override
    public StructuralMotif getCopy() {
        return new StructuralMotif(this);
    }

}
