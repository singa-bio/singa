package de.bioforscher.singa.mathematics.algorithms.clustering;

import de.bioforscher.singa.mathematics.matrices.LabeledMatrix;
import de.bioforscher.singa.mathematics.matrices.LabeledRegularMatrix;
import de.bioforscher.singa.mathematics.matrices.Matrix;
import de.bioforscher.singa.mathematics.matrices.RegularMatrix;
import de.bioforscher.singa.mathematics.vectors.RegularVector;
import de.bioforscher.singa.mathematics.vectors.Vectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Implementation of the affinity propagation algorithm, according to:
 * <pre>
 * Frey, Brendan J., and Delbert Dueck.
 * "Clustering by passing messages between data points." Science 315.5814 (2007): 972-976.
 * </pre>
 *
 * @param <DataType> The type of the data used for clustering.
 * @author fk
 */
public class AffinityPropagation<DataType> implements Clustering<DataType> {

    private static final Logger logger = LoggerFactory.getLogger(AffinityPropagation.class);
    private static final int MIN_STABLE_EPOCHS = 10;
    private final List<DataType> dataPoints;
    private final int dataSize;
    private final int maximalEpochs;
    private final LabeledMatrix<DataType> distanceMatrix;
    private final double lambda;
    private LabeledMatrix<DataType> similarityMatrix;
    private LabeledMatrix<DataType> availabilityMatrix;
    private LabeledMatrix<DataType> responsibilityMatrix;
    private int epoch;
    private List<List<DataType>> exemplarDecisions;
    private Map<DataType, List<DataType>> clusters;

    private AffinityPropagation(Builder<DataType> builder) {
        logger.info("affinity propagation initialized with {} data points", builder.dataPoints.size());
        dataPoints = builder.dataPoints;
        dataSize = dataPoints.size();
        similarityMatrix = builder.matrix;
        lambda = builder.lambda;
        double selfSimilarity = builder.selfSimilarity;
        maximalEpochs = builder.maximalEpochs;
        checkInput(dataPoints, similarityMatrix);
        double[][] invertedValues;
        // convert to similarity matrix if distance matrix provided
        if (builder.distance) {
            distanceMatrix = similarityMatrix;
            invertedValues = similarityMatrix.additivelyInvert().getElements();
            // assign inverted self similarities
            for (int i = 0; i < invertedValues.length; i++) {
                invertedValues[i][i] = -selfSimilarity;
            }
        } else {
            invertedValues = similarityMatrix.getElements();
            distanceMatrix = new LabeledRegularMatrix<>(new RegularMatrix(invertedValues).additivelyInvert().getElements());
            distanceMatrix.setRowLabels(dataPoints);
            distanceMatrix.setColumnLabels(dataPoints);
            // assign self similarities
            for (int i = 0; i < invertedValues.length; i++) {
                invertedValues[i][i] = selfSimilarity;
            }
        }
        similarityMatrix = new LabeledRegularMatrix<>(invertedValues);
        similarityMatrix.setRowLabels(dataPoints);
        similarityMatrix.setColumnLabels(dataPoints);
        initialize();
        run();
    }

    public static <DataType> DataStep<DataType> create() {
        return new Builder<>();
    }

    @Override
    public List<DataType> getDataPoints() {
        return dataPoints;
    }

    @Override
    public LabeledMatrix<DataType> getDistanceMatrix() {
        return distanceMatrix;
    }

    @Override
    public Map<DataType, List<DataType>> getClusters() {
        return clusters;
    }

    private void initialize() {
        // initialize responsibility matrix with zeros
        responsibilityMatrix = new LabeledRegularMatrix<>(new double[dataSize][dataSize]);
        responsibilityMatrix.setRowLabels(dataPoints);
        responsibilityMatrix.setColumnLabels(dataPoints);

        // initialize availability matrix with zeros
        availabilityMatrix = new LabeledRegularMatrix<>(new double[dataSize][dataSize]);
        availabilityMatrix.setRowLabels(dataPoints);
        availabilityMatrix.setColumnLabels(dataPoints);

        exemplarDecisions = new ArrayList<>();
    }

    /**
     * Checks whether the data matches the labels of the provided matrix.
     *
     * @param data The data to be checked.
     * @param matrix The matrix that contains similarity/distance values.
     */
    private void checkInput(List<DataType> data, LabeledMatrix<DataType> matrix) {
        List<DataType> rowLabels = matrix.getRowLabels();
        Objects.requireNonNull(rowLabels);
        if (!data.equals(rowLabels)) {
            throw new IllegalArgumentException("The data does not match the labels of the provided matrix.");
        }
    }

