package de.bioforscher.singa.chemistry.physical.branches;

import de.bioforscher.singa.chemistry.physical.families.AminoAcidFamily;
import de.bioforscher.singa.chemistry.physical.families.MatcherFamily;
import de.bioforscher.singa.chemistry.physical.families.NucleotideFamily;
import de.bioforscher.singa.chemistry.physical.leaves.AminoAcid;
import de.bioforscher.singa.chemistry.physical.leaves.LeafSubstructure;
import de.bioforscher.singa.chemistry.physical.leaves.Nucleotide;
import de.bioforscher.singa.chemistry.physical.model.LeafIdentifier;
import de.bioforscher.singa.chemistry.physical.model.Structure;
import de.bioforscher.singa.chemistry.physical.model.Substructure;
import de.bioforscher.singa.mathematics.vectors.Vector3D;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static de.bioforscher.singa.chemistry.physical.model.StructuralEntityFilter.ChainFilter;
import static de.bioforscher.singa.chemistry.physical.model.StructuralEntityFilter.LeafFilter.isAminoAcid;
import static de.bioforscher.singa.chemistry.physical.model.StructuralEntityFilter.LeafFilter.isNucleotide;

/**
 * @author cl
 */
public class StructuralMotif extends BranchSubstructure<StructuralMotif> {

    private static final int DEFAULT_IDENTIFIER = 0;

    public LinkedHashMap<Integer, Substructure<?>> orderedSubstructures;

    public StructuralMotif(int identifier) {
        super(identifier);
        this.orderedSubstructures = new LinkedHashMap<>();
    }

    public StructuralMotif(StructuralMotif branchSubstructure) {
        super(branchSubstructure);
        this.orderedSubstructures = new LinkedHashMap<>();
        for (LeafSubstructure<?, ?> leafSubstructure : branchSubstructure.getOrderedLeafSubstructures()) {
            this.orderedSubstructures.put(leafSubstructure.getIdentifier(),
                    getSubstructures().get(getSubstructures().indexOf(leafSubstructure)));
        }
    }

    /**
     * Creates a {@link StructuralMotif} by extracting the given residues identified by a list of {@link LeafIdentifier}s.
     *
     * @param identifier      The internally-used identifier of the {@link StructuralMotif}.
     * @param structure       The {@link Structure} from which the {@link StructuralMotif} should be extracted.
     * @param leafIdentifiers The {@link LeafIdentifier}s of the residues that should compose the {@link StructuralMotif}.
     * @return A new {@link StructuralMotif}.
     */
    public static StructuralMotif fromLeaves(int identifier, Structure structure, List<LeafIdentifier> leafIdentifiers) {
        StructuralMotif motif = new StructuralMotif(identifier);
        leafIdentifiers.forEach(leafIdentifer -> {
            Substructure subStructure = structure.getAllChains().stream()
                    .filter(ChainFilter.isInChain(leafIdentifer.getChainIdentifer()))
                    .findFirst()
                    .orElseThrow(NoSuchElementException::new)
                    .getSubstructure(leafIdentifer.getIdentifier())
                    .orElseThrow(NoSuchElementException::new);
            motif.addSubstructure(subStructure);
        });
        return motif;
    }

    /**
     * Creates a {@link StructuralMotif} by extracting the given residues identified by a list of {@link LeafIdentifier}s.
     * <b>The resulting structural motif has the internal default identifier. This method is only save to use if you
     * do not intent to bundle the resulting {@link StructuralMotif} in a superordinate
     * {@link BranchSubstructure}.</b>
     *
     * @param structure       The {@link Structure} from which the {@link StructuralMotif} should be extracted.
     * @param leafIdentifiers The {@link LeafIdentifier}s of the residues that should compose the {@link StructuralMotif}.
     * @return A new {@link StructuralMotif}.
     */
    public static StructuralMotif fromLeaves(Structure structure, List<LeafIdentifier> leafIdentifiers) {
        return fromLeaves(DEFAULT_IDENTIFIER, structure, leafIdentifiers);
    }

    /**
     * Forms a {@link StructuralMotif} out of the given {@link LeafSubstructure}s.
     *
     * @param identifier        The internally-used identifier of the {@link StructuralMotif}.
     * @param leafSubstructures The {@link LeafSubstructure}s that should compose the {@link StructuralMotif}.
     * @return A new {@link StructuralMotif}.
     */
    public static StructuralMotif fromLeaves(int identifier, List<LeafSubstructure<?, ?>> leafSubstructures) {
        StructuralMotif motif = new StructuralMotif(identifier);
        leafSubstructures.forEach(motif::addSubstructure);
        return motif;
    }

