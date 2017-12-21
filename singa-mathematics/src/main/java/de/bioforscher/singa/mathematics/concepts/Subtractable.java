package de.bioforscher.singa.mathematics.concepts;

/**
 * The "subtraction" operation can be any anticommutative and not associative
 * binary operation on a number concept.
 * <p>
 * It is anticommutative, meaning that changing the order changes the result. It
 * is not associative, meaning that when one subtracts more than two numbers,
 * the order in which subtraction is performed matters. Subtraction of the
 * "Additive Zero Element" does not change a number.
 *
 * @param <NumberConcept> A reference to the class or interface which the subtraction will
 * result in.
 * @author cl
 * @see <a href="https://en.wikipedia.org/wiki/Subtraction">Wikipedia: Subtraction</a>
 */
public interface Subtractable<NumberConcept extends Subtractable<NumberConcept>> {

    /**
     * Subtracts the given Object (called subtrahend) from this Object (called
     * minuend) and returns a new Object (called difference). Neither subtrahend
     * nor minuend shall be changed.
     *
     * @param subtrahend Another object.
     * @return The difference between this object and the given subtrahend
     * objects.
     */
    NumberConcept subtract(NumberConcept subtrahend);

}
