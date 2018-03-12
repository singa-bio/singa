package de.bioforscher.singa.sequence.algorithms.alignment;

import de.bioforscher.singa.mathematics.matrices.LabeledSymmetricMatrix;
import de.bioforscher.singa.sequence.model.ProteinSequence;
import de.bioforscher.singa.structure.algorithms.superimposition.scores.SubstitutionMatrix;
import de.bioforscher.singa.structure.model.families.AminoAcidFamily;
import de.bioforscher.singa.structure.model.families.StructuralFamily;

import static de.bioforscher.singa.structure.model.families.AminoAcidFamily.GAP;

/**
 * @author cl
 */
public class NeedlemanWunschGraph {

    private static final int DEFAULT_GAP_COST = -8;
    private final LabeledSymmetricMatrix<StructuralFamily> substitutionMatrix;

    private int gapCost = DEFAULT_GAP_COST;

    private DynamicProgrammingGraph graph;
    private DynamicProgrammingNode previousNode;

    public NeedlemanWunschGraph(SubstitutionMatrix substitutionMatrix, ProteinSequence firstSequence, ProteinSequence secondSequence) {
        this.substitutionMatrix = substitutionMatrix.getMatrix();
        graph = new DynamicProgrammingGraph(firstSequence, secondSequence);
        initialize();
        fillMatrix();
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

    private void initialize() {
        // initialize initial node
        DynamicProgrammingNode initialNode = new DynamicProgrammingNode(graph.nextNodeIdentifier());
        graph.addNode(initialNode, 0, 0);
        // initialize nodes for first sequence
        previousNode = initialNode;
        for (int firstSequenceIndex = 1; firstSequenceIndex < graph.getFirstSequence().getSequence().size(); firstSequenceIndex++) {
            final double score = firstSequenceIndex * gapCost;
            DynamicProgrammingNode currentNode = new DynamicProgrammingNode(graph.nextNodeIdentifier());
            currentNode.setScore(score);
            graph.addNode(currentNode, firstSequenceIndex, 0);
            graph.addEdgeBetween(new DynamicProgrammingEdge(graph.nextEdgeIdentifier(), gapCost, getGraph().getFirstSequence().getLetter(firstSequenceIndex), GAP), currentNode, previousNode);
            previousNode = currentNode;
        }
        // initialize nodes for second sequence
        previousNode = initialNode;
        for (int secondSequenceIndex = 1; secondSequenceIndex < graph.getSecondSequence().getSequence().size(); secondSequenceIndex++) {
            final double score = secondSequenceIndex * gapCost;
            DynamicProgrammingNode currentNode = new DynamicProgrammingNode(graph.nextNodeIdentifier());
            currentNode.setScore(score);
            graph.addNode(currentNode, 0, secondSequenceIndex);
            graph.addEdgeBetween(new DynamicProgrammingEdge(graph.nextEdgeIdentifier(), gapCost, GAP, getGraph().getSecondSequence().getLetter(secondSequenceIndex)), currentNode, previousNode);
            previousNode = currentNode;
        }
    }

    private void fillMatrix() {
        for (int firstIndex = 1; firstIndex < getGraph().getFirstSequenceLength(); firstIndex++) {
            for (int secondIndex = 1; secondIndex < getGraph().getSecondSequenceLength(); secondIndex++) {
                determineMatrixElement(firstIndex, secondIndex);
            }
        }
    }

    private void determineMatrixElement(int i, int j) {

        DynamicProgrammingNode currentNode = new DynamicProgrammingNode(graph.nextNodeIdentifier());
        graph.addNode(currentNode, i, j);
        AminoAcidFamily firstLetter = graph.getFirstSequence().getLetter(i);
        AminoAcidFamily secondLetter = graph.getSecondSequence().getLetter(j);

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

    public void backTrack() {




    }



}
