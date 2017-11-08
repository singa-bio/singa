package de.bioforscher.singa.mathematics.algorithms.optimization;

import de.bioforscher.singa.core.utility.Pair;
import de.bioforscher.singa.mathematics.matrices.LabeledMatrix;
import de.bioforscher.singa.mathematics.matrices.LabeledSymmetricMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An implementation of the Kuhn-Munkres algorithm, or Hungarian algorithm, to solve the assignment problem. This
 * implementation determines the assignment with minimal costs according to a given cost matrix in O(n^3). The
 * implementation was adapted from:
 * <pre>
 * Copyright (c) 2012 Kevin L. Stern
 * <a href="https://github.com/KevinStern/software-and-algorithms">https://github.com/KevinStern/software-and-algorithms</a>
 * <pre>
 * @param <DataType> The type of the data that is assigned.
 * @author fk
 */
public class KuhnMunkres<DataType> {

    private static final Logger logger = LoggerFactory.getLogger(KuhnMunkres.class);

    private final LabeledMatrix<DataType> labeledCostMatrix;
    private double[][] costMatrix;
    private int dimension;
    private int cols;
    private int rows;
    private double[] labelByWorker;
    private double[] labelByJob;
    private int[] minSlackWorkerByJob;
    private double[] minSlackValueByJob;
    private boolean[] committedWorkers;
    private int[] parentWorkerByCommittedJob;
    private int[] matchJobByWorker;
    private int[] matchWorkerByJob;
    private List<Pair<DataType>> assignedPairs;

    public KuhnMunkres(LabeledMatrix<DataType> labeledCostMatrix) {
        logger.info("calculating optimal assignment for cost matrix\n{}", labeledCostMatrix.getStringRepresentation());
        this.labeledCostMatrix = labeledCostMatrix;
        if (this.labeledCostMatrix instanceof LabeledSymmetricMatrix) {
            throw new IllegalArgumentException("cost matrix cannot be symmetric because elements cannot be assigned to themselves");
        } else {
            initialize(this.labeledCostMatrix.getElements());
        }
        execute();
    }

    private void execute() {
        reduce();
        computeInitialSolution();
        greedyMatch();
        int w = fetchUnmatchedWorker();
        while (w < dimension) {
            initializePhase(w);
            executePhase();
            w = fetchUnmatchedWorker();
        }
        int[] result = Arrays.copyOf(matchJobByWorker, rows);
        for (w = 0; w < result.length; w++) {
            if (result[w] >= cols) {
                result[w] = -1;
            }
        }
        assignPairs(result);
    }

    private void assignPairs(int[] result) {
        assignedPairs = new ArrayList<>();
        for (int i = 0; i < labeledCostMatrix.getRowDimension(); i++) {
            assignedPairs.add(new Pair<>(labeledCostMatrix.getRowLabel(i), labeledCostMatrix.getColumnLabel(result[i])));
        }

    }

    private void executePhase() {
        while (true) {
            int minSlackWorker = -1, minSlackJob = -1;
            double minSlackValue = Double.POSITIVE_INFINITY;
            for (int j = 0; j < dimension; j++) {
                if (parentWorkerByCommittedJob[j] == -1) {
                    if (minSlackValueByJob[j] < minSlackValue) {
                        minSlackValue = minSlackValueByJob[j];
                        minSlackWorker = minSlackWorkerByJob[j];
                        minSlackJob = j;
                    }
                }
            }
            if (minSlackValue > 0) {
                updateLabeling(minSlackValue);
            }
            parentWorkerByCommittedJob[minSlackJob] = minSlackWorker;
            if (matchWorkerByJob[minSlackJob] == -1) {
                int committedJob = minSlackJob;
                int parentWorker = parentWorkerByCommittedJob[committedJob];
                while (true) {
                    int temp = matchJobByWorker[parentWorker];
                    match(parentWorker, committedJob);
                    committedJob = temp;
                    if (committedJob == -1) {
                        break;
                    }
                    parentWorker = parentWorkerByCommittedJob[committedJob];
                }
                return;
            } else {
                int worker = matchWorkerByJob[minSlackJob];
                committedWorkers[worker] = true;
                for (int j = 0; j < dimension; j++) {
                    if (parentWorkerByCommittedJob[j] == -1) {
                        double slack = costMatrix[worker][j] - labelByWorker[worker]
                                - labelByJob[j];
                        if (minSlackValueByJob[j] > slack) {
                            minSlackValueByJob[j] = slack;
                            minSlackWorkerByJob[j] = worker;
                        }
                    }
                }
            }
        }
    }

