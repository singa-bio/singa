package bio.singa.mathematics.combinatorics;

import java.util.*;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * An implementation of permutations realized as stream as shown on:
 *
 * @author fk
 * @see <a href="https://minborgsjavapot.blogspot.de/2015/07/java-8-master-permutations.html">Minborgs Javapot</a>
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

    /**
     * Combines several collections of elements and create permutations of all of them, taking one element from each
     * collection, and keeping the same order in resultant lists as the one in original list of collections.<br>
     *
     * Input
     * <pre>
     * {{a,b,c}, {1,2,3,4}}</pre>
     * Output
     * <pre>
     * {{a,1}, {a,2}, {a,3}, {a,4}, {b,1}, {b,2}, {b,3}, {b,4}, {c,1}, {c,2}, {c,3}, {c,4}}</pre>
     *
     *
     * @param <T> The content of the list.
     * @param collections Original list of collections which elements have to be combined.
     * @return Resultant collection of lists with all permutations of original list.
     * @see <a href="https://stackoverflow.com/a/23870892">Stack Overflow Answer</a>
     */
    public static <T> List<List<T>> permutations(List<List<T>> collections) {
        if (collections == null || collections.isEmpty()) {
            return Collections.emptyList();
        } else {
            List<List<T>> res = new LinkedList<>();
            permutationsHelper(collections, res, 0, new LinkedList<>());
            return res;
        }
    }

    private static <T> void permutationsHelper(List<List<T>> ori, List<List<T>> res, int d, List<T> current) {
        // if depth equals number of original collections, final reached, add and return
        if (d == ori.size()) {
            res.add(current);
            return;
        }
        // iterate from current collection and copy 'current' element N times, one for each element
        Collection<T> currentCollection = ori.get(d);
        for (T element : currentCollection) {
            List<T> copy = new LinkedList<>(current);
            copy.add(element);
            permutationsHelper(ori, res, d + 1, copy);
        }
    }


    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T> Stream<Stream<T>> of(T... items) {
        List<T> itemList = Arrays.asList(items);
        return LongStream.range(0, factorial(items.length))
                .mapToObj(no -> permutation(no, itemList).stream());
    }

}