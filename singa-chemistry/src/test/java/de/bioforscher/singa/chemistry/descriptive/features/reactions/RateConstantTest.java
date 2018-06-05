package de.bioforscher.singa.chemistry.descriptive.features.reactions;

import org.junit.BeforeClass;
import org.junit.Test;
import tec.uom.se.quantity.Quantities;
import tec.uom.se.unit.ProductUnit;

import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static org.junit.Assert.assertEquals;
import static tec.uom.se.AbstractUnit.ONE;
import static tec.uom.se.unit.MetricPrefix.*;
import static tec.uom.se.unit.Units.*;

/**
 * @author cl
 */
public class RateConstantTest {

    private static RateConstant zeroOrder;
    private static RateConstant firstOrder;
    private static RateConstant secondOder;

    @BeforeClass
    public static void createRates() {
        zeroOrder = RateConstant.create(1.0)
                .forward()
                .zeroOrder()
                .concentrationUnit(NANO(MOLE_PER_LITRE))
                .timeUnit(SECOND)
                .build();
        firstOrder = RateConstant.create(2.0)
                .forward()
                .firstOrder()
                .timeUnit(SECOND)
                .build();
        secondOder = RateConstant.create(3.0)
                .backward()
                .secondOder()
                .concentrationUnit(MILLI(MOLE_PER_LITRE))
                .timeUnit(MINUTE)
                .build();
    }

    @Test
    public void scaleZeroOrderRate() {
        // scale to 10 seconds
        zeroOrder.scale(Quantities.getQuantity(10, SECOND));
        // / 10e-9 from nano mole per litre to mole per litre
        // * 10 from 10 seconds
        // / 10E-12 from litre to um3
        // = 1.0e-8 mol/l*s
        assertEquals(new ProductUnit<>(MOLE.divide(MICRO(METRE).pow(3).multiply(SECOND))), zeroOrder.getScaledQuantity().getUnit());
        assertEquals(1.0e-23, zeroOrder.getScaledQuantity().getValue().doubleValue(), 1.0E-16);
    }

    @Test
    public void scaleFirstOrderRate() {
        // scale to one minute
        // independent from concentration
        firstOrder.scale(Quantities.getQuantity(1, MINUTE));
        // * 60 from 1 minute (60) seconds
        // = 120 1/min
        assertEquals(new ProductUnit<>(ONE.divide(MINUTE)), firstOrder.getScaledQuantity().getUnit());
        assertEquals(120.0, firstOrder.getScaledQuantity().getValue().doubleValue(), 0.0);
    }

    @Test
    public void scaleSecondOrderRate() {
        // scale to one milli second
        secondOder.scale(Quantities.getQuantity(1, MILLI(SECOND)));
        // * 0.001 from milli mole to mole
        // / 60 from minute to seconds
        // / 0.001 from seconds to milli seconds
        // * 10E-12 from litre to um3
        // = 0.05 l/mol*ms
        assertEquals(new ProductUnit<>(MICRO(METRE).pow(3).divide(MOLE.multiply(MILLI(SECOND)))), secondOder.getScaledQuantity().getUnit());
        assertEquals(5.0E13, secondOder.getScaledQuantity().getValue().doubleValue(), 0.0);
    }

}
