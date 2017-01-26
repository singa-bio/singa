package de.bioforscher.chemistry.algorithms.superimposition;

import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.chemistry.physical.atoms.AtomFilter;
import de.bioforscher.chemistry.physical.atoms.representations.RepresentationScheme;
import de.bioforscher.chemistry.physical.atoms.representations.RepresentationSchemeType;
import de.bioforscher.chemistry.physical.branches.BranchSubstructure;
import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;
import de.bioforscher.chemistry.physical.model.Substructure;
import de.bioforscher.core.utility.Pair;
import de.bioforscher.mathematics.algorithms.superimposition.VectorSuperimposer;
import de.bioforscher.mathematics.algorithms.superimposition.VectorSuperimposition;
import de.bioforscher.mathematics.combinatorics.StreamPermutations;
import de.bioforscher.mathematics.matrices.Matrix;
import de.bioforscher.mathematics.vectors.Vector;
import de.bioforscher.mathematics.vectors.Vector3D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * A
 *
 * @author fk
 */
public class SubStructureSuperimposer {

    private static final Logger logger = LoggerFactory.getLogger(SubstructureSuperimposition.class);
    private static final Predicate<Atom> DEFAULT_ATOM_FILTER = AtomFilter.isArbitrary();
    private final Predicate<Atom> atomFilter;
    private final RepresentationScheme representationScheme;
    private final List<LeafSubstructure<?, ?>> reference;
    private final List<LeafSubstructure<?, ?>> candidate;

    private double rmsd;
    private Vector translation;
    private Matrix rotation;

    private SubStructureSuperimposer(BranchSubstructure reference, BranchSubstructure candidate) {
        this(reference, candidate, AtomFilter.isArbitrary(), null);
    }

    private SubStructureSuperimposer(BranchSubstructure<?> reference, BranchSubstructure<?> candidate, Predicate<Atom> atomFilter,
                                     RepresentationScheme representationScheme) {
        this.reference = reference.getLeafSubstructures();
        this.candidate = candidate.getLeafSubstructures();
        this.atomFilter = atomFilter;
        this.representationScheme = representationScheme;

        if (this.reference.size() != this.candidate.size() || this.reference.isEmpty() || this.candidate.isEmpty())
            throw new IllegalArgumentException("Two lists of substructures cannot be superimposed if they " +
                    "differ in size.");
    }

    private SubStructureSuperimposer(List<LeafSubstructure<?, ?>> reference, List<LeafSubstructure<?, ?>> candidate,
                                     Predicate<Atom> atomFilter, RepresentationScheme representationScheme) {
        this.reference = reference;
        this.candidate = candidate;
        this.atomFilter = atomFilter;
        this.representationScheme = representationScheme;
    }

    private SubStructureSuperimposer(List<LeafSubstructure<?, ?>> reference, List<LeafSubstructure<?, ?>> candidate) {
        this(reference, candidate, DEFAULT_ATOM_FILTER, null);
    }

    public SubStructureSuperimposer(List<LeafSubstructure<?, ?>> reference, List<LeafSubstructure<?, ?>> candidate,
                                    RepresentationScheme representationScheme) {
        this(reference, candidate, DEFAULT_ATOM_FILTER, representationScheme);
    }

    public static SubstructureSuperimposition calculateIdealSubstructureSuperimposition(List<LeafSubstructure<?, ?>> reference,
                                                                                        List<LeafSubstructure<?, ?>> candidate) {
        return new SubStructureSuperimposer(reference, candidate).calculateIdealSuperimposition();
    }

    public static SubstructureSuperimposition calculateIdealSubstructureSuperimposition(List<LeafSubstructure<?, ?>> reference,
                                                                                        List<LeafSubstructure<?, ?>> candidate,
                                                                                        Predicate<Atom> atomFilter) {
        return new SubStructureSuperimposer(reference, candidate, atomFilter, null).calculateIdealSuperimposition();
    }

