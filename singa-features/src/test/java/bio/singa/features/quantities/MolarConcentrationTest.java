package bio.singa.features.quantities;

import bio.singa.features.parameters.Environment;
import org.junit.jupiter.api.Test;
import bio.singa.features.units.UnitRegistry;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.AmountOfSubstance;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Volume;

import static bio.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static tec.uom.se.AbstractUnit.ONE;
import static tec.uom.se.unit.MetricPrefix.MILLI;
import static tec.uom.se.unit.Units.CUBIC_METRE;
import static tec.uom.se.unit.Units.MOLE;

/**
 * @author cl
 */
class MolarConcentrationTest {

    @Before
    @After
    public void cleanUpRegistry() {
        UnitRegistry.reinitialize();
    }

    @Test
    void addQuantity() {
        MolarConcentration first = new MolarConcentration(1.0, MOLE_PER_LITRE);
        MolarConcentration second = new MolarConcentration(1.0, MILLI(MOLE_PER_LITRE));
        MolarConcentration actualResult = first.add(second);
        MolarConcentration expectedResult = new MolarConcentration(1.001, MOLE_PER_LITRE);
        assertEquals(actualResult, expectedResult);
    }

    @Test
    void subtractQuantity() {
        MolarConcentration first = new MolarConcentration(1.0, MOLE_PER_LITRE);
        MolarConcentration second = new MolarConcentration(1.0, MILLI(MOLE_PER_LITRE));
        MolarConcentration actualResult = first.subtract(second);
        MolarConcentration expectedResult = new MolarConcentration(0.999, MOLE_PER_LITRE);
        assertEquals(actualResult, expectedResult);
    }

    @Test
    void multiplyQuantity() {
        MolarConcentration first = new MolarConcentration(1.0, MOLE_PER_LITRE);
        MolarConcentration second = new MolarConcentration(2.0, MOLE_PER_LITRE);
        ComparableQuantity<?> actualResult = first.multiply(second);
        ComparableQuantity<?> expectedResult = Quantities.getQuantity(2.0, MOLE_PER_LITRE.pow(2));
        assertEquals(actualResult, expectedResult);
    }

    @Test
    void multiplyNumber() {
        MolarConcentration quantity = new MolarConcentration(1.0, MOLE_PER_LITRE);
        MolarConcentration actualResult = quantity.multiply(2.0);
        MolarConcentration expectedResult = new MolarConcentration(2.0, MOLE_PER_LITRE);
        assertEquals(actualResult, expectedResult);
    }

    @Test
    void divideQuantity() {
        MolarConcentration first = new MolarConcentration(1.0, MOLE_PER_LITRE);
        MolarConcentration second = new MolarConcentration(2.0, MOLE_PER_LITRE);
        ComparableQuantity<?> actualResult = first.divide(second);
        ComparableQuantity<?> expectedResult = Quantities.getQuantity(0.5, ONE);
        assertEquals(actualResult, expectedResult);
    }

    @Test
    void divideNumber() {
        MolarConcentration quantity = new MolarConcentration(1.0, MOLE_PER_LITRE);
        MolarConcentration actualResult = quantity.divide(2.0);
        MolarConcentration expectedResult = new MolarConcentration(0.5, MOLE_PER_LITRE);
        assertEquals(actualResult, expectedResult);
    }

    @Test
    void inverse() {
        MolarConcentration quantity = new MolarConcentration(1.0, MOLE_PER_LITRE);
        ComparableQuantity<?> actualResult = quantity.inverse();
        ComparableQuantity<?> expectedResult = Quantities.getQuantity(1.0, ONE.divide(MOLE_PER_LITRE));
        assertEquals(actualResult, expectedResult);
    }

    @Test
    void concentrationToMoles() {
        MolarConcentration molePerLitre = new MolarConcentration(1.0, MOLE_PER_LITRE);
        Quantity<Volume> volume = Quantities.getQuantity(2, CUBIC_METRE);
        Quantity<AmountOfSubstance> actualResult = MolarConcentration.concentrationToMoles(molePerLitre,volume);
        Quantity<AmountOfSubstance> expectedResult = Quantities.getQuantity(2000.0, MOLE);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void concentrationToMolecules() {
        MolarConcentration molePerLitre = new MolarConcentration(0.1, MOLE_PER_LITRE);
        Quantity<Volume> volume = Quantities.getQuantity(1.0, UnitRegistry.getVolumeUnit());
        Quantity<Dimensionless> actualResult = MolarConcentration.concentrationToMolecules(molePerLitre, volume);
        Quantity<Dimensionless> expectedResult = Quantities.getQuantity(6.022140857000001E7, ONE);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void moleculesToConcentration() {
        Quantity<MolarConcentration> concentration = MolarConcentration.moleculesToConcentration(4000, Quantities.getQuantity(2.0, CUBIC_METRE));
        Quantity<Dimensionless> molecules = MolarConcentration.concentrationToMolecules(concentration, Quantities.getQuantity(1.0, CUBIC_METRE));
        assertEquals(2000, molecules.getValue().doubleValue(), 1e-10);
    }
}