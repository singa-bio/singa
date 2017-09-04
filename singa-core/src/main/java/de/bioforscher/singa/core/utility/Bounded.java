package de.bioforscher.singa.core.utility;

/**
 * Defines a Object to be bounded by a lower and upper bound. The upper and the lower bound should be specified upon
 * Object creation. Afterwards it is possible to check whether an Object of the given Type is in between both bounds.
 *
 * @param <BoundType> The Type of the bound. Has to be {@link Comparable}.
 * @author cl
 */
public interface Bounded<BoundType extends Comparable<BoundType>> {

    /**
     * Returns the lower bound specified for this object.
     *
     * @return The lower bound specified for this object.
     */
    BoundType getLowerBound();

    /**
     * Returns the upper bound specified for this object.
     *
     * @return The upper bound specified for this object.
     */
    BoundType getUpperBound();

    /**
     * Checks whether the given value is larger than the lower bound and smaller than the upper bound specified for this
     * object.
     *
     * @param value The value to be checked.
     * @return {@code true} if the constraints are met.
     */
    default boolean isInRange(BoundType value) {
        return value.compareTo(getLowerBound()) >= 0 && value.compareTo(getUpperBound()) <= 0;
    }

    /**
     * Asserts whether the given value is larger than the lower bound and smaller than the upper bound specified for
     * this object. If not a {@link IllegalArgumentException} will be thrown.
     *
     * @param value The value to be asserted.
     * @throws IllegalArgumentException if the constraints are not met.
     */
    default void assertIfValueInRange(BoundType value) {
        if (isInRange(value)) {
            throw new IllegalArgumentException("The value " + value + " is not bounded between " + getLowerBound()
                    + " and " + getUpperBound() + ".");
        }
    }

}
