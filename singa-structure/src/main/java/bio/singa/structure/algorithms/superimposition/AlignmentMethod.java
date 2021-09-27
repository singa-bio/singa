package bio.singa.structure.algorithms.superimposition;

import bio.singa.core.utility.Pair;
import bio.singa.structure.algorithms.superimposition.consensus.ConsensusContainer;
import bio.singa.structure.algorithms.superimposition.fit3d.representations.RepresentationScheme;
import bio.singa.structure.algorithms.superimposition.fit3d.representations.RepresentationSchemeFactory;
import bio.singa.structure.algorithms.superimposition.fit3d.representations.RepresentationSchemeType;
import bio.singa.structure.model.families.AminoAcidFamily;
import bio.singa.structure.model.families.StructuralFamily;
import bio.singa.structure.model.oak.LeafIdentifier;
import bio.singa.structure.model.interfaces.Atom;
import bio.singa.structure.model.interfaces.AtomContainer;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.interfaces.LeafSubstructureContainer;
import bio.singa.structure.model.oak.LeafSubstructureFactory;
import bio.singa.structure.model.oak.OakAtom;
import bio.singa.structure.model.oak.OakLeafSubstructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author cl
 */
public class AlignmentMethod {

    private static final Logger logger = LoggerFactory.getLogger(AlignmentMethod.class);

    private boolean idealSuperimposition;
    private Predicate<Atom> atomFilter;
    private RepresentationScheme representationScheme;

    public AlignmentMethod() {
    }

    public boolean isIdealSuperimposition() {
        return idealSuperimposition;
    }

    public void setIdealSuperimposition(boolean idealSuperimposition) {
        this.idealSuperimposition = idealSuperimposition;
    }

    public Predicate<Atom> getAtomFilter() {
        return atomFilter;
    }

    public void setAtomFilter(Predicate<Atom> atomFilter) {
        this.atomFilter = atomFilter;
    }

    public RepresentationScheme getRepresentationScheme() {
        return representationScheme;
    }

    public void setRepresentationScheme(RepresentationScheme representationScheme) {
        this.representationScheme = representationScheme;
    }

    public void setRepresentationSchemeFromType(RepresentationSchemeType representationSchemeType) {
        if (representationSchemeType != null) {
            logger.info("using representation scheme {}", representationSchemeType);
            representationScheme = RepresentationSchemeFactory.createRepresentationScheme(representationSchemeType);
        }
    }

    protected SubstructureSuperimposition superimpose(ConsensusContainer reference, ConsensusContainer consensusContainer) {
        return superimpose(reference.getStructuralMotif(), consensusContainer.getStructuralMotif());
    }

    protected SubstructureSuperimposition superimpose(LeafSubstructureContainer reference, LeafSubstructureContainer candidate) {
        SubstructureSuperimposition superimposition;
        if (representationScheme == null) {
            superimposition = idealSuperimposition ?
                    SubstructureSuperimposer.calculateIdealSubstructureSuperimposition(
                            reference,
                            candidate, atomFilter) :
                    SubstructureSuperimposer.calculateSubstructureSuperimposition(
                            reference.getAllLeafSubstructures(),
                            candidate.getAllLeafSubstructures(), atomFilter);
        } else {
            superimposition = idealSuperimposition ?
                    SubstructureSuperimposer.calculateIdealSubstructureSuperimposition(
                            reference,
                            candidate, representationScheme) :
                    SubstructureSuperimposer.calculateSubstructureSuperimposition(
                            reference.getAllLeafSubstructures(),
                            candidate.getAllLeafSubstructures(), representationScheme);
        }
        return superimposition;
    }

