package de.bioforscher.mathematics.combinatorics;

import java.util.*;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * An implementation of permutations realized as stream as shown on:
 *
 * @see {https://minborgsjavapot.blogspot.de/2015/07/java-8-master-permutations.html}
 *
 * Created by fkaiser on 11.11.16.
 */
public class StreamPermutations {

    private static final int UPPER_BOUND = 20;

    private StreamPermutations() {
    }

    private static long factorial(int n) {
        if (n > UPPER_BOUND || n < 0)
            throw new IllegalArgumentException(n + " is out of range of the upper bound " + UPPER_BOUND);
        return LongStream.rangeClosed(2, n).reduce(1, (a, b) -> a * b);
    }

    public static <T> List<T> permutation(long no, List<T> items) {
        return permutationHelper(no,
                                 new LinkedList<>(Objects.requireNonNull(items)),
                                 new ArrayList<>());
    }

    private static <T> List<T> permutationHelper(long no, LinkedList<T> in, List<T> out) {
        if (in.isEmpty()) return out;
        long subFactorial = factorial(in.size() - 1);
        out.add(in.remove((int) (no / subFactorial)));
        return permutationHelper((int) (no % subFactorial), in, out);
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T> Stream<Stream<T>> of(T... items) {
        List<T> itemList = Arrays.asList(items);
        return LongStream.range(0, factorial(items.length))
                         .mapToObj(no -> permutation(no, itemList).stream());
    }
}