package de.bioforscher.singa.structure.algorithms.superimposition;

import de.bioforscher.singa.core.utility.Pair;
import de.bioforscher.singa.mathematics.algorithms.superimposition.VectorSuperimposer;
import de.bioforscher.singa.mathematics.algorithms.superimposition.VectorSuperimposition;
import de.bioforscher.singa.mathematics.combinatorics.StreamPermutations;
import de.bioforscher.singa.mathematics.matrices.Matrix;
import de.bioforscher.singa.mathematics.vectors.Vector;
import de.bioforscher.singa.mathematics.vectors.Vector3D;
import de.bioforscher.singa.structure.algorithms.superimposition.fit3d.representations.RepresentationScheme;
import de.bioforscher.singa.structure.algorithms.superimposition.fit3d.representations.RepresentationSchemeType;
import de.bioforscher.singa.structure.model.interfaces.Atom;
import de.bioforscher.singa.structure.model.interfaces.AtomContainer;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructure;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructureContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static de.bioforscher.singa.structure.model.oak.StructuralEntityFilter.AtomFilter;

/**
 * Calculates the ideal superimposition.
 *
 * @author fk
 */
public class SubstructureSuperimposer {

    private static final Logger logger = LoggerFactory.getLogger(SubstructureSuperimposition.class);
    private static final Predicate<Atom> DEFAULT_ATOM_FILTER = AtomFilter.isArbitrary();
    private final Predicate<Atom> atomFilter;
    private final RepresentationScheme representationScheme;
    private final List<LeafSubstructure<?>> reference;
    private final List<LeafSubstructure<?>> candidate;

    private double rmsd;
    private Vector translation;
    private Matrix rotation;

    private SubstructureSuperimposer(LeafSubstructureContainer reference, LeafSubstructureContainer candidate) {
        this(reference, candidate, AtomFilter.isArbitrary(), null);
    }

    private SubstructureSuperimposer(LeafSubstructureContainer reference, LeafSubstructureContainer candidate, Predicate<Atom> atomFilter,
                                     RepresentationScheme representationScheme) {
        this.reference = reference.getAllLeafSubstructures();
        this.candidate = candidate.getAllLeafSubstructures();
        this.atomFilter = atomFilter;
        this.representationScheme = representationScheme;

        if (this.reference.size() != this.candidate.size() || this.reference.isEmpty() || this.candidate.isEmpty())
            throw new IllegalArgumentException("Two lists of substructures cannot be superimposed if they " +
                    "differ in size.");
    }

    private SubstructureSuperimposer(List<LeafSubstructure<?>> reference, List<LeafSubstructure<?>> candidate,
                                     Predicate<Atom> atomFilter, RepresentationScheme representationScheme) {
        this.reference = reference;
        this.candidate = candidate;
        this.atomFilter = atomFilter;
        this.representationScheme = representationScheme;
    }

    private SubstructureSuperimposer(List<LeafSubstructure<?>> reference, List<LeafSubstructure<?>> candidate) {
        this(reference, candidate, DEFAULT_ATOM_FILTER, null);
    }

    public SubstructureSuperimposer(List<LeafSubstructure<?>> reference, List<LeafSubstructure<?>> candidate,
                                    RepresentationScheme representationScheme) {
        this(reference, candidate, DEFAULT_ATOM_FILTER, representationScheme);
    }

    public static SubstructureSuperimposition calculateIdealSubstructureSuperimposition(List<LeafSubstructure<?>> reference,
                                                                                        List<LeafSubstructure<?>> candidate) {
        return new SubstructureSuperimposer(reference, candidate).calculateIdealSuperimposition();
    }

    public static SubstructureSuperimposition calculateIdealSubstructureSuperimposition(List<LeafSubstructure<?>> reference,
                                                                                        List<LeafSubstructure<?>> candidate,
                                                                                        Predicate<Atom> atomFilter) {
        return new SubstructureSuperimposer(reference, candidate, atomFilter, null).calculateIdealSuperimposition();
    }

