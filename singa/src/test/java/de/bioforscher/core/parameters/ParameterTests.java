package de.bioforscher.core.parameters;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ParameterTests {

    @Test
    public void testParameterCreation() {
        DoubleParameter param = new DoubleParameter("A", 0.0, 10.0);
        assertEquals("A", param.getName());
        assertEquals(0.0, param.getLowerBound(), 0.0);
        assertEquals(10.0, param.getUpperBound(), 0.0);
    }

    @Test
    public void testParameterRangeTest() {
        DoubleParameter param = new DoubleParameter("A", 0.0, 10.0);
        assertTrue(param.isInRange(0.0));
        assertTrue(param.isInRange(5.0));
        assertTrue(param.isInRange(10.0));
    }

}
