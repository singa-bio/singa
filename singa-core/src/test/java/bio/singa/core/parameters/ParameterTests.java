package bio.singa.core.parameters;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParameterTests {

    @Test
    void testParameterCreation() {
        DoubleParameter param = new DoubleParameter("A", 0.0, 10.0);
        assertEquals("A", param.getName());
        assertEquals(0.0, param.getLowerBound().doubleValue());
        assertEquals(10.0, param.getUpperBound().doubleValue());
    }

    @Test
    void testParameterRangeTest() {
        DoubleParameter param = new DoubleParameter("A", 0.0, 10.0);
        assertTrue(param.isInRange(0.0));
        assertTrue(param.isInRange(5.0));
        assertTrue(param.isInRange(10.0));
    }

}
