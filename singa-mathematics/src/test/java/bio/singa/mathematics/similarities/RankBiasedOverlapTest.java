package bio.singa.mathematics.similarities;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author fk
 */
class RankBiasedOverlapTest {

    @Test
    void shouldCalculateRbo() {
        List<Integer> list1 = IntStream.range(1, 50).boxed().collect(Collectors.toList());
        List<Integer> list2 = IntStream.range(2, 47).boxed().collect(Collectors.toList());
        RankBiasedOverlap rankBiasedOverlap = new RankBiasedOverlap(list1, list2, 10, 0.35);
        assertEquals(0.1999, rankBiasedOverlap.getRbo(), 1E-4);
    }
}