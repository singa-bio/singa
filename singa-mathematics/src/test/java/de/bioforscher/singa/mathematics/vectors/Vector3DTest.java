package de.bioforscher.singa.mathematics.vectors;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class Vector3DTest {

    private Vector3D first;
    private Vector3D second;
    private double scalar;

    @Before
    public void initialize() {
        this.first = new Vector3D(10.0, 20.0, 30.0);
        this.second = new Vector3D(15.0, 25.0, 35.0);
        this.scalar = 2.0;
    }

    @Test
    public void add3DVectors() {
        Vector3D addition = this.first.add(this.second);
        assertArrayEquals(new double[]{25.0, 45.0, 65.0}, addition.getElements(), 0.0);
    }

    @Test
    public void invert3DVector() {
        Vector3D inversion = this.first.additivelyInvert();
        assertArrayEquals(new double[]{-10.0, -20.0, -30.0}, inversion.getElements(), 0.0);
    }

    @Test
    public void invertSingleDimensionOf3DVector() {
        Vector3D inversion = this.first.invertX().invertY().invertZ();
        assertArrayEquals(new double[]{-10, -20.0, -30.0}, inversion.getElements(), 0.0);
    }

    @Test
    public void subtract3DVectors() {
        Vector3D subtraction = this.first.subtract(this.second);
        assertArrayEquals(new double[]{-5.0, -5.0, -5.0}, subtraction.getElements(), 0.0);
    }

    @Test
    public void magnitudeOf3DVector() {
        double magnitude = this.first.getMagnitude();
        assertEquals(10.0 * Math.sqrt(14), magnitude, 0.0);
    }

    @Test
    public void multiply3DVectorWithScalar() {
        Vector3D multiplication = this.first.multiply(this.scalar);
        assertArrayEquals(new double[]{20.0, 40.0, 60.0}, multiplication.getElements(), 0.0);
    }

    @Test
    public void multiply3DVectors() {
        Vector3D multiplication = this.first.multiply(this.second);
        assertArrayEquals(new double[]{150.0, 500.0, 1050.0}, multiplication.getElements(), 0.0);
    }

    @Test
    public void divide3DVectorWithScalar() {
        Vector3D division = this.first.divide(this.scalar);
        assertArrayEquals(new double[]{5.0, 10.0, 15.0}, division.getElements(), 0.0);
    }

    @Test
    public void divide3DVectors() {
        Vector3D divisionion = this.first.divide(this.second);
        assertArrayEquals(new double[]{2.0 / 3.0, 0.8, 6.0 / 7.0}, divisionion.getElements(), 0.0);
    }

    @Test
    public void dotProduct3DVectors() {
        double dotProduct = this.first.dotProduct(this.second);
        assertEquals(1700.0, dotProduct, 0.0);
    }

    @Test
    public void crossProduct3DVectors() {
        Vector3D crossProduct = this.first.crossProduct(this.second);
        assertArrayEquals(new double[]{-50.0, 100, -50}, crossProduct.getElements(), 0.0);
    }

    @Test
    public void normalize3DVector() {
        Vector3D normalization = this.first.normalize();
        assertArrayEquals(new double[]{1 / Math.sqrt(14.0), Math.sqrt(2.0 / 7.0), 3 / Math.sqrt(14.0)}, normalization.getElements(), 1e-15);
        assertEquals(1.0, normalization.getMagnitude(), 1e-15);
    }


}
