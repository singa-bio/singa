package de.bioforscher.mathematics.metrics.model;

/**
 * A metric or distance function is a function that defines a distance between
 * each pair of elements of a set. A metric needs to be non-negative, symmetric,
 * has a identity of indiscernibles, and satisfies the triangle inequality.
 *
 * @param Type A reference to the type of object that is to be compared.
 * @author Christoph Leberecht
 * @version 1.0.0
 */
public interface Metric<Type> {

    /**
     * Calculates the distance between both Objects. The order of the input does
     * not matter as every metric is symmetric.
     *
     * @param first  The first object.
     * @param second The first2DVector object.
     * @return Their distance.
     */
    public double calculateDistance(Type first, Type second);

}