    /**
     * Forms a {@link StructuralMotif} out of the given {@link LeafSubstructure}s.
     * <b>The resulting structural motif has the internal default identifier. This method is only save to use if you
     * do not intent to bundle the resulting {@link StructuralMotif} in a superordinate
     * {@link BranchSubstructure}.</b>
     *
     * @param leafSubstructures The {@link LeafSubstructure}s that should compose the {@link StructuralMotif}.
     * @return A new {@link StructuralMotif}.
     */
    public static StructuralMotif fromLeaves(List<LeafSubstructure<?, ?>> leafSubstructures) {
        return fromLeaves(DEFAULT_IDENTIFIER, leafSubstructures);
    }

    @Override
    public void removeLeafSubstructure(LeafIdentifier leafIdentifier) {
        super.removeLeafSubstructure(leafIdentifier);
        // has to be done for structural motifs: remove desired leave substructure also from ordered storage
        this.orderedSubstructures.entrySet().removeIf(substructure -> substructure.getValue().getIdentifier()
                == leafIdentifier.getIdentifier());
    }

    /**
     * Returns the {@link LeafSubstructure}s of this {@link StructuralMotif} exactly in the order they were given
     * upon creation. This is a special implementation of {@link StructuralMotif} and should be used if one wants to
     * ensure a correct predefined order when working with the {@link LeafSubstructure}s independent of their sequence
     * order.
     *
     * @return The ordered {@link LeafSubstructure}s of this {@link StructuralMotif}.
     */
    public List<LeafSubstructure<?, ?>> getOrderedLeafSubstructures() {
        List<LeafSubstructure<?, ?>> leafSubstructures = new ArrayList<>();
        for (Substructure substructure : this.orderedSubstructures.values()) {
            if (substructure instanceof LeafSubstructure) {
                leafSubstructures.add((LeafSubstructure) substructure);
            } else if (substructure instanceof BranchSubstructure) {
                leafSubstructures.addAll(((BranchSubstructure<?>) substructure).getLeafSubstructures());
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
        return getSubstructures().stream()
                .map(Object::toString)
                .collect(Collectors.joining("_", getLeafSubstructures().stream()
                        .findAny()
                        .map(LeafSubstructure::getPdbIdentifier)
                        .orElse("") + "_", ""));
    }

    /**
     * Returns the size of the structural motif (the number of contained {@link LeafSubstructure}s).
     *
     * @return The size of the motif.
     */
    public int size() {
        return getLeafSubstructures().size();
    }

    public void addExchangeableFamily(LeafIdentifier leafIdentifier, MatcherFamily matcherFamily) {
        matcherFamily.getMembers().forEach(matcherFamilyMember -> addExchangeableFamily(leafIdentifier, matcherFamilyMember));
    }

    public void addExchangeableFamily(LeafIdentifier leafIdentifier, NucleotideFamily nucleotideFamily) {
        getLeafSubstructures().stream()
                .filter(isNucleotide())
                .map(Nucleotide.class::cast)
                .filter(aminoAcid -> aminoAcid.getLeafIdentifier().getChainIdentifer()
                        .equals(leafIdentifier.getChainIdentifer()))
                .filter(aminoAcid -> aminoAcid.getLeafIdentifier().getIdentifier()
                        == leafIdentifier.getIdentifier())
                .findFirst()
                .orElseThrow(NoSuchElementException::new)
                .addExchangeableFamily(nucleotideFamily);
    }

    public void addExchangeableFamily(LeafIdentifier leafIdentifier, AminoAcidFamily aminoAcidFamily) {
        getLeafSubstructures().stream()
                .filter(isAminoAcid())
                .map(AminoAcid.class::cast)
                .filter(aminoAcid -> aminoAcid.getLeafIdentifier().getChainIdentifer()
                        .equals(leafIdentifier.getChainIdentifer()))
                .filter(aminoAcid -> aminoAcid.getLeafIdentifier().getIdentifier()
                        == leafIdentifier.getIdentifier())
                .findFirst()
                .orElseThrow(NoSuchElementException::new)
                .addExchangeableFamily(aminoAcidFamily);
    }

    /**
     * Assigns an {@link AminoAcidFamily} to all members as exchange.
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
     * Assigns an {@link NucleotideFamily} to all members as exchange.
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
     * Assigns each member of a {@link MatcherFamily} to all members as exchange.
     *
     * @param matcherFamily The {@link MatcherFamily} that bundles all members which should be assigned as
     *                      exchangeable type.
     */
    public void addExchangeableFamilyToAll(MatcherFamily matcherFamily) {
        getLeafSubstructures().forEach(leafSubstructure -> matcherFamily.getMembers()
                .forEach(matcherFamilyMember -> addExchangeableFamily(leafSubstructure.getLeafIdentifier(),
                        matcherFamilyMember)));
    }


    @Override
    public StructuralMotif getCopy() {
        return new StructuralMotif(this);
    }

    @Override
    public void setPosition(Vector3D position) {
        //FIXME not yet implemented
        throw new UnsupportedOperationException();
    }
}
