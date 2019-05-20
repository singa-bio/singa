package bio.singa.features.quantities;

import bio.singa.features.units.UnitRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.AmountOfSubstance;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Volume;

import static bio.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static tech.units.indriya.AbstractUnit.ONE;
import static tech.units.indriya.unit.MetricPrefix.MILLI;
import static tech.units.indriya.unit.Units.*;

/**
 * @author cl
 */
class MolarConcentrationTest {

    @BeforeAll
    static void initialize() {
        UnitRegistry.reinitialize();
    }

    @AfterEach
    void cleanUp() {
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
        Quantity<AmountOfSubstance> actualResult = MolarConcentration.concentrationToMoles(molePerLitre, volume);
        Quantity<AmountOfSubstance> expectedResult = Quantities.getQuantity(2000.0, MOLE);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void concentrationToMolecules() {
        ComparableQuantity<MolarConcentration> molePerLitre = new MolarConcentration(0.1, MOLE_PER_LITRE).to(UnitRegistry.getConcentrationUnit());
        Quantity<Dimensionless> actualResult = MolarConcentration.concentrationToMolecules(molePerLitre.getValue().doubleValue());
        assertEquals(6.0221408570000015E7, actualResult.getValue().doubleValue());
    }

    @Test
    void moleculesToConcentration() {
        UnitRegistry.setSpace(Quantities.getQuantity(2.0, METRE));
        double concentration = MolarConcentration.moleculesToConcentration(4000);
        Quantity<Dimensionless> molecules = MolarConcentration.concentrationToMolecules(concentration);
        assertEquals(4000, molecules.getValue().doubleValue(), 1e-10);
    }
}