    public static SubstructureSuperimposition calculateIdealSubstructureSuperimposition(List<LeafSubstructure<?, ?>> reference,
                                                                                        List<LeafSubstructure<?, ?>> candidate,
                                                                                        RepresentationScheme representationScheme) {
        return new SubStructureSuperimposer(reference, candidate, DEFAULT_ATOM_FILTER, representationScheme).calculateIdealSuperimposition();
    }

    public static SubstructureSuperimposition calculateIdealSubstructureSuperimposition(BranchSubstructure reference,
                                                                                        BranchSubstructure candidate) {
        return new SubStructureSuperimposer(reference, candidate).calculateIdealSuperimposition();
    }

    public static SubstructureSuperimposition calculateIdealSubstructureSuperimposition(BranchSubstructure reference,
                                                                                        BranchSubstructure candidate,
                                                                                        Predicate<Atom> atomFilter) {
        return new SubStructureSuperimposer(reference, candidate, atomFilter, null).calculateIdealSuperimposition();
    }

    public static SubstructureSuperimposition calculateIdealSubstructureSuperimposition(BranchSubstructure reference,
                                                                                        BranchSubstructure candidate,
                                                                                        RepresentationScheme representationScheme) {
        return new SubStructureSuperimposer(reference, candidate, DEFAULT_ATOM_FILTER, representationScheme).calculateIdealSuperimposition();
    }

    public static SubstructureSuperimposition calculateSubstructureSuperimposition(List<LeafSubstructure<?, ?>> reference,
                                                                                   List<LeafSubstructure<?, ?>> candidate) {
        return new SubStructureSuperimposer(reference, candidate).calculateSuperimposition();
    }

    public static SubstructureSuperimposition calculateSubstructureSuperimposition(List<LeafSubstructure<?, ?>> reference,
                                                                                   List<LeafSubstructure<?, ?>> candidate,
                                                                                   Predicate<Atom> atomFilter) {
        return new SubStructureSuperimposer(reference, candidate, atomFilter, null).calculateSuperimposition();
    }

    public static SubstructureSuperimposition calculateSubstructureSuperimposition(List<LeafSubstructure<?, ?>> reference,
                                                                                   List<LeafSubstructure<?, ?>> candidate,
                                                                                   RepresentationScheme representationScheme) {
        return new SubStructureSuperimposer(reference, candidate, representationScheme).calculateSuperimposition();

    }

    public static SubstructureSuperimposition calculateSubstructureSuperimposition(BranchSubstructure reference,
                                                                                   BranchSubstructure candidate) {
        return new SubStructureSuperimposer(reference, candidate).calculateSuperimposition();
    }

    public static SubstructureSuperimposition calculateSubstructureSuperimposition(BranchSubstructure reference,
                                                                                   BranchSubstructure candidate,
                                                                                   Predicate<Atom> atomFilter) {
        return new SubStructureSuperimposer(reference, candidate, atomFilter, null).calculateSuperimposition();
    }

    public static SubstructureSuperimposition calculateSubstructureSuperimposition(BranchSubstructure reference,
                                                                                   BranchSubstructure candidate,
                                                                                   RepresentationScheme representationScheme) {
        return new SubStructureSuperimposer(reference, candidate, DEFAULT_ATOM_FILTER, representationScheme).calculateSuperimposition();
    }

