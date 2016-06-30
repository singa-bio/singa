package de.bioforscher.mathematics.concepts;

import java.util.Collection;
import java.util.Iterator;

/**
 * The "addition" operation can be any commutative and associative binary operation on a number concept.
 * <p>
 * Addition is commutative, meaning that order does not matter, and it is associative, meaning that when one adds more
 * than two numbers, the order in which addition is performed does not matter. Repeated addition of the "Additive One
 * Element" is the same as counting; addition of a "Additive Zero Element" does not change a number.
 *
 * @param <NumberConcept> A reference to the class or interface which the addition will result in.
 * @author Christoph Leberecht
 * @version 2.0.0
 * @see <a href="https://en.wikipedia.org/wiki/Addition">Wikipedia: Addition</a>
 */
public interface Addable<NumberConcept extends Addable<? extends NumberConcept>> {

    /**
     * Adds all the given Objects (called summands) and returns a new Object (called sum). No summand shall be changed.
     *
     * @param summands The summands.
     * @return The sum of all Objects.
     */
    static <NumberConcept extends Addable<NumberConcept>> NumberConcept sum(Collection<? extends NumberConcept> summands) {
        Iterator<? extends NumberConcept> iterator = summands.iterator();
        NumberConcept returnValue = iterator.next();
        while (iterator.hasNext()) {
            returnValue = returnValue.add(iterator.next());
        }
        return returnValue;
    }

    /**
     * Adds the given Object (called summand) to this Object (also called summand) and returns a new Object (called
     * sum). No summand shall be changed.
     *
     * @param summand Another object.
     * @return The sum of this object and the given summand.
     */
    NumberConcept add(NumberConcept summand);

}
