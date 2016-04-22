package de.bioforscher.mathematics.concepts;

/**
 * The "multiplication" operation can be any commutative and associative binary
 * operation on a number concept. Generally it is equivalent to adding as many
 * copies of one of them (multiplicand) as the value of the other one
 * (multiplier).
 * <p>
 * Multiplication is commutative, meaning that order does not matter, and it is
 * associative, meaning that when one multiplies more than two numbers, the
 * order in which multiplication is performed does not matter. The
 * multiplication of the "Multiplicative One Element" does not change the value
 * of the result. Any number multiplied by the "Multiplicative Zero Element"
 * will return the Zero element.
 *
 * @param <NumberConcept> A reference to the class or interface which the multiplication
 *                        will result in.
 * @author Christoph Leberecht
 * @version 1.0.0
 */
public interface Multipliable<NumberConcept extends Multipliable<NumberConcept>> {

    /**
     * Multiplies the given Object (called multiplicand) to this Object (called
     * multiplier) and returns a new Object (called product). Neither
     * multiplicand nor multiplier shall be changed.
     *
     * @param multiplicand Another Object.
     * @return The product of this Object and the given multiplicand.
     */
    NumberConcept multiply(NumberConcept multiplicand);

    /**
     * Multiplies all given Objects (called factors) and returns a new Object
     * (called product). None of the factors shall be changed.
     *
     * @param firstFactor The first factor.
     * @param moreFactors The other factors.
     * @return The product of all factors
     */
    @SafeVarargs
    static <NumberConcept extends Multipliable<NumberConcept>> NumberConcept product(NumberConcept firstFactor,
                                                                                     NumberConcept... moreFactors) {
        NumberConcept returnValue = firstFactor;
        for (NumberConcept element : moreFactors) {
            returnValue = returnValue.multiply(element);
        }
        return returnValue;
    }

}
