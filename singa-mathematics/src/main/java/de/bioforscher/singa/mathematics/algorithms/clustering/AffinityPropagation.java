package de.bioforscher.singa.mathematics.algorithms.clustering;

import de.bioforscher.singa.core.utility.Pair;
import de.bioforscher.singa.mathematics.matrices.LabeledMatrix;
import de.bioforscher.singa.mathematics.matrices.LabeledRegularMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AffinityPropagation<DataType> {

    private static final Logger logger = LoggerFactory.getLogger(AffinityPropagation.class);

    private final List<DataType> dataPoints;
    private final int dataSize;
    LabeledMatrix<DataType> similarityMatrix;
    LabeledMatrix<DataType> availabilityMatrix;
    LabeledMatrix<DataType> responsibilityMatrix;
    private int epoch;

    public AffinityPropagation(List<DataType> dataPoints, LabeledMatrix<DataType> matrix, boolean isDistance) {
        this.dataPoints = dataPoints;
        this.dataSize = dataPoints.size();
        checkInput(dataPoints, matrix);
        // convert to similarity matrix
        if (isDistance) {
            double[][] invertedValues = matrix.additivelyInvert().getElements();
            this.similarityMatrix = new LabeledRegularMatrix<>(invertedValues);
            this.similarityMatrix.setRowLabels(matrix.getRowLabels());
            this.similarityMatrix.setColumnLabels(matrix.getColumnLabels());
        }
        initialize();
    }

    private void initialize() {
        // initialize availability matrix with zeros
        this.availabilityMatrix = new LabeledRegularMatrix<>(new double[this.dataSize][this.dataSize]);
        this.availabilityMatrix.setRowLabels(this.dataPoints);
        this.availabilityMatrix.setColumnLabels(this.dataPoints);

        // initialize responsibility matrix with zeros
        this.responsibilityMatrix = new LabeledRegularMatrix<>(new double[this.dataSize][this.dataSize]);
        this.responsibilityMatrix.setRowLabels(this.dataPoints);
        this.responsibilityMatrix.setColumnLabels(this.dataPoints);
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

    private double calculateAvailability(Pair<DataType> dataPair) {
        double sum;
        double availability;
        if (!dataPair.getFirst().equals(dataPair.getSecond())) {
            sum = 0.0;
            for (DataType dataPoint : this.dataPoints) {
                if (dataPoint.equals(dataPair.getFirst()) || dataPoint.equals(dataPair.getSecond())) {
                    logger.trace("skipping same object for availability calculation: {} {} ", dataPoint, dataPair);
                    continue;
                }
                double currentValue = this.responsibilityMatrix.getValueForLabel(dataPoint, dataPair.getSecond());
                if (currentValue > 0) {
                    sum += currentValue;
                }
            }

            availability = sum + this.responsibilityMatrix.getValueForLabel(dataPair.getSecond(), dataPair.getSecond());

            if (availability > 0) {
                availability = 0.0;
            }

        } else {
            sum = 0.0;
            for (DataType dataPoint : this.dataPoints) {
                if (dataPoint.equals(dataPair.getSecond())) {
                    logger.trace("skipping same object for availability calculation: {} {} ", dataPoint, dataPair);
                    continue;
                }
                availability = this.responsibilityMatrix.getValueForLabel(dataPoint, dataPair.getSecond());
                if (availability > 0) {
                    sum += availability;
                }
            }
            availability = sum;
        }
        return availability;
    }

    private double calculateResponsibility(Pair<DataType> dataPair) {
        double maxValue = -Double.MAX_VALUE;
        for (DataType dataPoint : this.dataPoints) {
            if (dataPoint.equals(dataPair.getSecond())) {
                logger.trace("skipping same object for responsibility calculation: {} {}", dataPoint, dataPair);
                continue;
            }
            double currentValue = this.availabilityMatrix.getValueForLabel(dataPair.getFirst(), dataPoint)
                    + this.similarityMatrix.getValueForLabel(dataPair.getFirst(), dataPoint);
            if (currentValue > maxValue) {
                maxValue = currentValue;
            }
        }
        return this.similarityMatrix.getValueForLabel(dataPair.getFirst(), dataPair.getSecond()) - maxValue;
    }

    public void run() {
        List<Pair<DataType>> orderedPairs = createOrderedPairs();

        this.epoch = 0;
        while (true) {
            updateResponsibility(orderedPairs);
            assignExemplars();
            if (isConverged()) {
                break;
            }
            updateAvailability(orderedPairs);
            this.epoch++;
            System.out.println("resp:\n" + responsibilityMatrix.getStringRepresentation());
            System.out.println("ava:\n" + availabilityMatrix.getStringRepresentation());
            System.out.println();
            if (epoch > 10) {
                break;
            }
        }

    }

    private boolean isConverged() {
        return false;
    }

    private void assignExemplars() {

    }

    private void updateAvailability(List<Pair<DataType>> orderedPairs) {
        logger.debug("updating availabilities");
        double[][] updatedValues = new double[this.dataSize][this.dataSize];
        for (Pair<DataType> orderedPair : orderedPairs) {
            double value = calculateAvailability(orderedPair);
            Pair<Integer> position = this.similarityMatrix.getPositionFromLabels(orderedPair.getFirst(), orderedPair.getSecond());
            updatedValues[position.getFirst()][position.getSecond()] = value;
        }
        LabeledMatrix<DataType> updatedAvailabilityMatrix = new LabeledRegularMatrix<>(updatedValues);
        updatedAvailabilityMatrix.setRowLabels(this.dataPoints);
        updatedAvailabilityMatrix.setColumnLabels(this.dataPoints);
        this.availabilityMatrix = updatedAvailabilityMatrix;
    }

    private void updateResponsibility(List<Pair<DataType>> orderedPairs) {
        logger.debug("updating responsibilities");
        double[][] updatedValues = new double[this.dataSize][this.dataSize];
        for (Pair<DataType> orderedPair : orderedPairs) {
            double value = calculateResponsibility(orderedPair);
            Pair<Integer> position = this.similarityMatrix.getPositionFromLabels(orderedPair.getFirst(), orderedPair.getSecond());
            updatedValues[position.getFirst()][position.getSecond()] = value;
        }
        LabeledMatrix<DataType> updatedResponsibilityMatrix = new LabeledRegularMatrix<>(updatedValues);
        updatedResponsibilityMatrix.setRowLabels(this.dataPoints);
        updatedResponsibilityMatrix.setColumnLabels(this.dataPoints);
        this.responsibilityMatrix = updatedResponsibilityMatrix;
    }

    /**
     * Simply creates all possible pairs of the data points where order matters. TODO move this to utility class.
     *
     * @return The possible pairs of data points.
     */
    private List<Pair<DataType>> createOrderedPairs() {
        return this.dataPoints.stream()
                .flatMap(firstDataPoint -> this.dataPoints.stream()
                        .map(secondDataPoint -> new Pair<>(firstDataPoint, secondDataPoint)))
                .filter(pair -> !pair.getFirst().equals(pair.getSecond()))
                .collect(Collectors.toList());
    }
}
