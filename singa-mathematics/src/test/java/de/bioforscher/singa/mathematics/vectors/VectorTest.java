package de.bioforscher.singa.mathematics.vectors;

import de.bioforscher.singa.mathematics.exceptions.IncompatibleDimensionsException;
import de.bioforscher.singa.mathematics.matrices.RegularMatrix;
import de.bioforscher.singa.mathematics.metrics.model.VectorMetricProvider;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class VectorTest {

    private RegularVector first4DVector;
    private RegularVector first2DVector;
    private RegularVector second2DVector;
    private RegularVector first3DVector;
    private double scalar;

    @Before
    public void initialize() {
        this.first4DVector = new RegularVector(10.0, 20.0, 30.0, 40.0);
        this.first3DVector = new RegularVector(20.0, 30.0, 40.0);
        this.first2DVector = new RegularVector(15.0, 25.0);
        this.second2DVector = new RegularVector(2.0, 3.0);
        this.scalar = 2.0;
    }

    @Test
    public void testToString() {
        assertEquals(this.first4DVector.toString(), "Vector 4D (10.0, 20.0, 30.0, 40.0)");
        assertEquals(this.first2DVector.toString(), "Vector 2D (15.0, 25.0)");
    }

    @Test(expected = IncompatibleDimensionsException.class)
    public void testIncompatibleDimensionsException() throws IncompatibleDimensionsException {
        this.first4DVector.distanceTo(this.first2DVector);
    }

    @Test
    public void shouldConvertRegularTo2D() {
        Vector2D actual = this.first2DVector.as(Vector2D.class);
        assertEquals("Vector2D", actual.getClass().getSimpleName());
    }

    @Test
    public void shouldNotConvertRegularTo2D() {
        Vector2D actual = this.first3DVector.as(Vector2D.class);
        assertNull(actual);
    }

    @Test
    public void shouldConvertRegularTo3D() {
        Vector3D actual = this.first3DVector.as(Vector3D.class);
        assertEquals("Vector3D", actual.getClass().getSimpleName());
    }

    @Test
    public void shouldNotConvertRegularTo3D() {
        Vector3D actual = this.first2DVector.as(Vector3D.class);
        assertNull(actual);
    }

    @Test
    public void testDyadicProduct() {
        RegularMatrix dyadicProduct = this.first2DVector.dyadicProduct(this.second2DVector);
        assertTrue(Arrays.deepEquals(new double[][]{{30.0, 45.0}, {50.0, 75.0}}, dyadicProduct.getElements()));
    }

    @Test
    public void testDistanceCalculationWithDifferentMetic() {
        double actual = this.first2DVector.distanceTo(this.second2DVector, VectorMetricProvider.MANHATTAN_METRIC);
        assertEquals(35.0, actual, 0.0);
    }

    @Test
    public void calculateCentroid() {
        Vector v1 = new Vector3D(0, 1, 2);
        Vector v2 = new Vector3D(2, -2, 2);
        Vector v3 = new Vector3D(1, 4, -4);
        Vector actual = Vectors.getCentroid(Arrays.asList(v1, v2, v3));
        assertArrayEquals(new double[]{1.0, 1.0, 0.0}, actual.getElements(), 0.0);
    }

    @Test
    public void calculate3DCentroid() {
        Vector3D v1 = new Vector3D(0, 1, 2);
        Vector3D v2 = new Vector3D(2, -2, 2);
        Vector3D v3 = new Vector3D(1, 4, -4);
        Vector actual = Vectors3D.getCentroid(Arrays.asList(v1, v2, v3));
        assertArrayEquals(new double[]{1.0, 1.0, 0.0}, actual.getElements(), 0.0);
    }

}
