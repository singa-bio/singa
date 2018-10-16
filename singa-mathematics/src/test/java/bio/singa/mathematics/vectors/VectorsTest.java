package bio.singa.mathematics.vectors;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author fk
 */
public class VectorsTest {

    private RegularVector vector;

    @Before
    public void setUp() {
        double[] doubles = {0.5, 0.6, 0.7, 0.8};
        vector = new RegularVector(doubles);
    }

    @Test
    public void getAverage() {
        assertEquals(0.65, Vectors.getAverage(vector), 0.0);
    }

    @Test
    public void getMedian() {
        assertEquals((0.6 + 0.7) / 2, Vectors.getMedian(vector), 0.0);
        double[] doubles = {0.5, 0.6, 0.7, 0.8, 0.9};
        RegularVector oddVector = new RegularVector(doubles);
        assertEquals(0.7, Vectors.getMedian(oddVector), 0.0);
    }

    @Test
    public void getStandardDeviation() {
        assertEquals(0.12909, Vectors.getStandardDeviation(vector), 1E-4);
    }

    @Test
    public void getVariance() {
        assertEquals(0.01666, Vectors.getVariance(vector), 1E-4);
    }

    @Test
    public void getIndexOfMinimalElement() {
        assertEquals(0, Vectors.getIndexWithMinimalElement(vector));
    }

    @Test
    public void getIndexOfAbsoluteMinimalElement() {
        assertEquals(0, Vectors.getIndexWithAbsoluteMinimalElement(vector));
    }

    @Test
    public void getIndexOfMaximalElement() {
        assertEquals(3, Vectors.getIndexWithMaximalElement(vector));
    }

    @Test
    public void getIndexOfAbsoluteMaximalElement() {
        assertEquals(3, Vectors.getIndexWithAbsoluteMaximalElement(vector));
    }

    @Test
    public void haveSameDimension() {
    }

    @Test
    public void generateMultipleRandom2DVectors() {
    }

    @Test
    public void generateRandom2DVector() {
    }

    @Test
    public void generateStandardGaussian2DVector() {
    }

    @Test
    public void generateRandomUnit2DVector() {
    }

    @Test
    public void generateRandomVector3D() {
    }

    @Test
    public void getMaximalValueForIndex() {
    }

    @Test
    public void getMinimalValueForIndex() {
    }

    @Test
    public void getIndexWithMaximalElement() {
    }

    @Test
    public void getIndexWithMinimalElement() {
    }

    @Test
    public void getIndexWithAbsoluteMaximalElement() {
    }

    @Test
    public void getIndexWithAbsoluteMinimalElement() {
    }

    @Test
    public void getVectorsWithMinimalValueForIndex() {
    }

    @Test
    public void getCentroid() {
    }

    @Test
    public void orthonormalizeVectors() {
    }

    @Test
    public void gramSchmidtProjection() {
    }

    @Test
    public void accumulateGramSchmidtProjection() {
    }

    @Test
    public void dihedralAngle() {
        Vector3D a = new Vector3D(-54.324, 183.296, -26.325);
        Vector3D b = new Vector3D(-55.462, 184.385, -25.856);
        Vector3D c = new Vector3D(-54.869, 185.861, -25.805);
        Vector3D d = new Vector3D(-54.120, 186.257, -26.904);
        assertEquals(46.828, Vectors.dihedralAngle(a, b, c, d), 1E-3);

        Vector3D a1 = new Vector3D(0,0,0);
        Vector3D b1 = new Vector3D(1,0,0);
        Vector3D c1 = new Vector3D(2,0,0);
        Vector3D d1 = new Vector3D(3,0,0);
        assertTrue(Double.isNaN(Vectors.dihedralAngle(a1, b1, c1, d1)));
    }
}