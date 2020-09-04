package bio.singa.core.utility;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author cl
 */
class RangeTest {

    @Test
    void shouldDeterminecorrectOverlap() {
        Range<Integer> a = Range.of(10,20);
        Range<Integer> b = Range.of(30,40);
        assertNull(a.determineOverlap(b));
        assertNull(b.determineOverlap(a));

        Range<Integer> c = Range.of(15,25);
        assertEquals(Range.of(15,20), c.determineOverlap(a));
        assertEquals(Range.of(15,20), a.determineOverlap(c));

        Range<Integer> d = Range.of(0, 30);
        assertEquals(a, d.determineOverlap(a));
        assertEquals(a, a.determineOverlap(d));

        assertEquals(Range.of(30), b.determineOverlap(d));
    }
}