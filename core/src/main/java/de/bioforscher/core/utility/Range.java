package de.bioforscher.core.utility;

/**
 * Created by Christoph on 23.06.2016.
 */
public class Range<Type extends Comparable<Type>> implements Bounded<Type> {

    Pair<Type> values;

    public static <Type extends Comparable<Type>> Range<Type> of(Type lowerBound, Type upperBound) {
        return new Range<>(lowerBound, upperBound);
    }

    public static <Type extends Comparable<Type>> Range<Type> of(Type bound) {
        return new Range<>(bound, bound);
    }

    public Range(Type lowerBound, Type upperBound) {
        values = new Pair<Type>(lowerBound, upperBound);
    }

    @Override
    public Type getLowerBound() {
        return values.getFirst();
    }

    @Override
    public Type getUpperBound() {
        return values.getSecond();
    }

}
