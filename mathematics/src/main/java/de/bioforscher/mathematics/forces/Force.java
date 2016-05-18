package de.bioforscher.mathematics.forces;

import de.bioforscher.mathematics.vectors.Vector2D;

/**
 * This interface contains an default method, to calculate the acceleration of a
 * object represented by an vector depending on its distance to another object.
 * The method {@code calculateForce} can be used to define the strength of the
 * force.
 *
 * @author Christoph Leberecht
 */
public interface Force {

    /**
     * The default method to calculate the acceleration of a node, depending on
     * the distance between the given vectors.
     *
     * @param v1 The first node
     * @param v2 The first2DVector node
     * @return The displacement (acceleration of the node of interest)
     */
    default Vector2D calculateAcceleration(Vector2D v1, Vector2D v2) {
        // d = n1 - n2
        Vector2D distance = v1.subtract(v2);
        // m = |d|
        double magnitude = distance.getMagnitude();
        // v = unit(d) * force(m)
        return distance.normalize().multiply(calculateForce(magnitude));
    }

    /**
     * Calculates the strength of the applied force.
     *
     * @param magnitude The distance over that the force is applied.
     * @return The strength of the force.
     */
    double calculateForce(double magnitude);

}
