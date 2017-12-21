package de.bioforscher.singa.mathematics.topology;

/**
 * The Euclidean space or plane is a set of points, that satisfy certain
 * relationships, expressible in terms of distance and angle. Two fundamental
 * operations have to be defined: One is translation, which means a shifting of
 * the plane so that every point is shifted in the same direction and by the
 * same distance. The other is rotation about a fixed point in the plane, in
 * which every point in the plane turns about that fixed point through the same
 * angle.
 *
 * @author cl
 */
public class EuclideanSpace implements MetricSpace, TopologicalSpace {

    private final int dimension;
    private final CoordinateSystem coordinateSystem;

    /**
     * Creates a new EuclideanSpace.
     *
     * @param dimension        The dimension of the space.
     * @param coordinateSystem The used coordinate system.
     */
    public EuclideanSpace(int dimension, CoordinateSystem coordinateSystem) {
        this.dimension = dimension;
        this.coordinateSystem = coordinateSystem;
    }

    /**
     * Returns the dimension of this space.
     *
     * @return The dimension of this space.
     */
    public int getDimension() {
        return dimension;
    }

    /**
     * Returns the coordinate system.
     *
     * @return The coordinate system.
     */
    public CoordinateSystem getCoordinateSystem() {
        return coordinateSystem;
    }

    /**
     * Returns the degrees of freedom, that are available.
     *
     * @return The degrees of freedom, that are available.
     */
    public int getDegreesOfFreedom() {
        return dimension;
    }

    /**
     * Euclidean spaces are always open.
     */
    @Override
    public boolean isOpen() {
        return true;
    }

}
