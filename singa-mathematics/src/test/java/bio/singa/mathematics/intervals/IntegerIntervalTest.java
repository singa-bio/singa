package bio.singa.mathematics.intervals;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author cl
 */
class IntegerIntervalTest {

    @Test
    void intersectionTest() {
        IntegerInterval a = IntegerInterval.of(10,20);
        IntegerInterval b = IntegerInterval.of(30,40);
        assertNull(a.intersection(b));
        assertNull(b.intersection(a));

        IntegerInterval c = IntegerInterval.of(15,25);
        assertEquals(IntegerInterval.of(15,20), c.intersection(a));
        assertEquals(IntegerInterval.of(15,20), a.intersection(c));

        IntegerInterval d = IntegerInterval.of(0, 30);
        assertEquals(a, d.intersection(a));
        assertEquals(a, a.intersection(d));

        assertEquals(IntegerInterval.of(30), b.intersection(d));
    }

}