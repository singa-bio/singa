package de.bioforscher.singa.chemistry.physical.branches;

import de.bioforscher.singa.chemistry.algorithms.superimposition.fit3d.Fit3DBuilder;
import de.bioforscher.singa.chemistry.physical.families.AminoAcidFamily;
import de.bioforscher.singa.chemistry.physical.families.MatcherFamily;
import de.bioforscher.singa.chemistry.physical.families.NucleotideFamily;
import de.bioforscher.singa.chemistry.physical.leaves.AminoAcid;
import de.bioforscher.singa.chemistry.physical.leaves.LeafSubstructure;
import de.bioforscher.singa.chemistry.physical.leaves.Nucleotide;
import de.bioforscher.singa.chemistry.physical.model.LeafIdentifier;
import de.bioforscher.singa.chemistry.physical.model.Structure;
import de.bioforscher.singa.chemistry.physical.model.Substructure;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static de.bioforscher.singa.chemistry.physical.model.StructuralEntityFilter.LeafFilter.isAminoAcid;
import static de.bioforscher.singa.chemistry.physical.model.StructuralEntityFilter.LeafFilter.isNucleotide;

/**
 * @author cl
 */
public class StructuralMotif extends BranchSubstructure<StructuralMotif, String> {


    public LinkedHashMap<Object, Substructure<?, ?>> orderedSubstructures;

    private StructuralMotif(String identifier) {
        super(identifier);
        this.orderedSubstructures = new LinkedHashMap<>();
    }

    private StructuralMotif(String identifier, List<LeafSubstructure<?, ?>> leafSubstructures) {
        this(identifier);
        leafSubstructures.forEach(this::addSubstructure);
    }