    public List<LeafSubstructure<?>> determineConsensus(Map.Entry<SubstructureSuperimposition, Pair<ConsensusContainer>> substructurePair) {
        List<LeafSubstructure<?>> reference = substructurePair.getValue().getFirst().getStructuralMotif().getAllLeafSubstructures();
        List<LeafSubstructure<?>> candidate = substructurePair.getKey().getMappedFullCandidate();

        Map<Pair<LeafSubstructure>, Set<String>> perAtomAlignment = new LinkedHashMap<>();

        // create pairs of substructures to align
        IntStream.range(0, reference.size())
                .forEach(i -> perAtomAlignment.put(new Pair<>(reference.get(i), candidate.get(i)),
                        new HashSet<>()));

        // create atom subsets to align
        perAtomAlignment.entrySet()
                .forEach(this::defineIntersectingAtoms);

        // collect intersecting, filtered and sorted atoms
        List<List<Atom>> referenceAtoms;
        List<List<Atom>> candidateAtoms;
        if (representationScheme == null) {
            referenceAtoms = perAtomAlignment.entrySet().stream()
                    .map(pairSetEntry -> pairSetEntry.getKey().getFirst().getAllAtoms().stream()
                            .filter(atomFilter)
                            .filter(atom -> pairSetEntry.getValue().contains(atom.getAtomName()))
                            .sorted(Comparator.comparing(Atom::getAtomName))
                            .collect(Collectors.toList()))
                    .collect(Collectors.toList());
            candidateAtoms = perAtomAlignment.entrySet().stream()
                    .map(pairSetEntry -> pairSetEntry.getKey().getSecond().getAllAtoms().stream()
                            .filter(atomFilter)
                            .filter(atom -> pairSetEntry.getValue().contains(atom.getAtomName()))
                            .sorted(Comparator.comparing(Atom::getAtomName))
                            .collect(Collectors.toList()))
                    .collect(Collectors.toList());
        } else {
            referenceAtoms = perAtomAlignment.keySet().stream()
                    .map(strings -> {
                        List<Atom> atomList = new ArrayList<>();
                        atomList.add(representationScheme.determineRepresentingAtom(strings.getFirst()));
                        return atomList;
                    }).collect(Collectors.toList());
            candidateAtoms = perAtomAlignment.keySet().stream()
                    .map(strings -> {
                        List<Atom> atomList = new ArrayList<>();
                        atomList.add(representationScheme.determineRepresentingAtom(strings.getSecond()));
                        return atomList;
                    })
                    .collect(Collectors.toList());
        }

        // create consensus substructures
        List<LeafSubstructure<?>> consensusLeaveSubstructures = new ArrayList<>();
        int atomCounter = 1;
        int leafCounter = 1;
        for (int i = 0; i < referenceAtoms.size(); i++) {
            List<Atom> currentReferenceAtoms = referenceAtoms.get(i);
            List<Atom> currentCandidateAtoms = candidateAtoms.get(i);
            // average atoms
            List<OakAtom> averagedAtoms = new ArrayList<>();
            for (int j = 0; j < currentReferenceAtoms.size(); j++) {
                Atom referenceAtom = currentReferenceAtoms.get(j);
                Atom candidateAtom = currentCandidateAtoms.get(j);
                // calculate average atom
                averagedAtoms.add(new OakAtom(atomCounter,
                        referenceAtom.getElement(), referenceAtom.getAtomName(),
                        referenceAtom.getPosition().add(candidateAtom.getPosition()).divide(2.0)));
                atomCounter++;
            }

            // try to retain family notation for each consensus leaf substructure if possible
            StructuralFamily family = null;
            if (reference.get(i).getFamily().equals(candidate.get(i).getFamily())) {
                family = candidate.get(i).getFamily();
            }
            // default to unknown if family type differs
            if (family == null) {
                family = AminoAcidFamily.UNKNOWN;
            }

            // create new atom container
            OakLeafSubstructure<?> leafSubstructure = LeafSubstructureFactory.createLeafSubstructure(new LeafIdentifier(leafCounter), family);
            averagedAtoms.forEach(leafSubstructure::addAtom);
            consensusLeaveSubstructures.add(leafSubstructure);
            leafCounter++;
        }
        return consensusLeaveSubstructures;
    }

    /**
     * Determines the intersecting atoms for a {@link Pair} of {@link AtomContainer}s.
     *
     * @param pairListEntry the map entry for which intersecting atoms should be defined
     */
    private void defineIntersectingAtoms(Map.Entry<Pair<LeafSubstructure>, Set<String>> pairListEntry) {
        if (representationScheme == null) {
            pairListEntry.getValue().addAll(pairListEntry.getKey().getFirst().getAllAtoms().stream()
                    .filter(atomFilter)
                    .map(Atom::getAtomName)
                    .collect(Collectors.toSet()));
            pairListEntry.getValue().retainAll(pairListEntry.getKey().getSecond().getAllAtoms().stream()
                    .filter(atomFilter)
                    .map(Atom::getAtomName)
                    .collect(Collectors.toSet()));
        } else {
            pairListEntry.getValue().add(representationScheme.determineRepresentingAtom(pairListEntry.getKey().getFirst()).getAtomName());
            pairListEntry.getValue().add(representationScheme.determineRepresentingAtom(pairListEntry.getKey().getSecond()).getAtomName());
        }
    }

}
