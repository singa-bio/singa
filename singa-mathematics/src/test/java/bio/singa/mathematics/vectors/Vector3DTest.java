package bio.singa.mathematics.vectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Vector3DTest {

    private static Vector3D first;
    private static Vector3D second;
    private static double scalar;

    @BeforeAll
    static void initialize() {
        first = new Vector3D(10.0, 20.0, 30.0);
        second = new Vector3D(15.0, 25.0, 35.0);
        scalar = 2.0;
    }

    @Test
    void add3DVectors() {
        Vector3D addition = first.add(second);
        assertArrayEquals(new double[]{25.0, 45.0, 65.0}, addition.getElements());
    }

    @Test
    void invert3DVector() {
        Vector3D inversion = first.additivelyInvert();
        assertArrayEquals(new double[]{-10.0, -20.0, -30.0}, inversion.getElements());
    }

    @Test
    void invertSingleDimensionOf3DVector() {
        Vector3D inversion = first.invertX().invertY().invertZ();
        assertArrayEquals(new double[]{-10, -20.0, -30.0}, inversion.getElements());
    }

    @Test
    void subtract3DVectors() {
        Vector3D subtraction = first.subtract(second);
        assertArrayEquals(new double[]{-5.0, -5.0, -5.0}, subtraction.getElements());
    }

    @Test
    void magnitudeOf3DVector() {
        double magnitude = first.getMagnitude();
        assertEquals(10.0 * Math.sqrt(14), magnitude);
    }

    @Test
    void multiply3DVectorWithScalar() {
        Vector3D multiplication = first.multiply(scalar);
        assertArrayEquals(new double[]{20.0, 40.0, 60.0}, multiplication.getElements());
    }

    @Test
    void multiply3DVectors() {
        Vector3D multiplication = first.multiply(second);
        assertArrayEquals(new double[]{150.0, 500.0, 1050.0}, multiplication.getElements());
    }

    @Test
    void divide3DVectorWithScalar() {
        Vector3D division = first.divide(scalar);
        assertArrayEquals(new double[]{5.0, 10.0, 15.0}, division.getElements());
    }

    @Test
    void divide3DVectors() {
        Vector3D divisionion = first.divide(second);
        assertArrayEquals(new double[]{2.0 / 3.0, 0.8, 6.0 / 7.0}, divisionion.getElements());
    }

    @Test
    void dotProduct3DVectors() {
        double dotProduct = first.dotProduct(second);
        assertEquals(1700.0, dotProduct);
    }

    @Test
    void crossProduct3DVectors() {
        Vector3D crossProduct = first.crossProduct(second);
        assertArrayEquals(new double[]{-50.0, 100, -50}, crossProduct.getElements());
    }

    @Test
    void normalize3DVector() {
        Vector3D normalization = first.normalize();
        assertArrayEquals(new double[]{1 / Math.sqrt(14.0), Math.sqrt(2.0 / 7.0), 3 / Math.sqrt(14.0)}, normalization.getElements(), 1e-15);
        assertEquals(1.0, normalization.getMagnitude(), 1e-15);
    }


}
