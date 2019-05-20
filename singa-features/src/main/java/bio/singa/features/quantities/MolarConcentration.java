package bio.singa.features.quantities;

import bio.singa.features.units.UnitRegistry;
import tech.units.indriya.AbstractQuantity;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.ProductUnit;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.AmountOfSubstance;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Volume;
import java.math.BigDecimal;
import java.util.Objects;

import static tech.units.indriya.unit.Units.CUBIC_METRE;
import static tech.units.indriya.unit.Units.MOLE;

/**
 * Molar concentration, also called molarity, amount concentration or substance
 * concentration, is a measure of the concentration of a solute in a solution,
 * or of any chemical species in terms of amount of substance in a given volume.
 *
 * @author cl
 */
public class MolarConcentration extends AbstractQuantity<MolarConcentration> {

    final private double value;

    public MolarConcentration(double value, Unit<MolarConcentration> unit) {
        super(unit);
        this.value = value;
    }

    @Override
    public Double getValue() {
        return value;
    }

    @Override
    public double doubleValue(Unit<MolarConcentration> unit) {
        return (super.getUnit().equals(unit)) ? value : super.getUnit().getConverterTo(unit).convert(value);
    }



    @Override
    public long longValue(Unit<MolarConcentration> unit) {
        double result = doubleValue(unit);
        if ((result < Long.MIN_VALUE) || (result > Long.MAX_VALUE)) {
            throw new ArithmeticException("Overflow (" + result + ")");
        }
        return (long) result;
    }

    @Override
    public MolarConcentration add(Quantity<MolarConcentration> that) {
        if (getUnit().equals(that.getUnit())) {
            return new MolarConcentration(value + that.getValue().doubleValue(), getUnit());
        }
        Quantity<MolarConcentration> converted = that.to(getUnit());
        return new MolarConcentration(value + converted.getValue().doubleValue(), getUnit());
    }

    @Override
    public MolarConcentration subtract(Quantity<MolarConcentration> that) {
        if (getUnit().equals(that.getUnit())) {
            return new MolarConcentration(value - that.getValue().doubleValue(), getUnit());
        }
        Quantity<MolarConcentration> converted = that.to(getUnit());
        return new MolarConcentration(value - converted.getValue().doubleValue(), getUnit());
    }

    @Override
    public ComparableQuantity<?> multiply(Quantity<?> that) {
        return Quantities.getQuantity(value * that.getValue().doubleValue(), getUnit().multiply(that.getUnit()));
    }

    @Override
    public MolarConcentration multiply(Number that) {
        return new MolarConcentration(value * that.doubleValue(), getUnit());
    }

    @Override
    public ComparableQuantity<?> divide(Quantity<?> that) {
        return Quantities.getQuantity(value / that.getValue().doubleValue(), getUnit().divide(that.getUnit()));
    }

    @Override
    public MolarConcentration divide(Number that) {
        return new MolarConcentration(value / that.doubleValue(), getUnit());
    }

    @Override
    public AbstractQuantity<?> inverse() {
        return (AbstractQuantity<?>) Quantities.getQuantity(1d / value, getUnit().inverse());
    }

    @Override
    public boolean isBig() {
        return false;
    }

    @Override
    public BigDecimal decimalValue(Unit<MolarConcentration> unit) throws ArithmeticException {
        throw new IllegalArgumentException("Not implemented. We should not extend the abstract implementation of Quantity.");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Quantity<?>) {
            Quantity<?> that = (Quantity<?>) obj;
            return Objects.equals(getUnit(), that.getUnit()) && Equalizer.hasEquality(value, that.getValue());
        }
        return false;
    }

    public static double moleculesToConcentration(double numberOfMolecules) {
        ComparableQuantity<AmountOfSubstance> mole = Quantities.getQuantity(numberOfMolecules / NaturalConstants.AVOGADRO_CONSTANT.getValue().doubleValue(), MOLE);
        return mole.to(UnitRegistry.getDefaultUnit(MOLE).asType(AmountOfSubstance.class)).divide(UnitRegistry.getVolume()).getValue().doubleValue();
    }

    public static Quantity<AmountOfSubstance> concentrationToMoles(Quantity<MolarConcentration> concentration, Quantity<Volume> volume) {
        Quantity<Volume> transformedVolume = volume.to(CUBIC_METRE);
        Quantity<MolarConcentration> transformedConcentration = concentration.to(new ProductUnit<>(MOLE.divide(CUBIC_METRE)));
        Quantity<?> multiply = transformedConcentration.multiply(transformedVolume);
        return multiply.asType(AmountOfSubstance.class);
    }

    public static Quantity<Dimensionless> concentrationToMolecules(double concentration) {
        return concentrationToMoles(UnitRegistry.concentration(concentration), UnitRegistry.getVolume()).multiply(NaturalConstants.AVOGADRO_CONSTANT).asType(Dimensionless.class);
    }

}
