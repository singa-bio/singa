package de.bioforscher.singa.mathematics.algorithms.optimization;

import de.bioforscher.singa.core.utility.Pair;
import de.bioforscher.singa.mathematics.matrices.LabeledMatrix;
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
        initialize(this.labeledCostMatrix.getElements());
        execute();
    }

    private void execute() {
        reduce();
        computeInitialSolution();
        greedyMatch();
        int w = fetchUnmatchedWorker();
        while (w < this.dimension) {
            initializePhase(w);
            executePhase();
            w = fetchUnmatchedWorker();
        }
        int[] result = Arrays.copyOf(this.matchJobByWorker, this.rows);
        for (w = 0; w < result.length; w++) {
            if (result[w] >= this.cols) {
                result[w] = -1;
            }
        }
        assignPairs(result);
    }

    private void assignPairs(int[] result) {
        this.assignedPairs = new ArrayList<>();
        for (int i = 0; i < this.labeledCostMatrix.getRowDimension(); i++) {
            this.assignedPairs.add(new Pair<>(this.labeledCostMatrix.getRowLabel(i), this.labeledCostMatrix.getColumnLabel(result[i])));
        }

    }

    private void executePhase() {
        while (true) {
            int minSlackWorker = -1, minSlackJob = -1;
            double minSlackValue = Double.POSITIVE_INFINITY;
            for (int j = 0; j < this.dimension; j++) {
                if (this.parentWorkerByCommittedJob[j] == -1) {
                    if (this.minSlackValueByJob[j] < minSlackValue) {
                        minSlackValue = this.minSlackValueByJob[j];
                        minSlackWorker = this.minSlackWorkerByJob[j];
                        minSlackJob = j;
                    }
                }
            }
            if (minSlackValue > 0) {
                updateLabeling(minSlackValue);
            }
            this.parentWorkerByCommittedJob[minSlackJob] = minSlackWorker;
            if (this.matchWorkerByJob[minSlackJob] == -1) {
                int committedJob = minSlackJob;
                int parentWorker = this.parentWorkerByCommittedJob[committedJob];
                while (true) {
                    int temp = this.matchJobByWorker[parentWorker];
                    match(parentWorker, committedJob);
                    committedJob = temp;
                    if (committedJob == -1) {
                        break;
                    }
                    parentWorker = this.parentWorkerByCommittedJob[committedJob];
                }
                return;
            } else {
                int worker = this.matchWorkerByJob[minSlackJob];
                this.committedWorkers[worker] = true;
                for (int j = 0; j < this.dimension; j++) {
                    if (this.parentWorkerByCommittedJob[j] == -1) {
                        double slack = this.costMatrix[worker][j] - this.labelByWorker[worker]
                                - this.labelByJob[j];
                        if (this.minSlackValueByJob[j] > slack) {
                            this.minSlackValueByJob[j] = slack;
                            this.minSlackWorkerByJob[j] = worker;
                        }
                    }
                }
            }
        }
    }

    private void updateLabeling(double slack) {
        for (int w = 0; w < this.dimension; w++) {
            if (this.committedWorkers[w]) {
                this.labelByWorker[w] += slack;
            }
        }
        for (int j = 0; j < this.dimension; j++) {
            if (this.parentWorkerByCommittedJob[j] != -1) {
                this.labelByJob[j] -= slack;
            } else {
                this.minSlackValueByJob[j] -= slack;
            }
        }
    }

    private void initializePhase(int w) {
        Arrays.fill(this.committedWorkers, false);
        Arrays.fill(this.parentWorkerByCommittedJob, -1);
        this.committedWorkers[w] = true;
        for (int j = 0; j < this.dimension; j++) {
            this.minSlackValueByJob[j] = this.costMatrix[w][j] - this.labelByWorker[w]
                    - this.labelByJob[j];
            this.minSlackWorkerByJob[j] = w;
        }

    }

    private int fetchUnmatchedWorker() {
        int w;
        for (w = 0; w < this.dimension; w++) {
            if (this.matchJobByWorker[w] == -1) {
                break;
            }
        }
        return w;
    }

    private void greedyMatch() {
        for (int w = 0; w < this.dimension; w++) {
            for (int j = 0; j < this.dimension; j++) {
                if (this.matchJobByWorker[w] == -1 && this.matchWorkerByJob[j] == -1
                        && this.costMatrix[w][j] - this.labelByWorker[w] - this.labelByJob[j] == 0) {
                    match(w, j);
                }
            }
        }
    }

    private void match(int w, int j) {
        this.matchJobByWorker[w] = j;
        this.matchWorkerByJob[j] = w;
    }

    private void initialize(double[][] costMatrix) {
        this.dimension = Math.max(costMatrix.length, costMatrix[0].length);
        this.rows = costMatrix.length;
        this.cols = costMatrix[0].length;
        this.costMatrix = new double[this.dimension][this.dimension];
        for (int w = 0; w < this.dimension; w++) {
            if (w < costMatrix.length) {
                if (costMatrix[w].length != this.cols) {
                    throw new IllegalArgumentException("irregular cost matrix");
                }
                for (int j = 0; j < this.cols; j++) {
                    if (Double.isInfinite(costMatrix[w][j])) {
                        throw new IllegalArgumentException("infinite cost");
                    }
                    if (Double.isNaN(costMatrix[w][j])) {
                        throw new IllegalArgumentException("NaN cost");
                    }
                }
                this.costMatrix[w] = Arrays.copyOf(costMatrix[w], this.dimension);
            } else {
                this.costMatrix[w] = new double[this.dimension];
            }
        }
        this.labelByWorker = new double[this.dimension];
        this.labelByJob = new double[this.dimension];
        this.minSlackWorkerByJob = new int[this.dimension];
        this.minSlackValueByJob = new double[this.dimension];
        this.committedWorkers = new boolean[this.dimension];
        this.parentWorkerByCommittedJob = new int[this.dimension];
        this.matchJobByWorker = new int[this.dimension];
        Arrays.fill(this.matchJobByWorker, -1);
        this.matchWorkerByJob = new int[this.dimension];
        Arrays.fill(this.matchWorkerByJob, -1);
    }

    private void reduce() {
        for (int w = 0; w < this.dimension; w++) {
            double min = Double.POSITIVE_INFINITY;
            for (int j = 0; j < this.dimension; j++) {
                if (this.costMatrix[w][j] < min) {
                    min = this.costMatrix[w][j];
                }
            }
            for (int j = 0; j < this.dimension; j++) {
                this.costMatrix[w][j] -= min;
            }
        }
        double[] min = new double[this.dimension];
        for (int j = 0; j < this.dimension; j++) {
            min[j] = Double.POSITIVE_INFINITY;
        }
        for (int w = 0; w < this.dimension; w++) {
            for (int j = 0; j < this.dimension; j++) {
                if (this.costMatrix[w][j] < min[j]) {
                    min[j] = this.costMatrix[w][j];
                }
            }
        }
        for (int w = 0; w < this.dimension; w++) {
            for (int j = 0; j < this.dimension; j++) {
                this.costMatrix[w][j] -= min[j];
            }
        }
    }

    private void computeInitialSolution() {
        for (int j = 0; j < this.dimension; j++) {
            this.labelByJob[j] = Double.POSITIVE_INFINITY;
        }
        for (int w = 0; w < this.dimension; w++) {
            for (int j = 0; j < this.dimension; j++) {
                if (this.costMatrix[w][j] < this.labelByJob[j]) {
                    this.labelByJob[j] = this.costMatrix[w][j];
                }
            }
        }
    }

    public List<Pair<DataType>> getAssignedPairs() {
        return this.assignedPairs;
    }
}
