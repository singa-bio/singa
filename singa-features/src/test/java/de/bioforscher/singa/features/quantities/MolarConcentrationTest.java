package de.bioforscher.singa.features.quantities;

import de.bioforscher.singa.features.parameters.Environment;
import org.junit.Test;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.AmountOfSubstance;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Volume;

import static de.bioforscher.singa.features.units.UnitProvider.MOLECULES;
import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static org.junit.Assert.assertEquals;
import static tec.uom.se.AbstractUnit.ONE;
import static tec.uom.se.unit.MetricPrefix.MICRO;
import static tec.uom.se.unit.MetricPrefix.MILLI;
import static tec.uom.se.unit.Units.*;

/**
 * @author cl
 */
public class MolarConcentrationTest {

    @Test
    public void addQuantity() {
        MolarConcentration first = new MolarConcentration(1.0, MOLE_PER_LITRE);
        MolarConcentration second = new MolarConcentration(1.0, MILLI(MOLE_PER_LITRE));
        MolarConcentration actualResult = first.add(second);
        MolarConcentration expectedResult = new MolarConcentration(1.001, MOLE_PER_LITRE);
        assertEquals(actualResult, expectedResult);
    }

    @Test
    public void subtractQuantity() {
        MolarConcentration first = new MolarConcentration(1.0, MOLE_PER_LITRE);
        MolarConcentration second = new MolarConcentration(1.0, MILLI(MOLE_PER_LITRE));
        MolarConcentration actualResult = first.subtract(second);
        MolarConcentration expectedResult = new MolarConcentration(0.999, MOLE_PER_LITRE);
        assertEquals(actualResult, expectedResult);
    }

    @Test
    public void multiplyQuantity() {
        MolarConcentration first = new MolarConcentration(1.0, MOLE_PER_LITRE);
        MolarConcentration second = new MolarConcentration(2.0, MOLE_PER_LITRE);
        ComparableQuantity<?> actualResult = first.multiply(second);
        ComparableQuantity<?> expectedResult = Quantities.getQuantity(2.0, MOLE_PER_LITRE.pow(2));
        assertEquals(actualResult, expectedResult);
    }

    @Test
    public void multiplyNumber() {
        MolarConcentration quantity = new MolarConcentration(1.0, MOLE_PER_LITRE);
        MolarConcentration actualResult = quantity.multiply(2.0);
        MolarConcentration expectedResult = new MolarConcentration(2.0, MOLE_PER_LITRE);
        assertEquals(actualResult, expectedResult);
    }

    @Test
    public void divideQuantity() {
        MolarConcentration first = new MolarConcentration(1.0, MOLE_PER_LITRE);
        MolarConcentration second = new MolarConcentration(2.0, MOLE_PER_LITRE);
        ComparableQuantity<?> actualResult = first.divide(second);
        ComparableQuantity<?> expectedResult = Quantities.getQuantity(0.5, ONE);
        assertEquals(actualResult, expectedResult);
    }

    @Test
    public void divideNumber() {
        MolarConcentration quantity = new MolarConcentration(1.0, MOLE_PER_LITRE);
        MolarConcentration actualResult = quantity.divide(2.0);
        MolarConcentration expectedResult = new MolarConcentration(0.5, MOLE_PER_LITRE);
        assertEquals(actualResult, expectedResult);
    }

    @Test
    public void inverse() {
        MolarConcentration quantity = new MolarConcentration(1.0, MOLE_PER_LITRE);
        ComparableQuantity<?> actualResult = quantity.inverse();
        ComparableQuantity<?> expectedResult = Quantities.getQuantity(1.0, ONE.divide(MOLE_PER_LITRE));
        assertEquals(actualResult, expectedResult);
    }

    @Test
    public void toMolesOfSubstance() {
        MolarConcentration molePerLitre = new MolarConcentration(1.0, MOLE_PER_LITRE);
        Quantity<Volume> volume = Quantities.getQuantity(2, CUBIC_METRE);
        Quantity<AmountOfSubstance> actualResult = MolarConcentration.toMolesOfSubstance(molePerLitre,volume);
        Quantity<AmountOfSubstance> expectedResult = Quantities.getQuantity(2000.0, MOLE);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void toMoleculesOfSubstance() {
        MolarConcentration molePerLitre = new MolarConcentration(0.1, MOLE_PER_LITRE);
        Environment.setNodeDistance(Quantities.getQuantity(1, MICRO(METRE)));
        Quantity<Volume> volume = Quantities.getQuantity(1.0, Environment.getTransformedVolume());
        Quantity<Dimensionless> actualResult = MolarConcentration.toMoleculesOfSubstance(molePerLitre, volume);
        Quantity<Dimensionless> expectedResult = Quantities.getQuantity(6.022140857000001E7, MOLECULES);
        assertEquals(expectedResult, actualResult);
    }
}