package de.bioforscher.core.utility;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Christoph on 21.06.2016.
 */
public class PairTest {

    @Test
    public void shouldNotBeEqual() {
        Pair p1 = new Pair<>(1, 2);
        Pair p2 = new Pair<>(2, 1);
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

}