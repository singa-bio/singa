package de.bioforscher.singa.mathematics.algorithms.superimposition;

import de.bioforscher.singa.mathematics.matrices.Matrix;
import de.bioforscher.singa.mathematics.vectors.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of a {@link Superimposition} for {@link Vector}s.
 * <p>
 *
 * @author fk
 */
public class VectorSuperimposition<VectorType extends Vector> implements Superimposition<VectorType> {

    private final double rmsd;
    private final Vector translation;
    private final Matrix rotation;
    private final List<VectorType> reference;
    private final List<VectorType> candidate;
    private final List<VectorType> mappedCandidate;

    public VectorSuperimposition(double rmsd, Vector translation, Matrix rotation, List<VectorType> reference,
                                 List<VectorType> candidate,
                                 List<VectorType> mappedCandidate) {
        this.rmsd = rmsd;
        this.translation = translation;
        this.rotation = rotation;
        this.reference = reference;
        this.candidate = candidate;
        this.mappedCandidate = mappedCandidate;
    }

    @Override
    public double getRmsd() {
        return rmsd;
    }

    @Override
    public Vector getTranslation() {
        return translation;
    }

    @Override
    public Matrix getRotation() {
        return rotation;
    }

    @Override
    public List<VectorType> getMappedCandidate() {
        return mappedCandidate;
    }

    /**
     * Applies the superimposition to the given list of {@link Vector}s and returns new instances.
     *
     * @param vectors the {@link Vector}s to which this superimposition should be applied
     * @return new instances of mapped {@link Vector}s
     */
    @Override
    public List<VectorType> applyTo(List<VectorType> vectors) {
        List<VectorType> list = new ArrayList<>();
        for (VectorType vector : vectors) {
            Vector add = rotation.transpose().multiply(vector).add(translation);
            list.add((VectorType) add);
        }
        return list;
    }

    @Override
    public List<VectorType> getReference() {
        return reference;
    }

    @Override
    public List<VectorType> getCandidate() {
        return candidate;
    }
}
