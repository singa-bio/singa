package bio.singa.mathematics.geometry.bodies;

import bio.singa.mathematics.vectors.Vector3D;

/**
 * @author cl
 */
public class Sphere {

    private final Vector3D center;
    private final double radius;

    public Sphere(Vector3D center, double radius) {
        this.center = center;
        this.radius = radius;
    }

    public Vector3D getCenter() {
        return center;
    }

    public double getRadius() {
        return radius;
    }

    public double getVolume() {
        return 4.0 / 3.0 * Math.PI * radius * radius * radius;
    }

    @Override
    public String toString() {
        return "Sphere{" +
                "center=" + center +
                ", radius=" + radius +
                '}';
    }

    /**
     * Returns true if the given sphere intersects with this.
     *
     * @param sphere The sphere to check.
     * @return True if intersection.
     */
    public boolean intersect(Sphere sphere) {
        Vector3D difference = center.subtract(sphere.getCenter());
        return difference.dotProduct(difference) < Math.pow((radius + sphere.getRadius()), 2);
    }
}
