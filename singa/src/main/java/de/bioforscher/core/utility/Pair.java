package de.bioforscher.core.utility;

/**
 * A Pair is a composition of two objects (ValueType).<br> The values are not exchangeable. The Pair (1,2) is not equal
 * to (2,1).
 */
public class Pair<ValueType> {

    private final ValueType first;
    private final ValueType second;

    public Pair(ValueType first, ValueType second) {
        this.first = first;
        this.second = second;
    }

    public ValueType getFirst() {
        return first;
    }

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
}
