package de.bioforscher.singa.mathematics.algorithms.superimposition;

import de.bioforscher.singa.mathematics.vectors.Vector;
import de.bioforscher.singa.mathematics.vectors.Vector3D;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static de.bioforscher.singa.mathematics.NumberConceptAssertion.assertVectorEquals;
import static org.junit.Assert.assertEquals;

/**
 * A test for the {@link VectorSuperimposer}.
 *
 * @author fk
 */
public class VectorSuperimposerTest {

    private List<Vector> reference;
    private List<Vector> candidate;

    @Before
    public void setUp() {
        reference = new ArrayList<>();
        reference.add(new Vector3D(6.994, 8.354, 42.405));
        reference.add(new Vector3D(9.429, 7.479, 48.266));
        reference.add(new Vector3D(5.547, 0.158, 42.050));

        candidate = new ArrayList<>();
        candidate.add(new Vector3D(3.908, 12.066, -6.159));
        candidate.add(new Vector3D(4.588, 6.531, -9.119));
        candidate.add(new Vector3D(12.080, 12.645, -7.073));
    }

    @Test
    public void calculateSuperimposition() {
        VectorSuperimposition vectorSuperimposition = VectorSuperimposer.calculateVectorSuperimposition(reference, candidate);
        assertEquals(0.19986139479017428, vectorSuperimposition.getRmsd(), 0.1E-6);
    }

    @Test
    public void calculateIdealSuperimposition() {
        VectorSuperimposition vectorSuperimposition = VectorSuperimposer.calculateIdealVectorSuperimposition(reference, candidate);
        assertEquals(0.19986139479017428, vectorSuperimposition.getRmsd(), 0.1E-6);
    }

    @Test
    public void calculateKuhnMunkresSuperimposition() {
        VectorSuperimposition vectorSuperimposition = VectorSuperimposer.calculateKuhnMunkresSuperimposition(reference, candidate);
        assertEquals(1.3611043603449626, vectorSuperimposition.getRmsd(), 0.1E-6);
    }

    @Test
    public void applySuperimposition() {
        VectorSuperimposition<Vector> vectorSuperimposition = VectorSuperimposer.calculateVectorSuperimposition(reference, candidate);
        List<Vector> mappedCandidate = vectorSuperimposition.applyTo(candidate);
        assertVectorEquals(mappedCandidate.get(0), vectorSuperimposition.getMappedCandidate().get(0), 0.0);
        assertVectorEquals(mappedCandidate.get(1), vectorSuperimposition.getMappedCandidate().get(1), 0.0);
        assertVectorEquals(mappedCandidate.get(2), vectorSuperimposition.getMappedCandidate().get(2), 0.0);
    }
}