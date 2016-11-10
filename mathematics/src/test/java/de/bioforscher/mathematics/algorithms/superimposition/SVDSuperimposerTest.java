package de.bioforscher.mathematics.algorithms.superimposition;

import de.bioforscher.mathematics.vectors.Vector;
import de.bioforscher.mathematics.vectors.Vector3D;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by fkaiser on 19.10.16.
 */
public class SVDSuperimposerTest {

    private List<Vector> reference;
    private List<Vector> candidate;

    @Before
    public void setUp() throws Exception {
        this.reference = new ArrayList<>();
        this.reference.add(new Vector3D(6.994, 8.354, 42.405));
        this.reference.add(new Vector3D(9.429, 7.479, 48.266));
        this.reference.add(new Vector3D(5.547, 0.158, 42.050));

        this.candidate = new ArrayList<>();
        this.candidate.add(new Vector3D(3.908, 12.066, -6.159));
        this.candidate.add(new Vector3D(4.588, 6.531, -9.119));
        this.candidate.add(new Vector3D(12.080, 12.645, -7.073));
    }

    @Test
    public void calculateSuperimposition() throws Exception {
        Superimposition superimposition = SVDSuperimposer.calculateSVDSuperimposition(this.reference, this.candidate);
        assertEquals(0.19986139479017428, superimposition.getRmsd(), 0.1E-6);
    }

    @Test
    public void calculateIdealSuperimposition() throws Exception {

    }

}