package de.bioforscher.mathematics.algorithms.superimposition;

import de.bioforscher.mathematics.matrices.Matrix;
import de.bioforscher.mathematics.vectors.Vector;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A container object representing a superimposition.
 * <p>
 *
 * @author fk
 */
public class Superimposition {

    private double rmsd;
    private Vector translation;
    private Matrix rotation;
    private List<Vector> mappedCandidate;

    public Superimposition(double rmsd, Vector translation, Matrix rotation, List<Vector> mappedCandidate) {
        this.rmsd = rmsd;
        this.translation = translation;
        this.rotation = rotation;
        this.mappedCandidate = mappedCandidate;
    }

    public double getRmsd() {
        return this.rmsd;
    }

    public Vector getTranslation() {
        return this.translation;
    }

    public Matrix getRotation() {
        return this.rotation;
    }

    public List<Vector> getMappedCandidate() {
        return this.mappedCandidate;
    }

    /**
     * Applies the superimposition to the given list of {@link Vector}s and returns new instances.
     * @param vectors the {@link Vector}s to which this superimposition should be applied
     * @return new instances of mapped {@link Vector}s
     */
    public List<Vector> applyTo(List<Vector> vectors) {
        return vectors.stream().map(vector -> this.rotation.transpose().multiply(vector).add(this.translation))
                .collect(Collectors.toList());
    }
}
