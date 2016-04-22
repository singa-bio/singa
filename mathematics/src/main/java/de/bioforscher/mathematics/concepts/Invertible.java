package de.bioforscher.mathematics.concepts;

/**
 * In abstract algebra, the idea of an inverse element generalizes concepts of a
 * negation. The inverse is an element, that can 'undo' the effect of a
 * combination with another given element.
 *
 * @param <NumberConcept> A reference to the Class or Interface which the inverse element will be a type of.
 * @author Christoph Leberecht
 * @version 1.0.0
 */
public interface Invertible<NumberConcept> {

    /**
     * Determines whether this object has a corresponding inverse element.
     *
     * @return {@code true} if and only if this object has an inverse element.
     */
    default boolean isInvertible() {
        return true;
    }

}
