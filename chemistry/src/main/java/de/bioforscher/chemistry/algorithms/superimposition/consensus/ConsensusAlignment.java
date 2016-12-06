package de.bioforscher.chemistry.algorithms.superimposition.consensus;

import de.bioforscher.chemistry.algorithms.superimposition.SubStructureSuperimposer;
import de.bioforscher.chemistry.algorithms.superimposition.SubstructureSuperimposition;
import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.chemistry.physical.atoms.AtomFilter;
import de.bioforscher.chemistry.physical.atoms.AtomName;
import de.bioforscher.chemistry.physical.atoms.RegularAtom;
import de.bioforscher.chemistry.physical.branches.BranchSubstructure;
import de.bioforscher.chemistry.physical.families.ResidueFamily;
import de.bioforscher.chemistry.physical.leafes.AtomContainer;
import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;
import de.bioforscher.core.utility.Pair;
import de.bioforscher.mathematics.graphs.trees.BinaryTree;
import de.bioforscher.mathematics.graphs.trees.BinaryTreeNode;
import de.bioforscher.mathematics.matrices.LabeledSymmetricMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author fk
 */
public class ConsensusAlignment {

    private static final Logger logger = LoggerFactory.getLogger(ConsensusAlignment.class);
    private static final Predicate<Atom> DEFAULT_ATOM_FILTER = AtomFilter.isArbitrary();

    private final List<List<LeafSubstructure<?, ?>>> inputStructures;
    private final List<BinaryTree<List<LeafSubstructure<?, ?>>>> consensusTrees;
    private final List<Double> alignmentTrace;
    private final List<Integer> alignmentCounts;
    private final Predicate<Atom> atomFilter;
    private double consensusScore;
    private int iterationCounter;
    private TreeMap<SubstructureSuperimposition, Pair<List<LeafSubstructure<?, ?>>>> alignments;
    private LabeledSymmetricMatrix<List<LeafSubstructure<?, ?>>> distanceMatrix;
    private List<BinaryTreeNode<List<LeafSubstructure<?, ?>>>> leaves;
    private List<LeafSubstructure<?, ?>> currentConsensus;

    public ConsensusAlignment(List<List<LeafSubstructure<?, ?>>> inputStructures) {
        this(inputStructures, DEFAULT_ATOM_FILTER);
    }

    public ConsensusAlignment(List<List<LeafSubstructure<?, ?>>> inputStructures, Predicate<Atom> atomFilter) {
//        this.inputStructures = inputStructures.stream().map(this::toContainer);
        this.inputStructures = inputStructures;
        this.atomFilter = atomFilter;
        // check if all substructures are of the same size
        if (inputStructures.stream()
                .map(List::size)
                .collect(Collectors.toSet()).size() != 1) {
            throw new IllegalArgumentException("all substructures must contain the same number of leaf structures to" +
                    "calculate a consensus alignment");
        }

        this.iterationCounter = 0;
        this.alignmentTrace = new ArrayList<>();
        this.alignmentCounts = new ArrayList<>();

        // initialize tree storage
        this.consensusTrees = new ArrayList<>();

        // calculate initial alignments
        calculateInitialAlignments();

        logger.info("{} initial alignment pairs were computed, in total we have to compute {} alignments",
                this.alignments.size(), this.alignments.size() * (this.inputStructures.size() - 1));

        // create initial tree leaves
        createTreeLeaves();

        // start calculating the consensus alignment
        calculateConsensusAlignment();
    }

    /**
     * Converts the given {@link LeafSubstructure}s to a container object that holds the consensus score.
     *
     * @param leafSubstructures the {@link LeafSubstructure}s to be converted
     * @return the container object
     */
    private ConsensusContainer toContainer(List<LeafSubstructure<?, ?>> leafSubstructures) {
        return new ConsensusContainer(leafSubstructures);
    }

    /**
     * Returns the top-level tree of this consensus alignment.
     *
     * @return the top-level tree
     */
    public BinaryTree<List<LeafSubstructure<?, ?>>> getTopConsensusTree() {
        return this.consensusTrees.get(this.consensusTrees.size() - 1);
    }

    /**
     * Returns all trees that were constructed during this alignment.
     *
     * @return all trees that were constructed
     */
    public List<BinaryTree<List<LeafSubstructure<?, ?>>>> getConsensusTrees() {
        return this.consensusTrees;
    }

