package bio.singa.structure.algorithms.superimposition.consensus;

import bio.singa.core.utility.Pair;
import bio.singa.mathematics.graphs.trees.BinaryTree;
import bio.singa.mathematics.graphs.trees.BinaryTreeNode;
import bio.singa.mathematics.matrices.LabeledSymmetricMatrix;
import bio.singa.structure.algorithms.superimposition.AlignmentMethod;
import bio.singa.structure.algorithms.superimposition.SubstructureSuperimposition;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.general.StructuralMotif;
import bio.singa.structure.io.general.StructureWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A consensus alignment of same-sized {@link StructuralMotif}s can be used to cluster them according their geometric
 * similarity in a multi-structure alignment manner as described in:
 * <pre>to be published</pre>
 * <b>NOTE:</b> Copies of given {@link StructuralMotif}s will be used, so original structures are not altered.
 *
 * @author fk
 */
public class ConsensusAlignment extends AlignmentMethod {

    private static final Logger logger = LoggerFactory.getLogger(ConsensusAlignment.class);

    private final List<ConsensusContainer> input;

    private final List<BinaryTree<ConsensusContainer>> consensusTrees;
    private final List<Double> alignmentTrace;
    private final List<Integer> alignmentCounts;

    private final boolean alignWithinClusters;
    private final double clusterCutoff;

    private double consensusScore;
    private int iterationCounter;
    private TreeMap<SubstructureSuperimposition, Pair<ConsensusContainer>> alignments;
    private LabeledSymmetricMatrix<ConsensusContainer> distanceMatrix;
    private List<BinaryTreeNode<ConsensusContainer>> leaves;
    private ConsensusContainer currentConsensus;
    private List<BinaryTree<ConsensusContainer>> clusters;

