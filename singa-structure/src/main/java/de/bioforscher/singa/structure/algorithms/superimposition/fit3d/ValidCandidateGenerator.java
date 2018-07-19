package de.bioforscher.singa.structure.algorithms.superimposition.fit3d;

import de.bioforscher.singa.mathematics.graphs.model.DirectedGraph;
import de.bioforscher.singa.mathematics.graphs.model.GenericNode;
import de.bioforscher.singa.mathematics.matrices.LabeledSymmetricMatrix;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Assembles the correct candidates used for alignment by constructing a search tree. Every full path in the tree
 * corresponds to a valid candidate alignment.
 *
 * @author fk
 */
public class ValidCandidateGenerator {

    private static final Logger logger = LoggerFactory.getLogger(ValidCandidateGenerator.class);

    private final List<LeafSubstructure<?>> queryMotif;
    private final List<LeafSubstructure<?>> environment;
    private final List<List<LeafSubstructure<?>>> candidates;
    private double squaredFilterThreshold;
    private Map<Integer, List<Double>> pairwiseQueryMotifDistanceMap;
    private LabeledSymmetricMatrix<LeafSubstructure<?>> squaredDistanceMatrix;
    private DirectedGraph<GenericNode<LeafSubstructure<?>>> searchSpace;

    public ValidCandidateGenerator(List<LeafSubstructure<?>> queryMotif, List<LeafSubstructure<?>> environment) {
        this.queryMotif = queryMotif;
        this.environment = environment;
        candidates = new ArrayList<>();
        generateCandidates();
    }

    public ValidCandidateGenerator(List<LeafSubstructure<?>> queryMotif, List<LeafSubstructure<?>> environment,
                                   Map<Integer, List<Double>> pairwiseQueryMotifDistanceMap,
                                   LabeledSymmetricMatrix<LeafSubstructure<?>> squaredDistanceMatrix,
                                   double squaredFilterThreshold) {
        this.queryMotif = queryMotif;
        this.environment = environment;
        this.pairwiseQueryMotifDistanceMap = pairwiseQueryMotifDistanceMap;
        this.squaredDistanceMatrix = squaredDistanceMatrix;
        this.squaredFilterThreshold = squaredFilterThreshold;
        candidates = new ArrayList<>();
        generateCandidates();
    }

    /**
     * Returns true if the candidate {@link LeafSubstructure} is compatible to the motif {@link LeafSubstructure}
     *
     * @param motifLeafSubstructure The {@link LeafSubstructure} of the motif to be checked.
     * @param candidateLeafSubstructure The {@link LeafSubstructure} of the candidate ensemble.
     * @return True if labels are compatible.
     */
    private static boolean checkCompatibility(LeafSubstructure<?> motifLeafSubstructure, LeafSubstructure<?> candidateLeafSubstructure) {
        if (motifLeafSubstructure.getFamily().equals(candidateLeafSubstructure.getFamily())) {
            return true;
        }
        return motifLeafSubstructure.getExchangeableFamilies().contains(candidateLeafSubstructure.getFamily());
    }

    public List<List<LeafSubstructure<?>>> getCandidates() {
        return candidates;
    }

    public DirectedGraph<GenericNode<LeafSubstructure<?>>> getSearchSpace() {
        return searchSpace;
    }

    private void generateCandidates() {
        searchSpace = new DirectedGraph<>();
        GenericNode<LeafSubstructure<?>> root = new GenericNode<>(searchSpace.nextNodeIdentifier(), null);
        searchSpace.addNode(root);
        for (LeafSubstructure<?> leafSubstructure : environment) {
            growSearchSpace(queryMotif.size() - 1, searchSpace, root, leafSubstructure);
        }
    }

