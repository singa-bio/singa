package bio.singa.mathematics.vectors;

import bio.singa.mathematics.exceptions.IncompatibleDimensionsException;
import bio.singa.mathematics.matrices.RegularMatrix;
import bio.singa.mathematics.metrics.model.VectorMetricProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class VectorTest {

    private static RegularVector first4DVector;
    private static RegularVector first2DVector;
    private static RegularVector second2DVector;
    private static RegularVector first3DVector;

    @BeforeAll
    static void initialize() {
        first4DVector = new RegularVector(10.0, 20.0, 30.0, 40.0);
        first3DVector = new RegularVector(20.0, 30.0, 40.0);
        first2DVector = new RegularVector(15.0, 25.0);
        second2DVector = new RegularVector(2.0, 3.0);
    }

    @Test
    void testToString() {
        assertEquals(first4DVector.toString(), "Vector 4D (10.0, 20.0, 30.0, 40.0)");
        assertEquals(first2DVector.toString(), "Vector 2D (15.0, 25.0)");
    }

    @Test
    void testIncompatibleDimensionsException() throws IncompatibleDimensionsException {
        assertThrows(IncompatibleDimensionsException.class,
                () -> first4DVector.distanceTo(first2DVector));
    }

    @Test
    void shouldConvertRegularTo2D() {
        Vector2D actual = first2DVector.as(Vector2D.class);
        assertEquals("Vector2D", actual.getClass().getSimpleName());
    }

    @Test
    void shouldNotConvertRegularTo2D() {
        Vector2D actual = first3DVector.as(Vector2D.class);
        assertNull(actual);
    }

    @Test
    void shouldConvertRegularTo3D() {
        Vector3D actual = first3DVector.as(Vector3D.class);
        assertEquals("Vector3D", actual.getClass().getSimpleName());
    }

    @Test
    void shouldNotConvertRegularTo3D() {
        Vector3D actual = first2DVector.as(Vector3D.class);
        assertNull(actual);
    }

    @Test
    void testDyadicProduct() {
        RegularMatrix dyadicProduct = first2DVector.dyadicProduct(second2DVector);
        assertTrue(Arrays.deepEquals(new double[][]{{30.0, 45.0}, {50.0, 75.0}}, dyadicProduct.getElements()));
    }

    @Test
    void testDistanceCalculationWithDifferentMetic() {
        double actual = first2DVector.distanceTo(second2DVector, VectorMetricProvider.MANHATTAN_METRIC);
        assertEquals(35.0, actual);
    }

    @Test
    void calculateCentroid() {
        Vector v1 = new Vector3D(0, 1, 2);
        Vector v2 = new Vector3D(2, -2, 2);
        Vector v3 = new Vector3D(1, 4, -4);
        Vector actual = Vectors.getCentroid(Arrays.asList(v1, v2, v3));
        assertArrayEquals(new double[]{1.0, 1.0, 0.0}, actual.getElements());
    }

    @Test
    void calculate3DCentroid() {
        Vector3D v1 = new Vector3D(0, 1, 2);
        Vector3D v2 = new Vector3D(2, -2, 2);
        Vector3D v3 = new Vector3D(1, 4, -4);
        Vector actual = Vectors3D.get3DCentroid(Arrays.asList(v1, v2, v3));
        assertArrayEquals(new double[]{1.0, 1.0, 0.0}, actual.getElements());
    }

}
