package de.bioforscher.mathematics.algorithms.superimposition;

import de.bioforscher.mathematics.matrices.Matrix;
import de.bioforscher.mathematics.vectors.Vector;

import java.util.List;

/**
 * A container object representing a superimposition.
 * <p>
 * Created by fkaiser on 19.10.16.
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
}
