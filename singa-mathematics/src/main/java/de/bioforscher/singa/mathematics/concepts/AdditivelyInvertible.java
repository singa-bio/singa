package de.bioforscher.singa.mathematics.concepts;

/**
 * The "additive inversion" operation can be any unary operation that returns
 * the "opposite" of this Object.
 * <p>
 * The additive inverse of a number and the number itself combined with an
 * additive operation yield the additively neutral element called "Zero Element".
 *
 * @param <NumberConcept> A reference to the Class or Interface which the inverse element
 *                        will be a type of.
 * @author cl
 * @see <a href="https://en.wikipedia.org/wiki/Additive_inverse">Wikipedia: Additive inverse</a>
 */
public interface AdditivelyInvertible<NumberConcept> extends Invertible<NumberConcept> {

    /**
     * Returns the corresponding additive inverse element.
     *
     * @return the corresponding additive inverse element.
     */
    NumberConcept additivelyInvert();

}
