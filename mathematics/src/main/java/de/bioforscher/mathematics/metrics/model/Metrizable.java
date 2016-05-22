package de.bioforscher.mathematics.metrics.model;

/**
 * Any object that implements this interface has to define a method to calculate a distance between two of those
 * objects. Ideally the calculation is defined as a {@link Metric} of this type.
 *
 * @param <Type> The type of the underlying distance calculated.
 * @author Christoph Leberecht
 * @version 1.0.0
 */
public interface Metrizable<Type> {

    /**
     * Calculates the distance from this object to another object.
     *
     * @param another The other object.
     * @return The distance between both objects.
     */
    double distanceTo(Type another);

}