    /**
     * Starts the calculation of the consensus alignments.
     */
    private void calculateConsensusAlignment() {
        // iteratively reduce candidates
        while (!this.alignments.isEmpty()) {
            findAndMergeClosestPair();
        }
    }

    /**
     * Finds and merges the closest pair of all input structures and recomputes the alignment.
     */
    public void findAndMergeClosestPair() {

        this.iterationCounter++;

        Pair<List<LeafSubstructure<?, ?>>> closestPair = this.alignments.firstEntry().getValue();
        SubstructureSuperimposition closestPairSuperimposition = this.alignments.firstKey();
        this.alignmentTrace.add(closestPairSuperimposition.getRmsd());
        this.alignmentCounts.add(this.inputStructures.size());

        logger.info("closest pair for iteration {} is {}", this.iterationCounter, closestPair);

        // sum up closest pair RMSD
        this.consensusScore += closestPairSuperimposition.getRmsd();

        createConsensus(this.alignments.firstEntry());
        updateAlignments(this.alignments.firstEntry());
    }

    /**
     * Removes the given pair from alignments and recomputes them.
     *
     * @param substructurePair the pairto be removed
     */
    private void updateAlignments(Map.Entry<SubstructureSuperimposition,
            Pair<List<LeafSubstructure<?, ?>>>> substructurePair) {

        Iterator<Map.Entry<SubstructureSuperimposition, Pair<List<LeafSubstructure<?, ?>>>>> alignmentsIterator =
                this.alignments.entrySet().iterator();

        while (alignmentsIterator.hasNext()) {

            Map.Entry<SubstructureSuperimposition, Pair<List<LeafSubstructure<?, ?>>>> currentAlignment =
                    alignmentsIterator.next();

            boolean referenceObservationMatches = currentAlignment.getValue().getFirst()
                    .equals(substructurePair.getValue().getFirst()) ||
                    currentAlignment.getValue().getFirst().equals(substructurePair.getValue().getSecond());
            boolean candidateObservationMatches = currentAlignment.getValue().getSecond()
                    .equals(substructurePair.getValue().getSecond()) ||
                    currentAlignment.getValue().getSecond().equals(substructurePair.getValue().getFirst());

            // remove alignment
            if (referenceObservationMatches || candidateObservationMatches) {
                alignmentsIterator.remove();
            }
        }

        // remove from input list
        this.inputStructures.removeIf(inputStructure -> inputStructure.equals(substructurePair.getValue().getFirst()));
        this.inputStructures.removeIf(inputStructure -> inputStructure.equals(substructurePair.getValue().getSecond()));

        // add new alignments
        for (List<LeafSubstructure<?, ?>> inputStructure : this.inputStructures) {

            // calculate superimposition
            SubstructureSuperimposition superimposition = SubStructureSuperimposer
                    .calculateSubstructureSuperimposition(this.currentConsensus, inputStructure);

            // store alignment
            Pair<List<LeafSubstructure<?, ?>>> alignmentPair = new Pair<>(this.currentConsensus, inputStructure);
            this.alignments.put(superimposition, alignmentPair);
            this.alignments.put(superimposition, alignmentPair);
        }

        // add consensusObservation to itemsetObservations
        this.inputStructures.add(this.currentConsensus);
    }

