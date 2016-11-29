package de.bioforscher.mathematics.concepts;

/**
 * The multiplicative inverse of a number and the number itself combined with an
 * multiplicative operation yield the neutral element (One Element).
 *
 * @param <NumberConcept> A reference to the Class or Interface which the inverse element will be a type of.
 * @author cl
 * @see <a href="https://en.wikipedia.org/wiki/Multiplicative_inverse">Wikipedia: Multiplicative inverse</a>
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
    default boolean isMultiplicativelyInvertible() {
        return true;
    }

}