    private void growSearchSpace(int remainingQueryLeafSubstructures, DirectedGraph<GenericNode<LeafSubstructure<?>>> searchSpace, GenericNode<LeafSubstructure<?>> searchSpaceParent, LeafSubstructure<?> candidateLeafSubstructure) {
        logger.debug("iteration depth {}", remainingQueryLeafSubstructures + 1);

        List<LeafSubstructure<?>> queryPath = new ArrayList<>();
        queryPath.add(searchSpaceParent.getContent());

        GenericNode<LeafSubstructure<?>> currentNode = searchSpaceParent;
        while (currentNode.getContent() != null) {
            List<GenericNode<LeafSubstructure<?>>> neighbors = currentNode.getNeighbours();
            if (neighbors.size() > 1) {
                throw new IllegalStateException("Search space is not well-formed.");
            }
            currentNode = neighbors.get(0);
            queryPath.add(currentNode.getContent());
        }

        // (1) first condition:
        // path to the current node does not already contain the candidate
        if (!queryPath.contains(candidateLeafSubstructure)) {

            logger.debug("first condition passed for {}", candidateLeafSubstructure);

            // the index of the query residue that is to be matched
            int currentQueryDepth = queryMotif.size() - remainingQueryLeafSubstructures - 1;

            // (2) second condition:
            // labels of the matched residues must be compatible
            if (checkCompatibility(queryMotif.get(currentQueryDepth), candidateLeafSubstructure)) {
                logger.debug("second condition passed for {}", candidateLeafSubstructure);

                // (3) third condition:
                // pair of similar distance must be in query motif
                if (pairwiseQueryMotifDistanceMap != null && squaredDistanceMatrix != null) {
                    LeafSubstructure<?> parentLeafSubstructure = searchSpaceParent.getContent();
                    if (parentLeafSubstructure != null) {
                        int hashCode = Fit3DAlignment.generateLabelHashCode(parentLeafSubstructure.getFamily(), candidateLeafSubstructure.getFamily());
                        // distance between candidate and its parent must be similar to one in the motif
                        List<Double> distancesQuery = pairwiseQueryMotifDistanceMap.get(hashCode);
                        double distanceCandidate = squaredDistanceMatrix.getValueForLabel(parentLeafSubstructure, candidateLeafSubstructure);
                        boolean compatibleDistances = distancesQuery.stream()
                                .anyMatch(distance -> distanceCandidate > distance - squaredFilterThreshold && distanceCandidate < distance + squaredFilterThreshold);
                        if (compatibleDistances) {
                            logger.debug("third condition passed for {}", candidateLeafSubstructure);
                            addValidNode(remainingQueryLeafSubstructures, searchSpace, searchSpaceParent, candidateLeafSubstructure, queryPath);
                        } else {
                            logger.debug("third condition failed for {}", candidateLeafSubstructure);
                        }
                    } else {
                        // third condition cannot be tested for the root node
                        addValidNode(remainingQueryLeafSubstructures, searchSpace, searchSpaceParent, candidateLeafSubstructure, queryPath);
                    }
                } else {
                    // if distance filtering is not enabled
                    addValidNode(remainingQueryLeafSubstructures, searchSpace, searchSpaceParent, candidateLeafSubstructure, queryPath);
                }
            } else {
                logger.debug("second condition failed for {}", candidateLeafSubstructure);
            }
        } else {
            logger.debug("first condition failed for {}", candidateLeafSubstructure);
        }
    }

    /**
     * If all conditions are passed, this method adds the next valid candidate.
     *
     * @param remainingQueryLeafSubstructures The number of remaining query {@link LeafSubstructure}s.
     * @param searchSpace The current search space.
     * @param searchSpaceParent The parent of the current candidate {@link LeafSubstructure}.
     * @param candidateLeafSubstructure The current candidate {@link LeafSubstructure}.
     * @param queryPath The so far constructed valid path.
     */
    private void addValidNode(int remainingQueryLeafSubstructures, DirectedGraph<GenericNode<LeafSubstructure<?>>> searchSpace, GenericNode<LeafSubstructure<?>> searchSpaceParent, LeafSubstructure<?> candidateLeafSubstructure, List<LeafSubstructure<?>> queryPath) {
        GenericNode<LeafSubstructure<?>> validCandidateNode = new GenericNode<>(searchSpace.nextNodeIdentifier(), candidateLeafSubstructure);
        searchSpace.addNode(validCandidateNode);
        searchSpace.addEdgeBetween(searchSpace.nextEdgeIdentifier(), validCandidateNode, searchSpaceParent);
        if (remainingQueryLeafSubstructures > 0) {
            // grow search space for all residues in the environment
            for (LeafSubstructure<?> leafSubstructure : environment) {
                growSearchSpace(remainingQueryLeafSubstructures - 1, searchSpace, validCandidateNode, leafSubstructure);
            }
        } else {
            logger.debug("full candidate found");
            queryPath.remove(queryPath.size() - 1);
            queryPath.add(0, validCandidateNode.getContent());
            // invert list as path is reversed
            Collections.reverse(queryPath);
            candidates.add(queryPath);
        }
    }
}
