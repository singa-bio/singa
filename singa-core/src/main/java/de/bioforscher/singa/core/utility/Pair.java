package de.bioforscher.singa.core.utility;

/**
 * A {@link Pair} is a composition of two objects. This should only be used to return two connected objects because of Java's
 * limited ability to return multiple values. The values of a Pair are therefore final.<br>
 * The values are not exchangeable. The Pair (1,2) is not equal to (2,1).
 *
 * @param <ValueType> The type values to be stored in the pair.
 */
public class Pair<ValueType> {

    /**
     * The first value.
     */
    private final ValueType first;

    /**
     * The second value.
     */
    private final ValueType second;

    /**
     * Creates a new {@link Pair}.
     *
     * @param first The first value.
     * @param second The second value.
     */
    public Pair(ValueType first, ValueType second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Returns the first value.
     *
     * @return The first value.
     */
    public ValueType getFirst() {
        return first;
    }

    /**
     * Returns the second value.
     *
     * @return The second value.
     */
    public ValueType getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Pair<?> pair = (Pair<?>) o;
        if (first != null ? !first.equals(pair.first) : pair.first != null)
            return false;
        return second != null ? second.equals(pair.second) : pair.second == null;
    }

    @Override
    public int hashCode() {
        int result = first != null ? first.hashCode() : 0;
        result = 31 * result + (second != null ? second.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}
