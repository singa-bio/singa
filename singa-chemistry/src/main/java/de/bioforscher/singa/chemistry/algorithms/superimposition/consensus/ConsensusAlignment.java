package de.bioforscher.singa.chemistry.algorithms.superimposition.consensus;

import de.bioforscher.singa.chemistry.algorithms.superimposition.SubStructureSuperimposer;
import de.bioforscher.singa.chemistry.algorithms.superimposition.SubstructureSuperimposition;
import de.bioforscher.singa.chemistry.parser.pdb.structures.StructureWriter;
import de.bioforscher.singa.chemistry.physical.atoms.Atom;
import de.bioforscher.singa.chemistry.physical.atoms.RegularAtom;
import de.bioforscher.singa.chemistry.physical.branches.BranchSubstructure;
import de.bioforscher.singa.chemistry.physical.branches.StructuralMotif;
import de.bioforscher.singa.chemistry.physical.families.AminoAcidFamily;
import de.bioforscher.singa.chemistry.physical.leafes.AtomContainer;
import de.bioforscher.singa.chemistry.physical.leafes.LeafSubstructure;
import de.bioforscher.singa.chemistry.physical.model.LeafIdentifier;
import de.bioforscher.singa.core.utility.Pair;
import de.bioforscher.singa.mathematics.graphs.trees.BinaryTree;
import de.bioforscher.singa.mathematics.graphs.trees.BinaryTreeNode;
import de.bioforscher.singa.mathematics.matrices.LabeledSymmetricMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static de.bioforscher.singa.chemistry.physical.model.StructuralEntityFilter.AtomFilter;

/**
 * A consensus alignment of same-sized {@link StructuralMotif}s can be used to cluster them according their
 * geometric similarity in a multi-structure alignment manner as described in:
 * <p>
 * <pre>to be published</pre>
 *
 * TODO this has to be rewritten as soon as equals of {@link StructuralMotif} is implemented
 *
 * <b>NOTE:</b> Copies of given {@link StructuralMotif}s will be used, so original structures are not altered.
 *
 * @author fk
 */
public class ConsensusAlignment {

    private static final Logger logger = LoggerFactory.getLogger(ConsensusAlignment.class);
    private static final double DEFAULT_CLUSTER_CUTOFF_VALUE = 0.5;
    private static final boolean DEFAULT_ALIGN_WITHIN_CLUSTERS = true;
    private static final Predicate<Atom> DEFAULT_ATOM_FILTER = AtomFilter.isArbitrary();
    private static final boolean DEFAULT_IDEAL_SUPERIMPOSITION = false;

    private final List<ConsensusContainer> inputStructures;
    private final boolean idealSuperimposition;
    private final List<BinaryTree<ConsensusContainer>> consensusTrees;
    private final List<Double> alignmentTrace;
    private final List<Integer> alignmentCounts;
    private final Predicate<Atom> atomFilter;
    private boolean alignWithinClusters;
    private double consensusScore;
    private int iterationCounter;
    private TreeMap<SubstructureSuperimposition, Pair<ConsensusContainer>> alignments;
    private LabeledSymmetricMatrix<ConsensusContainer> distanceMatrix;
    private List<BinaryTreeNode<ConsensusContainer>> leaves;
    private ConsensusContainer currentConsensus;
    private double clusterCutoffValue;
    private List<BinaryTree<ConsensusContainer>> clusters;

    public ConsensusAlignment(List<StructuralMotif> inputStructures) {
        this(inputStructures, DEFAULT_CLUSTER_CUTOFF_VALUE, DEFAULT_ALIGN_WITHIN_CLUSTERS, DEFAULT_ATOM_FILTER, DEFAULT_IDEAL_SUPERIMPOSITION);
    }

    public ConsensusAlignment(List<StructuralMotif> inputStructures, double clusterCutoffValue) {
        this(inputStructures, clusterCutoffValue, DEFAULT_ALIGN_WITHIN_CLUSTERS, DEFAULT_ATOM_FILTER, DEFAULT_IDEAL_SUPERIMPOSITION);
    }

