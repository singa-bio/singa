package bio.singa.features.parameters;

import bio.singa.features.quantities.MolarConcentration;
import org.junit.Test;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;
import tec.uom.se.unit.ProductUnit;

import static bio.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static org.junit.Assert.assertEquals;
import static tec.uom.se.unit.MetricPrefix.MICRO;
import static tec.uom.se.unit.Units.METRE;
import static tec.uom.se.unit.Units.MOLE;

/**
 * @author cl
 */
public class EnvironmentTest {

    @Test
    public void getTransformedMolarConcentration() {
        final ComparableQuantity<MolarConcentration> molePerLitre = Quantities.getQuantity(1, MOLE_PER_LITRE);
        final ComparableQuantity<MolarConcentration> molePerCubicMicroMetre = molePerLitre.to(new ProductUnit<>(MOLE.divide(MICRO(METRE).multiply(MICRO(METRE).multiply(MICRO(METRE))))));
        Environment.setNodeDistance(Quantities.getQuantity(1, MICRO(METRE)));
        assertEquals(molePerCubicMicroMetre, molePerLitre.to(Environment.getConcentrationUnit()));
        Environment.setNodeDistance(Quantities.getQuantity(2, MICRO(METRE)));
        assertEquals(1.0E-15 , molePerLitre.to(Environment.getConcentrationUnit()).getValue());
    }

}