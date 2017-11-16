package de.bioforscher.singa.mathematics.algorithms.geometry;

import de.bioforscher.singa.mathematics.geometry.bodies.Sphere;
import de.bioforscher.singa.mathematics.vectors.Vector3D;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class AbacusTest {

    @Test
    public void testAbacus() {

        List<Sphere> spheres = new ArrayList<>();
        spheres.add(new Sphere(new Vector3D(1.0, 2.0, 3.0), 3.0));
        spheres.add(new Sphere(new Vector3D(4.0, 5.0, 6.0), 4.0));

        Abacus abacus = new Abacus(spheres);
        abacus.calcualte();

    }


}