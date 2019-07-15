package bio.singa.mathematics.combinatorics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author cl
 */
class StreamCombinationsTest {

    @Test
    @DisplayName("combinations - combinations of different sizes")
    void testCombinationStream() {
        List<String> list = new ArrayList<>();
        list.add("A");
        list.add("B");
        list.add("C");

        List<List<String>> size1Combinations = StreamCombinations.combinations(1, list).collect(Collectors.toList());
        assertTrue(size1Combinations.contains(Collections.singletonList("A")));
        assertTrue(size1Combinations.contains(Collections.singletonList("B")));
        assertTrue(size1Combinations.contains(Collections.singletonList("C")));
        assertEquals(3, size1Combinations.size());

        List<List<String>> size2Combinations = StreamCombinations.combinations(2, list).collect(Collectors.toList());
        List<String> ab = new ArrayList<>();
        ab.add("A");
        ab.add("B");
        assertTrue(size2Combinations.contains(ab));
        List<String> ac = new ArrayList<>();
        ac.add("A");
        ac.add("C");
        assertTrue(size2Combinations.contains(ac));
        List<String> bc = new ArrayList<>();
        bc.add("B");
        bc.add("C");
        assertTrue(size2Combinations.contains(bc));
        assertEquals(3, size2Combinations.size());

        List<List<String>> size3Combinations = StreamCombinations.combinations(3, list).collect(Collectors.toList());
        List<String> abc = new ArrayList<>();
        abc.add("A");
        abc.add("B");
        abc.add("C");
        assertTrue(size3Combinations.contains(abc));
        assertEquals(1, size3Combinations.size());
    }
}