package de.bioforscher.mathematics.concepts;

/**
 * The multiplicative inverse of a number and the number itself combined with an
 * multiplicative operation yield the neutral element (One Element).
 *
 * @param <NumberConcept> A reference to the Class or Interface which the inverse element will be a type of.
 * @author Christoph Leberecht
 * @version 1.0.0
 */
public interface MultiplicativelyInvertible<NumberConcept> extends Invertible<NumberConcept> {

    /**
     * @return the corresponding multiplicative inverse element.
     */
    NumberConcept multiplicativelyInvert();

    /**
     * Determines whether this object has a corresponding multiplicative inverse element.
     *
     * @return {@code true} if and only if this object has an multiplicative inverse element.
     */
    public default boolean isMultiplicativelyInvertible() {
        return true;
    }

}
