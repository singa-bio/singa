package bio.singa.mathematics.quaternions;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

/**
 * @author fk
 */
public class RegularQuaternionTest {

    @Test
    public void shouldAdditivelyInvertQuaternion() {

        Quaternion quaternion = new RegularQuaternion(0.707, 0, 0, -0.707);
        Quaternion invert = quaternion.additivelyInvert();

        assertArrayEquals(new double[]{-0.707, 0, 0, 0.707}, invert.getElements(), 1E-3);
    }
}