    private String toAlignmentString(Map<Pair<LeafSubstructure<?, ?>>, Set<String>> perAtomAlignment) {
        StringJoiner referenceNameJoiner = new StringJoiner("|", "|", "|");
        perAtomAlignment.keySet().forEach(pair ->
                referenceNameJoiner.add(String.format("%-50s", pair.getFirst().toString())));
        StringJoiner atomNameJoiner = new StringJoiner("|", "|", "|");
        if (this.representationScheme == null) {
            perAtomAlignment.values().forEach(atomNames -> atomNameJoiner
                    .add(String.format("%-50s", atomNames.stream()
                            .sorted()
                            .collect(Collectors.joining("-")))));
        } else {
            perAtomAlignment.values().forEach(atomNames -> atomNameJoiner
                    .add(String.format("%-50s", Stream.of(RepresentationSchemeType.values())
                            .filter(representationSchemeType -> representationSchemeType.getCompatibleRepresentationScheme()
                                    .isInstance(this.representationScheme))
                            .findAny().orElse(RepresentationSchemeType.CA))));
        }
        StringJoiner candidateNameJoiner = new StringJoiner("|", "|", "|");
        perAtomAlignment.keySet().forEach(pair ->
                candidateNameJoiner.add(String.format("%-50s", pair.getSecond().toString())));
        StringJoiner alignmentJoiner = new StringJoiner("\n", "\n", "");
        alignmentJoiner.add(referenceNameJoiner.toString());
        alignmentJoiner.add(atomNameJoiner.toString());
        alignmentJoiner.add(candidateNameJoiner.toString());
        return alignmentJoiner.toString();
    }

    /**
     * Finds the ideal superimposition (LRMSD = min(RMSD)) for a list of {@link LeafSubstructure}.
     * <p>
     * <b>NOTE:</b> The superimposition is not necessarily the best. When matching incompatible residues one can obtain
     * a pseudo-better RMSD due to reduction of atoms.
     *
     * @return the pseudo-ideal superimposition
     */
    private SubstructureSuperimposition calculateIdealSuperimposition() {
        Optional<SubstructureSuperimposition> optionalSuperimposition = StreamPermutations.of(
                this.candidate.toArray(new LeafSubstructure<?, ?>[this.candidate.size()]))
                .parallel()
                .map(s -> s.collect(Collectors.toList()))
                .map(permutedCandidates -> new SubStructureSuperimposer(this.reference,
                        permutedCandidates, this.atomFilter, this.representationScheme)
                        .calculateSuperimposition())
                .reduce((SubstructureSuperimposition s1, SubstructureSuperimposition s2) ->
                        s1.getRmsd() < s2.getRmsd() ? s1 : s2);
        return optionalSuperimposition.orElse(null);
    }

