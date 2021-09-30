package bio.singa.structure.algorithms.superimposition;

import bio.singa.core.utility.Pair;
import bio.singa.mathematics.algorithms.optimization.KuhnMunkres;
import bio.singa.mathematics.algorithms.superimposition.Superimposition;
import bio.singa.mathematics.algorithms.superimposition.VectorQuaternionSuperimposer;
import bio.singa.mathematics.algorithms.superimposition.VectorSuperimposition;
import bio.singa.mathematics.combinatorics.StreamPermutations;
import bio.singa.mathematics.matrices.LabeledMatrix;
import bio.singa.mathematics.matrices.LabeledRegularMatrix;
import bio.singa.mathematics.matrices.Matrix;
import bio.singa.mathematics.vectors.Vector;
import bio.singa.mathematics.vectors.Vector3D;
import bio.singa.structure.algorithms.superimposition.fit3d.representations.RepresentationScheme;
import bio.singa.structure.algorithms.superimposition.scores.SubstitutionMatrix;
import bio.singa.structure.model.interfaces.Atom;
import bio.singa.structure.model.interfaces.AtomContainer;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.interfaces.LeafSubstructureContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static bio.singa.structure.model.oak.StructuralEntityFilter.AtomFilter;

/**
 * Calculates the various {@link Superimposition}s for structures.
 * TODO migrate to builder pattern to avoid overloaded constructor
 *
 * @author fk
 */
public class SubstructureSuperimposer {

    private static final Logger logger = LoggerFactory.getLogger(SubstructureSuperimposition.class);
    private static final Predicate<Atom> DEFAULT_ATOM_FILTER = AtomFilter.isArbitrary();

    protected final List<LeafSubstructure> reference;
    protected final List<LeafSubstructure> candidate;

    private final Predicate<Atom> atomFilter;
    private final RepresentationScheme representationScheme;
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

