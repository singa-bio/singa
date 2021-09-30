package bio.singa.structure.model.interfaces;

import bio.singa.structure.model.families.StructuralFamily;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Everything that contains leaf substructures. Provides default methods to retrieve {@link AminoAcid}s, {@link
 * Nucleotide}s, and {@link Ligand}s.
 *
 * @author cl
 */
public interface LeafSubstructureContainer extends AtomContainer {

    /**
     * Returns all {@link LeafSubstructure}s.
     *
     * @return All {@link LeafSubstructure}s.
     */
    List<LeafSubstructure> getAllLeafSubstructures();

    /**
     * Returns an {@link Optional} of the {@link LeafSubstructure} with the given identifier. If no LeafSubstructure
     * with the identifier could be found, an empty optional is returned.
     *
     * @param leafIdentifier The identifier of the leaf.
     * @return An {@link Optional} encapsulating the {@link LeafSubstructure}.
     */
    Optional<LeafSubstructure> getLeafSubstructure(LeafIdentifier leafIdentifier);

    LeafSubstructure getFirstLeafSubstructure();

    /**
     * Removes a {@link LeafSubstructure} from this container.
     *
     * @param leafIdentifier The identifier of the leaf.
     * @return True if the {@link LeafSubstructure} was removed.
     */
    boolean removeLeafSubstructure(LeafIdentifier leafIdentifier);

    default boolean removeLeafSubstructure(LeafSubstructure leafSubstructure) {
        return removeLeafSubstructure(leafSubstructure.getIdentifier());
    }

    /**
     * Removes all {@link LeafSubstructure}s from this container that are not referenced in the given
     * {@link LeafSubstructureContainer}. Basically all LeafSubstructures are removed that do not match any of the
     * given containers families. This method also keeps exchangeable families if any are defined.
     *
     * @param leafSubstructuresToKeep The leaf structures that are kept.
     */
    default void removeLeafSubstructuresNotRelevantFor(LeafSubstructureContainer leafSubstructuresToKeep) {
        // collect all containing types (own types <b>plus</b> exchangeable types) of the query motif
        Set<StructuralFamily> containingTypes = leafSubstructuresToKeep.getAllLeafSubstructures().stream()
                .map(LeafSubstructure::getContainingFamilies)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        List<LeafSubstructure> toBeRemoved = getAllLeafSubstructures().stream()
                .filter(leafSubstructure -> !containingTypes.contains(leafSubstructure.getFamily()))
                .collect(Collectors.toList());
        toBeRemoved.forEach(this::removeLeafSubstructure);
    }

    default int getNumberOfLeafSubstructures() {
        return getAllLeafSubstructures().size();
    }

    /**
     * Returns all {@link AminoAcid}s.
     *
     * @return All {@link AminoAcid}s.
     */
    default List<AminoAcid> getAllAminoAcids() {
        List<AminoAcid> aminoAcids = new ArrayList<>();
        for (LeafSubstructure leafSubstructure : getAllLeafSubstructures()) {
            if (leafSubstructure instanceof AminoAcid) {
                aminoAcids.add((AminoAcid) leafSubstructure);
            }
        }
        return aminoAcids;
    }

    /**
     * Returns an {@link Optional} of the {@link AminoAcid} with the given identifier. If no LeafSubstructure with the
     * identifier could be found or the LeafSubstructure is no AminoAcid, an empty optional is returned.
     *
     * @param leafIdentifier The identifier of the amino acid.
     * @return An {@link Optional} encapsulating the {@link AminoAcid}.
     */
    default Optional<AminoAcid> getAminoAcid(LeafIdentifier leafIdentifier) {
        Optional<LeafSubstructure> leafSubstructureOptional = getLeafSubstructure(leafIdentifier);
        if (!leafSubstructureOptional.isPresent()) {
            return Optional.empty();
        }
        final LeafSubstructure leafSubstructure = leafSubstructureOptional.get();
        if (!(leafSubstructure instanceof AminoAcid)) {
            return Optional.empty();
        }
        return Optional.of((AminoAcid) leafSubstructure);
    }

