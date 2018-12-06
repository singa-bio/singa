package bio.singa.mathematics.geometry.bodies;

import bio.singa.mathematics.geometry.edges.LineSegment;
import bio.singa.mathematics.geometry.faces.Circle;
import bio.singa.mathematics.topology.grids.rectangular.MooreRectangularDirection;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.mathematics.vectors.Vector3D;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static bio.singa.mathematics.topology.grids.rectangular.MooreRectangularDirection.*;
import static java.lang.Math.*;

/**
 * @author cl
 */
public class Spheres {

    private Spheres() {

    }

    public static double calculateSurface(double radius) {
        return 4.0*Math.PI*radius*radius;
    }

    public static double calculateSphereTriangleSurface(Sphere sphere, Vector3D point1, Vector3D point2, Vector3D point3) {
        // move everything to centre
        Vector3D p1c = point1.subtract(sphere.getCenter());
        Vector3D p2c = point2.subtract(sphere.getCenter());
        Vector3D p3c = point3.subtract(sphere.getCenter());
        return calculateCenteredSphereTriangleSurface(sphere, p1c, p2c, p3c);
    }

    public static double calculateCenteredSphereTriangleSurface(Sphere sphere, Vector3D point1, Vector3D point2, Vector3D point3) {
        // https://en.wikipedia.org/wiki/Solution_of_triangles
        // http://mathworld.wolfram.com/SphericalTrigonometry.html
        // sides in angular units
        double alpha = point1.angleTo(point2);
        double beta = point1.angleTo(point3);
        double gamma = point2.angleTo(point3);
        // triangles angles
        double a = acos((cos(alpha) - cos(beta) * cos(gamma)) / (sin(beta) * sin(gamma)));
        double b = acos((cos(beta) - cos(gamma) * cos(alpha)) / (sin(gamma) * sin(alpha)));
        double c = acos((cos(gamma) - cos(alpha) * cos(beta)) / (sin(alpha) * sin(beta)));
        // http://mathworld.wolfram.com/SphericalTriangle.html
        return sphere.getRadius() * sphere.getRadius() * (a + b + c - PI);
    }

    /**
     * Very specific method to slice sphere with an edge that represents a flat surface. This results in two sphere
     * caps.
     *
     * @param sphereCentre the centre of the sphere
     * @param sphereRadius the radius of the sphere
     * @param lineSegment the edge
     * @return the surface of the sphere cap that was sliced (the smaller section)
     */
    public static double calculateSphereSlice(Vector2D sphereCentre, double sphereRadius, LineSegment lineSegment) {
        double height = sphereRadius - lineSegment.distanceTo(sphereCentre);
        return 2 * PI * sphereRadius * height;
    }

    /**
     * Vey specific method to calculate the surface sliced by a horizontal beam from a sphere with z coordinate 0.
     * The beam has to be inside of the sphere.
     *
     * @param sphereCentre the centre of the sphere
     * @param sphereRadius the radius of the sphere
     * @param beam the position of the beam
     * @return The slices in their directions relative to the beam the area of the slice is given relative to the total surface.
     */
    public static Map<MooreRectangularDirection, Double> calculateSphereSlice(Vector2D sphereCentre, double sphereRadius, Vector2D beam) {
        // construct base circle
        Circle baseCircle = new Circle(sphereCentre, sphereRadius);
        // project beam to surface in x (horizontal) direction
        Set<Double> xProjection = baseCircle.getXValue(beam.getY());
        Iterator<Double> xIterator = xProjection.iterator();
        double x1 = xIterator.next();
        double x2 = xIterator.next();
        // project beam to surface in y (vertical) direction
        Set<Double> yProjection = baseCircle.getYValue(beam.getX());
        Iterator<Double> yIterator = yProjection.iterator();
        double y1 = yIterator.next();
        double y2 = yIterator.next();
        // project entrance point of beam on sphere (z projection)
        double z = sqrt(-pow(sphereCentre.getX(), 2)
                + 2 * sphereCentre.getX() * beam.getX()
                - pow(sphereCentre.getY(), 2)
                + 2 * sphereCentre.getY() * beam.getY()
                + pow(sphereRadius, 2)
                - pow(beam.getX(), 2)
                - pow(beam.getY(), 2));
        // create the corresponding vectors on the surface
        Vector3D vSouth = new Vector3D(beam.getX(), y1, 0);
        Vector3D vNorth = new Vector3D(beam.getX(), y2, 0);
        Vector3D vEast = new Vector3D(x1, beam.getY(), 0);
        Vector3D vWest = new Vector3D(x2, beam.getY(), 0);
        Vector3D vTop = new Vector3D(beam.getX(), beam.getY(), z);
        // test basti hypothesis
//        double alpha = vNorth.angleTo(vEast);
//        double beta = vEast.angleTo(vSouth);
//        double gamma = vSouth.angleTo(vWest);
//        double delta = vWest.angleTo(vNorth);
//
//        double sum = alpha+beta+gamma+delta;
//
//        System.out.println("A = "+(alpha/sum));
//        System.out.println("B = "+(beta/sum));
//        System.out.println("C = "+(gamma/sum));
//        System.out.println("D = "+(delta/sum));
//        System.out.println();

        // the actual sphere
        Sphere sphere = new Sphere(new Vector3D(sphereCentre.getX(), sphereCentre.getY(), 0), sphereRadius);
        // calculate the surfaces of the created triangles
        double sNorthEast = calculateSphereTriangleSurface(sphere, vNorth, vEast, vTop);
        double sNorthWest = calculateSphereTriangleSurface(sphere, vNorth, vWest, vTop);
        double sSouthEast = calculateSphereTriangleSurface(sphere, vSouth, vEast, vTop);
        double sSouthWest = calculateSphereTriangleSurface(sphere, vSouth, vWest, vTop);

        double sumSurface = sNorthEast+sNorthWest+sSouthEast+sSouthWest;
//
//        System.out.println("A = "+(sNorthEast/sumSurface));
//        System.out.println("B = "+(sSouthEast/sumSurface));
//        System.out.println("C = "+(sSouthWest/sumSurface));#
//        System.out.println("D = "+(sNorthWest/sumSurface));
//        System.out.println();
        // assign and return results
        EnumMap<MooreRectangularDirection, Double> results = new EnumMap<>(MooreRectangularDirection.class);
        results.put(SOUTH_WEST, sNorthEast/sumSurface);
        results.put(SOUTH_EAST, sNorthWest/sumSurface);
        results.put(NORTH_WEST, sSouthEast/sumSurface);
        results.put(NORTH_EAST, sSouthWest/sumSurface);
        return results;
    }

}
