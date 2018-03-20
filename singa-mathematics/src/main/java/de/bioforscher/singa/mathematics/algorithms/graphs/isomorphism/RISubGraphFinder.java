package de.bioforscher.singa.mathematics.algorithms.graphs.isomorphism;

import de.bioforscher.singa.mathematics.graphs.model.*;
import de.bioforscher.singa.mathematics.vectors.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;

/**
 * @author fk
 */
public class RISubGraphFinder<NodeType extends Node<NodeType, VectorType, IdentifierType>, EdgeType extends Edge<NodeType>,
        VectorType extends Vector, IdentifierType, GraphType extends Graph<NodeType, EdgeType, IdentifierType>, NodeConditionType,
        EdgeConditionType> {

    private static final Logger logger = LoggerFactory.getLogger(RISubGraphFinder.class);
    private final List<NodeType> mu;
    private final List<NodeType> ptmu;
    private final GraphType patternGraph;
    private final GraphType targetGraph;
    private final Function<NodeType, NodeConditionType> nodeConditionExtractor;
    private final Function<EdgeType, EdgeConditionType> edgeConditionExtractor;
    private DirectedGraph<GenericNode<NodeType>> parentGraph;
    private DirectedGraph<GenericNode<NodeType>> searchSpace;
    private List<List<NodeType>> fullMatches;

    public RISubGraphFinder(GraphType patternGraph, GraphType targetGraph, Function<NodeType, NodeConditionType> nodeConditionExtractor,
                            Function<EdgeType, EdgeConditionType> edgeConditionExtractor) {
        this.patternGraph = patternGraph;
        this.targetGraph = targetGraph;
        this.nodeConditionExtractor = nodeConditionExtractor;
        this.edgeConditionExtractor = edgeConditionExtractor;
        mu = new ArrayList<>();
        ptmu = new ArrayList<>();
        fullMatches = new ArrayList<>();
        calculateMu();
        buildParentGraph();
        matchTargetGraph();
    }

    public List<List<NodeType>> getFullMatches() {
        return fullMatches;
    }

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

    private void matchTargetGraph() {
        searchSpace = new DirectedGraph<>();
        GenericNode<NodeType> root = new GenericNode<>(searchSpace.nextNodeIdentifier(), null);
        searchSpace.addNode(root);
        for (NodeType x : targetGraph.getNodes()) {
            growSearchSpace(mu.size() - 1, searchSpace, root, x);
        }
        System.out.println();
    }

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
            logger.info("pairing pattern node {} and target node {} in depth {}", patternNode, candidateTargetNode, currentPatternDepth);
            // second condition (1b):
            // the target node matched to the pattern parent node of the current pattern node is a neighbour of the
            // current target node
            if (noParent || mptui.getNeighbours().contains(candidateTargetNode)) {
                // third condition (2)
                // the labels of the node in the pattern graph is equal to the label of the node in the target graph
                if (nodeConditionExtractor.apply(patternNode).equals(nodeConditionExtractor.apply(candidateTargetNode))) {
                    // fourth condition (3):
                    // the number of edges of the candidate target node is larger than or equal to the number of edges
                    // connected to supposed pattern node
                    if (candidateTargetNode.getNeighbours().size() >= patternNode.getNeighbours().size()) {

                        // only evaluate condition (4) if ui has parent
                        boolean parentToCandidateEdgeMatches = false;
                        boolean candidateToParentEdgeMatches = false;
                        if (!noParent) {

                            Optional<EdgeType> targetSpaceEdgeParentToCandidate = targetGraph.getEdgeBetween(targetSpaceParent.getContent(), candidateTargetNode);
                            Optional<EdgeType> targetSpaceEdgeCandidateToParent = targetGraph.getEdgeBetween(candidateTargetNode, targetSpaceParent.getContent());

                            Optional<EdgeType> patternEdgeParentToCandidate = patternGraph.getEdgeBetween(ptui, patternNode);
                            Optional<EdgeType> patternEdgeCandidateToParent = patternGraph.getEdgeBetween(patternNode, ptui);

                            parentToCandidateEdgeMatches = checkEdgeCondition(targetSpaceEdgeParentToCandidate, patternEdgeParentToCandidate);

                            candidateToParentEdgeMatches = checkEdgeCondition(targetSpaceEdgeCandidateToParent, patternEdgeCandidateToParent);
                        }

                        if (noParent || (parentToCandidateEdgeMatches && candidateToParentEdgeMatches)) {
                            // all conditions passed
                            logger.info("all conditions passed");
                            // create node for target space
                            GenericNode<NodeType> validTargetSpaceNode = new GenericNode<>(targetSpace.nextNodeIdentifier(), candidateTargetNode);
                            // add node to target space
                            targetSpace.addNode(validTargetSpaceNode);
                            // connect the new node to its parent in the search space
                            targetSpace.addEdgeBetween(targetSpace.nextEdgeIdentifier(), validTargetSpaceNode, targetSpaceParent);
                            // if there are nodes left in the pattern graph, that are unpaired
                            if (remainingPatternNodes > 0) {
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
                            logger.info("(4) ... ");
                        }

                    } else {
                        logger.info("(3) ... ");
                    }
                } else {
                    logger.info("(2) Label Condition: pattern label {} and target label {} did not match", nodeConditionExtractor.apply(patternNode), nodeConditionExtractor.apply(candidateTargetNode));
                }
            } else {
                logger.info("(1) Parent Condition: pattern node {} and target node {} did not match", patternNode, candidateTargetNode);
            }
        }
    }

    private boolean checkEdgeCondition(Optional<EdgeType> targetSpaceEdgeCandidateToParent, Optional<EdgeType> patternEdgeCandidateToParent) {
        if (targetSpaceEdgeCandidateToParent.isPresent() && patternEdgeCandidateToParent.isPresent()) {
            EdgeType matchEdgeCP = targetSpaceEdgeCandidateToParent.get();
            EdgeType patternEdgeCP = patternEdgeCandidateToParent.get();
            return edgeConditionExtractor.apply(matchEdgeCP).equals(edgeConditionExtractor.apply(patternEdgeCP));
        } else {
            return true;
        }
    }

    private void calculateMu() {
        // determine node with highest degree and use as starting node
        List<NodeType> v = new ArrayList<>(patternGraph.getNodes());
        if (v.isEmpty()) {
            throw new IllegalArgumentException("The pattern graph does not contain any nodes.");
        }
        v.sort(Comparator.comparing(Node::getDegree));
        NodeType u0 = v.get(v.size() - 1);

        logger.info("u0 is {}", u0);

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
                    System.out.println("changed winner " + um);
                } else if (vuvis == maxVuvis && vmneig > maxVmneig) {
                    um = ui;
                    maxVuvis = vuvis;
                    maxVmneig = vmneig;
                    maxVmunv = vmunv;
                    System.out.println("changed winner " + um);
                } else if (vuvis == maxVuvis && vmneig == maxVmneig && vmunv > maxVmunv) {
                    um = ui;
                    maxVuvis = vuvis;
                    maxVmneig = vmneig;
                    maxVmunv = vmunv;
                    System.out.println("changed winner " + um);
                }
                System.out.println(ui + " - " + vuvis + " " + vmneig + " " + vmunv);
            }

            mu.add(um);

            for (NodeType currentMu : mu) {
                if (patternGraph.getEdgeBetween(um, currentMu).isPresent()) {
                    ptmu.add(currentMu);
                    break;
                }
            }

            logger.info("mu is {}", mu);
            logger.info("ptmu is {}", ptmu);
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

    public DirectedGraph<GenericNode<NodeType>> getParentGraph() {
        return parentGraph;
    }

    public DirectedGraph<GenericNode<NodeType>> getSearchSpace() {
        return searchSpace;
    }

}
