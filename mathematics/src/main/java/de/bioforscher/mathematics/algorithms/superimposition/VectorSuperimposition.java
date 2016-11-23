package de.bioforscher.mathematics.algorithms.superimposition;

import de.bioforscher.mathematics.matrices.Matrix;
import de.bioforscher.mathematics.vectors.Vector;

import java.util.List;
import java.util.stream.Collectors;

/**
 * An implementation of a {@link Superimposition} for {@link Vector}s.
 * <p>
 *
 * @author fk
 */
public class VectorSuperimposition implements Superimposition<Vector> {

    private final double rmsd;
    private final Vector translation;
    private final Matrix rotation;
    private final List<Vector> mappedCandidate;

    public VectorSuperimposition(double rmsd, Vector translation, Matrix rotation, List<Vector> mappedCandidate) {
        this.rmsd = rmsd;
        this.translation = translation;
        this.rotation = rotation;
        this.mappedCandidate = mappedCandidate;
    }

    @Override
    public double getRmsd() {
        return this.rmsd;
    }

    @Override
    public Vector getTranslation() {
        return this.translation;
    }

    @Override
    public Matrix getRotation() {
        return this.rotation;
    }

    @Override
    public List<Vector> getMappedCandidate() {
        return this.mappedCandidate;
    }

    /**
     * Applies the superimposition to the given list of {@link Vector}s and returns new instances.
     *
     * @param vectors the {@link Vector}s to which this superimposition should be applied
     * @return new instances of mapped {@link Vector}s
     */
    @Override
    public List<Vector> applyTo(List<Vector> vectors) {
        return vectors.stream().map(vector -> this.rotation.transpose().multiply(vector).add(this.translation))
                .collect(Collectors.toList());
    }
}
