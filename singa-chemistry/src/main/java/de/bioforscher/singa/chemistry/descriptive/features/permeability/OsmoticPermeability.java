package de.bioforscher.singa.chemistry.descriptive.features.permeability;

import de.bioforscher.singa.chemistry.descriptive.entities.Transporter;
import de.bioforscher.singa.features.model.AbstractFeature;
import de.bioforscher.singa.features.model.FeatureOrigin;
import de.bioforscher.singa.features.model.ScalableFeature;
import tec.units.ri.quantity.Quantities;
import tec.units.ri.unit.ProductUnit;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import static tec.units.ri.unit.Units.METRE;
import static tec.units.ri.unit.Units.SECOND;

/**
 * Represents the rate at which a substrate of a {@link Transporter} may be transported through the membrane.
 *
 * @author cl
 */
public class OsmoticPermeability extends AbstractFeature<Quantity<OsmoticPermeability>> implements Quantity<OsmoticPermeability>, ScalableFeature<Quantity<OsmoticPermeability>> {

    /**
     * Unit most commonly used to describe osmotic permeability.
     */
    public static final Unit<OsmoticPermeability> CUBIC_CENTIMETER_PER_SECOND = new ProductUnit<>(METRE.divide(100).pow(3).divide(SECOND));

    /**
     * Unit used to calculate in simulations since concentrations are stored in mol per litre
     */
    public static final Unit<OsmoticPermeability> LITRE_PER_SECOND = new ProductUnit<>(METRE.divide(10).pow(3).divide(SECOND));

    private Quantity<OsmoticPermeability> scaledQuantity;
    private Quantity<OsmoticPermeability> halfScaledQuantity;

    public OsmoticPermeability(Quantity<OsmoticPermeability> osmoticPermeabilityQuantity, FeatureOrigin featureOrigin) {
        super(osmoticPermeabilityQuantity.to(LITRE_PER_SECOND), featureOrigin);
    }

    public OsmoticPermeability(double osmoticPermeabilityQuantity, FeatureOrigin featureOrigin) {
        super(Quantities.getQuantity(osmoticPermeabilityQuantity, CUBIC_CENTIMETER_PER_SECOND).to(LITRE_PER_SECOND), featureOrigin);
    }

    @Override
    public Quantity<OsmoticPermeability> add(Quantity<OsmoticPermeability> augend) {
        return null;
    }

    @Override
    public Quantity<OsmoticPermeability> subtract(Quantity<OsmoticPermeability> subtrahend) {
        return null;
    }

    @Override
    public Quantity<?> divide(Quantity<?> divisor) {
        return null;
    }

    @Override
    public Quantity<OsmoticPermeability> divide(Number divisor) {
        return null;
    }

    @Override
    public Quantity<?> multiply(Quantity<?> multiplier) {
        return null;
    }

    @Override
    public Quantity<OsmoticPermeability> multiply(Number multiplier) {
        return null;
    }

    @Override
    public Quantity<?> inverse() {
        return null;
    }

    @Override
    public Quantity<OsmoticPermeability> to(Unit<OsmoticPermeability> unit) {
        return null;
    }

    @Override
    public <T extends Quantity<T>> Quantity<T> asType(Class<T> type) throws ClassCastException {
        return null;
    }

    @Override
    public Number getValue() {
        return null;
    }

    @Override
    public Unit<OsmoticPermeability> getUnit() {
        return null;
    }

    @Override
    public void scale(Quantity<Time> time, Quantity<Length> space) {

    }

    @Override
    public Quantity<OsmoticPermeability> getScaledQuantity() {
        return null;
    }

    @Override
    public Quantity<OsmoticPermeability> getHalfScaledQuantity() {
        return null;
    }

}
