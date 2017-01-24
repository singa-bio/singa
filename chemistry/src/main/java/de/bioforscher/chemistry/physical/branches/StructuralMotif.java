package de.bioforscher.chemistry.physical.branches;

import de.bioforscher.chemistry.physical.families.MatcherFamily;
import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;
import de.bioforscher.chemistry.physical.model.*;
import de.bioforscher.mathematics.vectors.Vector3D;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Created by leberech on 16/12/16.
 */
public class StructuralMotif extends BranchSubstructure<StructuralMotif> {

    public StructuralMotif(int identifier) {
        super(identifier);
    }

    public StructuralMotif(StructuralMotif branchSubstructure) {
        super(branchSubstructure);
    }

    /**
     * Creates a {@link StructuralMotif} by extracting the given residues identified by a list of {@link LeafIdentifier}s.
     *
     * @param identifier      The internally-used identifier of the {@link StructuralMotif}.
     * @param structure       The {@link Structure} from which the {@link StructuralMotif} should be extracted.
     * @param leafIdentifiers The {@link LeafIdentifier}s of the residues that should compose the {@link StructuralMotif}.
     * @return A new {@link StructuralMotif}.
     */
    public static StructuralMotif fromLeafs(int identifier, Structure structure, List<LeafIdentifier> leafIdentifiers) {
        StructuralMotif motif = new StructuralMotif(identifier);
        leafIdentifiers.forEach(leafIdentifer -> {
            Substructure subStructure = structure.getAllChains().stream()
                    .filter(StructureFilter.isInChain(leafIdentifer.getChainIdentifer()))
                    .findFirst()
                    .orElseThrow(NoSuchElementException::new)
                    .getSubstructure(leafIdentifer.getLeafIdentifer())
                    .orElseThrow(NoSuchElementException::new);
            motif.addSubstructure(subStructure);
        });
        return motif;
    }

    /**
     * Forms a {@link StructuralMotif} out of the given {@link LeafSubstructure}s.
     *
     * @param identifier        The internally-used identifier of the {@link StructuralMotif}.
     * @param leafSubstructures The {@link LeafSubstructure}s that should compose the {@link StructuralMotif}.
     * @return A new {@link StructuralMotif}.
     */
    public static StructuralMotif fromLeafs(int identifier, List<LeafSubstructure<?,?>> leafSubstructures) {
        StructuralMotif motif = new StructuralMotif(identifier);
        leafSubstructures.forEach(motif::addSubstructure);
        return motif;
    }

    @Override
    public String toString() {
        return getSubstructures().stream()
                .map(Object::toString)
                .collect(Collectors.joining("_", getLeafSubstructures().stream()
                        .findAny()
                        .map(LeafSubstructure::getPdbId)
                        .orElse("") + "|", ""));
    }

    /**
     * Returns the size of the structural motif (the number of contained
     * {@link de.bioforscher.chemistry.physical.leafes.LeafSubstructure}s.
     *
     * @return The size of the motif.
     */
    public int size() {
        return getLeafSubstructures().size();
    }

    /**
     * FIXME: here we have to find a nice solution to generify definition of exchanges
     *
     * @param leafIdentifier   The LeafIdentifier that represents the {@link LeafSubstructure} for which an exchangeable
     *                         type should be assigned.
     * @param exchangeableType The {@link StructuralFamily} which should be assigned as exchangeable type.
     */
    public void addExchangeableType(LeafIdentifier leafIdentifier, StructuralFamily exchangeableType) {
        if (exchangeableType instanceof MatcherFamily) {
            ((MatcherFamily) exchangeableType).getMembers().forEach(aminoAcidFamily ->
                    getLeafSubstructures().stream()
                            .filter(leafSubstructure -> leafSubstructure.getLeafIdentifier().equals(leafIdentifier))
                            .findFirst()
                            .map(Exchangeable.class::cast).orElseThrow(NoSuchElementException::new)
                            .addExchangeableType(aminoAcidFamily)
            );
        } else {
            getLeafSubstructures().stream()
                    .filter(leafSubstructure -> leafSubstructure.getLeafIdentifier().equals(leafIdentifier))
                    .findFirst()
                    .map(Exchangeable.class::cast).orElseThrow(NoSuchElementException::new)
                    .addExchangeableType(exchangeableType);
        }
//        getSubstructure(leafIdentifier.getLeafIdentifer())
//                .map(Exchangeable.class::cast)
//                .orElseThrow(NoSuchElementException::new)
//                .addExchangeableType(exchangeableType);
    }

    /**
     * FIXME: here we have to find a nice solution to generify definition of exchanges
     *
     * @param exchangeableType The {@link StructuralFamily} which should be assigned as exchangeable type.
     */
    public void addExchangeableTypeToAll(StructuralFamily exchangeableType) {
        if (exchangeableType instanceof MatcherFamily) {
            ((MatcherFamily) exchangeableType).getMembers().forEach(aminoAcidFamily ->
                    getLeafSubstructures().stream()
                            .map(Exchangeable.class::cast)
                            .forEach(exchangeable -> exchangeable.addExchangeableType(aminoAcidFamily)));
        } else {
            getLeafSubstructures().stream()
                    .map(Exchangeable.class::cast)
                    .forEach(exchangeable -> exchangeable.addExchangeableType(exchangeableType));
        }
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