    /**
     * Creates a consensus representation of a {@link Pair} of lists of {@link LeafSubstructure}s by averaging
     * coordinates of each position of the list.
     *
     * @param substructurePair the pair to be merged
     */
    private void createConsensus(Map.Entry<SubstructureSuperimposition,
            Pair<List<LeafSubstructure<?, ?>>>> substructurePair) {

        List<LeafSubstructure<?, ?>> reference = substructurePair.getValue().getFirst();
        List<LeafSubstructure<?, ?>> candidate = substructurePair.getValue().getSecond();

//        Chain chainReference = new Chain(0);
//        reference.forEach(chainReference::addSubstructure);
//        Chain chainCandidate = new Chain(1);
//        candidate.forEach(chainCandidate::addSubstructure);
//        Structure structure = new Structure();
//        structure.addSubstructure(chainReference);
//        structure.addSubstructure(chainCandidate);
//        StructureViewer.structure = structure;
//        Application.launch(StructureViewer.class);

        Map<Pair<LeafSubstructure<?, ?>>, Set<AtomName>> perAtomAlignment = new LinkedHashMap<>();

        // create pairs of substructures to align
        IntStream.range(0, reference.size())
                .forEach(i -> perAtomAlignment.put(new Pair<>(reference.get(i), candidate.get(i)),
                        new HashSet<>()));

        // create atom subsets to align
        perAtomAlignment.entrySet()
                .forEach(this::defineIntersectingAtoms);

        // collect intersecting, filtered and sorted atoms
        List<List<Atom>> referenceAtoms = perAtomAlignment.entrySet().stream()
                .map(pairSetEntry -> pairSetEntry.getKey().getFirst().getAllAtoms().stream()
                        .filter(this.atomFilter)
                        .filter(atom -> pairSetEntry.getValue().contains(atom.getAtomName()))
                        .sorted(Comparator.comparing(Atom::getAtomNameString))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
        List<List<Atom>> candidateAtoms = perAtomAlignment.entrySet().stream()
                .map(pairSetEntry -> pairSetEntry.getKey().getSecond().getAllAtoms().stream()
                        .filter(this.atomFilter)
                        .filter(atom -> pairSetEntry.getValue().contains(atom.getAtomName()))
                        .sorted(Comparator.comparing(Atom::getAtomNameString))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        // create consensus substructures
        this.currentConsensus = new ArrayList<>();
        for (int i = 0; i < referenceAtoms.size(); i++) {
            List<Atom> currentReferenceAtoms = referenceAtoms.get(i);
            List<Atom> currentCandidateAtoms = candidateAtoms.get(i);
            // average atoms
            List<Atom> averagedAtoms = new ArrayList<>();
            for (int j = 0; j < currentReferenceAtoms.size(); j++) {
                Atom referenceAtom = currentReferenceAtoms.get(j);
                Atom candidateAtom = currentCandidateAtoms.get(j);
                // calculate average atom
                // TODO what is the identifier of the new atom?
                averagedAtoms.add(new RegularAtom(referenceAtom.getIdentifier(),
                        referenceAtom.getElement(), referenceAtom.getAtomNameString(),
                        referenceAtom.getPosition().add(candidateAtom.getPosition()).divide(2.0)));
            }
            // create new atom container
            // TODO what is the identifier and the type of the new atom container?
            AtomContainer<ResidueFamily> atomContainer = new AtomContainer<>(i, ResidueFamily.ALANINE);
            averagedAtoms.forEach(atomContainer::addNode);
            this.currentConsensus.add(atomContainer);
        }


        // create tree node
        BinaryTreeNode<List<LeafSubstructure<?, ?>>> leftNode;
        BinaryTreeNode<List<LeafSubstructure<?, ?>>> rightNode;
        BinaryTreeNode<List<LeafSubstructure<?, ?>>> consensusNode;

        // both nodes have to be leaves
        if (this.iterationCounter == 1) {
            leftNode = findLeave(substructurePair.getValue().getFirst());
            rightNode = findLeave(substructurePair.getValue().getSecond());
            consensusNode = new BinaryTreeNode<>(this.currentConsensus, leftNode, rightNode);
        } else {
            // try to find matching node in existing tree
            leftNode = findNode(substructurePair.getValue().getFirst());
            // if node not found in existing tree it has to be a leave
            if (leftNode == null) {
                leftNode = findLeave(substructurePair.getValue().getFirst());
            }
            // try to find matching node in existing trees
            rightNode = findNode(substructurePair.getValue().getSecond());
            // if node not found in existing tree it has to be a leave
            if (rightNode == null) {
                rightNode = findLeave(substructurePair.getValue().getSecond());
            }
            consensusNode = new BinaryTreeNode<>(this.currentConsensus, leftNode, rightNode);
        }

        // create and set consensus tree
        BinaryTree<List<LeafSubstructure<?, ?>>> consensusTree = new BinaryTree<>(consensusNode);
        // TODO do we need to store the consensus tree?
        //        consensusObservation.setConsensusTree(consensusTree);
        this.consensusTrees.add(consensusTree);

        // FIXME here we definitely need to store something
        // calculate consensus distances (half the RMSD of the consensus alignment)
//        itemAlignmentPair.getReferenceObservation().setConsensusDistance(itemAlignmentPair.getRMSD() / 2);
//        itemAlignmentPair.getCandidateObservation().setConsensusDistance(itemAlignmentPair.getRMSD() / 2);
    }

    /**
     * Returns the leave in the alignment tree that is equal to the given list of {@link LeafSubstructure}s.
     *
     * @param leafSubstructures the list of {@link LeafSubstructure} for which a leave in the alignment tree should be found
     * @return the corresponding leave
     */
    private BinaryTreeNode<List<LeafSubstructure<?, ?>>> findLeave(List<LeafSubstructure<?, ?>> leafSubstructures) {
        return this.leaves.stream()
                .filter(leave -> leave.getData().equals(leafSubstructures))
                .findFirst().orElseThrow(() -> new ConsensusException("failed during tree construction"));
    }

    /**
     * Searches in all existing alignment trees to find the node containing the given list of {@link LeafSubstructure}s.
     *
     * @param leafSubstructures the list of {@link LeafSubstructure}s for which a node should be found
     * @return the node containing the given list of {@link LeafSubstructure}s or null if it was not found
     */
    private BinaryTreeNode<List<LeafSubstructure<?, ?>>> findNode(List<LeafSubstructure<?, ?>> leafSubstructures) {
        BinaryTreeNode<List<LeafSubstructure<?, ?>>> nodeForObservation = null;
        for (BinaryTree<List<LeafSubstructure<?, ?>>> tree : this.consensusTrees) {
            nodeForObservation = tree.findNode(leafSubstructures);
            if (nodeForObservation != null) {
                break;
            }
        }
        // TODO use Optional here
        return nodeForObservation;
    }

    /**
     * Determines the intersecting atoms for a {@link Pair} of {@link BranchSubstructure}s.
     *
     * @param pairListEntry the map entry for which intersecting atoms should be defined
     */
    private void defineIntersectingAtoms(Map.Entry<Pair<LeafSubstructure<?, ?>>, Set<AtomName>> pairListEntry) {
        pairListEntry.getValue().addAll(pairListEntry.getKey().getFirst().getAllAtoms().stream()
                .filter(this.atomFilter)
                .map(Atom::getAtomName)
                .collect(Collectors.toSet()));
        pairListEntry.getValue().retainAll(pairListEntry.getKey().getSecond().getAllAtoms().stream()
                .filter(this.atomFilter)
                .map(Atom::getAtomName)
                .collect(Collectors.toSet()));
    }

    /**
     * Initially calculates the leaves of the alignment tree.
     */
    private void createTreeLeaves() {
        this.leaves = this.inputStructures.stream()
                .map(BinaryTreeNode::new)
                .collect(Collectors.toList());
    }


    /**
     * Initially calculates all pairwise alignments.
     */

    private void calculateInitialAlignments() {
        this.alignments = new TreeMap<>(Comparator.comparing(SubstructureSuperimposition::getRmsd));
        double[][] temporaryDistanceMatrix = new double[this.inputStructures.size()][this.inputStructures.size()];
        List<List<LeafSubstructure<?, ?>>> distanceMatrixLabels = new ArrayList<>();
//        // initially append first label (remove consensus score annotation by splitting at colon)
        distanceMatrixLabels.add(this.inputStructures.get(0));

        for (int i = 0; i < this.inputStructures.size() - 1; i++) {

            for (int j = i + 1; j < this.inputStructures.size(); j++) {

                List<LeafSubstructure<?, ?>> reference = this.inputStructures.get(i);
                List<LeafSubstructure<?, ?>> candidate = this.inputStructures.get(j);

                // calculate superimposition
                SubstructureSuperimposition superimposition = SubStructureSuperimposer
                        .calculateSubstructureSuperimposition(reference, candidate);

                // store alignment
                Pair<List<LeafSubstructure<?, ?>>> alignmentPair = new Pair<>(reference, candidate);
                this.alignments.put(superimposition, alignmentPair);

                // store distance matrix
                temporaryDistanceMatrix[i][j] = superimposition.getRmsd();
                temporaryDistanceMatrix[j][i] = superimposition.getRmsd();

            }
            // store label
            distanceMatrixLabels.add(this.inputStructures.get(i + 1));
        }
        this.distanceMatrix = new LabeledSymmetricMatrix<>(temporaryDistanceMatrix);
        this.distanceMatrix.setColumnLabels(distanceMatrixLabels);
    }
}
