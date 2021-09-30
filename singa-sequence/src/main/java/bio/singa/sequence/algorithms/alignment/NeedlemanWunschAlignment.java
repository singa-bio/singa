package bio.singa.sequence.algorithms.alignment;

import bio.singa.mathematics.algorithms.graphs.ShortestPathFinder;
import bio.singa.mathematics.graphs.model.GraphPath;
import bio.singa.mathematics.matrices.LabeledSymmetricMatrix;
import bio.singa.sequence.model.ProteinSequence;
import bio.singa.structure.algorithms.superimposition.scores.SubstitutionMatrix;
import bio.singa.structure.model.families.AminoAcidFamily;
import bio.singa.structure.model.families.StructuralFamily;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.stream.Collectors;

import static bio.singa.structure.model.families.GAP;

/**
 * @author cl
 */
public class NeedlemanWunschAlignment {

    private static final Logger logger = LoggerFactory.getLogger(NeedlemanWunschAlignment.class);

    private static final int DEFAULT_GAP_COST = -8;
    private int gapCost = DEFAULT_GAP_COST;

    private final LabeledSymmetricMatrix<StructuralFamily> substitutionMatrix;
    private DynamicProgrammingGraph graph;

    private ProteinSequence alignedFirstSequence;
    private ProteinSequence alignedSecondSequence;

    public NeedlemanWunschAlignment(SubstitutionMatrix substitutionMatrix, ProteinSequence firstSequence, ProteinSequence secondSequence) {
        this.substitutionMatrix = substitutionMatrix.getMatrix();
        graph = new DynamicProgrammingGraph(firstSequence, secondSequence);
        logger.info("Computing alignment using Neddleman Wunsch for sequences:\n {} \n {}", firstSequence, secondSequence);
        initialize();
        fillMatrix();
        backTrack();
    }

    public DynamicProgrammingGraph getGraph() {
        return graph;
    }

    public int getGapCost() {
        return gapCost;
    }

    public void setGapCost(int gapCost) {
        this.gapCost = gapCost;
    }

    public double getScore() {
        return graph.getNode(graph.getFirstSequenceLength(), graph.getSecondSequenceLength()).getScore();
    }

    public ProteinSequence getAlignedFirstSequence() {
        return alignedFirstSequence;
    }

    public ProteinSequence getAlignedSecondSequence() {
        return alignedSecondSequence;
    }

    private void initialize() {
        // initialize initial node
        DynamicProgrammingNode initialNode = new DynamicProgrammingNode(graph.nextNodeIdentifier());
        graph.addNode(initialNode, 0, 0);
        // initialize nodes for first sequence
        DynamicProgrammingNode previousNode = initialNode;
        for (int firstSequenceIndex = 0; firstSequenceIndex < graph.getFirstSequence().getSequence().size(); firstSequenceIndex++) {
            final double score = firstSequenceIndex * gapCost;
            DynamicProgrammingNode currentNode = new DynamicProgrammingNode(graph.nextNodeIdentifier());
            currentNode.setScore(score);
            graph.addNode(currentNode, firstSequenceIndex + 1, 0);
            graph.addEdgeBetween(new DynamicProgrammingEdge(graph.nextEdgeIdentifier(), gapCost, getGraph().getFirstSequence().getLetter(firstSequenceIndex), GAP), currentNode, previousNode);
            previousNode = currentNode;
        }
        // initialize nodes for second sequence
        previousNode = initialNode;
        for (int secondSequenceIndex = 0; secondSequenceIndex < graph.getSecondSequence().getSequence().size(); secondSequenceIndex++) {
            final double score = secondSequenceIndex * gapCost;
            DynamicProgrammingNode currentNode = new DynamicProgrammingNode(graph.nextNodeIdentifier());
            currentNode.setScore(score);
            graph.addNode(currentNode, 0, secondSequenceIndex + 1);
            graph.addEdgeBetween(new DynamicProgrammingEdge(graph.nextEdgeIdentifier(), gapCost, GAP, getGraph().getSecondSequence().getLetter(secondSequenceIndex)), currentNode, previousNode);
            previousNode = currentNode;
        }
        logger.debug("Initialized dynamic programming matrix.");
    }

    private void fillMatrix() {
        for (int firstIndex = 1; firstIndex < getGraph().getFirstSequenceLength() + 1; firstIndex++) {
            for (int secondIndex = 1; secondIndex < getGraph().getSecondSequenceLength() + 1; secondIndex++) {
                determineMatrixElement(firstIndex, secondIndex);
            }
        }
        logger.debug("Calculated alignment sores and paths.");
    }

