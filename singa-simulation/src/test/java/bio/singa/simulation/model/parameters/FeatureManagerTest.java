package bio.singa.simulation.model.parameters;

import bio.singa.chemistry.features.diffusivity.PixelDiffusivity;
import bio.singa.features.parameters.Environment;
import bio.singa.features.units.UnitRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;

import static bio.singa.chemistry.features.diffusivity.Diffusivity.SQUARE_CENTIMETRE_PER_SECOND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static tech.units.indriya.AbstractUnit.ONE;
import static tech.units.indriya.unit.MetricPrefix.*;
import static tech.units.indriya.unit.Units.*;

/**
 * @author cl
 */
public class FeatureManagerTest {

    @BeforeAll
    static void initialize() {
        UnitRegistry.reinitialize();
    }

    @AfterEach
    void cleanUp() {
        UnitRegistry.reinitialize();
    }

    @Test
    public void shouldConvertConcentration() {
        Unit<?> sourceUnit = MOLE.divide(MICRO(METRE).pow(3));
        Quantity<?> sourceQuantity = Quantities.getQuantity(2e-20, sourceUnit);
        assertEquals(2.0E-11, UnitRegistry.convert(sourceQuantity).getValue().doubleValue());
    }

    @Test
    public void shouldConvertDistance() {
        Unit<?> sourceUnit = CENTI(METRE);
        Quantity<?> sourceQuantity = Quantities.getQuantity(2, sourceUnit);
        assertEquals(20000.0, UnitRegistry.convert(sourceQuantity).getValue().doubleValue());
    }

    @Test
    public void shouldConvertArea() {
        Unit<?> sourceUnit = CENTI(METRE).pow(2);
        Quantity<?> sourceQuantity = Quantities.getQuantity(3, sourceUnit);
        assertEquals(3.0E8, UnitRegistry.convert(sourceQuantity).getValue().doubleValue());
    }

    @Test
    public void shouldConvertVolume() {
        Unit<?> sourceUnit = CENTI(METRE).pow(3);
        Quantity<?> sourceQuantity = Quantities.getQuantity(1.5, sourceUnit);
        assertEquals(1.5E12, UnitRegistry.convert(sourceQuantity).getValue().doubleValue());
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
        assertEquals(1E-8, UnitRegistry.convert(sourceQuantity).getValue().doubleValue());
    }

    @Test
    public void shouldConvertSecondOrderRate() {
        Unit<?> sourceUnit = ONE.divide(NANO(MOLE).divide(LITRE).multiply(MINUTE));
        Quantity<?> sourceQuantity = Quantities.getQuantity(0.3, sourceUnit);
        assertEquals(5E6, UnitRegistry.convert(sourceQuantity).getValue().doubleValue(), 1e-16);
    }

    @Test
    @DisplayName("scaling - displacement-based scaling")
    public void shouldScaleToEnvironment() {
        Environment.setSimulationExtend(100);
        Environment.setSystemExtend(Quantities.getQuantity(100, CENTI(METRE)));
        UnitRegistry.setSpace(Quantities.getQuantity(1, CENTI(METRE)));
        UnitRegistry.setTime(Quantities.getQuantity(1, SECOND));
        PixelDiffusivity diffusivity = PixelDiffusivity.of(Quantities.getQuantity(1, SQUARE_CENTIMETRE_PER_SECOND)).build();
        assertEquals(1.0, diffusivity.getScaledQuantity());

        UnitRegistry.setTime(Quantities.getQuantity(0.5, MILLI(SECOND)));
        assertEquals(0.5, diffusivity.getScaledQuantity(), 1e-8);

        UnitRegistry.setSpace(Quantities.getQuantity(2, MILLI(METRE)));
        assertEquals(5e-4, diffusivity.getScaledQuantity(), 1e-8);
    }

}