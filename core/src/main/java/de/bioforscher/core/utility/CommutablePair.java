package de.bioforscher.core.utility;

/**
 * This subclass of {@link Pair} only implements different equals and hashcode methods.<br>
 * The values are exchangeable. The Pair (1,2) is equal to (2,1).
 *
 * @param <ValueType> The type values to be stored in the pair.
 */
public class CommutablePair<ValueType> extends Pair<ValueType> {

    /**
     * Creates a new {@link Pair}.
     * @param first The first value.
     * @param second The second value.
     */
    public CommutablePair(ValueType first, ValueType second) {
        super(first, second);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        CommutablePair<?> other = (CommutablePair<?>) o;

        // this first equals other first
        if (getFirst().equals(other.getFirst())) {
            // this second equals other second
            return getSecond().equals(other.getSecond());
            // this first equals other second
        } else if (getFirst().equals(other.getSecond())) {
            // this second equals other first
            return getSecond().equals(other.getFirst());
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {
        // TODO does this work?
        int result = getFirst() != null ? getFirst().hashCode() : 0;
        result = 31 * result + (getSecond() != null ? getSecond().hashCode() : 0);
        return result;
    }

}
