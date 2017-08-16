package de.bioforscher.singa.core.utility;

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

    public static <Type extends Comparable<Type>> Range<Type> of(Type lowerBound, Type upperBound) {
        return new Range<>(lowerBound, upperBound);
    }

    public static <Type extends Comparable<Type>> Range<Type> of(Type bound) {
        return new Range<>(bound, bound);
    }

    public Range(RangeType lowerBound, RangeType upperBound) {
        if (lowerBound.compareTo(upperBound) > 0) {
            this.values = new Pair<>(upperBound, lowerBound);
        }
        this.values = new Pair<>(lowerBound, upperBound);
    }

    @Override
    public RangeType getLowerBound() {
        return this.values.getFirst();
    }

    @Override
    public RangeType getUpperBound() {
        return this.values.getSecond();
    }

}
