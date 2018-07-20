package bio.singa.mathematics.combinatorics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * An implementation of combinations of size <i>k</i> of <i>n</i> elements realized as stream as shown on:
 *
 * @author fk
 * @see <a href="https://stackoverflow.com/questions/28515516/enumeration-combinations-of-k-elements-using-java-8">Stackoverflow post of streaming elements for combinations</a>
 */
public class StreamCombinations {

    public static <T> Stream<List<T>> combinations(int size, List<T> list) {
        if (size == 0) {
            return Stream.of(Collections.emptyList());
        } else {
            return IntStream.range(0, list.size())
                    .boxed()
                    .flatMap(i -> combinations(size - 1, list.subList(i + 1, list.size()))
                            .map(t -> pipe(list.get(i), t)));
        }
    }

    private static <T> List<T> pipe(T head, List<T> tail) {
        List<T> newList = new ArrayList<>(tail);
        newList.add(0, head);
        return newList;
    }
}
