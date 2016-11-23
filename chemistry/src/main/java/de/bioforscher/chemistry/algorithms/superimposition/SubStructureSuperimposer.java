package de.bioforscher.chemistry.algorithms.superimposition;

import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.chemistry.physical.atoms.AtomFilter;
import de.bioforscher.chemistry.physical.atoms.AtomName;
import de.bioforscher.chemistry.physical.model.SubStructure;
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

/**
 * A
 *
 * @author fk
 */
public class SubStructureSuperimposer {

    private static final Logger logger = LoggerFactory.getLogger(SubStructureSuperimposition.class);
    private final Predicate<Atom> atomFilter;
    private final List<SubStructure> reference;
    private final List<SubStructure> candidate;

    private double rmsd;
    private Vector translation;
    private Matrix rotation;
    private Vector3D referenceCentroid;
    private List<SubStructure> shiftedReference;
    private Vector3D candidateCentroid;
    private List<SubStructure> shiftedCandidate;

    private SubStructureSuperimposer(SubStructure reference, SubStructure candidate) {
        this(reference, candidate, AtomFilter.isArbitrary());
    }

    private SubStructureSuperimposer(SubStructure reference, SubStructure candidate, Predicate<Atom> atomFilter) {
        this.reference = reference.getAtomContainingSubstructures();
        this.candidate = candidate.getAtomContainingSubstructures();
        this.atomFilter = atomFilter;

        if (this.reference.size() != this.candidate.size() || this.reference.isEmpty() || this.candidate.isEmpty())
            throw new IllegalArgumentException("Two lists of substructures cannot be superimposed if they " +
                    "differ in size.");
    }

    /**
     * This constructor is for internal use only and defines by contract that each {@link SubStructure} is already
     * an atom-containing {@link SubStructure}.
     *
     * @param reference  a list of atom-containing {@link SubStructure}s
     * @param candidate  a list of atom-containing {@link SubStructure}s
     * @param atomFilter the {@link AtomFilter} to be used
     */
    private SubStructureSuperimposer(List<SubStructure> reference, List<SubStructure> candidate,
                                     Predicate<Atom> atomFilter) {
        this.reference = reference;
        this.candidate = candidate;
        this.atomFilter = atomFilter;
    }

    /**
     * This constructor is for internal use only and defines by contract that each {@link SubStructure} is already
     * an atom-containing {@link SubStructure}.
     *
     * @param reference a list of atom-containing {@link SubStructure}s
     * @param candidate a list of atom-containing {@link SubStructure}s
     */
    private SubStructureSuperimposer(List<SubStructure> reference, List<SubStructure> candidate) {
        this(reference, candidate, AtomFilter.isArbitrary());
    }

    public static SubStructureSuperimposition calculateIdealSubstructureSuperimposition(SubStructure reference,
                                                                                        SubStructure candidate) {
        return new SubStructureSuperimposer(reference, candidate).calculateIdealSuperimposition();
    }

    public static SubStructureSuperimposition calculateIdealSubstructureSuperimposition(SubStructure reference,
                                                                                        SubStructure candidate,
                                                                                        Predicate<Atom> atomFilter) {
        return new SubStructureSuperimposer(reference, candidate, atomFilter).calculateIdealSuperimposition();
    }

    public static SubStructureSuperimposition calculateSubstructureSuperimposition(SubStructure reference,
                                                                                   SubStructure candidate) {
        return new SubStructureSuperimposer(reference, candidate).calculateSuperimposition();
    }

    public static SubStructureSuperimposition calculateSubstructureSuperimposition(SubStructure reference,
                                                                                   SubStructure candidate,
                                                                                   Predicate<Atom> atomFilter) {
        return new SubStructureSuperimposer(reference, candidate, atomFilter).calculateSuperimposition();
    }

