package bio.singa.structure.features.molarmass;

import bio.singa.features.model.AbstractFeature;
import bio.singa.features.model.Evidence;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.ProductUnit;

import javax.measure.Quantity;
import javax.measure.Unit;

import static tech.units.indriya.unit.Units.GRAM;
import static tech.units.indriya.unit.Units.MOLE;

/**
 * In chemistry, the molar mass is a physical property. It is defined as the mass of a given substance (chemical element
 * or chemical compound) divided by its amount of substance.
 *
 * @author cl
 */
public class MolarMass extends AbstractFeature<Quantity<MolarMass>> implements Quantity<MolarMass> {

    public static final Unit<MolarMass> GRAM_PER_MOLE = new ProductUnit<>(GRAM.divide(MOLE));

    public MolarMass(Quantity<MolarMass> quantity, Evidence evidence) {
        super(quantity, evidence);
    }

    public MolarMass(double quantity, Evidence evidence) {
        super(Quantities.getQuantity(quantity, GRAM_PER_MOLE), evidence);
    }

    public MolarMass(Quantity<MolarMass> quantity) {
        super(quantity);
    }

    public MolarMass(double quantity) {
        super(Quantities.getQuantity(quantity, GRAM_PER_MOLE));
    }

    @Override
    public Quantity<MolarMass> add(Quantity<MolarMass> augend) {
        return getContent().add(augend);
    }

    @Override
    public Quantity<MolarMass> subtract(Quantity<MolarMass> subtrahend) {
        return getContent().subtract(subtrahend);
    }

    @Override
    public Quantity<?> divide(Quantity<?> divisor) {
        return getContent().divide(divisor);
    }

    @Override
    public Quantity<MolarMass> divide(Number divisor) {
        return getContent().divide(divisor);
    }

    @Override
    public Quantity<?> multiply(Quantity<?> multiplier) {
        return getContent().multiply(multiplier);
    }

    @Override
    public Quantity<MolarMass> multiply(Number multiplier) {
        return getContent().multiply(multiplier);
    }

    @Override
    public Quantity<?> inverse() {
        return getContent().inverse();
    }

    @Override
    public Quantity<MolarMass> to(Unit<MolarMass> unit) {
        return getContent().to(unit);
    }

    @Override
    public <T extends Quantity<T>> Quantity<T> asType(Class<T> type) throws ClassCastException {
        return getContent().asType(type);
    }

    @Override
    public Number getValue() {
        return getContent().getValue();
    }

    @Override
    public Unit<MolarMass> getUnit() {
        return getContent().getUnit();
    }

}