    public ConsensusAlignment(List<StructuralMotif> inputStructures, double clusterCutoffValue,
                              boolean alignWithinClusters,
                              Predicate<Atom> atomFilter, boolean idealSuperimposition) {

        // convert given input structures to the data model
        this.inputStructures = inputStructures.stream()
                .map(ConsensusAlignment::toContainer)
                .collect(Collectors.toList());

        logger.info("consensus alignment initialized with {} structures", this.inputStructures.size());

        this.clusterCutoffValue = clusterCutoffValue;
        this.alignWithinClusters = alignWithinClusters;
        this.atomFilter = atomFilter;
        this.idealSuperimposition = idealSuperimposition;
        // check if all substructures are of the same size
        if (inputStructures.stream()
                .map(StructuralMotif::getLeafSubstructures)
                .map(List::size)
                .collect(Collectors.toSet()).size() != 1) {
            throw new IllegalArgumentException("all substructures must contain the same number of leaf structures to " +
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
        // split top level tree
        splitTopLevelTree();
        // align within clusters if specified
        if (this.alignWithinClusters) {
            alignWithinClusters();
        }
    }

    /**
     * Copies and converts the given {@link StructuralMotif}s to a container object that holds the consensus score.
     *
     * @param structuralMotif the {@link StructuralMotif}s to be converted
     * @return the container object
     */
    private static ConsensusContainer toContainer(StructuralMotif structuralMotif) {
        // use copy of given structural motif
        return new ConsensusContainer(structuralMotif.getCopy());
    }

    public List<BinaryTree<ConsensusContainer>> getClusters() {
        return this.clusters;
    }

    /**
     * Writes the created clusters to the specified {@link Path}.
     *
     * @param outputPath the desired output {@link Path}
     * @throws IOException If the path cannot be written to.
     */
    public void writeClusters(Path outputPath) throws IOException {
        logger.info("writing {} clusters to {}", this.clusters.size(), outputPath);
        Files.createDirectories(outputPath);
        for (int i = 0; i < this.clusters.size(); i++) {
            String clusterBaseLocation = "cluster_" + (i + 1) + "/";
            BinaryTree<ConsensusContainer> currentCluster = this.clusters.get(i);
            // write consensus
            if (currentCluster.getLeafNodes().size() > 1) {
                StructureWriter.writeLeafSubstructures(currentCluster.getRoot().getData().getStructuralMotif().getLeafSubstructures(),
                        outputPath.resolve(clusterBaseLocation + "consensus_" + (i + 1) + ".pdb"));
            }
            // write leaves
            for (BinaryTreeNode<ConsensusContainer> leafNode : currentCluster.getLeafNodes()) {
                if (leafNode.getData().getSuperimposition() != null) {
                    StructureWriter.writeLeafSubstructures(leafNode.getData().getSuperimposition().getMappedFullCandidate(),
                            outputPath.resolve(clusterBaseLocation + leafNode.getData().toString() + ".pdb"));
                } else {
                    StructureWriter.writeLeafSubstructures(leafNode.getData().getStructuralMotif().getLeafSubstructures(),
                            outputPath.resolve(clusterBaseLocation + leafNode.getData().toString() + ".pdb"));
                }
            }
        }
    }

    /**
     * Aligns all leaf nodes to the root of the tree (observations against consensus).
     */
    private void alignWithinClusters() {
        // skip one-trees
        this.clusters.stream().filter(cluster -> cluster.size() > 1).forEach(cluster -> {
            // reference is always the root consensus
            ConsensusContainer reference = cluster.getRoot().getData();
            cluster.getLeafNodes().stream().map(BinaryTreeNode::getData).forEach(consensusContainer -> {
                SubstructureSuperimposition superimposition = this.idealSuperimposition ?
                        SubStructureSuperimposer.calculateIdealSubstructureSuperimposition(
                                reference.getStructuralMotif().getLeafSubstructures(),
                                consensusContainer.getStructuralMotif().getLeafSubstructures()) :
                        SubStructureSuperimposer.calculateSubstructureSuperimposition(
                                reference.getStructuralMotif().getLeafSubstructures(),
                                consensusContainer.getStructuralMotif().getLeafSubstructures());
                consensusContainer.setSuperimposition(superimposition);
            });
        });
    }

    /**
     * Splits the top-level tree according to the cutoff value.
     */
    private void splitTopLevelTree() {
        // create list where all trees are stored
        this.clusters = new ArrayList<>();
        this.clusters.add(getTopConsensusTree());
        // start iterating over the list
        ListIterator<BinaryTree<ConsensusContainer>> clustersIterator = this.clusters.listIterator();
        while (clustersIterator.hasNext()) {
            // get current node and child nodes
            BinaryTreeNode<ConsensusContainer> currentNode = clustersIterator.next().getRoot();
            BinaryTreeNode<ConsensusContainer> leftNode = currentNode.getLeft();
            BinaryTreeNode<ConsensusContainer> rightNode = currentNode.getRight();

            // try to determine distances
            double leftDistance;
            if (leftNode != null) {
                leftDistance = leftNode.getData().getConsensusDistance();
            } else {
                leftDistance = 0.0;
            }
            double rightDistance;
            if (rightNode != null) {
                rightDistance = rightNode.getData().getConsensusDistance();
            } else {
                rightDistance = 0.0;
            }

            // split tree if distance exceeds cutoff value
            if (leftDistance > this.clusterCutoffValue || rightDistance > this.clusterCutoffValue) {

                // remove parent tree
                clustersIterator.remove();

                // after removing parent tree add new trees and shift pointer
                clustersIterator.add(new BinaryTree<>(currentNode.getLeft()));
                clustersIterator.previous();
                clustersIterator.add(new BinaryTree<>(currentNode.getRight()));
                clustersIterator.previous();
            }
        }
    }

    /**
     * Returns the top-level tree of this consensus alignment.
     *
     * @return the top-level tree
     */
    public BinaryTree<ConsensusContainer> getTopConsensusTree() {
        return this.consensusTrees.get(this.consensusTrees.size() - 1);
    }

    /**
     * Returns all trees that were constructed during this alignment.
     *
     * @return all trees that were constructed
     */
    public List<BinaryTree<ConsensusContainer>> getConsensusTrees() {
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
    private void findAndMergeClosestPair() {

        this.iterationCounter++;

        Pair<ConsensusContainer> closestPair = this.alignments.firstEntry().getValue();
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
     * Removes the given pair from the alignments and recomputes them.
     *
     * @param substructurePair the pair to be removed
     */
    private void updateAlignments(Map.Entry<SubstructureSuperimposition,
            Pair<ConsensusContainer>> substructurePair) {

        Iterator<Map.Entry<SubstructureSuperimposition, Pair<ConsensusContainer>>> alignmentsIterator =
                this.alignments.entrySet().iterator();

        while (alignmentsIterator.hasNext()) {

            Map.Entry<SubstructureSuperimposition, Pair<ConsensusContainer>> currentAlignment =
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
        for (ConsensusContainer inputStructure : this.inputStructures) {

            // calculate superimposition
            SubstructureSuperimposition superimposition = this.idealSuperimposition ?
                    SubStructureSuperimposer.calculateIdealSubstructureSuperimposition(
                            this.currentConsensus.getStructuralMotif().getLeafSubstructures(),
                            inputStructure.getStructuralMotif().getLeafSubstructures()) :
                    SubStructureSuperimposer.calculateSubstructureSuperimposition(
                            this.currentConsensus.getStructuralMotif().getLeafSubstructures(),
                            inputStructure.getStructuralMotif().getLeafSubstructures());

            // store alignment
            Pair<ConsensusContainer> alignmentPair = new Pair<>(this.currentConsensus, inputStructure);
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
            Pair<ConsensusContainer>> substructurePair) {

        List<LeafSubstructure<?, ?>> reference = substructurePair.getValue().getFirst().getStructuralMotif().getLeafSubstructures();
        List<LeafSubstructure<?, ?>> candidate = substructurePair.getValue().getSecond().getStructuralMotif().getLeafSubstructures();

//        Chain chainReference = new Chain(0);
//        reference.forEach(chainReference::addSubstructure);
//        Chain chainCandidate = new Chain(1);
//        candidate.forEach(chainCandidate::addSubstructure);
//        Structure structure = new Structure();
//        structure.addSubstructure(chainReference);
//        structure.addSubstructure(chainCandidate);
//        StructureViewer.structure = structure;
//        Application.launch(StructureViewer.class);

        Map<Pair<LeafSubstructure<?, ?>>, Set<String>> perAtomAlignment = new LinkedHashMap<>();

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
                        .filter(atom -> pairSetEntry.getValue().contains(atom.getAtomNameString()))
                        .sorted(Comparator.comparing(Atom::getAtomNameString))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
        List<List<Atom>> candidateAtoms = perAtomAlignment.entrySet().stream()
                .map(pairSetEntry -> pairSetEntry.getKey().getSecond().getAllAtoms().stream()
                        .filter(this.atomFilter)
                        .filter(atom -> pairSetEntry.getValue().contains(atom.getAtomNameString()))
                        .sorted(Comparator.comparing(Atom::getAtomNameString))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        // create consensus substructures
        List<LeafSubstructure<?, ?>> consensusLeaveSubstructures = new ArrayList<>();
        int atomCounter = 1;
        int leaveCounter = 1;
        for (int i = 0; i < referenceAtoms.size(); i++) {
            List<Atom> currentReferenceAtoms = referenceAtoms.get(i);
            List<Atom> currentCandidateAtoms = candidateAtoms.get(i);
            // average atoms
            List<Atom> averagedAtoms = new ArrayList<>();
            for (int j = 0; j < currentReferenceAtoms.size(); j++) {
                Atom referenceAtom = currentReferenceAtoms.get(j);
                Atom candidateAtom = currentCandidateAtoms.get(j);
                // calculate average atom
                averagedAtoms.add(new RegularAtom(atomCounter,
                        referenceAtom.getElement(), referenceAtom.getAtomNameString(),
                        referenceAtom.getPosition().add(candidateAtom.getPosition()).divide(2.0)));
                atomCounter++;
            }
            // create new atom container
            AtomContainer<AminoAcidFamily> atomContainer = new AtomContainer<>(new LeafIdentifier(leaveCounter),
                    AminoAcidFamily.ALANINE,
                    AminoAcidFamily.ALANINE.getThreeLetterCode());
            averagedAtoms.forEach(atomContainer::addNode);
            consensusLeaveSubstructures.add(atomContainer);
            leaveCounter++;
        }
        this.currentConsensus = new ConsensusContainer(StructuralMotif.fromLeafs(0, consensusLeaveSubstructures));


        // create tree node
        BinaryTreeNode<ConsensusContainer> leftNode;
        BinaryTreeNode<ConsensusContainer> rightNode;
        BinaryTreeNode<ConsensusContainer> consensusNode;

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
        BinaryTree<ConsensusContainer> consensusTree = new BinaryTree<>(consensusNode);
        this.currentConsensus.setConsensusTree(consensusTree);
        this.consensusTrees.add(consensusTree);

        // calculate consensus distances (half the RMSD of the consensus alignment)
        // FIXME something really strange happens here when modifying the values of the map they are not reflected to the tree
        consensusTree.getRoot().getLeft().getData().addToConsensusDistance(substructurePair.getKey().getRmsd() / 2);
        consensusTree.getRoot().getRight().getData().addToConsensusDistance(substructurePair.getKey().getRmsd() / 2);
        //        substructurePair.getValue().getFirst().addToConsensusDistance(substructurePair.getKey().getRmsd() / 2);
        //        substructurePair.getValue().getSecond().addToConsensusDistance(substructurePair.getKey().getRmsd() / 2);
    }

    /**
     * Returns the leave in the alignment tree that is equal to a given {@link ConsensusContainer}.
     *
     * @param consensusContainer the {@link ConsensusContainer} for which a leave in the alignment tree should be found
     * @return the corresponding leave
     */
    private BinaryTreeNode<ConsensusContainer> findLeave(ConsensusContainer consensusContainer) {
        return this.leaves.stream()
                .filter(leave -> leave.getData().equals(consensusContainer))
                .findFirst().orElseThrow(() -> new ConsensusException("failed during tree construction"));
    }

    /**
     * Searches in all existing alignment trees to find the node containing the given list of {@link LeafSubstructure}s.
     *
     * @param consensusContainer the list of {@link LeafSubstructure}s for which a node should be found
     * @return the node containing the given list of {@link LeafSubstructure}s or null if it was not found
     */
    private BinaryTreeNode<ConsensusContainer> findNode(ConsensusContainer consensusContainer) {
        BinaryTreeNode<ConsensusContainer> nodeForObservation = null;
        for (BinaryTree<ConsensusContainer> tree : this.consensusTrees) {
            nodeForObservation = tree.findNode(consensusContainer);
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
        List<ConsensusContainer> distanceMatrixLabels = new ArrayList<>();
        // initially append first label
        distanceMatrixLabels.add(this.inputStructures.get(0));

        for (int i = 0; i < this.inputStructures.size() - 1; i++) {

            for (int j = i + 1; j < this.inputStructures.size(); j++) {

                StructuralMotif reference = this.inputStructures.get(i).getStructuralMotif();
                StructuralMotif candidate = this.inputStructures.get(j).getStructuralMotif();

                // calculate superimposition
                SubstructureSuperimposition superimposition = this.idealSuperimposition ?
                        SubStructureSuperimposer.calculateIdealSubstructureSuperimposition(reference, candidate) :
                        SubStructureSuperimposer.calculateSubstructureSuperimposition(reference, candidate);

                // store alignment
                Pair<ConsensusContainer> alignmentPair = new Pair<>(new ConsensusContainer(reference),
                        new ConsensusContainer(candidate));
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