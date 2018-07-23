package bio.singa.mathematics.geometry.bodies;

import bio.singa.mathematics.vectors.Vector3D;

/**
 * @author cl
 */
public class Cube {

    private Vector3D center;
    private double sideLength;

    public Cube(Vector3D center, double sideLength) {
        this.center = center;
        this.sideLength = sideLength;
    }

    public Vector3D getCenter() {
        return center;
    }

    public void setCenter(Vector3D center) {
        this.center = center;
    }

    public double getSideLength() {
        return sideLength;
    }

    public void setSideLength(double sideLength) {
        this.sideLength = sideLength;
    }
}
