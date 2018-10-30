package bio.singa.mathematics.algorithms.geometry;

import bio.singa.mathematics.geometry.bodies.Sphere;
import bio.singa.mathematics.vectors.Vector3D;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author cl
 */
class OttVolumePredictorTest {

    @Test
    void testVolumePrediction() {
        List<Sphere> spheres = new ArrayList<>();
        final Sphere sphere = new Sphere(new Vector3D(0.0, 0.0, 0.0), 1.0);
        spheres.add(sphere);
        final double actualVolume = sphere.getVolume();
        final double predictedVolume = SphereVolumeEstimaton.predict(spheres);
        assertEquals(actualVolume, predictedVolume, 1e-1);
    }

}