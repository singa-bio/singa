package de.bioforscher.singa.features.quantities;

import de.bioforscher.singa.features.model.AbstractFeature;
import de.bioforscher.singa.features.model.FeatureOrigin;
import tec.uom.se.quantity.Quantities;
import tec.uom.se.unit.ProductUnit;

import javax.measure.Quantity;
import javax.measure.Unit;

import static tec.uom.se.unit.Units.CUBIC_METRE;
import static tec.uom.se.unit.Units.MOLE;

/**
 * The molar volume, symbol Vm, is the volume occupied by one mole of a substance (chemical element or chemical
 * compound) at a given temperature and pressure. It is equal to the molar mass (M) divided by the mass density (ρ). It
 * has the SI unit cubic metres per mole (m3/mol)
 *
 * @author cl
 */
public class MolarVolume extends AbstractFeature<Quantity<MolarVolume>> implements Quantity<MolarVolume> {

    public static final Unit<MolarVolume> CUBIC_METRE_PER_MOLE = new ProductUnit<>(CUBIC_METRE.divide(MOLE));
    public static final String SYMBOL = "V_m";

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

    @Override
    public String getSymbol() {
        return SYMBOL;
    }

}