    /**
     * Starts affinity propagation clustering until convergence or maximal epochs are reached.
     */
    private void run() {
        while (epoch < maximalEpochs) {
            updateResponsibilities();
            updateAvailabilities();
            assignExemplars();
            assignClusters();
            if (isConverged()) {
                break;
            }
            epoch++;
            if (epoch == maximalEpochs) {
                logger.info("terminating after reaching maximal epoch limit");
            }
        }
        logger.info("obtained {} clusters", clusters.size());
    }

    /**
     * Assigns cluster members of exemplars of last round, that is finding the closest exemplar for each data point.
     */
    private void assignClusters() {
        clusters = new HashMap<>();
        for (DataType currentDataPoint : dataPoints) {
            double bestSimilarity = -Double.MAX_VALUE;
            DataType bestExemplar = null;
            for (DataType exemplar : exemplarDecisions.get(exemplarDecisions.size() - 1)) {
                if (exemplar.equals(currentDataPoint)) {
                    bestExemplar = currentDataPoint;
                    break;
                }
                double similarity = similarityMatrix.getValueForLabel(currentDataPoint, exemplar);
                if (similarity > bestSimilarity) {
                    bestSimilarity = similarity;
                    bestExemplar = exemplar;
                }
            }
            if (clusters.containsKey(bestExemplar)) {
                clusters.get(bestExemplar).add(currentDataPoint);
            } else {
                List<DataType> cluster = new ArrayList<>();
                cluster.add(currentDataPoint);
                clusters.put(bestExemplar, cluster);
            }
        }
    }

    private void assignExemplars() {
        List<DataType> exemplars = new ArrayList<>();
        // calculate R+A
        LabeledMatrix<DataType> ra = new LabeledRegularMatrix<>(responsibilityMatrix.add(availabilityMatrix).getElements());
        ra.setRowLabels(dataPoints);
        ra.setColumnLabels(dataPoints);
        // obtain exemplars from RA (all positive values on principal diagonal)
        for (int i = 0; i < ra.getRowDimension(); i++) {
            if (ra.getElement(i, i) > 0) {
                exemplars.add(dataPoints.get(i));
            }
        }
        // store RA of each round for convergence check
        exemplarDecisions.add(exemplars);
    }

    /**
     * Updates the responsibilities of the current round.
     */
    private void updateResponsibilities() {
        double[][] updatedResponsibilities = new double[dataSize][dataSize];
        Matrix as = similarityMatrix.add(availabilityMatrix);
        for (int i = 0; i < dataPoints.size(); i++) {
            for (int j = 0; j < dataPoints.size(); j++) {
                double[] row = Arrays.copyOf(as.getRow(i).getElements(), as.getRow(i).getElements().length);
                row[j] = -Double.MAX_VALUE;
                RegularVector rowVector = new RegularVector(row);
                int positionOfMax = Vectors.getIndexWithMaximalElement(rowVector);
                double maxValue = rowVector.getElement(positionOfMax);
                double finalValue = similarityMatrix.getElement(i, j) - maxValue;
                updatedResponsibilities[i][j] = finalValue;
            }
        }
        LabeledMatrix<DataType> updatedResponsibilityMatrix = new LabeledRegularMatrix<>(updatedResponsibilities);
        responsibilityMatrix = applyLambda(updatedResponsibilityMatrix, responsibilityMatrix);
    }

    /**
     * Updates the availabilities of the current round.
     */
    private void updateAvailabilities() {
        double[][] updatedAvailabilities = new double[dataSize][dataSize];
        for (int i = 0; i < dataSize; i++) {
            for (int j = 0; j < dataSize; j++) {
                double[] column = responsibilityMatrix.getColumn(i).getElements();
                double sum = 0.0;
                for (int k = 0; k < column.length; k++) {
                    if (k == i || k == j) {
                        continue;
                    }
                    if (column[k] > 0) {
                        sum += column[k];
                    }
                }
                if (i == j) {
                    updatedAvailabilities[j][i] = sum;
                } else {
                    sum += responsibilityMatrix.getElement(i, i);
                    if (sum < 0) {
                        updatedAvailabilities[j][i] = sum;
                    } else {
                        updatedAvailabilities[j][i] = 0.0;
                    }
                }
            }
        }
        LabeledMatrix<DataType> updatedAvailabilityMatrix = new LabeledRegularMatrix<>(updatedAvailabilities);
        availabilityMatrix = applyLambda(updatedAvailabilityMatrix, availabilityMatrix);
    }

