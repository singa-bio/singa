package de.bioforscher.singa.simulation.exceptions;

/**
 * @author cl
 */
public class NumericalInstabilityException extends  RuntimeException {

    public NumericalInstabilityException() {
    }

    public NumericalInstabilityException(String message) {
        super(message);
    }

}
