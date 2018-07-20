package bio.singa.core.utility;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author cl
 */
public class PairTest {

    @Test
    public void shouldNotBeEqual() {
        Pair<Integer> p1 = new Pair<>(1, 2);
        Pair<Integer> p2 = new Pair<>(2, 1);
        // not the same
        assertFalse(p1.equals(p2));
    }

    @Test
    public void shouldBeEqual() {
        CommutablePair<Integer> p1 = new CommutablePair<>(1, 2);
        CommutablePair<Integer> p2 = new CommutablePair<>(1, 2);
        CommutablePair<Integer> p3 = new CommutablePair<>(2, 1);
        // really the same
        assertTrue(p1.equals(p2));
        // mutably the same
        assertTrue(p1.equals(p3));
    }

    @Test
    public void shouldGenerateCorrectHashes() {
        Pair<Double> p1 = new Pair<>(11.45, 17.09);
        Pair<Double> p2 = new Pair<>(17.09, 11.45);
        CommutablePair<String> p3 = new CommutablePair<>("Lobster", "Pineapple");
        CommutablePair<String> p4 = new CommutablePair<>("Pineapple", "Lobster");
        assertNotEquals(p1.hashCode(), p2.hashCode());
        assertEquals(p3.hashCode(), p4.hashCode());
    }

}