    private StructuralMotif(StructuralMotif structuralMotif) {
        super(structuralMotif);
        this.orderedSubstructures = new LinkedHashMap<>();
        for (LeafSubstructure<?, ?> leafSubstructure : structuralMotif.getOrderedLeafSubstructures()) {
            this.orderedSubstructures.put(leafSubstructure.getIdentifier(),
                    getSubstructures().get(getSubstructures().indexOf(leafSubstructure)));
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
        List<LeafSubstructure<?, ?>> leaves = new ArrayList<>();
        leafIdentifiers.forEach(leafIdentifer -> {
            LeafSubstructure<?, ?> subStructure = structure.getAllLeafSubstructures().stream()
                    .filter(leaf -> leaf.getChainIdentifier().equals(leafIdentifer.getChainIdentifier())
                            && leaf.getIdentifier().getSerial() == leafIdentifer.getSerial()
                            && leaf.getInsertionCode() == leafIdentifer.getInsertionCode())
                    .findFirst()
                    .orElseThrow(NoSuchElementException::new);
            leaves.add(subStructure);
        });

        return new StructuralMotif(generateMotifIdentifier(leaves), leaves);
    }

    /**
     * Forms a {@link StructuralMotif} out of the given {@link LeafSubstructure}s.
     *
     * @param leafSubstructures The {@link LeafSubstructure}s that should compose the {@link StructuralMotif}.
     * @return A new {@link StructuralMotif}.
     */
    public static StructuralMotif fromLeafSubstructures(List<LeafSubstructure<?, ?>> leafSubstructures) {
        return new StructuralMotif(generateMotifIdentifier(leafSubstructures), leafSubstructures);
    }

    private static String generateMotifIdentifier(List<LeafSubstructure<?, ?>> substructures) {
        String pdbIdentifier = substructures.iterator().next().getPdbIdentifier();
        return substructures.stream()
                .map(leafSubstructure -> leafSubstructure.getChainIdentifier() + "-" + leafSubstructure.getIdentifier().getSerial())
                .collect(Collectors.joining("_", pdbIdentifier + "_", ""));
    }

    @Override
    public <RemovableSubstructureType extends Substructure> void removeSubstructure(RemovableSubstructureType substructure) {
        super.removeSubstructure(substructure);
        // has to be done for structural motifs: remove desired leave substructure also from ordered storage
        this.orderedSubstructures.remove(substructure.getIdentifier());
    }

    /**
     * Returns the {@link LeafSubstructure}s of this {@link StructuralMotif} exactly in the order they were given upon
     * creation. This is a special implementation of {@link StructuralMotif} and should be used if one wants to ensure a
     * correct predefined order when working with the {@link LeafSubstructure}s independent of their sequence order.
     *
     * @return The ordered {@link LeafSubstructure}s of this {@link StructuralMotif}.
     */
    public List<LeafSubstructure<?, ?>> getOrderedLeafSubstructures() {
        List<LeafSubstructure<?, ?>> leafSubstructures = new ArrayList<>();
        for (Substructure substructure : this.orderedSubstructures.values()) {
            if (substructure instanceof LeafSubstructure) {
                leafSubstructures.add((LeafSubstructure) substructure);
            } else if (substructure instanceof BranchSubstructure) {
                leafSubstructures.addAll(((BranchSubstructure<?, ?>) substructure).getLeafSubstructures());
            }
        }
        return leafSubstructures;
    }

    @Override
    public void addSubstructure(Substructure substructure) {
        this.substructures.put(substructure.getIdentifier(), substructure);
        this.orderedSubstructures.put(substructure.getIdentifier(), substructure);
    }

    @Override
    public String toString() {
        return generateMotifIdentifier(getLeafSubstructures());
    }

    /**
     * Returns the size of the structural motif (the number of contained {@link LeafSubstructure}s).
     *
     * @return The size of the motif.
     */
    public int size() {
        return getLeafSubstructures().size();
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
        matcherFamily.getMembers().forEach(matcherFamilyMember -> addExchangeableFamily(leafIdentifier, matcherFamilyMember));
    }

    /**
     * For a given {@link LeafIdentifier} a possible exchange is added. This exchange is considered when searching with
     * {@link Fit3DBuilder Fit3D}.
     *
     * @param leafIdentifier The leaf to add the exchange to.
     * @param nucleotideFamily The {@link Nucleotide} that can be exchanged.
     */
    public void addExchangeableFamily(LeafIdentifier leafIdentifier, NucleotideFamily nucleotideFamily) {
        getLeafSubstructures().stream()
                .filter(isNucleotide())
                .map(Nucleotide.class::cast)
                .filter(nucleotide -> nucleotide.getIdentifier().getChainIdentifier()
                        .equals(leafIdentifier.getChainIdentifier()) &&
                        nucleotide.getIdentifier().getSerial() == leafIdentifier.getSerial())
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
        getLeafSubstructures().stream()
                .filter(isAminoAcid())
                .map(AminoAcid.class::cast)
                .filter(aminoAcid -> aminoAcid.getIdentifier().getChainIdentifier()
                        .equals(leafIdentifier.getChainIdentifier()))
                .filter(aminoAcid -> aminoAcid.getIdentifier().getSerial()
                        == leafIdentifier.getSerial())
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
        getLeafSubstructures().stream()
                .filter(isAminoAcid())
                .map(AminoAcid.class::cast)
                .forEach(exchangeable -> exchangeable.addExchangeableFamily(aminoAcidFamily));

    }

    /**
     * Assigns an {@link NucleotideFamily} to all members of this motif as exchange.
     *
     * @param nucleotideFamily The {@link NucleotideFamily} which should be assigned as exchangeable type.
     */
    public void addExchangeableFamilyToAll(NucleotideFamily nucleotideFamily) {
        getLeafSubstructures().stream()
                .filter(isNucleotide())
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
        getLeafSubstructures().forEach(leafSubstructure -> matcherFamily.getMembers()
                .forEach(matcherFamilyMember -> addExchangeableFamily(leafSubstructure.getIdentifier(),
                        matcherFamilyMember)));
    }

    @Override
    public StructuralMotif getCopy() {
        return new StructuralMotif(this);
    }

}
