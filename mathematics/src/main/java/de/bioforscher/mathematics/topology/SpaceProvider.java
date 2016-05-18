package de.bioforscher.mathematics.topology;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * The SpaceProvider class has some often used spaces predefined. It is also
 * possible to retrieve all predefined Spaces as a unmodifiable Set.
 *
 * @author Christoph Leberecht
 * @version 0.0.1
 */
public class SpaceProvider {

    private final Set<EuclideanSpace> spaces = new HashSet<>();

    private static final SpaceProvider INSTANCE = new SpaceProvider();

    /**
     * Returns all predefined Spaces as a unmodifiable Set.
     *
     * @return All predefined Spaces as a unmodifiable Set.
     */
    public Set<EuclideanSpace> getSpaces() {
        return Collections.unmodifiableSet(spaces);
    }

    private static EuclideanSpace addElement(EuclideanSpace element) {
        INSTANCE.spaces.add(element);
        return element;
    }

    /**
     * The two-dimensional Euclidean space using a Cartesian coordinate system.
     */
    public static final EuclideanSpace EUCLIDEAN_2D = addElement(new EuclideanSpace(2, CoordinateSystem.Cartesian));

    /**
     * The three-dimensional Euclidean space using a Cartesian coordinate system.
     */
    public static final EuclideanSpace EUCLIDEAN_3D = addElement(new EuclideanSpace(3, CoordinateSystem.Cartesian));

}
