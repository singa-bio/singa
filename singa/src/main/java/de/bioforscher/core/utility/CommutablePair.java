package de.bioforscher.core.utility;

/**
 * Created by Christoph on 21.06.2016.
 */
public class CommutablePair<ValueType> extends Pair<ValueType> {

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
        int result = getFirst() != null ? getFirst().hashCode() : 0;
        result = 31 * result + (getSecond() != null ? getSecond().hashCode() : 0);
        return result;
    }

}
