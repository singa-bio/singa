package de.bioforscher.singa.sequence.algorithms.alignment;

import de.bioforscher.singa.core.utility.Pair;
import de.bioforscher.singa.mathematics.matrices.LabeledMatrix;
import de.bioforscher.singa.mathematics.matrices.LabeledRegularMatrix;
import de.bioforscher.singa.sequence.model.interfaces.Sequence;
import de.bioforscher.singa.structure.algorithms.superimposition.scores.SubstitutionMatrix;
import de.bioforscher.singa.structure.model.families.StructuralFamily;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.DoubleStream;

/**
 * @author fk
 */
public class NeedlemanWunsch {

    private static final int GAP_COST = -8;
    private final SubstitutionMatrix substitutionMatrix;
    private final Pair<Sequence<? extends StructuralFamily<?>>> sequencePair;
    private LabeledMatrix<MatrixLabel> matrix;
    private Map<Pair<Integer>, Pair<Integer>> paths;

    public NeedlemanWunsch(SubstitutionMatrix substitutionMatrix, Pair<Sequence<? extends StructuralFamily<?>>> sequencePair) {
        this.substitutionMatrix = substitutionMatrix;
        this.sequencePair = sequencePair;
        paths = new HashMap<>();
        initializeMatrix();
        fillMatrix();
    }

    private void fillMatrix() {
        double[][] elements = matrix.getCopy().getElements();
        for (int i = 1; i < matrix.getRowDimension(); i++) {
            for (int j = 1; j < matrix.getColumnDimension(); j++) {
                elements[i][j] = getMatrixElement(elements, i, j);
                System.out.println();
            }
        }
        LabeledMatrix<MatrixLabel> filledMatrix = new LabeledRegularMatrix<>(elements);
        filledMatrix.setRowLabels(matrix.getRowLabels());
        filledMatrix.setColumnLabels(matrix.getColumnLabels());
        matrix = filledMatrix;
    }

    private double getMatrixElement(double[][] elements, int i, int j) {
        double first = elements[i - 1][j - 1]
                + getScore(sequencePair.getFirst().getSequence().get(i - 1), sequencePair.getSecond().getSequence().get(j - 1));
        double second = elements[i - 1][j] + GAP_COST;
        double third = elements[i][j - 1] + GAP_COST;

        double max = DoubleStream.of(first, second, third).max().getAsDouble();

        Pair<Integer> key = new Pair<>(i, j);
        if (first == max) {
            paths.put(key, new Pair<>(i - 1, j - 1));
        } else if (second == max) {
            paths.put(key, new Pair<>(i - 1, j));
        } else if (third == max) {
            paths.put(key, new Pair<>(i, j - 1));
        }

        return max;
    }

    private double getScore(StructuralFamily<?> firstStructuralFamily, StructuralFamily<?> secondStructuralFamily) {
        // TODO implement matrix scoring here
        if (firstStructuralFamily == secondStructuralFamily) {
            return 1;
        }
        return -1;
    }

    private void initializeMatrix() {
        Sequence<? extends StructuralFamily<?>> firstSequence = sequencePair.getFirst();
        Sequence<? extends StructuralFamily<?>> secondSequence = sequencePair.getSecond();

        double[][] elements = new double[firstSequence.getLength() + 1][secondSequence.getLength() + 1];

        for (int i = 0; i < firstSequence.getSequence().size() + 1; i++) {
            elements[i][0] = i * GAP_COST;
        }

        for (int i = 0; i < secondSequence.getSequence().size() + 1; i++) {
            elements[0][i] = i * GAP_COST;
        }

        List<MatrixLabel> rowLabels = new ArrayList<>();
        rowLabels.add(MatrixLabel.GAP);
        for (int i = 0; i < firstSequence.getSequence().size(); i++) {
            rowLabels.add(new MatrixLabel(i, firstSequence.getSequence().get(i)));
        }
        List<MatrixLabel> columnLabels = new ArrayList<>();
        columnLabels.add(MatrixLabel.GAP);
        for (int i = 0; i < secondSequence.getSequence().size(); i++) {
            columnLabels.add(new MatrixLabel(i, secondSequence.getSequence().get(i)));
        }

        matrix = new LabeledRegularMatrix<>(elements);
        matrix.setRowLabels(rowLabels);
        matrix.setColumnLabels(columnLabels);
    }

    public LabeledMatrix<MatrixLabel> getMatrix() {
        return matrix;
    }

    private static class MatrixLabel {

        private static final MatrixLabel GAP = new MatrixLabel(0, null);

        private int position;
        private StructuralFamily<?> label;

        public MatrixLabel(int position, StructuralFamily<?> label) {
            this.position = position;
            this.label = label;
        }

        @Override
        public String toString() {
            if (label != null) {
                return label.getOneLetterCode();
            } else {
                return "";
            }
        }
    }
}
