package bio.singa.features.model;


import org.junit.jupiter.api.Test;

import javax.measure.Unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tec.uom.se.AbstractUnit.ONE;
import static tec.uom.se.unit.MetricPrefix.CENTI;
import static tec.uom.se.unit.MetricPrefix.NANO;
import static tec.uom.se.unit.Units.*;

/**
 * @author cl
 */
public class ScalableFeatureTest {

    @Test
    public void testTimeScale() {
        Unit<?> first = ONE.divide(NANO(MOLE).divide(LITRE).multiply(MINUTE));
        assertEquals(-1, ScalableFeature.getTimeExponent(first));
        Unit<?> second = CENTI(METRE).pow(2);
        assertEquals(0, ScalableFeature.getTimeExponent(second));
    }

    @Test
    public void testSpaceScale() {
        Unit<?> first = ONE.divide(NANO(MOLE).divide(METRE.pow(3)).multiply(MINUTE));
        assertEquals(3, ScalableFeature.getSpaceExponent(first));
        Unit<?> second = MOLE.divide(LITRE.pow(3));
        assertEquals(-3, ScalableFeature.getSpaceExponent(second));
        Unit<?> third = CENTI(METRE);
        assertEquals(1, ScalableFeature.getSpaceExponent(third));
    }

    @Test
    public void testDiffusivity() {
        Unit<?> diffusivity = METRE.divide(100).pow(2).divide(SECOND);
        assertEquals(2, ScalableFeature.getSpaceExponent(diffusivity));
        assertEquals(-1, ScalableFeature.getTimeExponent(diffusivity));
    }

}