        if (this.reference.size() != this.candidate.size() || this.reference.isEmpty())
            throw new IllegalArgumentException("Two lists of substructures cannot be superimposed if they " +
                    "differ in size.");
    }

    private SubstructureSuperimposer(List<LeafSubstructure> reference, List<LeafSubstructure> candidate,
                                     Predicate<Atom> atomFilter, RepresentationScheme representationScheme) {
        this.reference = reference;
        this.candidate = candidate;
        this.atomFilter = atomFilter;
        this.representationScheme = representationScheme;
    }

    protected SubstructureSuperimposer(List<LeafSubstructure> reference, List<LeafSubstructure> candidate) {
        this(reference, candidate, DEFAULT_ATOM_FILTER, null);
    }

    private SubstructureSuperimposer(List<LeafSubstructure> reference, List<LeafSubstructure> candidate,
                                     RepresentationScheme representationScheme) {
        this(reference, candidate, DEFAULT_ATOM_FILTER, representationScheme);
    }

    public static SubstructureSuperimposition calculateIdealSubstructureSuperimposition(List<LeafSubstructure> reference,
                                                                                        List<LeafSubstructure> candidate) {
        return new SubstructureSuperimposer(reference, candidate).calculateIdealSuperimposition();
    }

    public static SubstructureSuperimposition calculateIdealSubstructureSuperimposition(List<LeafSubstructure> reference,
                                                                                        List<LeafSubstructure> candidate,
                                                                                        Predicate<Atom> atomFilter) {
        return new SubstructureSuperimposer(reference, candidate, atomFilter, null).calculateIdealSuperimposition();
    }

    public static SubstructureSuperimposition calculateIdealSubstructureSuperimposition(List<LeafSubstructure> reference,
                                                                                        List<LeafSubstructure> candidate,
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


    public static SubstructureSuperimposition calculateKuhnMunkresSubstructureSuperimposition(List<LeafSubstructure> reference,
                                                                                              List<LeafSubstructure> candidate,
                                                                                              SubstitutionMatrix substitutionMatrix,
                                                                                              boolean considerExchanges) {
        return new SubstructureSuperimposer(reference, candidate).calculateKuhnMunkresSuperimposition(substitutionMatrix, considerExchanges);
    }

    public static SubstructureSuperimposition calculateKuhnMunkresSubstructureSuperimposition(List<LeafSubstructure> reference,
                                                                                              List<LeafSubstructure> candidate,
                                                                                              Predicate<Atom> atomFilter,
                                                                                              SubstitutionMatrix substitutionMatrix,
                                                                                              boolean considerExchanges) {
        return new SubstructureSuperimposer(reference, candidate, atomFilter, null).calculateKuhnMunkresSuperimposition(substitutionMatrix, considerExchanges);
    }

    public static SubstructureSuperimposition calculateKuhnMunkresSubstructureSuperimposition(List<LeafSubstructure> reference,
                                                                                              List<LeafSubstructure> candidate,
                                                                                              RepresentationScheme representationScheme,
                                                                                              SubstitutionMatrix substitutionMatrix,
                                                                                              boolean considerExchanges) {
        return new SubstructureSuperimposer(reference, candidate, DEFAULT_ATOM_FILTER, representationScheme).calculateKuhnMunkresSuperimposition(substitutionMatrix, considerExchanges);
    }

    public static SubstructureSuperimposition calculateKuhnMunkresSubstructureSuperimposition(LeafSubstructureContainer reference,
                                                                                              LeafSubstructureContainer candidate,
                                                                                              SubstitutionMatrix substitutionMatrix,
                                                                                              boolean considerExchanges) {
        return new SubstructureSuperimposer(reference, candidate).calculateKuhnMunkresSuperimposition(substitutionMatrix, considerExchanges);
    }

    public static SubstructureSuperimposition calculateKuhnMunkresSubstructureSuperimposition(LeafSubstructureContainer reference,
                                                                                              LeafSubstructureContainer candidate,
                                                                                              Predicate<Atom> atomFilter,
                                                                                              SubstitutionMatrix substitutionMatrix,
                                                                                              boolean considerExchanges) {
        return new SubstructureSuperimposer(reference, candidate, atomFilter, null).calculateKuhnMunkresSuperimposition(substitutionMatrix, considerExchanges);
    }

    public static SubstructureSuperimposition calculateKuhnMunkresSubstructureSuperimposition(LeafSubstructureContainer reference,
                                                                                              LeafSubstructureContainer candidate,
                                                                                              RepresentationScheme representationScheme,
                                                                                              SubstitutionMatrix substitutionMatrix,
                                                                                              boolean considerExchanges) {
        return new SubstructureSuperimposer(reference, candidate, DEFAULT_ATOM_FILTER, representationScheme).calculateKuhnMunkresSuperimposition(substitutionMatrix, considerExchanges);
    }


    public static SubstructureSuperimposition calculateSubstructureSuperimposition(List<LeafSubstructure> reference,
                                                                                   List<LeafSubstructure> candidate) throws SubstructureSuperimpositionException {
        return new SubstructureSuperimposer(reference, candidate).calculateSuperimposition();
    }

    public static SubstructureSuperimposition calculateSubstructureSuperimposition(List<LeafSubstructure> reference,
                                                                                   List<LeafSubstructure> candidate,
                                                                                   Predicate<Atom> atomFilter) throws SubstructureSuperimpositionException {
        return new SubstructureSuperimposer(reference, candidate, atomFilter, null).calculateSuperimposition();
    }

    public static SubstructureSuperimposition calculateSubstructureSuperimposition(List<LeafSubstructure> reference,
                                                                                   List<LeafSubstructure> candidate,
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

    /**
     * Finds the superimposition for a list of {@link LeafSubstructure} according to their input order
     *
     * @return the superimposition according to their order
     */
    protected SubstructureSuperimposition calculateSuperimposition() throws SubstructureSuperimpositionException {

        Pair<List<Atom>> alignmentAtoms = defineAtoms();
        List<Atom> referenceAtoms = alignmentAtoms.getFirst();
        List<Atom> candidateAtoms = alignmentAtoms.getSecond();

        if (referenceAtoms.isEmpty() || candidateAtoms.isEmpty()) {
            logger.error("reference {} against candidate {} has no compatible atom sets: {} {}", reference, candidate, referenceAtoms, candidateAtoms);
            throw new SubstructureSuperimpositionException("failed to collect per atom alignment sets, no compatible atoms");
        }

        // calculate superimposition
        VectorSuperimposition<Vector3D> vectorSuperimposition = VectorQuaternionSuperimposer.calculateVectorSuperimposition(
                referenceAtoms.stream()
                        .map(Atom::getPosition)
                        .collect(Collectors.toList()),
                candidateAtoms.stream()
                        .map(Atom::getPosition)
                        .collect(Collectors.toList()));

        // store result
        translation = vectorSuperimposition.getTranslation();
        rotation = vectorSuperimposition.getRotation();
        double rmsd = vectorSuperimposition.getRmsd();

        // store mapping of atoms to vectors
        List<Vector3D> mappedPositions = vectorSuperimposition.getMappedCandidate();
        Map<Integer, Integer> positionMapping = new HashMap<>();
        for (int i = 0; i < mappedPositions.size(); i++) {
            positionMapping.put(candidateAtoms.get(i).getAtomIdentifier(), i);
        }

        // use a copy of the candidate to apply the mapping after calculating the superimposition
        List<LeafSubstructure> mappedCandidate = new ArrayList<>();
        for (LeafSubstructure leafSubstructure : candidate) {
            mappedCandidate.add(leafSubstructure.getCopy());
        }

        // also create a copy for the full all-atom candidate
        List<LeafSubstructure> mappedFullCandidate = new ArrayList<>();
        for (LeafSubstructure leafSubstructure : candidate) {
            mappedFullCandidate.add(leafSubstructure.getCopy());
        }

        // remove all atoms and bonds not part of the alignment
        List<List<Atom>> atomsToBeRemoved = mappedCandidate.stream()
                .map(subStructure -> subStructure.getAllAtoms().stream()
                        .filter(atom -> !positionMapping.containsKey(atom.getAtomIdentifier()))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        for (int i = 0; i < atomsToBeRemoved.size(); i++) {
            List<Atom> atoms = atomsToBeRemoved.get(i);
            for (Atom atom : atoms) {
                mappedCandidate.get(i).removeAtom(atom.getAtomIdentifier());
            }
        }

        // apply superimposition to copy of the candidate
        for (LeafSubstructure subStructure : mappedCandidate) {
            for (Atom atom : subStructure.getAllAtoms()) {
                Vector newPosition = mappedPositions.get(positionMapping.get(atom.getAtomIdentifier()));
                atom.setPosition(newPosition.as(Vector3D.class));
            }
        }

        // apply superimposition to full all-atom copy of the candidate
        mappedFullCandidate.stream()
                .map(AtomContainer::getAllAtoms)
                .flatMap(List::stream)
                .forEach(atom -> atom.setPosition(rotation
                        .transpose()
                        .multiply(atom.getPosition())
                        .add(translation).as(Vector3D.class)));

        if (logger.isDebugEnabled()) {
            logger.debug("superimposed substructures with RMSD {}{}", rmsd, toAlignmentString(mappedCandidate, alignmentAtoms));
        }

        // compose superimposition container
        return new SubstructureSuperimposition(vectorSuperimposition.getRmsd(),
                translation,
                rotation,
                reference,
                candidate,
                mappedCandidate, mappedFullCandidate);
    }

    /**
     * Method to define the intersecting {@link Atom}s that should be used for the alignment. If this is not a fragment-based
     * superimposition, the pairing is made based on atom names.
     *
     * @return The paired {@link Atom}s that constitute the alignment.
     */
    protected Pair<List<Atom>> defineAtoms() {

        Map<Pair<LeafSubstructure>, Set<String>> perAtomAlignment = new LinkedHashMap<>();

        // create pairs of substructures to align
        IntStream.range(0, reference.size())
                .forEach(i -> perAtomAlignment.put(new Pair<>(reference.get(i), candidate.get(i)),
                        new HashSet<>()));

        // create atom subsets to align
        perAtomAlignment.entrySet()
                .forEach(this::defineIntersectingAtoms);

        boolean nonMatchingAtoms = perAtomAlignment.values().stream()
                .anyMatch(Set::isEmpty);

        if (nonMatchingAtoms) {
            logger.error("reference {} against candidate {} has no compatible atom strings: {} {}", reference, candidate);
            throw new SubstructureSuperimpositionException("failed to collect per atom alignment sets, no compatible atoms");
        }
        List<Atom> referenceAtoms;
        List<Atom> candidateAtoms;
        // no representation scheme is defined
        if (representationScheme == null) {
            // collect intersecting, filtered and sorted reference atoms
            referenceAtoms = perAtomAlignment.entrySet().stream()
                    .flatMap(pairSetEntry -> pairSetEntry.getKey().getFirst().getAllAtoms().stream()
                            .filter(atomFilter)
                            .filter(atom -> pairSetEntry.getValue().contains(atom.getAtomName()))
                            .sorted(Comparator.comparing(Atom::getAtomName)))
                    .collect(Collectors.toList());
            candidateAtoms = perAtomAlignment.entrySet().stream()
                    .flatMap(pairSetEntry -> pairSetEntry.getKey().getSecond().getAllAtoms().stream()
                            .filter(atomFilter)
                            .filter(atom -> pairSetEntry.getValue().contains(atom.getAtomName()))
                            .sorted(Comparator.comparing(Atom::getAtomName)))
                    .collect(Collectors.toList());
        } else {
            // reduce each leaf substructure to single representation scheme atoms
            referenceAtoms = perAtomAlignment.entrySet().stream()
                    .map(pairSetEntry -> pairSetEntry.getKey().getFirst())
                    .map(representationScheme::determineRepresentingAtom)
                    .collect(Collectors.toList());
            candidateAtoms = perAtomAlignment.entrySet().stream()
                    .map(pairSetEntry -> pairSetEntry.getKey().getSecond())
                    .map(representationScheme::determineRepresentingAtom)
                    .collect(Collectors.toList());
        }
        return new Pair<>(referenceAtoms, candidateAtoms);
    }

    /**
     * Determines the intersecting atoms for a {@link Pair} of {@link AtomContainer}s.
     *
     * @param pairListEntry the map entry for which intersecting atoms should be defined
     */
    private void defineIntersectingAtoms(Map.Entry<Pair<LeafSubstructure>, Set<String>> pairListEntry) {
        pairListEntry.getValue().addAll(pairListEntry.getKey().getFirst().getAllAtoms().stream()
                .filter(atomFilter)
                .map(Atom::getAtomName)
                .collect(Collectors.toSet()));
        pairListEntry.getValue().retainAll(pairListEntry.getKey().getSecond().getAllAtoms().stream()
                .filter(atomFilter)
                .map(Atom::getAtomName)
                .collect(Collectors.toSet()));
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
                candidate.toArray(new LeafSubstructure[0]))
                .parallel()
                .map(s -> s.collect(Collectors.toList()))
                .map(permutedCandidates -> {
                    try {
                        return new SubstructureSuperimposer(reference,
                                permutedCandidates, atomFilter, representationScheme)
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

    private SubstructureSuperimposition calculateKuhnMunkresSuperimposition(SubstitutionMatrix substitutionMatrix, boolean considerExchanges) {
        // create cost matrix based on substitution matrix
        double[][] costMatrixElements = new double[reference.size()][candidate.size()];
        for (int i = 0; i < reference.size(); i++) {
            for (int j = i; j < candidate.size(); j++) {
                LeafSubstructure referenceLeafSubstructure = reference.get(i);
                LeafSubstructure candidateLeafSubstructure = candidate.get(j);
                double substitutionCost = substitutionMatrix.toCostMatrix().getValueForLabel(
                        referenceLeafSubstructure.getFamily(), candidateLeafSubstructure.getFamily());
                // add penalties to all substructure pairs that should not be exchangeable
                if (considerExchanges) {
                    if (!referenceLeafSubstructure.getContainingFamilies().contains(candidateLeafSubstructure.getFamily())) {
                        substitutionCost = Double.MAX_VALUE;
                    }
                }
                costMatrixElements[i][j] = substitutionCost;
                costMatrixElements[j][i] = substitutionCost;
            }
        }
        LabeledMatrix<LeafSubstructure> costMatrix = new LabeledRegularMatrix<>(costMatrixElements);
        costMatrix.setRowLabels(reference);
        costMatrix.setColumnLabels(candidate);

        // calculate optimal assignments
        KuhnMunkres<LeafSubstructure> kuhnMunkres = new KuhnMunkres<>(costMatrix);
        List<Pair<LeafSubstructure>> assignedPairs = kuhnMunkres.getAssignedPairs();

        List<LeafSubstructure> updatedReference = assignedPairs.stream()
                .map(Pair::getFirst)
                .collect(Collectors.toList());
        List<LeafSubstructure> updatedCandidate = assignedPairs.stream()
                .map(Pair::getSecond)
                .collect(Collectors.toList());

        return new SubstructureSuperimposer(updatedReference, updatedCandidate, atomFilter, representationScheme)
                .calculateSuperimposition();
    }

    private String toAlignmentString(List<LeafSubstructure> mappedCandidate, Pair<List<Atom>> alignmentAtoms) {

        StringJoiner referenceNameJoiner = new StringJoiner("|", "|", "|");
        reference.forEach(referenceLeafSubstructure ->
                referenceNameJoiner.add(String.format("%-100s", referenceLeafSubstructure.toString())));

        StringJoiner atomNameJoiner = new StringJoiner("|", "|", "|");
        for (int i = 0; i < reference.size(); i++) {
            LeafSubstructure referenceLeafSubstructure = reference.get(i);
            LeafSubstructure candidateLeafSubstructure = candidate.get(i);
            List<Atom> referenceAtoms = alignmentAtoms.getFirst();
            referenceAtoms.retainAll(referenceLeafSubstructure.getAllAtoms());
            List<Atom> candidateAtoms = alignmentAtoms.getSecond();
            candidateAtoms.retainAll(candidateLeafSubstructure.getAllAtoms());
            StringJoiner atomStringJoiner = new StringJoiner(" - ");
            for (int j = 0; j < referenceAtoms.size(); j++) {
                atomStringJoiner.add(referenceAtoms.get(j).getAtomName() + "." + candidateAtoms.get(j).getAtomName());
            }
            atomNameJoiner.add(String.format("%-100s", atomStringJoiner.toString()));
        }
//        if (representationScheme == null) {
//            alignmentAtoms.forEach(atomNames -> atomNameJoiner
//                    .add(String.format("%-50s", atomNames.stream()
//                            .sorted()
//                            .collect(Collectors.joining("-")))));
//        } else {
//            perAtomAlignment.values().forEach(atomNames -> atomNameJoiner
//                    .add(String.format("%-50s", Stream.of(RepresentationSchemeType.values())
//                            .filter(representationSchemeType -> representationSchemeType.getCompatibleRepresentationScheme()
//                                    .isInstance(representationScheme))
//                            .findAny().orElse(RepresentationSchemeType.ALPHA_CARBON))));
//        }
        StringJoiner candidateNameJoiner = new StringJoiner("|", "|", "|");
        mappedCandidate.forEach(candidateLeafSubstructure ->
                candidateNameJoiner.add(String.format("%-100s", candidateLeafSubstructure.toString())));
        StringJoiner alignmentJoiner = new StringJoiner("\n", "\n", "");
        alignmentJoiner.add(referenceNameJoiner.toString());
        alignmentJoiner.add(atomNameJoiner.toString());
        alignmentJoiner.add(candidateNameJoiner.toString());
        return alignmentJoiner.toString();
    }
}
