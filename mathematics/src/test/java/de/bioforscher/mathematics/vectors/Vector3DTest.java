package de.bioforscher.mathematics.vectors;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class Vector3DTest {

    Vector3D first;
    Vector3D second;
    double scalar;

    @Before
    public void initialize() {
        this.first = new Vector3D(10.0, 20.0, 30.0);
        this.second = new Vector3D(15.0, 25.0, 35.0);
        this.scalar = 2.0;
    }

    @Test
    public void add3DVectors() {
        Vector3D addition = first.add(second);
        assertArrayEquals(new double[]{25.0, 45.0, 65.0}, addition.getElements(), 0.0);
    }

    @Test
    public void invert3DVector() {
        Vector3D inversion = first.additivelyInvert();
        assertArrayEquals(new double[]{-10.0, -20.0, -30.0}, inversion.getElements(), 0.0);
    }

    @Test
    public void invertSingleDimensionOf3DVector() {
        Vector3D inversion = first.invertX().invertY().invertZ();
        assertArrayEquals(new double[]{-10, -20.0, -30.0}, inversion.getElements(), 0.0);
    }

    @Test
    public void substract3DVectors() {
        Vector3D substraction = first.subtract(second);
        assertArrayEquals(new double[]{-5.0, -5.0, -5.0}, substraction.getElements(), 0.0);
    }

    @Test
    public void magnitudeOf3DVector() {
        double magnitude = first.getMagnitude();
        assertEquals(10.0 * Math.sqrt(14), magnitude, 0.0);
    }

    @Test
    public void multiply3DVectorWithScalar() {
        Vector3D multiplication = first.multiply(scalar);
        assertArrayEquals(new double[]{20.0, 40.0, 60.0}, multiplication.getElements(), 0.0);
    }

    @Test
    public void multiply3DVectors() {
        Vector3D multiplication = first.multiply(second);
        assertArrayEquals(new double[]{150.0, 500.0, 1050.0}, multiplication.getElements(), 0.0);
    }

    @Test
    public void divide3DVectorWithScalar() {
        Vector3D division = first.divide(scalar);
        assertArrayEquals(new double[]{5.0, 10.0, 15.0}, division.getElements(), 0.0);
    }

    @Test
    public void divide3DVectors() {
        Vector3D divisionion = first.divide(second);
        assertArrayEquals(new double[]{2.0 / 3.0, 0.8, 6.0 / 7.0}, divisionion.getElements(), 0.0);
    }

    @Test
    public void dotProduct3DVectors() {
        double dotProduct = first.dotProduct(second);
        assertEquals(1700.0, dotProduct, 0.0);
    }

    @Test
    public void crossProduct3DVectors() {
        Vector3D crossProduct = first.crossProduct(second);
        assertArrayEquals(new double[]{-50.0, 100, -50}, crossProduct.getElements(), 0.0);
    }

    @Test
    public void normalize3DVector() {
        Vector3D normalization = first.normalize();
        assertArrayEquals(new double[]{1 / Math.sqrt(14.0), Math.sqrt(2.0 / 7.0), 3 / Math.sqrt(14.0)}, normalization.getElements(), 1e-15);
        assertEquals(1.0, normalization.getMagnitude(), 1e-15);
    }


}
