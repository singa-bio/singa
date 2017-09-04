package de.bioforscher.singa.mathematics.topology;

/**
 * A topological space may be defined as a set of points, along with a set of
 * neighbourhoods for each point. Generally it allows for the definition of
 * concepts such as continuity, connectedness, and convergence.
 * <p>
 * Currently this class is a stub and subject to change.
 *
 * @author cl
 */
public interface TopologicalSpace {

    /**
     * Intuitively, an open set provides a method to distinguish two points.
     * Open and closed are not mutually exclusive.
     *
     * @return {@code true} if the set is open.
     */
    boolean isOpen();

}
