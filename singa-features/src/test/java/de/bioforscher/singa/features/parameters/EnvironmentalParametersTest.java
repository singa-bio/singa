package de.bioforscher.singa.features.parameters;

import de.bioforscher.singa.features.quantities.MolarConcentration;
import org.junit.Test;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;
import tec.uom.se.unit.ProductUnit;

import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static org.junit.Assert.assertEquals;
import static tec.uom.se.unit.MetricPrefix.MICRO;
import static tec.uom.se.unit.Units.METRE;
import static tec.uom.se.unit.Units.MOLE;

/**
 * @author cl
 */
public class EnvironmentalParametersTest {

    @Test
    public void getTransformedMolarConcentration() {
        final ComparableQuantity<MolarConcentration> molePerLitre = Quantities.getQuantity(1, MOLE_PER_LITRE);
        final ComparableQuantity<MolarConcentration> molePerCubicMicroMetre = molePerLitre.to(new ProductUnit<>(MOLE.divide(MICRO(METRE).multiply(MICRO(METRE).multiply(MICRO(METRE))))));
        EnvironmentalParameters.setNodeDistance(Quantities.getQuantity(1, MICRO(METRE)));
        assertEquals(molePerCubicMicroMetre, molePerLitre.to(EnvironmentalParameters.getTransformedMolarConcentration()));
        EnvironmentalParameters.setNodeDistance(Quantities.getQuantity(2, MICRO(METRE)));
        assertEquals(1.25E-16 , molePerLitre.to(EnvironmentalParameters.getTransformedMolarConcentration()).getValue());
        EnvironmentalParameters.setNodeDistance(Quantities.getQuantity(0.5, MICRO(METRE)));
        assertEquals(8.0E-15, molePerLitre.to(EnvironmentalParameters.getTransformedMolarConcentration()).getValue());

    }
}