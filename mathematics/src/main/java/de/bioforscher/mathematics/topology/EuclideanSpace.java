package de.bioforscher.mathematics.topology;

import de.bioforscher.mathematics.concepts.Dimension;

/**
 * The Euclidean space or plane is a set of points, that satisfy certain
 * relationships, expressible in terms of distance and angle. Two fundamental
 * operations have to be defined: One is translation, which means a shifting of
 * the plane so that every point is shifted in the same direction and by the
 * same distance. The other is rotation about a fixed point in the plane, in
 * which every point in the plane turns about that fixed point through the same
 * angle.
 *
 * @author Christoph Leberecht
 * @version 0.0.1
 */
public class EuclideanSpace implements MetricSpace, TopologicalSpace {

    private final Dimension dimension;
    private final CoordinateSystem coordinateSystem;

    /**
     * Creates a new EuclideanSpace.
     *
     * @param dimension        The dimension of the space.
     * @param coordinateSystem The used coordinate system.
     */
    public EuclideanSpace(Dimension dimension, CoordinateSystem coordinateSystem) {
        this.dimension = dimension;
        this.coordinateSystem = coordinateSystem;
    }

    /**
     * Creates a new EuclideanSpace.
     *
     * @param dimensionality   The dimensionality of the space.
     * @param coordinateSystem The used coordinate system.
     */
    public EuclideanSpace(int dimensionality, CoordinateSystem coordinateSystem) {
        this.dimension = new Dimension(dimensionality);
        this.coordinateSystem = coordinateSystem;
    }

    /**
     * Returns the dimension of this space.
     *
     * @return The dimension of this space.
     */
    public Dimension getDimension() {
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
        return this.dimension.getDegreesOfFreedom();
    }

    /**
     * Euclidean spaces are always open.
     */
    @Override
    public boolean isOpen() {
        return true;
    }

}
