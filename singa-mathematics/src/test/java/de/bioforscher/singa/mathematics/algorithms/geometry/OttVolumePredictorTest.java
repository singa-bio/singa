package de.bioforscher.singa.mathematics.algorithms.geometry;

import de.bioforscher.singa.mathematics.geometry.bodies.Sphere;
import de.bioforscher.singa.mathematics.vectors.Vector3D;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author cl
 */
public class OttVolumePredictorTest {

    @Test
    public void testVolumePrediction() {
        List<Sphere> spheres = new ArrayList<>();
        final Sphere sphere = new Sphere(new Vector3D(0.0, 0.0, 0.0), 1.0);
        spheres.add(sphere);
        final double actualVolume = sphere.getVolume();
        final double predictedVolume = SphereVolumeEstimaton.predict(spheres);
        assertEquals(actualVolume, predictedVolume, 1e-1);
    }

}