    /**
     * Returns all {@link Nucleotide}s.
     *
     * @return All {@link Nucleotide}s.
     */
    default List<Nucleotide> getAllNucleotides() {
        List<Nucleotide> nucleotides = new ArrayList<>();
        for (LeafSubstructure leafSubstructure : getAllLeafSubstructures()) {
            if (leafSubstructure instanceof Nucleotide) {
                nucleotides.add((Nucleotide) leafSubstructure);
            }
        }
        return nucleotides;
    }

    /**
     * Returns an {@link Optional} of the {@link Nucleotide} with the given identifier. If no LeafSubstructure with the
     * identifier could be found or the LeafSubstructure is no Nucleotide, an empty optional is returned.
     *
     * @param leafIdentifier The identifier of the nucleotide.
     * @return An {@link Optional} encapsulating the {@link Nucleotide}.
     */
    default Optional<Nucleotide> getNucleotide(LeafIdentifier leafIdentifier) {
        Optional<LeafSubstructure> leafSubstructureOptional = getLeafSubstructure(leafIdentifier);
        if (!leafSubstructureOptional.isPresent()) {
            return Optional.empty();
        }
        final LeafSubstructure leafSubstructure = leafSubstructureOptional.get();
        if (!(leafSubstructure instanceof Nucleotide)) {
            return Optional.empty();
        }
        return Optional.of((Nucleotide) leafSubstructure);
    }

    /**
     * Returns all {@link Ligand}s.
     *
     * @return All {@link Ligand}s.
     */
    default List<Ligand> getAllLigands() {
        List<Ligand> ligands = new ArrayList<>();
        for (LeafSubstructure leafSubstructure : getAllLeafSubstructures()) {
            if (leafSubstructure instanceof Ligand) {
                ligands.add((Ligand) leafSubstructure);
            }
        }
        return ligands;
    }

    /**
     * Returns an {@link Optional} of the {@link Ligand} with the given identifier. If no LeafSubstructure with the
     * identifier could be found or the LeafSubstructure is no Ligand, an empty optional is returned.
     *
     * @param leafIdentifier The identifier of the ligand.
     * @return An {@link Optional} encapsulating the {@link Ligand}.
     */
    default Optional<Ligand> getLigand(LeafIdentifier leafIdentifier) {
        Optional<LeafSubstructure> leafSubstructureOptional = getLeafSubstructure(leafIdentifier);
        if (!leafSubstructureOptional.isPresent()) {
            return Optional.empty();
        }
        final LeafSubstructure leafSubstructure = leafSubstructureOptional.get();
        if (!(leafSubstructure instanceof Ligand)) {
            return Optional.empty();
        }
        return Optional.of((Ligand) leafSubstructure);
    }

    /**
     * Returns all {@link Atom}s.
     *
     * @return All {@link Atom}s.
     */
    default List<Atom> getAllAtoms() {
        List<Atom> atoms = new ArrayList<>();
        final List<LeafSubstructure> allLeafSubstructures = getAllLeafSubstructures();
        for (LeafSubstructure leafSubstructure : allLeafSubstructures) {
            atoms.addAll(leafSubstructure.getAllAtoms());
        }
        return atoms;
    }

    /**
     * Returns an {@link Optional} of the {@link Atom} with the given identifier. If no Atom with the identifier could
     * be found an empty optional is returned.
     *
     * @param atomIdentifier The identifier of the atom.
     * @return An {@link Optional} encapsulating the {@link Atom}.
     */
    default Optional<Atom> getAtom(int atomIdentifier) {
        for (LeafSubstructure leafSubstructure : getAllLeafSubstructures()) {
            final Optional<Atom> optionalAtom = leafSubstructure.getAtom(atomIdentifier);
            if (optionalAtom.isPresent()) {
                return optionalAtom;
            }
        }
        return Optional.empty();
    }


    <ContainerType extends LeafSubstructureContainer> ContainerType getCopy();

}
