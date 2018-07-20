package bio.singa.mathematics.algorithms.graphs.isomorphism;

import bio.singa.core.utility.Pair;
import bio.singa.mathematics.graphs.model.*;
import bio.singa.mathematics.vectors.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * An implementation of the algorithm described in:
 * <pre>
 *     Bonnici, V, Giugno, R, Pulvirenti, A, Shasha, D, Ferro, A (2013).
 *     A subgraph isomorphism algorithm and its application to biochemical data.
 *     BMC Bioinformatics, 14 Suppl 7:S13.
 * </pre>
 * This subgraph detection algorithm can be applied on any {@link Graph} and allows the flexible definition of isomorphism
 * conditions via {@link Function}s.
 *
 * @author fk
 */
public class RISubgraphFinder<NodeType extends Node<NodeType, VectorType, IdentifierType>, EdgeType extends Edge<NodeType>,
        VectorType extends Vector, IdentifierType, GraphType extends Graph<NodeType, EdgeType, IdentifierType>> {

    private static final Logger logger = LoggerFactory.getLogger(RISubgraphFinder.class);
    private final List<NodeType> mu;
    private final List<NodeType> ptmu;

    private final GraphType patternGraph;
    private final GraphType targetGraph;
    private final BiFunction<NodeType, NodeType, Boolean> nodeConditionExtractor;
    private final BiFunction<EdgeType, EdgeType, Boolean> edgeConditionExtractor;
    private final int minimalPartialMatchSize;
    private final List<List<NodeType>> fullMatches;
    private final Map<Integer, List<List<NodeType>>> partialMatches;
    private DirectedGraph<GenericNode<NodeType>> parentGraph;
    private DirectedGraph<GenericNode<NodeType>> searchSpace;
    private List<List<Pair<NodeType>>> fullMatchPairs;

    public RISubgraphFinder(GraphType patternGraph, GraphType targetGraph, BiFunction<NodeType, NodeType, Boolean> nodeConditionExtractor,
                            BiFunction<EdgeType, EdgeType, Boolean> edgeConditionExtractor) {
        this(patternGraph, targetGraph, nodeConditionExtractor, edgeConditionExtractor, patternGraph.getNodes().size());
    }

    public RISubgraphFinder(GraphType patternGraph, GraphType targetGraph, BiFunction<NodeType, NodeType, Boolean> nodeConditionExtractor,
                            BiFunction<EdgeType, EdgeType, Boolean> edgeConditionExtractor, int minimalPartialMatchSize) {
        this.patternGraph = patternGraph;
        this.targetGraph = targetGraph;
        this.nodeConditionExtractor = nodeConditionExtractor;
        this.edgeConditionExtractor = edgeConditionExtractor;
        this.minimalPartialMatchSize = minimalPartialMatchSize;
        mu = new ArrayList<>();
        ptmu = new ArrayList<>();
        fullMatches = new ArrayList<>();
        partialMatches = new TreeMap<>();
        calculateMu();
        buildParentGraph();
        matchTargetGraph();
        logger.info("found {} full and {} partial (minimum size: {}) matches in target graph", fullMatches.size(), partialMatches.size(), minimalPartialMatchSize);
        if (!fullMatches.isEmpty()) {
            logger.info("full matches are: {}", fullMatches);
        }
    }

    /**
     * Calculates the ordering of pattern nodes according to the GreatestConstraintFirst algorithm.
     */
    private void calculateMu() {
        // determine node with highest degree and use as starting node
        List<NodeType> v = new ArrayList<>(patternGraph.getNodes());
        if (v.isEmpty()) {
            throw new IllegalArgumentException("The pattern graph does not contain any nodes.");
        }
        v.sort(Comparator.comparing(Node::getDegree));
        NodeType u0 = v.get(v.size() - 1);

        logger.info("highest degree node is {} with degree {}", u0, u0.getDegree());

        // initially remove highest degree node
        v.remove(u0);

        // add highest degree node as start
        mu.add(u0);
        ptmu.add(null);

        Set<NodeType> neighboursOfMu = new HashSet<>();

        NodeType um = u0;
        while (!v.isEmpty()) {

            int maxVuvis = Integer.MIN_VALUE;
            int maxVmneig = Integer.MIN_VALUE;
            int maxVmunv = Integer.MIN_VALUE;

            // neighbors of nodes in mu
            neighboursOfMu.addAll(um.getNeighbours());

            for (NodeType ui : v) {
                int vuvis = calculateVuvis(ui);
                int vmneig = calculateVmneig(ui, neighboursOfMu);
                int vmunv = calculateVmunv(ui, neighboursOfMu);

                if (vuvis > maxVuvis) {
                    um = ui;
                    maxVuvis = vuvis;
                    maxVmneig = vmneig;
                    maxVmunv = vmunv;
                    logger.debug("changed winner is: {}", um);
                } else if (vuvis == maxVuvis && vmneig > maxVmneig) {
                    um = ui;
                    maxVuvis = vuvis;
                    maxVmneig = vmneig;
                    maxVmunv = vmunv;
                    logger.debug("changed winner is: {}", um);
                } else if (vuvis == maxVuvis && vmneig == maxVmneig && vmunv > maxVmunv) {
                    um = ui;
                    maxVuvis = vuvis;
                    maxVmneig = vmneig;
                    maxVmunv = vmunv;
                    logger.debug("changed winner is: {}", um);
                }
                logger.debug("{} has score: {}-{}-{}", ui, vuvis, vmneig, vmunv);
            }

            mu.add(um);

            for (NodeType currentMu : mu) {
                if (patternGraph.getEdgeBetween(um, currentMu).isPresent()) {
                    ptmu.add(currentMu);
                    break;
                }
            }
            logger.debug("mu is {}", mu);
            logger.debug("ptmu is {}", ptmu);
            neighboursOfMu.remove(um);
            v.remove(um);
        }
    }

    private int calculateVuvis(NodeType ui) {
        Set<NodeType> neighboursOfUi = new HashSet<>(ui.getNeighbours());
        neighboursOfUi.retainAll(mu);
        return neighboursOfUi.size();
    }

    private int calculateVmunv(NodeType ui, Set<NodeType> neighboursOfMu) {
        Set<NodeType> neighboursOfUi = new HashSet<>(ui.getNeighbours());
        neighboursOfUi.removeAll(mu);
        neighboursOfUi.removeAll(neighboursOfMu);
        return neighboursOfUi.size();
    }

    private int calculateVmneig(NodeType ui, Set<NodeType> neighboursOfMu) {
        Set<NodeType> neighboursOfUi = new HashSet<>(ui.getNeighbours());
        neighboursOfUi.removeAll(mu);
        neighboursOfUi.retainAll(neighboursOfMu);
        return neighboursOfUi.size();
    }

    /**
     * Builds the parent graph of the pattern graph.
     */
    private void buildParentGraph() {
        parentGraph = new DirectedGraph<>();
        for (NodeType patternNode : mu) {
            parentGraph.addNode(new GenericNode<>(parentGraph.nextNodeIdentifier(), patternNode));
        }
        for (int i = 0; i < ptmu.size(); i++) {
            NodeType currentSource = ptmu.get(i);
            Optional<GenericNode<NodeType>> source = parentGraph.getNodes().stream()
                    .filter(node -> node.getContent().equals(currentSource))
                    .findFirst();
            NodeType currentTarget = mu.get(i);
            Optional<GenericNode<NodeType>> target = parentGraph.getNodes().stream()
                    .filter(node -> node.getContent().equals(currentTarget))
                    .findFirst();
            if (source.isPresent() && target.isPresent()) {
                parentGraph.addEdgeBetween(target.get(), source.get());
            }
        }
    }

    /**
     * Recursively grows the search space and prunes away unfeasible branches in the search tree.
     */
    private void matchTargetGraph() {
        searchSpace = new DirectedGraph<>();
        GenericNode<NodeType> root = new GenericNode<>(searchSpace.nextNodeIdentifier(), null);
        searchSpace.addNode(root);
        for (NodeType x : targetGraph.getNodes()) {
            growSearchSpace(mu.size() - 1, searchSpace, root, x);
        }
        logger.info("constructed search space graph is: {}", searchSpace);
    }

    /**
     * Recursively called method that grows the search space.
     *
     * @param remainingPatternNodes The number of remaining pattern nodes to process.
     * @param targetSpace The current search space.
     * @param targetSpaceParent The parent of the search space.
     * @param candidateTargetNode The next node that should be considered for insertion in the search space.
     */
    private void growSearchSpace(int remainingPatternNodes, DirectedGraph<GenericNode<NodeType>> targetSpace, GenericNode<NodeType> targetSpaceParent, NodeType candidateTargetNode) {
        // prepare path to the current node in the target space
        List<NodeType> targetSpacePath = new ArrayList<>();
        // add previously added node (content because the actual node is encapsulated in another node in the target space)
        targetSpacePath.add(targetSpaceParent.getContent());
        // the previously added node is the starting point for the parent traversal
        GenericNode<NodeType> currentNode = targetSpaceParent;
        // while the parent is not the root node (node with content null)
        while (currentNode.getContent() != null) {
            // since target space is a directed graph with edges pointing to parents, this returns the parent node
            List<GenericNode<NodeType>> neighbours = currentNode.getNeighbours();
            // since every node has one and only one parent
            if (neighbours.size() > 1) {
                // this must not happen
                throw new IllegalStateException("Search space is not well-formed.");
            }
            // update current node
            currentNode = neighbours.get(0);
            // add the current node (parent of previous node) to the path of parents
            targetSpacePath.add(currentNode.getContent());
        }

        // first (1a) condition:
        // path to the current node does not already contain the node that is to be matched
        if (!targetSpacePath.contains(candidateTargetNode)) {
            // the index of the pattern node that is to be matched
            int currentPatternDepth = mu.size() - remainingPatternNodes - 1;
            // the pattern parent node of the pattern node that is to be matched
            NodeType ptui = ptmu.get(currentPatternDepth);
            // a flag that determines if the current iteration can have parents
            boolean noParent = true;
            // if there is a parent
            NodeType mptui = null;
            if (ptui != null) {
                // get the index of the pattern parent in the pattern
                int ptuiIndex = mu.indexOf(ptui);
                // get the node of the target space that was matched with the pattern parent
                // the path is build in reverse, therefor the index is calculated
                mptui = targetSpacePath.get(targetSpacePath.size() - ptuiIndex - 2);
                // lift flag, indicating that parents need to be considered in the current iteration
                noParent = false;
            }

            NodeType patternNode = mu.get(currentPatternDepth);
            logger.debug("pairing pattern node {} and target node {} in depth {}", patternNode, candidateTargetNode, currentPatternDepth);
            // second condition (1b):
            // the target node matched to the pattern parent node of the current pattern node is a neighbour of the
            // current target node
            if (noParent || mptui.getNeighbours().contains(candidateTargetNode)) {
                // third condition (2)
                // the labels of the node in the pattern graph is equal to the label of the node in the target graph
                if (nodeConditionExtractor.apply(patternNode, candidateTargetNode)) {
                    // fourth condition (3):
                    // the number of edges of the candidate target node is larger than or equal to the number of edges
                    // connected to supposed pattern node
                    if ((candidateTargetNode.getNeighbours().size() >= patternNode.getNeighbours().size()) &&
                            // magic check authored by sb
                            checkConsumedNeighbors(candidateTargetNode, targetSpacePath, currentPatternDepth)) {

                        // only evaluate condition (4) if ui has parent
                        boolean parentToCandidateEdgeMatches = false;
                        boolean candidateToParentEdgeMatches = false;
                        if (!noParent) {


                            Optional<EdgeType> targetSpaceEdgeParentToCandidate = targetGraph.getEdgeBetween(mptui, candidateTargetNode);
                            Optional<EdgeType> targetSpaceEdgeCandidateToParent = targetGraph.getEdgeBetween(candidateTargetNode, mptui);

                            Optional<EdgeType> patternEdgeParentToCandidate = patternGraph.getEdgeBetween(ptui, patternNode);
                            Optional<EdgeType> patternEdgeCandidateToParent = patternGraph.getEdgeBetween(patternNode, ptui);

                            parentToCandidateEdgeMatches = checkEdgeCondition(targetSpaceEdgeParentToCandidate, patternEdgeParentToCandidate);

                            candidateToParentEdgeMatches = checkEdgeCondition(targetSpaceEdgeCandidateToParent, patternEdgeCandidateToParent);
                        }

                        if (noParent || (parentToCandidateEdgeMatches && candidateToParentEdgeMatches)) {
                            // all conditions passed
                            logger.debug("all conditions passed");
                            // create node for target space
                            GenericNode<NodeType> validTargetSpaceNode = new GenericNode<>(targetSpace.nextNodeIdentifier(), candidateTargetNode);
                            // add node to target space
                            targetSpace.addNode(validTargetSpaceNode);
                            // connect the new node to its parent in the search space
                            targetSpace.addEdgeBetween(targetSpace.nextEdgeIdentifier(), validTargetSpaceNode, targetSpaceParent);
                            // if there are nodes left in the pattern graph, that are unpaired
                            if (remainingPatternNodes > 0) {
                                // consider partial matches if depth is sufficient
                                if (currentPatternDepth >= minimalPartialMatchSize - 1) {
                                    List<NodeType> partialMatch = new ArrayList<>(targetSpacePath);
                                    partialMatch.remove(partialMatch.size() - 1);
                                    partialMatch.add(0, validTargetSpaceNode.getContent());
                                    List<List<NodeType>> currentPartialMatches;
                                    if (partialMatches.containsKey(currentPatternDepth)) {
                                        currentPartialMatches = partialMatches.get(currentPatternDepth);
                                        currentPartialMatches.add(partialMatch);
                                    } else {
                                        currentPartialMatches = new ArrayList<>();
                                        currentPartialMatches.add(partialMatch);
                                        partialMatches.put(currentPatternDepth + 1, currentPartialMatches);
                                    }
                                }

                                // for all nodes of the target graph
                                for (NodeType targetNode : targetGraph.getNodes()) {
                                    // try to enlarge search space
                                    growSearchSpace(remainingPatternNodes - 1, targetSpace, validTargetSpaceNode, targetNode);
                                }
                            } else {
                                targetSpacePath.remove(targetSpacePath.size() - 1);
                                targetSpacePath.add(0, validTargetSpaceNode.getContent());

//                                NodeType lastTargetNode = targetSpacePath.get(targetSpacePath.size() - 1);
//
//                                Optional<EdgeType> targetFirstToLast = targetGraph.getEdgeBetween(lastTargetNode, validTargetSpaceNode.getContent());
//                                Optional<EdgeType> targetLastToFirst = targetGraph.getEdgeBetween(validTargetSpaceNode.getContent(), lastTargetNode);
//
//                                Optional<EdgeType> patternFirstToLast = patternGraph.getEdgeBetween(mu.get(0), patternNode);
//                                Optional<EdgeType> patternLastToFirst = patternGraph.getEdgeBetween(patternNode, mu.get(0));
//
//                                boolean firstToLastMatches = checkEdgeCondition(targetFirstToLast, patternFirstToLast);
//                                boolean lastToFirstMatches = checkEdgeCondition(targetLastToFirst, patternLastToFirst);
//
//                                if (firstToLastMatches && lastToFirstMatches) {
                                fullMatches.add(targetSpacePath);
//                                }else{
//                                    logger.info("(5) ... edge FL:{}-{} LF:{}-{}", targetFirstToLast,patternFirstToLast,targetLastToFirst,patternLastToFirst);
//                                }
                            }
                        } else {
                            logger.debug("(4) Edge Condition failed: edge condition for pattern node {} and target node {} did not match", edgeConditionExtractor, patternNode, candidateTargetNode);
                        }
                    } else {
                        logger.debug("(3) Connectivity Condition: pattern node neighbors {} are less than target node neighbors {}", patternNode.getNeighbours(), candidateTargetNode.getNeighbours());
                    }
                } else {
                    logger.debug("(2) Node Condition: node condition {} does not match for pattern node {} and target node {}", nodeConditionExtractor, patternNode, candidateTargetNode);
                }
            } else {
                logger.debug("(1) Parent Condition: pattern node {} and target node {} did not match", patternNode, candidateTargetNode);
            }
        }
    }

    /**
     * Magic condition which is not entirely clear.
     *
     * @param candidateTargetNode The target node to be added.
     * @param targetSpacePath The current path in the search space.
     * @param currentPatternDepth The current depth in the search space.
     * @return True if condition is met.
     */
    private boolean checkConsumedNeighbors(NodeType candidateTargetNode, List<NodeType> targetSpacePath, int currentPatternDepth) {
        NodeType patternNode = mu.get(currentPatternDepth);
        long candidateNeighborCount = candidateTargetNode.getNeighbours().stream()
                .filter(targetSpacePath::contains)
                .count();
        List<NodeType> patternPath = mu.subList(0, currentPatternDepth);
        long patternNeighborCount = patternNode.getNeighbours().stream()
                .filter(patternPath::contains)
                .count();
        return candidateNeighborCount >= patternNeighborCount;
    }

    /**
     * Checks the edge condition.
     *
     * @param targetSpaceEdgeCandidateToParent First edge to check.
     * @param patternEdgeCandidateToParent Second edge to check.
     * @return True if conditions are met.
     */
    private boolean checkEdgeCondition(Optional<EdgeType> targetSpaceEdgeCandidateToParent, Optional<EdgeType> patternEdgeCandidateToParent) {
        if (targetSpaceEdgeCandidateToParent.isPresent() && patternEdgeCandidateToParent.isPresent()) {
            EdgeType matchEdgeCP = targetSpaceEdgeCandidateToParent.get();
            EdgeType patternEdgeCP = patternEdgeCandidateToParent.get();
            return edgeConditionExtractor.apply(patternEdgeCP, matchEdgeCP);
        } else {
            return true;
        }
    }

    public DirectedGraph<GenericNode<NodeType>> getParentGraph() {
        return parentGraph;
    }

    public DirectedGraph<GenericNode<NodeType>> getSearchSpace() {
        return searchSpace;
    }

    public List<List<NodeType>> getFullMatches() {
        return fullMatches;
    }

    /**
     * Returns the full matches as {@link Pair}s between the pattern nodes and the target nodes.
     *
     * @return The full matches as {@link Pair}s.
     */
    public List<List<Pair<NodeType>>> getFullMatchPairs() {
        if (fullMatchPairs != null) {
            return fullMatchPairs;
        }
        fullMatchPairs = new ArrayList<>();
        for (List<NodeType> fullMatch : fullMatches) {
            List<Pair<NodeType>> fullMatchPair = new ArrayList<>();
            for (int i = 0; i < fullMatch.size(); i++) {
                NodeType targetNode = fullMatch.get(fullMatch.size() - i - 1);
                fullMatchPair.add(new Pair<>(mu.get(i), targetNode));
            }
            fullMatchPairs.add(fullMatchPair);
        }
        return fullMatchPairs;
    }


    public Map<Integer, List<List<NodeType>>> getPartialMatches() {
        return partialMatches;
    }


    public GraphType getPatternGraph() {
        return patternGraph;
    }

    public GraphType getTargetGraph() {
        return targetGraph;
    }
}
