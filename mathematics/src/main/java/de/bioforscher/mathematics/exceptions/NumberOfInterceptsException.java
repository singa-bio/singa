package de.bioforscher.mathematics.exceptions;

/**
 * Should be used when another quantity of intercepts are expected.
 *
 * @author Christoph Leberecht
 * @version 0.0.0
 */
public class NumberOfInterceptsException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NumberOfInterceptsException(String message) {
        super(message);
    }

}
