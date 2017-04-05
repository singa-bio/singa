package de.bioforscher.singa.mathematics.metrics.model;

import de.bioforscher.singa.mathematics.matrices.SymmetricMatrix;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A metric or distance function is a function that defines a distance between each pair of elements of a set. A metric
 * needs to be non-negative, symmetric, has a identity of indiscernibles, and satisfies the triangle inequality.
 *
 * @param <MetrizableType> A reference to the type of object that is to be compared.
 * @author cl
 * @see <a href="https://en.wikipedia.org/wiki/Metric_(mathematics)">Wikipedia: Metric</a>
 */
public interface Metric<MetrizableType> {

    /**
     * Calculates the distance between both Objects. The order of the input does not matter as every metric is
     * symmetric.
     *
     * @param first The first object.
     * @param second The second object.
     * @return Their distance.
     */
    double calculateDistance(MetrizableType first, MetrizableType second);

    /**
     * Calculates the pairwise distance of all elements in the given list. Every distance is only calculated once. The
     * indices of the list are preserved (the distance from the first to the second element in the list can be retrieved
     * with {@code getElement(0,1)} or {@code getElement(1,0)}).
     *
     * @param list The list of elements.
     * @param <SubType> The type or a subtype of this Metric type.
     * @return A {@link SymmetricMatrix} (in this case called distance matrix) with the pairwise distances.
     * @see <a href="https://en.wikipedia.org/wiki/Distance_matrix">Wikipedia: Distance matrix</a>
     */
    default <SubType extends MetrizableType> SymmetricMatrix calculateDistancesPairwise(List<SubType> list) {
        // initialize jagged array
        double[][] compactValues = new double[list.size()][];
        for (int rowIndex = 0; rowIndex < list.size(); rowIndex++) {
            compactValues[rowIndex] = new double[rowIndex + 1];
        }
        // compute distances
        for (int rowIndex = 0; rowIndex < compactValues.length; rowIndex++) {
            for (int columnIndex = 0; columnIndex < compactValues[rowIndex].length; columnIndex++) {
                compactValues[rowIndex][columnIndex] = calculateDistance(list.get(rowIndex), list.get(columnIndex));
            }
        }
        return new SymmetricMatrix(compactValues);
    }

    /**
     * Calculates the distance for each Vector in the given target list to the reference vector.
     * @param list The list of targets.
     * @param reference The reference vector.
     * @param <SubType> The Type or Subtype of the Metrizable.
     * @return A mapping of the target vector to its distance.
     */
    default <SubType extends MetrizableType> Map<SubType, Double> calculateDistancesToReference(List<SubType> list,
                                                                                                SubType reference) {
        Map<SubType, Double> result = new HashMap<>();
        list.forEach(point -> result.put(point, calculateDistance(point, reference)));
        return result;
    }

    /**
     * Returns the closest element in the given target list to the reference vector.
     * @param list The list of targets.
     * @param reference The reference vector.
     * @param <SubType> The Type or Subtype of the Metrizable.
     * @returnA A Entry with the closest element and its distance.
     */
    default <SubType extends MetrizableType> Map.Entry<SubType, Double> calculateClosestDistance(List<SubType> list,
                                                                                                 SubType reference) {
        Map<SubType, Double> distances = calculateDistancesToReference(list, reference);
        Map.Entry<SubType, Double> min = null;
        for (Map.Entry<SubType, Double> entry : distances.entrySet()) {
            if (min == null || min.getValue() > entry.getValue()) {
                min = entry;
            }
        }
        return min;
    }

}
