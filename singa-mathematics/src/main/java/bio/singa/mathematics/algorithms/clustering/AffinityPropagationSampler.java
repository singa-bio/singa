package bio.singa.mathematics.algorithms.clustering;

import bio.singa.mathematics.matrices.LabeledMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class AffinityPropagationSampler<DataType> {

    private static final Logger logger = LoggerFactory.getLogger(AffinityPropagationSampler.class);

    private static final double DEFAULT_STEP_SIZE = 1.0;

    private final Deque<Double> samplingPoints;
    private final NavigableMap<Double, Integer> samplingResults;
    private final List<DataType> dataPoints;
    private final LabeledMatrix<DataType> matrix;
    private double upperBound = Double.POSITIVE_INFINITY;
    private double lowerBound = Double.NEGATIVE_INFINITY;
    private double stepSize;
    private double currentSelfSimilarity;

    public AffinityPropagationSampler(List<DataType> dataPoints, LabeledMatrix<DataType> matrix) {
        this.dataPoints = dataPoints;
        this.matrix = matrix;
        samplingPoints = new ArrayDeque<>();
        samplingResults = new TreeMap<>();
        stepSize = DEFAULT_STEP_SIZE;
        throw new UnsupportedOperationException("Sampling of affinity propagation is currently not supported");
        // run();
    }

    private void run() {
        AffinityPropagation<DataType> initialRun = AffinityPropagation.<DataType>create()
                .dataPoints(dataPoints)
                .matrix(matrix)
                .isDistance(false)
                .selfSimilarityByMedian()
                .run();
        currentSelfSimilarity = initialRun.getSelfSimilarity();
        takeSample(currentSelfSimilarity, initialRun.getClusters().size());

        checkForBoundary(initialRun);
        determineNextSamplingPoint();
        sample();
    }

    private void checkForBoundary(AffinityPropagation<DataType> run) {
        if (run.getClusters().size() == dataPoints.size()) {
            upperBound = currentSelfSimilarity;
        }
        if (run.getClusters().size() == 1) {
            lowerBound = currentSelfSimilarity;
        }
    }

    private void sample() {
        int tries = 0;
        while (!samplingPoints.isEmpty() && tries < 50) {
            double selfSimilarity = samplingPoints.pop();
            AffinityPropagation<DataType> run = AffinityPropagation.<DataType>create()
                    .dataPoints(dataPoints)
                    .matrix(matrix)
                    .isDistance(false)
                    .selfSimilarity(selfSimilarity)
                    .run();
            takeSample(selfSimilarity, run.getClusters().size());
            currentSelfSimilarity = selfSimilarity;
            checkForBoundary(run);
            determineStepWidth();
            // determineNextSamplingPoint();
            tries++;
        }
        // out
        for (Map.Entry<Double, Integer> doubleIntegerEntry : samplingResults.entrySet()) {
            // System.out.println(doubleIntegerEntry.getKey() + ", " + doubleIntegerEntry.getValue());
        }
    }

    private void determineStepWidth() {
        // currently at the lower bond
        if (currentSelfSimilarity == lowerBound) {
            // try to go up
            double nextStep = currentSelfSimilarity + stepSize;
            // get next higher entry
            Map.Entry<Double, Integer> higherEntry = samplingResults.higherEntry(currentSelfSimilarity);
            // going up results in crossing upper bound
            if (nextStep > upperBound) {
                // reduce step size
                stepSize = (nextStep - upperBound) / 2.0;
                determineStepWidth();
            }
            // if no next entry is available
            if (higherEntry == null) {
                // add a sampling point to the stack
                requestSampling(nextStep);
                return;
            }
            // calculate delta
            int lowerClusterNumber = samplingResults.get(currentSelfSimilarity);
            int upperClusterNumber = higherEntry.getValue();
            int delta = upperClusterNumber - lowerClusterNumber;
            // no plateau
            if (delta == 0) {
                stepSize *= 1.2;
            }
            requestSampling(nextStep);

        } else if (currentSelfSimilarity == upperBound) {
            // currently at upper bond
            // try to go down
            double nextStep = currentSelfSimilarity - stepSize;
            // get next lower entry
            Map.Entry<Double, Integer> lowerEntry = samplingResults.lowerEntry(currentSelfSimilarity);
            // going up results in crossing lower bound
            if (nextStep < lowerBound) {
                // reduce step size
                stepSize = (nextStep - upperBound) / 2.0;
                determineStepWidth();
            }
            // if no next entry is available
            if (lowerEntry == null) {
                // add a sampling point to the stack
                requestSampling(nextStep);
                return;
            }
            // calculate delta
            int lowerClusterNumber = lowerEntry.getValue();
            int upperClusterNumber = samplingResults.get(currentSelfSimilarity);
            int delta = upperClusterNumber - lowerClusterNumber;
            // no plateau
            if (delta == 0) {
                stepSize *= 1.2;
            }
            requestSampling(nextStep);
        } else {
            determineNextSamplingPoint();
        }

    }

    private void requestSampling(double selfSimilarity) {
        logger.trace("Requesting sample for {}", selfSimilarity);
        if (selfSimilarity > upperBound && selfSimilarity < lowerBound &&
                samplingResults.keySet().contains(selfSimilarity)) {
            samplingPoints.add(selfSimilarity);
        } else {
            // check next closer value
            double distanceToUpper = upperBound - selfSimilarity;
            double distanceToLower = selfSimilarity - lowerBound;
            logger.trace("adjusting step size to {}", stepSize);
            double nextUpper = selfSimilarity + stepSize;
            double nextLower = selfSimilarity - stepSize;
            //if (distanceToUpper < distanceToLower) {
                if (nextLower > lowerBound) {
                    requestSampling(nextLower);
                } else {
                    stepSize *= 0.8;
                    requestSampling(selfSimilarity);
                }
            // } else {
                if (nextUpper < upperBound) {
                    requestSampling(nextUpper);
                } else {
                    stepSize *= 0.8;
                    requestSampling(selfSimilarity);
                }
            // }
        }
    }

    private void takeSample(double selfSimilarity, int clusters) {
        logger.trace("Added sample {} - {}", selfSimilarity, clusters);
        samplingResults.put(selfSimilarity, clusters);
    }

    private void determineNextSamplingPoint() {

        if (!Double.isInfinite(upperBound)) {
            requestSampling(currentSelfSimilarity - stepSize);
            return;
        }

        if (!Double.isInfinite(lowerBound)) {
            requestSampling(currentSelfSimilarity + stepSize);
            return;
        }

        samplingPoints.add(currentSelfSimilarity - stepSize);
        samplingPoints.add(currentSelfSimilarity + stepSize);
    }
}
