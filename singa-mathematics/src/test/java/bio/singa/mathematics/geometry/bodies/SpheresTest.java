package bio.singa.mathematics.geometry.bodies;

import bio.singa.mathematics.geometry.edges.SimpleLineSegment;
import bio.singa.mathematics.topology.grids.rectangular.MooreRectangularDirection;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.mathematics.vectors.Vector3D;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author cl
 */
class SpheresTest {

    @Test
    @DisplayName("sphere surface calculation - arbitrary")
    void calculateSurface() {
        assertEquals(Spheres.calculateSurface(3), 4.0*Math.PI*3.0*3.0);
    }

    @Test
    @DisplayName("spherical triangle calculation - trivial")
    void calculateSphereTriangleSurface1() {
        Sphere sphere = new Sphere(new Vector3D(0, 0, 0), 1);
        Vector3D v1 = new Vector3D(1, 0, 0);
        Vector3D v2 = new Vector3D(0, 1, 0);
        Vector3D v3 = new Vector3D(0, 0, 1);
        double surface = Spheres.calculateSphereTriangleSurface(sphere, v1, v2, v3);
        assertEquals(sphere.getSurface() / 8.0, surface);
    }

    @Test
    @DisplayName("spherical triangle calculation - offset coordinates")
    void calculateSphereTriangleSurface2() {
        Sphere sphere = new Sphere(new Vector3D(1, 1, 1), 1);
        Vector3D v1 = new Vector3D(2, 1, 1);
        Vector3D v2 = new Vector3D(1, 2, 1);
        Vector3D v3 = new Vector3D(1, 1, 2);
        double surface = Spheres.calculateSphereTriangleSurface(sphere, v1, v2, v3);
        assertEquals(sphere.getSurface() / 8.0, surface);
    }

    @Test
    @DisplayName("spherical triangle calculation - different radius")
    void calculateSphereTriangleSurface3() {
        Sphere sphere = new Sphere(new Vector3D(0, 0, 0), 2);
        Vector3D v1 = new Vector3D(2, 0, 0);
        Vector3D v2 = new Vector3D(0, 2, 0);
        Vector3D v3 = new Vector3D(0, 0, 2);
        double surface = Spheres.calculateSphereTriangleSurface(sphere, v1, v2, v3);
        assertEquals(sphere.getSurface() / 8.0, surface);
    }

    @Test
    @DisplayName("spherical slicing with beam - trivial")
    void calculateSphereSlice1() {
        Map<MooreRectangularDirection, Double> sphereSlices = Spheres.calculateSphereSlice(new Vector2D(0, 0), 1, new Vector2D(0.0, 0.0));
        for (Double surface : sphereSlices.values()) {
            assertEquals(Math.PI/2.0, surface.doubleValue());
        }
    }

    @Test
    @DisplayName("spherical slicing with beam - arbitrary")
    void calculateSphereSlice2() {
        Map<MooreRectangularDirection, Double> sphereSlices = Spheres.calculateSphereSlice(new Vector2D(0, 0), 1,  new Vector2D(0.5, 0.6));
        double surface = 0.0;
        for (Double triangleSurface : sphereSlices.values()) {
            surface += triangleSurface;
        }
        assertEquals(Math.PI*2.0, surface, 1e-12);
    }

    @Test
    @DisplayName("spherical slicing with beam - offset coordinates")
    void calculateSphereSlice3() {
        Map<MooreRectangularDirection, Double> sphereSlices = Spheres.calculateSphereSlice(new Vector2D(17, -3), 1,  new Vector2D(17.1, -2.1));
        double surface = 0.0;
        for (Double triangleSurface : sphereSlices.values()) {
            surface += triangleSurface;
        }
        assertEquals(Math.PI*2, surface, 1e-12);
    }

    @Test
    @DisplayName("spherical slicing with edge - trivial horizontal")
    void calculateSphereSlice4() {
        double actual = Spheres.calculateSphereSlice(new Vector2D(0, 0), 1, new SimpleLineSegment(new Vector2D(2, 0), new Vector2D(-2, 0)));
        assertEquals(Math.PI*2, actual);
    }

    @Test
    @DisplayName("spherical slicing with edge - trivial vertical")
    void calculateSphereSlice5() {
        double actual = Spheres.calculateSphereSlice(new Vector2D(0, 0), 1, new SimpleLineSegment(new Vector2D(0, 2), new Vector2D(0, -2)));
        assertEquals(Math.PI*2, actual);
    }

    @Test
    @DisplayName("spherical slicing with edge - arbitrary")
    void calculateSphereSlice6() {
        double actual = Spheres.calculateSphereSlice(new Vector2D(4.5, 5.0), 2, new SimpleLineSegment(new Vector2D(0, 1), new Vector2D(10, 12)));
        assertEquals(17.10233837874191, actual);
    }
}