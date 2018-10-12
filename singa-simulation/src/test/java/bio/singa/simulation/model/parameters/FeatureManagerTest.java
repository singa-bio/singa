package bio.singa.simulation.model.parameters;

import bio.singa.chemistry.features.diffusivity.Diffusivity;
import bio.singa.features.model.FeatureOrigin;
import bio.singa.features.parameters.Environment;
import bio.singa.features.units.UnitRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;

import static org.junit.Assert.assertEquals;
import static tec.uom.se.AbstractUnit.ONE;
import static tec.uom.se.unit.MetricPrefix.*;
import static tec.uom.se.unit.Units.*;

/**
 * @author cl
 */
public class FeatureManagerTest {

    @Before
    @After
    public void resetEnvironment() {
        UnitRegistry.reinitialize();
    }

    @Test
    public void shouldConvertConcentration() {
        Unit<?> sourceUnit = MOLE.divide(MICRO(METRE).pow(3));
        Quantity<?> sourceQuantity = Quantities.getQuantity(2e-20, sourceUnit);
        assertEquals(2.0E-11, UnitRegistry.convert(sourceQuantity).getValue().doubleValue(), 0.0);
    }

    @Test
    public void shouldConvertDistance() {
        Unit<?> sourceUnit = CENTI(METRE);
        Quantity<?> sourceQuantity = Quantities.getQuantity(2, sourceUnit);
        assertEquals(20000.0, UnitRegistry.convert(sourceQuantity).getValue().doubleValue(), 0.0);
    }

    @Test
    public void shouldConvertArea() {
        Unit<?> sourceUnit = CENTI(METRE).pow(2);
        Quantity<?> sourceQuantity = Quantities.getQuantity(3, sourceUnit);
        assertEquals(3.0E8, UnitRegistry.convert(sourceQuantity).getValue().doubleValue(), 0.0);
    }

    @Test
    public void shouldConvertVolume() {
        Unit<?> sourceUnit = CENTI(METRE).pow(3);
        Quantity<?> sourceQuantity = Quantities.getQuantity(1.5, sourceUnit);
        assertEquals(1.5E12, UnitRegistry.convert(sourceQuantity).getValue().doubleValue(), 0.0);
    }

    @Test
    public void shouldConvertZeroOrderRate() {
        Unit<?> sourceUnit = PICO(MOLE).divide(LITRE.multiply(MINUTE));
        Quantity<?> sourceQuantity = Quantities.getQuantity(0.03, sourceUnit);
        assertEquals(5E-28, UnitRegistry.convert(sourceQuantity).getValue().doubleValue(), 1e-16);
    }

    @Test
    public void shouldConvertFirstOrderRate() {
        Unit<?> sourceUnit = ONE.divide(MINUTE);
        Quantity<?> sourceQuantity = Quantities.getQuantity(0.6, sourceUnit);
        assertEquals(1E-8, UnitRegistry.convert(sourceQuantity).getValue().doubleValue(), 0);
    }

    @Test
    public void shouldConvertSecondOrderRate() {
        Unit<?> sourceUnit = ONE.divide(NANO(MOLE).divide(LITRE).multiply(MINUTE));
        Quantity<?> sourceQuantity = Quantities.getQuantity(0.3, sourceUnit);
        assertEquals(5E6, UnitRegistry.convert(sourceQuantity).getValue().doubleValue(), 1e-16);
    }

    @Test
    public void shouldScaleToEnvironment() {
        Diffusivity diffusivity = new Diffusivity(1, FeatureOrigin.MANUALLY_ANNOTATED);
        Quantity<?> first = UnitRegistry.scale(diffusivity);
        assertEquals(100.0, first.getValue().doubleValue(), 0.0);

        UnitRegistry.setTime(Quantities.getQuantity(0.5, MILLI(SECOND)));
        Quantity<?> second = UnitRegistry.scale(diffusivity);
        assertEquals(50000.0, second.getValue().doubleValue(), 0.0);

        UnitRegistry.setSpace(Quantities.getQuantity(2, MILLI(METRE)));
        Quantity<?> third = UnitRegistry.scale(diffusivity);
        assertEquals(0.0125, third.getValue().doubleValue(), 0.0);

        Environment.reset();
    }

}