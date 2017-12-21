package de.bioforscher.singa.structure.algorithms.superimposition.fit3d;

/**
 * An uncaught exception that may occur during a {@link Fit3D} calculation.
 *
 * @author fk
 */
public class Fit3DException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public Fit3DException(String message) {
        super(message);
    }
}