package bio.singa.mathematics.quaternions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * @author fk
 */
class RegularQuaternionTest {

    @Test
    void shouldAdditivelyInvertQuaternion() {

        Quaternion quaternion = new RegularQuaternion(0.707, 0, 0, -0.707);
        Quaternion invert = quaternion.additivelyInvert();

        assertArrayEquals(new double[]{-0.707, 0, 0, 0.707}, invert.getElements(), 1E-3);
    }
}