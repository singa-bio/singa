package de.bioforscher.singa.structure.features.molarvolume;

import de.bioforscher.singa.features.model.AbstractFeature;
import de.bioforscher.singa.features.model.FeatureOrigin;
import tec.units.ri.quantity.Quantities;
import tec.units.ri.unit.ProductUnit;

import javax.measure.Quantity;
import javax.measure.Unit;

import static tec.units.ri.unit.Units.CUBIC_METRE;
import static tec.units.ri.unit.Units.MOLE;

/**
 * @author cl
 */
public class MolarVolume extends AbstractFeature<Quantity<MolarVolume>> implements Quantity<MolarVolume> {

    public static final Unit<MolarVolume> CUBIC_METRE_PER_MOLE = new ProductUnit<>(CUBIC_METRE.divide(MOLE));

    public MolarVolume(Quantity<MolarVolume> quantity, FeatureOrigin featureOrigin) {
        super(quantity, featureOrigin);
    }

    public MolarVolume(double quantity, FeatureOrigin featureOrigin) {
        super(Quantities.getQuantity(quantity, CUBIC_METRE_PER_MOLE), featureOrigin);
    }

    @Override
    public Quantity<MolarVolume> add(Quantity<MolarVolume> augend) {
        return getFeatureContent().add(augend);
    }

    @Override
    public Quantity<MolarVolume> subtract(Quantity<MolarVolume> subtrahend) {
        return getFeatureContent().subtract(subtrahend);
    }

    @Override
    public Quantity<?> divide(Quantity<?> divisor) {
        return getFeatureContent().divide(divisor);
    }

    @Override
    public Quantity<MolarVolume> divide(Number divisor) {
        return getFeatureContent().divide(divisor);
    }

    @Override
    public Quantity<?> multiply(Quantity<?> multiplier) {
        return getFeatureContent().multiply(multiplier);
    }

    @Override
    public Quantity<MolarVolume> multiply(Number multiplier) {
        return getFeatureContent().multiply(multiplier);
    }

    @Override
    public Quantity<?> inverse() {
        return getFeatureContent().inverse();
    }

    @Override
    public Quantity<MolarVolume> to(Unit<MolarVolume> unit) {
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
    public Unit<MolarVolume> getUnit() {
        return getFeatureContent().getUnit();
    }
}
