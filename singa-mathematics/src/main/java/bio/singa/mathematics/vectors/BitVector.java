package bio.singa.mathematics.vectors;

import bio.singa.mathematics.concepts.MultiDimensional;
import bio.singa.mathematics.metrics.model.Metrizable;

import java.lang.reflect.InvocationTargetException;
import java.util.regex.Pattern;

/**
 * A {@link BitVector} is a {@link MultiDimensional} collection of boolean values that is {@link Metrizable} with
 * metrics for boolean-valued vectors.
 *
 * @author fk
 */
public interface BitVector extends MultiDimensional<BitVector>, Metrizable<BitVector> {

    Pattern BIT_STRING_PATTERN = Pattern.compile("[0|1]+");

    static BitVector fromBitString(String bitString) {
        if (BIT_STRING_PATTERN.matcher(bitString).matches()) {
            char[] chars = bitString.toCharArray();
            boolean[] elements = new boolean[chars.length];
            for (int i = 0; i < chars.length; i++) {
                elements[i] = chars[i] != '0';
            }
            return new RegularBitVector(elements);
        } else {
            throw new IllegalArgumentException("the given bit string is malformed");
        }
    }

    boolean getElement(int index);

    boolean[] getElements();

    int getDimension();

    /**
     * Returns an explicit copy of this vector. A new array is created and filled with values.
     *
     * @param <V> The concrete implementation of this vector.
     * @return An exact copy of and as a unrelated copy (safe to modify).
     */
    default <V extends BitVector> V getCopy() {
        final boolean[] copyOfElements = new boolean[getElements().length];
        System.arraycopy(getElements(), 0, copyOfElements, 0, getElements().length);
        try {
            return (V) getClass().getConstructor(boolean[].class).newInstance((Object) copyOfElements);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
            throw new UnsupportedOperationException("Instance types must match to copy successfully.");
        }
    }

    /**
     * Returns true if all values evaluate to true.
     *
     * @return True if all true.
     */
    default boolean isAllTrue() {
        for (boolean element : getElements()) {
            if (!element) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if all values evaluate to false.
     *
     * @return True if all false.
     */
    default boolean isAllFalse() {
        for (boolean element : getElements()) {
            if (element) {
                return false;
            }
        }
        return true;
    }
}
