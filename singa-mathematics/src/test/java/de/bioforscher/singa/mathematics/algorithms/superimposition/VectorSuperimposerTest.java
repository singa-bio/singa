package de.bioforscher.singa.mathematics.algorithms.superimposition;

import de.bioforscher.singa.mathematics.vectors.Vector;
import de.bioforscher.singa.mathematics.vectors.Vector3D;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * A test for the {@link VectorSuperimposer}.
 *
 * @author fk
 */
public class VectorSuperimposerTest {

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
        VectorSuperimposition vectorSuperimposition = VectorSuperimposer.calculateVectorSuperimposition(this.reference, this.candidate);
        assertEquals(0.19986139479017428, vectorSuperimposition.getRmsd(), 0.1E-6);
    }

    @Test
    public void calculateIdealSuperimposition() throws Exception {
        VectorSuperimposition vectorSuperimposition = VectorSuperimposer.calculateIdealVectorSuperimposition(this.reference, this.candidate);
        assertEquals(0.19986139479017428, vectorSuperimposition.getRmsd(), 0.1E-6);
    }

    @Test
    public void applySuperimposition() {
        VectorSuperimposition vectorSuperimposition = VectorSuperimposer.calculateVectorSuperimposition(this.reference, this.candidate);
        List<Vector> mappedCandidate = vectorSuperimposition.applyTo(this.candidate);
        assertTrue(Arrays.equals(mappedCandidate.get(0).getElements(),
                vectorSuperimposition.getMappedCandidate().get(0).getElements()));
        assertTrue(Arrays.equals(mappedCandidate.get(1).getElements(),
                vectorSuperimposition.getMappedCandidate().get(1).getElements()));
        assertTrue(Arrays.equals(mappedCandidate.get(2).getElements(),
                vectorSuperimposition.getMappedCandidate().get(2).getElements()));
    }
}