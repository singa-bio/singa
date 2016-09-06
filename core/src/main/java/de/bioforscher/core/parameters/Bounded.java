package de.bioforscher.core.parameters;

/**
 * Defines a Object to be bounded by a lower and upper bound. The upper and the
 * lower bound should be specified upon Object creation. Afterwards it is
 * possible to check whether an Object of the given Type is in between both
 * bounds.
 *
 * @param <Type> The Type of the bound. Has to be {@link Comparable}.
 * @author Christoph Leberecht
 * @version 1.0.0
 */
public interface Bounded<Type extends Comparable<Type>> {

    /**
     * Returns the lower bound specified for this object.
     *
     * @return The lower bound specified for this object.
     */
    Type getLowerBound();

    /**
     * Returns the upper bound specified for this object.
     *
     * @return The upper bound specified for this object.
     */
    Type getUpperBound();

    /**
     * Checks whether the given value is larger than the lower bound and smaller
     * than the upper bound specified for this object.
     *
     * @param value The value to be checked.
     * @return {@code true} if the constraints are met.
     */
    default boolean isInRange(Type value) {
        return value.compareTo(getLowerBound()) >= 0 && value.compareTo(getUpperBound()) <= 0;
    }

    /**
     * Asserts whether the given value is larger than the lower bound and
     * smaller than the upper bound specified for this object. If not a
     * {@link IllegalArgumentException} will be thrown.
     *
     * @param value The value to be asserted.
     * @throws IllegalArgumentException if the constraints are not met.
     */
    default void assertIfValueInRange(Type value) {
        if (isInRange(value)) {
            throw new IllegalArgumentException("The value " + value + " is not bounded between " + getLowerBound()
                    + " and " + getUpperBound() + ".");
        }
    }

}