    public static SubstructureSuperimposition calculateIdealSubstructureSuperimposition(List<LeafSubstructure<?>> reference,
                                                                                        List<LeafSubstructure<?>> candidate,
                                                                                        RepresentationScheme representationScheme) {
        return new SubstructureSuperimposer(reference, candidate, DEFAULT_ATOM_FILTER, representationScheme).calculateIdealSuperimposition();
    }

    public static SubstructureSuperimposition calculateIdealSubstructureSuperimposition(LeafSubstructureContainer reference,
                                                                                        LeafSubstructureContainer candidate) {
        return new SubstructureSuperimposer(reference, candidate).calculateIdealSuperimposition();
    }

    public static SubstructureSuperimposition calculateIdealSubstructureSuperimposition(LeafSubstructureContainer reference,
                                                                                        LeafSubstructureContainer candidate,
                                                                                        Predicate<Atom> atomFilter) {
        return new SubstructureSuperimposer(reference, candidate, atomFilter, null).calculateIdealSuperimposition();
    }

    public static SubstructureSuperimposition calculateIdealSubstructureSuperimposition(LeafSubstructureContainer reference,
                                                                                        LeafSubstructureContainer candidate,
                                                                                        RepresentationScheme representationScheme) {
        return new SubstructureSuperimposer(reference, candidate, DEFAULT_ATOM_FILTER, representationScheme).calculateIdealSuperimposition();
    }

    public static SubstructureSuperimposition calculateSubstructureSuperimposition(List<LeafSubstructure<?>> reference,
                                                                                   List<LeafSubstructure<?>> candidate) throws SubstructureSuperimpositionException {
        return new SubstructureSuperimposer(reference, candidate).calculateSuperimposition();
    }

    public static SubstructureSuperimposition calculateSubstructureSuperimposition(List<LeafSubstructure<?>> reference,
                                                                                   List<LeafSubstructure<?>> candidate,
                                                                                   Predicate<Atom> atomFilter) throws SubstructureSuperimpositionException {
        return new SubstructureSuperimposer(reference, candidate, atomFilter, null).calculateSuperimposition();
    }

    public static SubstructureSuperimposition calculateSubstructureSuperimposition(List<LeafSubstructure<?>> reference,
                                                                                   List<LeafSubstructure<?>> candidate,
                                                                                   RepresentationScheme representationScheme) throws SubstructureSuperimpositionException {
        return new SubstructureSuperimposer(reference, candidate, representationScheme).calculateSuperimposition();

    }

    public static SubstructureSuperimposition calculateSubstructureSuperimposition(LeafSubstructureContainer reference,
                                                                                   LeafSubstructureContainer candidate) throws SubstructureSuperimpositionException {
        return new SubstructureSuperimposer(reference, candidate).calculateSuperimposition();
    }

    public static SubstructureSuperimposition calculateSubstructureSuperimposition(LeafSubstructureContainer reference,
                                                                                   LeafSubstructureContainer candidate,
                                                                                   Predicate<Atom> atomFilter) throws SubstructureSuperimpositionException {
        return new SubstructureSuperimposer(reference, candidate, atomFilter, null).calculateSuperimposition();
    }

    public static SubstructureSuperimposition calculateSubstructureSuperimposition(LeafSubstructureContainer reference,
                                                                                   LeafSubstructureContainer candidate,
                                                                                   RepresentationScheme representationScheme) throws SubstructureSuperimpositionException {
        return new SubstructureSuperimposer(reference, candidate, DEFAULT_ATOM_FILTER, representationScheme).calculateSuperimposition();
    }