    private void determineMatrixElement(int i, int j) {

        DynamicProgrammingNode currentNode = new DynamicProgrammingNode(graph.nextNodeIdentifier());
        graph.addNode(currentNode, i, j);
        AminoAcidFamily firstLetter = graph.getFirstSequence().getLetter(i - 1);
        AminoAcidFamily secondLetter = graph.getSecondSequence().getLetter(j - 1);

        DynamicProgrammingNode diagonalNode = graph.getNode(i - 1, j - 1);
        DynamicProgrammingNode leftNode = graph.getNode(i - 1, j);
        DynamicProgrammingNode upperNode = graph.getNode(i, j - 1);

        double substitutionScore = substitutionMatrix.getValueForLabel(firstLetter, secondLetter);
        double diagonalScore = diagonalNode.getScore() + substitutionScore;
        double leftScore = leftNode.getScore() + gapCost;
        double upperScore = upperNode.getScore() + gapCost;

        if (diagonalScore == upperScore) {
            if (diagonalScore == leftScore) {
                // add diagonal, upper and left
                graph.addEdgeBetween(new DynamicProgrammingEdge(graph.nextEdgeIdentifier(), substitutionScore, firstLetter, secondLetter), currentNode, diagonalNode);
                graph.addEdgeBetween(new DynamicProgrammingEdge(graph.nextEdgeIdentifier(), gapCost, GAP, secondLetter), currentNode, upperNode);
                graph.addEdgeBetween(new DynamicProgrammingEdge(graph.nextEdgeIdentifier(), gapCost, firstLetter, GAP), currentNode, leftNode);
                currentNode.setScore(diagonalScore);

            } else if (diagonalScore > leftScore) {
                // add diagonal and upper
                graph.addEdgeBetween(new DynamicProgrammingEdge(graph.nextEdgeIdentifier(), substitutionScore, firstLetter, secondLetter), currentNode, diagonalNode);
                graph.addEdgeBetween(new DynamicProgrammingEdge(graph.nextEdgeIdentifier(), gapCost, GAP, secondLetter), currentNode, upperNode);
                currentNode.setScore(diagonalScore);
            } else {
                // -> left was larger
                // add only left
                graph.addEdgeBetween(new DynamicProgrammingEdge(graph.nextEdgeIdentifier(), gapCost, firstLetter, GAP), currentNode, leftNode);
                currentNode.setScore(leftScore);
            }
        } else if (diagonalScore < upperScore) {
            if (upperScore == leftScore) {
                // add upper and left
                graph.addEdgeBetween(new DynamicProgrammingEdge(graph.nextEdgeIdentifier(), gapCost, GAP, secondLetter), currentNode, upperNode);
                graph.addEdgeBetween(new DynamicProgrammingEdge(graph.nextEdgeIdentifier(), gapCost, firstLetter, GAP), currentNode, leftNode);
                currentNode.setScore(upperScore);
            } else if (upperScore > leftScore) {
                // add upper
                graph.addEdgeBetween(new DynamicProgrammingEdge(graph.nextEdgeIdentifier(), gapCost, GAP, secondLetter), currentNode, upperNode);
                currentNode.setScore(upperScore);
            } else {
                // -> left was larger
                // add left
                graph.addEdgeBetween(new DynamicProgrammingEdge(graph.nextEdgeIdentifier(), gapCost, firstLetter, GAP), currentNode, leftNode);
                currentNode.setScore(leftScore);
            }
        } else if (upperScore == leftScore) {
            // add diagonal
            graph.addEdgeBetween(new DynamicProgrammingEdge(graph.nextEdgeIdentifier(), substitutionScore, firstLetter, secondLetter), currentNode, diagonalNode);
            currentNode.setScore(diagonalScore);
        } else if (diagonalScore == leftScore) {
            // add diagonal and left
            graph.addEdgeBetween(new DynamicProgrammingEdge(graph.nextEdgeIdentifier(), substitutionScore, firstLetter, secondLetter), currentNode, diagonalNode);
            graph.addEdgeBetween(new DynamicProgrammingEdge(graph.nextEdgeIdentifier(), gapCost, firstLetter, GAP), currentNode, leftNode);
            currentNode.setScore(diagonalScore);
        } else if (diagonalScore > leftScore) {
            // add diagonal
            graph.addEdgeBetween(new DynamicProgrammingEdge(graph.nextEdgeIdentifier(), substitutionScore, firstLetter, secondLetter), currentNode, diagonalNode);
            currentNode.setScore(diagonalScore);
        } else {
            // add left
            graph.addEdgeBetween(new DynamicProgrammingEdge(graph.nextEdgeIdentifier(), gapCost, firstLetter, GAP), currentNode, leftNode);
            currentNode.setScore(leftScore);
        }

    }

    private void backTrack() {
        DynamicProgrammingNode lastNode = graph.getNode(graph.getFirstSequenceLength(), graph.getSecondSequenceLength());
        GraphPath<DynamicProgrammingNode, DynamicProgrammingEdge> path = ShortestPathFinder.findBasedOnPredicate(graph, lastNode, node -> node.getIdentifier() == 0);
        Collections.reverse(path.getEdges());
        alignedFirstSequence = new ProteinSequence(path.getEdges().stream().map(DynamicProgrammingEdge::getFirst).collect(Collectors.toList()));
        alignedSecondSequence = new ProteinSequence(path.getEdges().stream().map(DynamicProgrammingEdge::getSecond).collect(Collectors.toList()));
        logger.debug("Backtracked optimal alignment with score {} :\n {} \n {}", lastNode.getScore(), alignedFirstSequence, alignedSecondSequence);
    }

}
