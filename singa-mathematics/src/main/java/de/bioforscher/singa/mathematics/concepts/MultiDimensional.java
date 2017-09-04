package de.bioforscher.singa.mathematics.concepts;

import de.bioforscher.singa.mathematics.exceptions.IncompatibleDimensionsException;

/**
 * The {@link MultiDimensional} interface defines whether a Number concept has
 * one or more Dimensions. It provides methods to compare dimensions of
 * different Objects for calculations, that are dimensional sensitive.
 *
 * @param <NumberConcept> The type of the concept that has multiple dimensions.
 * @author cl
 */
public interface MultiDimensional<NumberConcept extends MultiDimensional<NumberConcept>> {

    /**
     * Determines whether this number concept has the same dimensionality as the
     * given number concept.
     *
     * @param element Another number concept.
     * @return {@code true} if, and only if this vector has the same dimension
     * as the given vector.
     */
    boolean hasSameDimensions(NumberConcept element);

    /**
     * Returns the dimensional representation as a String.
     *
     * @return The dimensional representation as a String.
     */
    String getDimensionAsString();

    /**
     * Asserts that the given number concept has the same dimension as this
     * vector. If not a {@link IncompatibleDimensionsException} will be thrown.
     *
     * @param element Another number concept.
     * @throws IncompatibleDimensionsException if this number concept has another dimension than the given
     *                                         number concept.
     */
    default void assertThatDimensionsMatch(NumberConcept element) {
        if (!hasSameDimensions(element)) {
            throw new IncompatibleDimensionsException(this, element);
        }
    }

}
