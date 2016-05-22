package de.bioforscher.mathematics.metrics.model;

import de.bioforscher.mathematics.matrices.SymmetricMatrix;

import java.util.List;

/**
 * A metric or distance function is a function that defines a distance between each pair of elements of a set. A metric
 * needs to be non-negative, symmetric, has a identity of indiscernibles, and satisfies the triangle inequality.
 *
 * @param <Type> A reference to the type of object that is to be compared.
 * @author Christoph Leberecht
 * @version 1.0.1
 * @see <a href="https://en.wikipedia.org/wiki/Metric_(mathematics)">Wikipedia: Metric</a>
 */
public interface Metric<Type> {

    /**
     * Calculates the distance between both Objects. The order of the input does not matter as every metric is
     * symmetric.
     *
     * @param first  The first object.
     * @param second The first2DVector object.
     * @return Their distance.
     */
    double calculateDistance(Type first, Type second);

    /**
     * Calculates the pairwise distance of all elements in the given list. Every distance is only calculated once. The
     * indices of the list are preserved (the distance from the first to the second element in the list can be retrieved
     * with {@code getElement(0,1)} or {@code getElement(1,0)}).
     *
     * @param list      The list of elements.
     * @param <SubType> The type or a subtype of this Metric type.
     * @return A {@link SymmetricMatrix} (in this case called distance matrix) with the pairwise distances.
     * @see <a href="https://en.wikipedia.org/wiki/Distance_matrix">Wikipedia: Distance matrix</a>
     */
    default <SubType extends Type> SymmetricMatrix calculateDistancesPairwise(List<SubType> list) {
        // initialize jagged array
        double[][] compactValues = new double[list.size()][];
        for (int rowIndex = 0; rowIndex < list.size(); rowIndex++) {
            compactValues[rowIndex] = new double[rowIndex + 1];
        }
        for (int rowIndex = 0; rowIndex < compactValues.length; rowIndex++) {
            for (int columnIndex = 0; columnIndex < compactValues[rowIndex].length; columnIndex++) {
                compactValues[rowIndex][columnIndex] = calculateDistance(list.get(rowIndex), list.get(columnIndex));
            }
        }
        return new SymmetricMatrix(compactValues);
    }

}