    private String toAlignmentString(Map<Pair<LeafSubstructure<?>>, Set<String>> perAtomAlignment) {
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
     * Finds the ideal superimposition (LRMSD = min(RMSD)) for a list of {@link LeafSubstructure}. <p> <b>NOTE:</b> The
     * superimposition is not necessarily the best. When matching incompatible residues one can obtain a pseudo-better
     * RMSD due to reduction of atoms.
     *
     * @return the pseudo-ideal superimposition
     */
    private SubstructureSuperimposition calculateIdealSuperimposition() throws SubstructureSuperimpositionException {
        Optional<SubstructureSuperimposition> optionalSuperimposition = StreamPermutations.of(
                this.candidate.toArray(new LeafSubstructure<?>[this.candidate.size()]))
                .parallel()
                .map(s -> s.collect(Collectors.toList()))
                .map(permutedCandidates -> {
                    try {
                        return new SubstructureSuperimposer(this.reference,
                                permutedCandidates, this.atomFilter, this.representationScheme)
                                .calculateSuperimposition();
                    } catch (SubstructureSuperimpositionException e) {
                        logger.error("failed to calculate substructure superimposition", e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .reduce((SubstructureSuperimposition s1, SubstructureSuperimposition s2) ->
                        s1.getRmsd() < s2.getRmsd() ? s1 : s2);
        return optionalSuperimposition.orElseThrow(() -> new SubstructureSuperimpositionException("no ideal superimposition found"));
    }

    /**
     * Finds the superimposition for a list of {@link LeafSubstructure} according to their input order
     *
     * @return the superimposition according to their order
     */
    private SubstructureSuperimposition calculateSuperimposition() throws SubstructureSuperimpositionException {

        Map<Pair<LeafSubstructure<?>>, Set<String>> perAtomAlignment = new LinkedHashMap<>();

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
                            .filter(atom -> pairSetEntry.getValue().contains(atom.getAtomName()))
                            .sorted(Comparator.comparing(Atom::getAtomName)))
                    .collect(Collectors.toList());
            candidateAtoms = perAtomAlignment.entrySet().stream()
                    .flatMap(pairSetEntry -> pairSetEntry.getKey().getSecond().getAllAtoms().stream()
                            .filter(this.atomFilter)
                            .filter(atom -> pairSetEntry.getValue().contains(atom.getAtomName()))
                            .sorted(Comparator.comparing(Atom::getAtomName)))
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

        if (referenceAtoms.isEmpty() || candidateAtoms.isEmpty()) {
            logger.error("reference {} against candidate {} has no compatible atom sets: {} {}", this.reference, this.candidate, referenceAtoms, candidateAtoms);
            throw new SubstructureSuperimpositionException("failed to collect per atom alignment sets, no compatible atoms");
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
        List<LeafSubstructure<?>> mappedCandidate = new ArrayList<>();
        for (LeafSubstructure<?> leafSubstructure : this.candidate) {
            mappedCandidate.add(leafSubstructure.getCopy());
        }

        // also create a copy for the full all-atom candidate
        List<LeafSubstructure<?>> mappedFullCandidate = new ArrayList<>();
        for (LeafSubstructure<?> leafSubstructure : this.candidate) {
            mappedFullCandidate.add(leafSubstructure.getCopy());
        }

        // remove all atoms and bonds not part of the alignment
        List<List<Atom>> atomsToBeRemoved = mappedCandidate.stream()
                .map(subStructure -> subStructure.getAllAtoms().stream()
                        .filter(atom -> !positionMapping.containsKey(atom.getIdentifier()))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        for (int i = 0; i < atomsToBeRemoved.size(); i++) {
            List<Atom> atoms = atomsToBeRemoved.get(i);
            for (Atom atom : atoms) {
                mappedCandidate.get(i).removeAtom(atom.getIdentifier());
            }
        }

        // apply superimposition to copy of the candidate
        for (LeafSubstructure<?> subStructure : mappedCandidate) {
            for (Atom atom : subStructure.getAllAtoms()) {
                Vector newPosition = mappedPositions.get(positionMapping.get(atom.getIdentifier()));
                // FIXME provide class for 3d vector superimposition
                Vector3D v3 = newPosition.as(Vector3D.class);
                atom.setPosition(v3);
            }
        }

        // apply superimposition to full all-atom copy of the candidate
        mappedFullCandidate.stream()
                .map(AtomContainer::getAllAtoms)
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
     * Determines the intersecting atoms for a {@link Pair} of {@link AtomContainer}s.
     *
     * @param pairListEntry the map entry for which intersecting atoms should be defined
     */
    private void defineIntersectingAtoms(Map.Entry<Pair<LeafSubstructure<?>>, Set<String>> pairListEntry) {

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
