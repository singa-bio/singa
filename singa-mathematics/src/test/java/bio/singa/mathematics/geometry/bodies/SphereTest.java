package bio.singa.mathematics.geometry.bodies;

import bio.singa.mathematics.vectors.Vector3D;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SphereTest {
    @Test
    public void intersect() {
        Sphere sphere1 = new Sphere(new Vector3D(0.0, 0.0, 0.0), 1.0);
        Sphere sphere2 = new Sphere(new Vector3D(-1.0, 0.0, 0.0), 1.0);
        Sphere sphere3 = new Sphere(new Vector3D(-2.0, 0.0, 0.0), 1.0);
        assertTrue(sphere1.intersect(sphere2));
        assertFalse(sphere1.intersect(sphere3));
    }
}
