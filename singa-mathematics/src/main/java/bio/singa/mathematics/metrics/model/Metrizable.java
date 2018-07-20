package bio.singa.mathematics.metrics.model;

/**
 * Any object that implements this interface has to define a method to calculate a distance between two of those
 * objects. Ideally the calculation is defined as a {@link Metric} of this type.
 *
 * @param <MetrizableType> The type of the Metrizable.
 * @author cl
 */
public interface Metrizable<MetrizableType extends Metrizable<MetrizableType>> {

    /**
     * Calculates the distance from this object to another object.
     *
     * @param another The other object.
     * @return The distance between both objects.
     */
    double distanceTo(MetrizableType another);

    /**
     * Calculates the distance from this object to another object using the given applicable metric.
     *
     * @param another The other object.
     * @param metric The metric to calculate the distance with.
     * @return The distance between both objects.
     */
    double distanceTo(MetrizableType another, Metric<MetrizableType> metric);

}
