package de.bioforscher.singa.sequence.algorithms.alignment;

import de.bioforscher.singa.sequence.model.ProteinSequence;
import de.bioforscher.singa.structure.algorithms.superimposition.scores.SubstitutionMatrix;

/**
 * @author cl
 */
public class NeedlemanWunschGraph {

    private static final int DEFAULT_GAP_COST = -8;
    private final SubstitutionMatrix substitutionMatrix;

    private int gapCost = DEFAULT_GAP_COST;

    private DynamicProgrammingGraph graph;
    private DynamicProgrammingNode previousNode;

    public NeedlemanWunschGraph(SubstitutionMatrix substitutionMatrix, ProteinSequence firstSequence, ProteinSequence secondSequence) {
        this.substitutionMatrix = substitutionMatrix;
        graph = new DynamicProgrammingGraph(firstSequence, secondSequence);
        initialize();
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
        graph.addNode(initialNode);
        // initialize nodes for first sequence
        previousNode = initialNode;
        for (int firstSequenceIndex = 0; firstSequenceIndex < graph.getFirstSequence().getSequence().size() + 1; firstSequenceIndex++) {
            final double weight = firstSequenceIndex * gapCost;
            previousNode = appendNode(weight, firstSequenceIndex, 0);
        }
        // initialize nodes for second sequence
        previousNode = initialNode;
        for (int secondSequenceIndex = 0; secondSequenceIndex < graph.getSecondSequence().getSequence().size() + 1; secondSequenceIndex++) {
            final double weight = secondSequenceIndex * gapCost;
            previousNode = appendNode(weight, 0, secondSequenceIndex);
        }
    }

    private void fillMatrix() {
// TODO continue here
//        for (int i = 1; i < matrix.getRowDimension(); i++) {
//            for (int j = 1; j < matrix.getColumnDimension(); j++) {
//                elements[i][j] = getMatrixElement(elements, i, j);
//                System.out.println();
//            }
//        }
//        LabeledMatrix<NeedlemanWunsch.MatrixLabel> filledMatrix = new LabeledRegularMatrix<>(elements);
//        filledMatrix.setRowLabels(matrix.getRowLabels());
//        filledMatrix.setColumnLabels(matrix.getColumnLabels());
//        matrix = filledMatrix;
    }

    private DynamicProgrammingNode appendNode(double weight, int firstIndex, int secondIndex) {
        DynamicProgrammingNode currentNode = new DynamicProgrammingNode(graph.nextNodeIdentifier());
        graph.addNode(currentNode, firstIndex, secondIndex);
        graph.addEdgeBetween(currentNode, previousNode, weight);
        return currentNode;
    }

    public DynamicProgrammingGraph getGraph() {
        return graph;
    }

}
