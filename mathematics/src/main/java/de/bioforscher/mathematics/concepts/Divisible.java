package de.bioforscher.mathematics.concepts;

/**
 * The "division" operation can be any anticommutative and not associative
 * binary operation on a number concept.
 * <p>
 * Division is anticommutative, meaning that changing the order changes the result. It
 * is not associative, meaning that when one divides more than two numbers, the
 * order in which division is performed matters. Division of the
 * "Multiplicative Zero Element" is impossible. Division by the
 * "Multiplicative One Element" does not change the result.
 *
 * @param <NumberConcept> A reference to the class or interface which the division will
 *                        result in.
 * @author Christoph Leberecht
 * @version 1.0.0
 * @see <a href="https://en.wikipedia.org/wiki/Division_(mathematics)">Wikipedia: Division</a>
 */
public interface Divisible<NumberConcept extends Divisible<NumberConcept>> {

    /**
     * Divides the given Object (called divisor) with this Object (called
     * dividend) and returns a new Object (called quotient). Neither divisor nor
     * dividend shall be changed.
     *
     * @param divisor Another object.
     * @return The quotient of this object and the given divisor.
     */
    NumberConcept divide(NumberConcept divisor);

}