    ConsensusAlignment(ConsensusBuilder.Builder builder) {

        // convertToSpheres given input structures to data model
        input = builder.structuralMotifs.stream()
                .map(ConsensusAlignment::toContainer)
                .collect(Collectors.toList());

        logger.info("consensus alignment initialized with {} structures", input.size());

        clusterCutoff = builder.clusterCutoff;
        alignWithinClusters = builder.alignWithinClusters;

        setAtomFilter(builder.atomFilter);

        // create representation scheme if given
        setRepresentationSchemeFromType(builder.representationSchemeType);

        setIdealSuperimposition(builder.idealSuperimposition);

        // check if all substructures are of the same size
        if (input.stream()
                .map(ConsensusContainer::getStructuralMotif)
                .map(StructuralMotif::getAllLeafSubstructures)
                .map(List::size)
                .collect(Collectors.toSet()).size() != 1) {
            throw new ConsensusException("all substructures must contain the same number of leaf structures to " +
                    "calculate a consensus alignment");
        }

        iterationCounter = 0;
        alignmentTrace = new ArrayList<>();
        alignmentCounts = new ArrayList<>();

        // initialize tree storage
        consensusTrees = new ArrayList<>();

        // calculate initial alignments
        calculateInitialAlignments();

        logger.info("{} initial alignment pairs were computed, in total we have to compute {} alignments",
                alignments.size(), alignments.size() * (input.size() - 1));

        // create initial tree leaves
        createTreeLeaves();
        // start calculating the consensus alignment
        calculateConsensusAlignment();
        // split top level tree
        splitTopLevelTree();
        // align within clusters if specified
        if (alignWithinClusters) {
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
        return new ConsensusContainer(structuralMotif.getCopy(), false);
    }

    public List<Double> getAlignmentTrace() {
        return alignmentTrace;
    }

    public List<BinaryTree<ConsensusContainer>> getClusters() {
        return clusters;
    }

    /**
     * Writes the created clusters to the specified {@link Path}.
     *
     * @param outputPath the desired output {@link Path}
     * @throws IOException If the path cannot be written to.
     */
    public void writeClusters(Path outputPath) throws IOException {
        logger.info("writing {} clusters to {}", clusters.size(), outputPath);
        Files.createDirectories(outputPath);
        for (int i = 0; i < clusters.size(); i++) {
            String clusterBaseLocation = "cluster_" + (i + 1) + "/";
            BinaryTree<ConsensusContainer> currentCluster = clusters.get(i);
            // write consensus
            if (currentCluster.getLeafNodes().size() > 1) {
                StructureWriter.pdb()
                        .substructures(currentCluster.getRoot().getData().getStructuralMotif().getAllLeafSubstructures())
                        .defaultSettings()
                        .writeToPath(outputPath.resolve(clusterBaseLocation + "consensus_" + (i + 1) + ".pdb"));
            }
            // write leaves
            for (BinaryTreeNode<ConsensusContainer> leafNode : currentCluster.getLeafNodes()) {
                if (leafNode.getData().getSuperimposition() != null) {
                    StructureWriter.pdb()
                            .substructures(leafNode.getData().getSuperimposition().getMappedFullCandidate())
                            .defaultSettings()
                            .writeToPath(outputPath.resolve(clusterBaseLocation + leafNode.getData().toString() + ".pdb"));
                } else {
                    StructureWriter.pdb()
                            .substructures(leafNode.getData().getStructuralMotif().getAllLeafSubstructures())
                            .defaultSettings()
                            .writeToPath(outputPath.resolve(clusterBaseLocation + leafNode.getData().toString() + ".pdb"));
                }
            }
        }
    }

    /**
     * Aligns all leaf nodes to the root of the tree (observations against consensus).
     */
    private void alignWithinClusters() {
        // skip one-trees
        clusters.stream().filter(cluster -> cluster.size() > 1).forEach(cluster -> {
            // reference is always the root consensus
            ConsensusContainer reference = cluster.getRoot().getData();
            cluster.getLeafNodes().stream().map(BinaryTreeNode::getData).forEach(consensusContainer -> {
                SubstructureSuperimposition superimposition = superimpose(reference, consensusContainer);
                consensusContainer.setSuperimposition(superimposition);
            });
        });
    }

    /**
     * Splits the top-level tree according to the cutoff value.
     */
    private void splitTopLevelTree() {
        // create list where all trees are stored
        clusters = new ArrayList<>();
        clusters.add(getTopConsensusTree());
        // start iterating over the list
        ListIterator<BinaryTree<ConsensusContainer>> clustersIterator = clusters.listIterator();
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
            if (leftDistance > clusterCutoff || rightDistance > clusterCutoff) {

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
        return consensusTrees.get(consensusTrees.size() - 1);
    }

    /**
     * Returns all trees that were constructed during this alignment.
     *
     * @return all trees that were constructed
     */
    public List<BinaryTree<ConsensusContainer>> getConsensusTrees() {
        return consensusTrees;
    }

    /**
     * Starts the calculation of the consensus alignments.
     */
    private void calculateConsensusAlignment() {
        // iteratively reduce candidates
        while (!alignments.isEmpty()) {
            findAndMergeClosestPair();
        }
    }

    /**
     * Returns the consensus score of this {@link ConsensusAlignment}, i.e. the sum of all closest pairs RMSD values.
     *
     * @return The consensus score
     */
    public double getConsensusScore() {
        return consensusScore;
    }

    /**
     * Returns the consensus score normalized by number of iterations and the size of the input structures:
     *
     * @return The normalized consensus score.
     */
    public double getNormalizedConsensusScore() {
        return consensusScore / (iterationCounter * input.get(0).getStructuralMotif().size());
    }

    /**
     * Finds and merges the closest pair of all input structures and recomputes the alignment.
     */
    private void findAndMergeClosestPair() {

        iterationCounter++;

        Pair<ConsensusContainer> closestPair = alignments.firstEntry().getValue();
        SubstructureSuperimposition closestPairSuperimposition = alignments.firstKey();
        double closestPairRmsd = closestPairSuperimposition.getRmsd();
        alignmentTrace.add(closestPairRmsd);
        alignmentCounts.add(input.size());

        logger.debug("closest pair for iteration {} is {} with RMSD {}", iterationCounter, closestPair, closestPairRmsd);

        // sum up closest pair RMSD
        consensusScore += closestPairRmsd;

        createConsensus(alignments.firstEntry());
        updateAlignments(alignments.firstEntry());
    }

    /**
     * Removes the given pair from the alignments and recomputes them.
     *
     * @param substructurePair the pair to be removed
     */
    private void updateAlignments(Map.Entry<SubstructureSuperimposition,
            Pair<ConsensusContainer>> substructurePair) {

        Iterator<Map.Entry<SubstructureSuperimposition, Pair<ConsensusContainer>>> alignmentsIterator =
                alignments.entrySet().iterator();

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
        input.removeIf(inputStructure -> inputStructure.equals(substructurePair.getValue().getFirst()));
        input.removeIf(inputStructure -> inputStructure.equals(substructurePair.getValue().getSecond()));

        // add new alignments
        for (ConsensusContainer inputStructure : input) {

            // calculate superimposition
            SubstructureSuperimposition superimposition = superimpose(currentConsensus, inputStructure);

            // store alignment
            Pair<ConsensusContainer> alignmentPair = new Pair<>(currentConsensus, inputStructure);
            alignments.put(superimposition, alignmentPair);
        }

        // add consensusObservation to itemsetObservations
        input.add(currentConsensus);
    }

    /**
     * Creates a consensus representation of a {@link Pair} of lists of {@link LeafSubstructure}s by averaging
     * coordinates of each position of the list.
     *
     * @param substructurePair the pair to be merged
     */
    private void createConsensus(Map.Entry<SubstructureSuperimposition, Pair<ConsensusContainer>> substructurePair) {

        List<LeafSubstructure> consensusLeaveSubstructures = determineConsensus(substructurePair);

        currentConsensus = new ConsensusContainer(StructuralMotif.fromLeafSubstructures(consensusLeaveSubstructures), true);

        // create tree node
        BinaryTreeNode<ConsensusContainer> leftNode;
        BinaryTreeNode<ConsensusContainer> rightNode;
        BinaryTreeNode<ConsensusContainer> consensusNode;

        // both nodes have to be leaves
        if (iterationCounter == 1) {
            leftNode = findLeave(substructurePair.getValue().getFirst());
            rightNode = findLeave(substructurePair.getValue().getSecond());
            consensusNode = new BinaryTreeNode<>(currentConsensus, leftNode, rightNode);
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
            consensusNode = new BinaryTreeNode<>(currentConsensus, leftNode, rightNode);
        }

        // create and set consensus tree
        BinaryTree<ConsensusContainer> consensusTree = new BinaryTree<>(consensusNode);
        currentConsensus.setConsensusTree(consensusTree);
        consensusTrees.add(consensusTree);

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
        return leaves.stream()
                .filter(leave -> leave.getData().equals(consensusContainer))
                .findFirst().orElseThrow(() -> new ConsensusException("failed during tree construction"));
    }

    /**
     * Searches in all existing alignment trees to find the node containing the given list of {@link
     * LeafSubstructure}s.
     *
     * @param consensusContainer the list of {@link LeafSubstructure}s for which a node should be found
     * @return the node containing the given list of {@link LeafSubstructure}s or null if it was not found
     */
    private BinaryTreeNode<ConsensusContainer> findNode(ConsensusContainer consensusContainer) {
        BinaryTreeNode<ConsensusContainer> nodeForObservation = null;
        for (BinaryTree<ConsensusContainer> tree : consensusTrees) {
            nodeForObservation = tree.findNode(consensusContainer);
            if (nodeForObservation != null) {
                break;
            }
        }
        // TODO use Optional here
        return nodeForObservation;
    }



    /**
     * Initially calculates the leaves of the alignment tree.
     */
    private void createTreeLeaves() {
        leaves = input.stream()
                .map(BinaryTreeNode::new)
                .collect(Collectors.toList());
    }


    /**
     * Initially calculates all pairwise alignments.
     */

    private void calculateInitialAlignments() {
        alignments = new TreeMap<>(Comparator.comparing(SubstructureSuperimposition::getRmsd));
        double[][] temporaryDistanceMatrix = new double[input.size()][input.size()];
        List<ConsensusContainer> distanceMatrixLabels = new ArrayList<>();
        // initially append first label
        distanceMatrixLabels.add(input.get(0));

        int alignmentCounter = 0;
        for (int i = 0; i < input.size() - 1; i++) {

            for (int j = i + 1; j < input.size(); j++) {

                StructuralMotif reference = input.get(i).getStructuralMotif();
                StructuralMotif candidate = input.get(j).getStructuralMotif();

                // calculate superimposition
                SubstructureSuperimposition superimposition = superimpose(reference, candidate);

                // store alignment
                Pair<ConsensusContainer> alignmentPair = new Pair<>(new ConsensusContainer(reference, false),
                        new ConsensusContainer(candidate, false));
                alignments.put(superimposition, alignmentPair);

                // store distance matrix
                temporaryDistanceMatrix[i][j] = superimposition.getRmsd();
                temporaryDistanceMatrix[j][i] = superimposition.getRmsd();

                alignmentCounter++;
                if (alignmentCounter % 1000 == 0) {
                    logger.info("computed {} of {} initial alignments ", alignmentCounter, input.size() *
                            ((input.size() - 1) / 2));
                }
            }
            // store label
            distanceMatrixLabels.add(input.get(i + 1));
        }
        distanceMatrix = new LabeledSymmetricMatrix<>(temporaryDistanceMatrix);
        distanceMatrix.setColumnLabels(distanceMatrixLabels);
    }
}