    /**
     * Applies the dampening factor M=M*(1-lambda)+M'(lambda) to avoid oscillating.
     *
     * @param updatedMatrix The updated matrix M.
     * @param oldMatrix The old matrix M'.
     * @return The updated matrix with dampening factor applied.
     */
    private LabeledMatrix<DataType> applyLambda(LabeledMatrix<DataType> updatedMatrix, LabeledMatrix<DataType> oldMatrix) {
        LabeledMatrix<DataType> dampenedMatrix = new LabeledRegularMatrix<>(updatedMatrix.multiply(1 - lambda).add(oldMatrix.multiply(lambda)).getElements());
        dampenedMatrix.setRowLabels(dataPoints);
        dampenedMatrix.setColumnLabels(dataPoints);
        return dampenedMatrix;
    }

    /**
     * Checks for the convergence of the algorithm, that is that exemplar choice does not change for MIN_STABLE_EPOCHS.
     *
     * @return True if converged.
     */
    private boolean isConverged() {
        boolean converged;
        if (exemplarDecisions.size() < MIN_STABLE_EPOCHS) {
            return false;
        } else {
            converged = true;
            int lowerBound = exemplarDecisions.size() - MIN_STABLE_EPOCHS;
            for (int i = exemplarDecisions.size() - 1; i > lowerBound; i--) {
                if (!exemplarDecisions.get(i).equals(exemplarDecisions.get(i - 1))) {
                    converged = false;
                }
            }
        }
        if (converged) {
            logger.debug("converged in epoch {}/{}", epoch, maximalEpochs);
        } else {
            logger.debug("not converged in epoch {}/{}", epoch, maximalEpochs);
        }
        return converged;
    }


    public LabeledMatrix<DataType> getSimilarityMatrix() {
        return similarityMatrix;
    }

    public void setSimilarityMatrix(LabeledMatrix<DataType> similarityMatrix) {
        this.similarityMatrix = similarityMatrix;
    }

    public LabeledMatrix<DataType> getAvailabilityMatrix() {
        return availabilityMatrix;
    }

    public void setAvailabilityMatrix(LabeledMatrix<DataType> availabilityMatrix) {
        this.availabilityMatrix = availabilityMatrix;
    }

    public LabeledMatrix<DataType> getResponsibilityMatrix() {
        return responsibilityMatrix;
    }

    public void setResponsibilityMatrix(LabeledMatrix<DataType> responsibilityMatrix) {
        this.responsibilityMatrix = responsibilityMatrix;
    }

    public interface DataStep<DataType> {
        MatrixStep<DataType> dataPoints(List<DataType> dataPoints);
    }

    public interface MatrixStep<DataType> {
        DistanceStep<DataType> matrix(LabeledMatrix<DataType> matrix);
    }

    public interface DistanceStep<DataType> {
        ParameterStep<DataType> isDistance(boolean isDistance);
    }

    public interface ParameterStep<DataType> {
        ParameterStep<DataType> selfSimilarity(double selfSimilarity);

        ParameterStep<DataType> lambda(double lambda);

        ParameterStep<DataType> maximalEpochs(int maximalEpochs);

        AffinityPropagation<DataType> run();
    }

    public static class Builder<DataType> implements DataStep<DataType>, MatrixStep<DataType>, DistanceStep<DataType>, ParameterStep<DataType> {

        private static final double DEFAULT_SELF_SIMILARITY = -0.5;
        private static final double DEFAULT_LAMBDA = 0.5;
        private static final int DEFAULT_MAXIMAL_EPOCHS = 1000;

        private List<DataType> dataPoints;
        private LabeledMatrix<DataType> matrix;
        private double selfSimilarity = DEFAULT_SELF_SIMILARITY;
        private double lambda = DEFAULT_LAMBDA;
        private int maximalEpochs = DEFAULT_MAXIMAL_EPOCHS;
        private boolean distance;

        @Override
        public MatrixStep<DataType> dataPoints(List<DataType> dataPoints) {
            this.dataPoints = dataPoints;
            return this;
        }

        @Override
        public DistanceStep<DataType> matrix(LabeledMatrix<DataType> matrix) {
            this.matrix = matrix;
            return this;
        }

        @Override
        public ParameterStep<DataType> selfSimilarity(double selfSimilarity) {
            this.selfSimilarity = selfSimilarity;
            return this;

        }

        @Override
        public ParameterStep<DataType> isDistance(boolean distance) {
            this.distance = distance;
            return this;
        }

        @Override
        public ParameterStep<DataType> lambda(double lambda) {
            this.lambda = lambda;
            return this;
        }

        @Override
        public ParameterStep<DataType> maximalEpochs(int maximalEpochs) {
            this.maximalEpochs = maximalEpochs;
            return this;
        }

        @Override
        public AffinityPropagation<DataType> run() {
            return new AffinityPropagation<>(this);
        }
    }
}
