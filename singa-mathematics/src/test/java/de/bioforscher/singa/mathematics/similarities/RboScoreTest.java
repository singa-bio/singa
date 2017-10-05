package de.bioforscher.singa.mathematics.similarities;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author fk
 */
public class RboScoreTest {

    @Test
    public void shouldCalculateRbo() {
        List<Integer> list1 = IntStream.range(1, 50).boxed().collect(Collectors.toList());
        List<Integer> list2 = IntStream.range(2, 47).boxed().collect(Collectors.toList());
        RboScore rboScore = new RboScore(list1, list2, 10, 0.35);
        Assert.assertEquals(0.1999,rboScore.getRbo(),1E-4);
    }
}