    /**
     * Finds the superimposition for a list of {@link LeafSubstructure} according to their input order
     *
     * @return the superimposition according to their order
     */
    private SubstructureSuperimposition calculateSuperimposition() {

        Map<Pair<LeafSubstructure<?, ?>>, Set<String>> perAtomAlignment = new LinkedHashMap<>();

        // create pairs of substructures to align
        IntStream.range(0, this.reference.size())
                .forEach(i -> perAtomAlignment.put(new Pair<>(this.reference.get(i), this.candidate.get(i)),
                        new HashSet<>()));

        // create atom subsets to align
        perAtomAlignment.entrySet()
                .forEach(this::defineIntersectingAtoms);

        List<Atom> referenceAtoms;
        List<Atom> candidateAtoms;
        // no representation scheme is defined
        if (this.representationScheme == null) {
            // collect intersecting, filtered and sorted reference atoms
            referenceAtoms = perAtomAlignment.entrySet().stream()
                    .flatMap(pairSetEntry -> pairSetEntry.getKey().getFirst().getAllAtoms().stream()
                            .filter(this.atomFilter)
                            .filter(atom -> pairSetEntry.getValue().contains(atom.getAtomNameString()))
                            .sorted(Comparator.comparing(Atom::getAtomNameString)))
                    .collect(Collectors.toList());
            candidateAtoms = perAtomAlignment.entrySet().stream()
                    .flatMap(pairSetEntry -> pairSetEntry.getKey().getSecond().getAllAtoms().stream()
                            .filter(this.atomFilter)
                            .filter(atom -> pairSetEntry.getValue().contains(atom.getAtomNameString()))
                            .sorted(Comparator.comparing(Atom::getAtomNameString)))
                    .collect(Collectors.toList());
        } else {
            // reduce each leaf substructure to single representation scheme atoms
            referenceAtoms = perAtomAlignment.entrySet().stream()
                    .map(pairSetEntry -> pairSetEntry.getKey().getFirst())
                    .map(this.representationScheme::determineRepresentingAtom)
                    .collect(Collectors.toList());
            candidateAtoms = perAtomAlignment.entrySet().stream()
                    .map(pairSetEntry -> pairSetEntry.getKey().getSecond())
                    .map(this.representationScheme::determineRepresentingAtom)
                    .collect(Collectors.toList());
        }

        // calculate superimposition
        VectorSuperimposition vectorSuperimposition = VectorSuperimposer.calculateVectorSuperimposition(
                referenceAtoms.stream()
                        .map(Atom::getPosition)
                        .collect(Collectors.toList()),
                candidateAtoms.stream()
                        .map(Atom::getPosition)
                        .collect(Collectors.toList()));

        // store result
        this.translation = vectorSuperimposition.getTranslation();
        this.rotation = vectorSuperimposition.getRotation();
        this.rmsd = vectorSuperimposition.getRmsd();

        // store mapping of atoms to vectors
        List<Vector> mappedPositions = vectorSuperimposition.getMappedCandidate();
        Map<Integer, Integer> positionMapping = new HashMap<>();
        for (int i = 0; i < mappedPositions.size(); i++) {
            positionMapping.put(candidateAtoms.get(i).getIdentifier(), i);
        }

        // use a copy of the candidate to apply the mapping after calculating the superimposition
        List<LeafSubstructure<?, ?>> mappedCandidate = this.candidate.stream()
                .map(LeafSubstructure::getCopy)
                .collect(Collectors.toList());

        // also create a copy for the full all-atom candidate
        List<LeafSubstructure<?, ?>> mappedFullCandidate = this.candidate.stream()
                .map(LeafSubstructure::getCopy)
                .collect(Collectors.toList());

        // remove all atoms and bonds not part of the alignment
        List<Integer> atomIdsToBeRemoved = mappedCandidate.stream()
                .flatMap(subStructure -> subStructure.getAllAtoms().stream())
                .filter(atom -> !positionMapping.containsKey(atom.getIdentifier()))
                .map(Atom::getIdentifier)
                .collect(Collectors.toList());
        atomIdsToBeRemoved.forEach(id -> mappedCandidate
                .forEach(subStructure -> subStructure.removeNode(id)));

        // apply superimposition to copy of the candidate
        mappedCandidate.forEach(subStructure -> subStructure.getAllAtoms()
                .forEach(atom -> atom.setPosition(mappedPositions
                        .get(positionMapping.get(atom.getIdentifier()))
                        .as(Vector3D.class))));

        // apply superimposition to full all-atom copy of the candidate
        mappedFullCandidate.stream()
                .map(Substructure::getAllAtoms)
                .flatMap(List::stream)
                .forEach(atom -> atom.setPosition(this.rotation
                        .transpose()
                        .multiply(atom.getPosition())
                        .add(this.translation).as(Vector3D.class)));

        logger.debug("superimposed substructures with RMSD {}{}", this.rmsd, toAlignmentString(perAtomAlignment));

        // compose superimposition container
        return new SubstructureSuperimposition(vectorSuperimposition.getRmsd(),
                this.translation,
                this.rotation,
                this.reference,
                this.candidate,
                mappedCandidate, mappedFullCandidate);
    }

    /**
     * Determines the intersecting atoms for a {@link Pair} of {@link BranchSubstructure}s.
     *
     * @param pairListEntry the map entry for which intersecting atoms should be defined
     */
    private void defineIntersectingAtoms(Map.Entry<Pair<LeafSubstructure<?, ?>>, Set<String>> pairListEntry) {

        pairListEntry.getValue().addAll(pairListEntry.getKey().getFirst().getAllAtoms().stream()
                .filter(this.atomFilter)
                .map(Atom::getAtomNameString)
                .collect(Collectors.toSet()));
        pairListEntry.getValue().retainAll(pairListEntry.getKey().getSecond().getAllAtoms().stream()
                .filter(this.atomFilter)
                .map(Atom::getAtomNameString)
                .collect(Collectors.toSet()));
    }
}
