package de.bioforscher.singa.structure.features.molarmass;

import de.bioforscher.singa.features.model.AbstractFeature;
import de.bioforscher.singa.features.model.FeatureOrigin;
import tec.uom.se.quantity.Quantities;
import tec.uom.se.unit.ProductUnit;

import javax.measure.Quantity;
import javax.measure.Unit;

import static tec.uom.se.unit.Units.GRAM;
import static tec.uom.se.unit.Units.MOLE;

/**
 * In chemistry, the molar mass is a physical property. It is defined as the mass of a given substance (chemical element
 * or chemical compound) divided by its amount of substance.
 *
 * @author cl
 */
public class MolarMass extends AbstractFeature<Quantity<MolarMass>> implements Quantity<MolarMass> {

    public static final Unit<MolarMass> GRAM_PER_MOLE = new ProductUnit<>(GRAM.divide(MOLE));

    public MolarMass(Quantity<MolarMass> quantity, FeatureOrigin featureOrigin) {
        super(quantity, featureOrigin);
    }

    public MolarMass(double quantity, FeatureOrigin featureOrigin) {
        super(Quantities.getQuantity(quantity, GRAM_PER_MOLE), featureOrigin);
    }

    @Override
    public Quantity<MolarMass> add(Quantity<MolarMass> augend) {
        return getFeatureContent().add(augend);
    }

    @Override
    public Quantity<MolarMass> subtract(Quantity<MolarMass> subtrahend) {
        return getFeatureContent().subtract(subtrahend);
    }

    @Override
    public Quantity<?> divide(Quantity<?> divisor) {
        return getFeatureContent().divide(divisor);
    }

    @Override
    public Quantity<MolarMass> divide(Number divisor) {
        return getFeatureContent().divide(divisor);
    }

    @Override
    public Quantity<?> multiply(Quantity<?> multiplier) {
        return getFeatureContent().multiply(multiplier);
    }

    @Override
    public Quantity<MolarMass> multiply(Number multiplier) {
        return getFeatureContent().multiply(multiplier);
    }

    @Override
    public Quantity<?> inverse() {
        return getFeatureContent().inverse();
    }

    @Override
    public Quantity<MolarMass> to(Unit<MolarMass> unit) {
        return getFeatureContent().to(unit);
    }

    @Override
    public <T extends Quantity<T>> Quantity<T> asType(Class<T> type) throws ClassCastException {
        return getFeatureContent().asType(type);
    }

    @Override
    public Number getValue() {
        return getFeatureContent().getValue();
    }

    @Override
    public Unit<MolarMass> getUnit() {
        return getFeatureContent().getUnit();
    }
}
