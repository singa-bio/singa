package de.bioforscher.singa.mathematics.exceptions;

/**
 * Should be used when another quantity of intercepts are expected.
 *
 * @author cl
 */
public class NumberOfInterceptsException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NumberOfInterceptsException(String message) {
        super(message);
    }

}
