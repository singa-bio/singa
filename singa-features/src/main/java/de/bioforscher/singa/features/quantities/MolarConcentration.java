package de.bioforscher.singa.features.quantities;

import tec.uom.se.AbstractConverter;
import tec.uom.se.AbstractQuantity;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;
import tec.uom.se.unit.ProductUnit;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.AmountOfSubstance;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Volume;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;

import static de.bioforscher.singa.features.units.UnitProvider.MOLECULES;
import static tec.uom.se.unit.Units.*;

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
    public BigDecimal decimalValue(Unit<MolarConcentration> unit, MathContext ctx) throws ArithmeticException {
        BigDecimal decimal = BigDecimal.valueOf(value);
        return (super.getUnit().equals(unit)) ? decimal : ((AbstractConverter) super.getUnit().getConverterTo(unit)).convert(decimal, ctx);
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

    public static Quantity<MolarConcentration> moleculesToConcentration(double numberOfMolecules, Quantity<Volume> targetVolume) {
        Quantity<AmountOfSubstance> mole = Quantities.getQuantity(numberOfMolecules / NaturalConstants.AVOGADRO_CONSTANT.getValue().doubleValue(), MOLE);
        Quantity<Volume> litre = targetVolume.to(LITRE);
        return mole.divide(litre).asType(MolarConcentration.class);
    }

    public static Quantity<AmountOfSubstance> concentrationToMoles(Quantity<MolarConcentration> concentration, Quantity<Volume> volume) {
        Quantity<Volume> transformedVolume = volume.to(CUBIC_METRE);
        Quantity<MolarConcentration> transformedConcentration = concentration.to(new ProductUnit<>(MOLE.divide(CUBIC_METRE)));
        Quantity<?> multiply = transformedConcentration.multiply(transformedVolume);
        return multiply.asType(AmountOfSubstance.class);
    }

    public static Quantity<Dimensionless> concentrationToMolecules(Quantity<MolarConcentration> concentration, Quantity<Volume> volume) {
        return concentrationToMoles(concentration, volume).multiply(NaturalConstants.AVOGADRO_CONSTANT).asType(Dimensionless.class).to(MOLECULES);
    }

}
