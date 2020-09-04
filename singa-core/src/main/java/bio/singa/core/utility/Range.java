package bio.singa.core.utility;

import java.util.Objects;

/**
 * The range defines any interval between two comparable objects.
 *
 * @author cl
 */
public class Range<RangeType extends Comparable<RangeType>> implements Bounded<RangeType> {

    /**
     * The bounds of this range.
     */
    private Pair<RangeType> values;

    /**
     * Creates a new Range.
     *
     * @param lowerBound The lower bond.
     * @param upperBound The upper bond.
     */
    public Range(RangeType lowerBound, RangeType upperBound) {
        if (lowerBound.compareTo(upperBound) > 0) {
            values = new Pair<>(upperBound, lowerBound);
        }
        values = new Pair<>(lowerBound, upperBound);
    }

    /**
     * Creates a new Range.
     *
     * @param lowerBound The lower bond.
     * @param upperBound The upper bond.
     * @param <Type> The type of Comparable used.
     * @return The new Range.
     */
    public static <Type extends Comparable<Type>> Range<Type> of(Type lowerBound, Type upperBound) {
        return new Range<>(lowerBound, upperBound);
    }

    /**
     * Creates a new Range, where lower and upper bond are identical.
     *
     * @param bound The bond.
     * @param <Type> The type of Comparable used.
     * @return The new Range.
     */
    public static <Type extends Comparable<Type>> Range<Type> of(Type bound) {
        return new Range<>(bound, bound);
    }

    @Override
    public RangeType getLowerBound() {
        return values.getFirst();
    }

    @Override
    public RangeType getUpperBound() {
        return values.getSecond();
    }

    public Range<RangeType> determineOverlap(Range<RangeType> range) {
        RangeType thisLow = getLowerBound();
        RangeType thisUp = getUpperBound();
        RangeType thatLow = range.getLowerBound();
        RangeType thatUp = range.getUpperBound();

        // they are equal
        if(equals(range)) {
            return new Range<>(thisLow, thisUp);
        }
        RangeType low;
        if (thisLow.compareTo(thatLow) > 0) {
            low = thisLow;
        } else {
            low = thatLow;
        }

        RangeType up;
        if (thisUp.compareTo(thatUp) < 0) {
            up = thisUp;
        } else {
            up = thatUp;
        }

        if (low.compareTo(up) > 0) {
            return null;
        } else {
            return new Range<>(low, up);
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Range<?> range = (Range<?>) o;
        return Objects.equals(values, range.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }

    @Override
    public String toString() {
        return "["+getLowerBound()+", "+getUpperBound()+"]";
    }
}
