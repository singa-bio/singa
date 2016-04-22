package de.bioforscher.mathematics.topology;

/**
 * A metric space is a set for which distances between all members of the set
 * are defined. Those distances, taken together, are called a metric on the set.
 *
 * @author Christoph Leberecht
 * @version 0.0.1
 */
public interface MetricSpace {

    /**
     * Returns {@code true} if it is possible to apply a Metric to this space.
     *
     * @return {@code true} if it is possible to apply a Metric to this space.
     */
    default boolean isMetrizable() {
        return true;
    }

}
