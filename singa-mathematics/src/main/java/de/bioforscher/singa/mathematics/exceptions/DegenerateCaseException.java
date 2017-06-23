package de.bioforscher.singa.mathematics.exceptions;

/**
 * In mathematics, a degenerate case is a limiting case in which an element of a
 * class of objects is qualitatively different from the rest of the class and
 * hence belongs to another, usually simpler, class. E.g. A point is a
 * degenerate circle, namely one with radius 0.
 * <p>
 * If an object is constructed such that this is the case an exception should be thrown.
 *
 * @author cl
 */
public class DegenerateCaseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DegenerateCaseException(String message) {
        super(message);
    }

}