    private void updateLabeling(double slack) {
        for (int w = 0; w < dimension; w++) {
            if (committedWorkers[w]) {
                labelByWorker[w] += slack;
            }
        }
        for (int j = 0; j < dimension; j++) {
            if (parentWorkerByCommittedJob[j] != -1) {
                labelByJob[j] -= slack;
            } else {
                minSlackValueByJob[j] -= slack;
            }
        }
    }

    private void initializePhase(int w) {
        Arrays.fill(committedWorkers, false);
        Arrays.fill(parentWorkerByCommittedJob, -1);
        committedWorkers[w] = true;
        for (int j = 0; j < dimension; j++) {
            minSlackValueByJob[j] = costMatrix[w][j] - labelByWorker[w]
                    - labelByJob[j];
            minSlackWorkerByJob[j] = w;
        }

    }

    private int fetchUnmatchedWorker() {
        int w;
        for (w = 0; w < dimension; w++) {
            if (matchJobByWorker[w] == -1) {
                break;
            }
        }
        return w;
    }

    private void greedyMatch() {
        for (int w = 0; w < dimension; w++) {
            for (int j = 0; j < dimension; j++) {
                if (matchJobByWorker[w] == -1 && matchWorkerByJob[j] == -1
                        && costMatrix[w][j] - labelByWorker[w] - labelByJob[j] == 0) {
                    match(w, j);
                }
            }
        }
    }

    private void match(int w, int j) {
        matchJobByWorker[w] = j;
        matchWorkerByJob[j] = w;
    }

    private void initialize(double[][] costMatrix) {
        dimension = Math.max(costMatrix.length, costMatrix[0].length);
        rows = costMatrix.length;
        cols = costMatrix[0].length;
        this.costMatrix = new double[dimension][dimension];
        for (int w = 0; w < dimension; w++) {
            if (w < costMatrix.length) {
                if (costMatrix[w].length != cols) {
                    throw new IllegalArgumentException("irregular cost matrix");
                }
                for (int j = 0; j < cols; j++) {
                    if (Double.isInfinite(costMatrix[w][j])) {
                        throw new IllegalArgumentException("infinite cost");
                    }
                    if (Double.isNaN(costMatrix[w][j])) {
                        throw new IllegalArgumentException("NaN cost");
                    }
                }
                this.costMatrix[w] = Arrays.copyOf(costMatrix[w], dimension);
            } else {
                this.costMatrix[w] = new double[dimension];
            }
        }
        labelByWorker = new double[dimension];
        labelByJob = new double[dimension];
        minSlackWorkerByJob = new int[dimension];
        minSlackValueByJob = new double[dimension];
        committedWorkers = new boolean[dimension];
        parentWorkerByCommittedJob = new int[dimension];
        matchJobByWorker = new int[dimension];
        Arrays.fill(matchJobByWorker, -1);
        matchWorkerByJob = new int[dimension];
        Arrays.fill(matchWorkerByJob, -1);
    }

    private void reduce() {
        for (int w = 0; w < dimension; w++) {
            double min = Double.POSITIVE_INFINITY;
            for (int j = 0; j < dimension; j++) {
                if (costMatrix[w][j] < min) {
                    min = costMatrix[w][j];
                }
            }
            for (int j = 0; j < dimension; j++) {
                costMatrix[w][j] -= min;
            }
        }
        double[] min = new double[dimension];
        for (int j = 0; j < dimension; j++) {
            min[j] = Double.POSITIVE_INFINITY;
        }
        for (int w = 0; w < dimension; w++) {
            for (int j = 0; j < dimension; j++) {
                if (costMatrix[w][j] < min[j]) {
                    min[j] = costMatrix[w][j];
                }
            }
        }
        for (int w = 0; w < dimension; w++) {
            for (int j = 0; j < dimension; j++) {
                costMatrix[w][j] -= min[j];
            }
        }
    }

    private void computeInitialSolution() {
        for (int j = 0; j < dimension; j++) {
            labelByJob[j] = Double.POSITIVE_INFINITY;
        }
        for (int w = 0; w < dimension; w++) {
            for (int j = 0; j < dimension; j++) {
                if (costMatrix[w][j] < labelByJob[j]) {
                    labelByJob[j] = costMatrix[w][j];
                }
            }
        }
    }

    public List<Pair<DataType>> getAssignedPairs() {
        return assignedPairs;
    }
}