    private static String toAlignmentString(Map<Pair<SubStructure>, Set<AtomName>> perAtomAlignment) {
        StringJoiner referenceNameJoiner = new StringJoiner("|", "|", "|");
        perAtomAlignment.keySet().forEach(pair ->
                referenceNameJoiner.add(String.format("%-50s", pair.getFirst().toString())));
        StringJoiner atomNameJoiner = new StringJoiner("|", "|", "|");
        perAtomAlignment.values().forEach(atomNames -> atomNameJoiner
                .add(String.format("%-50s", atomNames.stream()
                        .map(AtomName::getName)
                        .sorted()
                        .collect(Collectors.joining("-")))));
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
     * Finds the ideal superimposition (LRMSD = min(RMSD)) for a list of substrutures.
     * <p>
     * <b>NOTE:</b> The superimposition is not necessarily the best. When matching incompatible residues one can obtain
     * a pseudo-better RMSD due to reduction of atoms.
     *
     * @return the pseudo-ideal superimposition
     */
    private SubStructureSuperimposition calculateIdealSuperimposition() {
        Optional<SubStructureSuperimposition> optionalSuperimposition = StreamPermutations.of(
                this.candidate.toArray(new SubStructure[this.candidate.size()]))
                .parallel()
                .map(s -> s.collect(Collectors.toList()))
                .map(permutedCandidates -> new SubStructureSuperimposer(this.reference,
                        permutedCandidates, this.atomFilter)
                        .calculateSuperimposition())
                .reduce((SubStructureSuperimposition s1, SubStructureSuperimposition s2) ->
                        s1.getRmsd() < s2.getRmsd() ? s1 : s2);
        return optionalSuperimposition.orElse(null);
    }

    private SubStructureSuperimposition calculateSuperimposition() {

        Map<Pair<SubStructure>, Set<AtomName>> perAtomAlignment = new LinkedHashMap<>();

        // create pairs of substructures to align
        IntStream.range(0, this.reference.size())
                .forEach(i -> perAtomAlignment.put(new Pair<>(this.reference.get(i), this.candidate.get(i)),
                        new HashSet<>()));

        // create atom subsets to align
        perAtomAlignment.entrySet().forEach(this::defineIntersectingAtoms);

        // collect intersecting, filtered and sorted reference atoms
        List<Atom> referenceAtoms = perAtomAlignment.entrySet().stream()
                .flatMap(pairSetEntry -> pairSetEntry.getKey().getFirst().getAllAtoms().stream()
                        .filter(this.atomFilter)
                        .filter(atom -> pairSetEntry.getValue().contains(atom.getAtomName()))
                        .sorted(Comparator.comparing(Atom::getAtomNameString)))
                .collect(Collectors.toList());
        List<Atom> candidateAtoms = perAtomAlignment.entrySet().stream()
                .flatMap(pairSetEntry -> pairSetEntry.getKey().getSecond().getAllAtoms().stream()
                        .filter(this.atomFilter)
                        .filter(atom -> pairSetEntry.getValue().contains(atom.getAtomName()))
                        .sorted(Comparator.comparing(Atom::getAtomNameString)))
                .collect(Collectors.toList());

        // calculate superimposition
        VectorSuperimposition vectorSuperimposition = VectorSuperimposer.calculateVectorSuperimposition(
                referenceAtoms.stream().map(Atom::getPosition).collect(Collectors.toList()),
                candidateAtoms.stream().map(Atom::getPosition).collect(Collectors.toList()));

        // store mapping of atoms to vectors
        List<Vector> mappedPositions = vectorSuperimposition.getMappedCandidate();
        Map<Integer, Integer> positionMapping = new HashMap<>();
        for (int i = 0; i < mappedPositions.size(); i++) {
            positionMapping.put(candidateAtoms.get(i).getIdentifier(), i);
        }

        // use a copy of the candidate to apply the mapping after calculating the superimposition
        List<SubStructure> mappedCandidate = this.candidate.stream()
                .map(SubStructure::getCopy)
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

        logger.info("superimposed substructures: {}", toAlignmentString(perAtomAlignment));

        // compose superimposition container
        return new SubStructureSuperimposition(vectorSuperimposition.getRmsd(),
                vectorSuperimposition.getTranslation(),
                vectorSuperimposition.getRotation(),
                mappedCandidate);
    }

    /**
     * Determines the intersecting atoms for a {@link Pair} of {@link SubStructure}s.
     *
     * @param pairListEntry the map entry for which intersecting atoms should be defined
     */
    private void defineIntersectingAtoms(Map.Entry<Pair<SubStructure>, Set<AtomName>> pairListEntry) {
        pairListEntry.getValue().addAll(pairListEntry.getKey().getFirst().getAllAtoms().stream()
                .filter(this.atomFilter)
                .map(Atom::getAtomName)
                .collect(Collectors.toSet()));
        pairListEntry.getValue().retainAll(pairListEntry.getKey().getSecond().getAllAtoms().stream()
                .filter(this.atomFilter)
                .map(Atom::getAtomName)
                .collect(Collectors.toSet()));
    }
}
