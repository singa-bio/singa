package de.bioforscher.singa.chemistry.descriptive.features.permeability;

import de.bioforscher.singa.chemistry.descriptive.entities.Transporter;
import de.bioforscher.singa.features.model.FeatureOrigin;
import de.bioforscher.singa.features.model.ScalableQuantityFeature;
import de.bioforscher.singa.features.parameters.Environment;
import tec.uom.se.quantity.Quantities;
import tec.uom.se.unit.ProductUnit;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import static tec.uom.se.unit.Units.METRE;
import static tec.uom.se.unit.Units.SECOND;

/**
 * Represents the rate at which a substrate of a {@link Transporter} may be transported through the membrane.
 *
 * @author cl
 */
public class OsmoticPermeability extends ScalableQuantityFeature<OsmoticPermeability> implements Quantity<OsmoticPermeability> {

    /**
     * Unit most commonly used to describe osmotic permeability.
     */
    public static final Unit<OsmoticPermeability> CUBIC_CENTIMETER_PER_SECOND = new ProductUnit<>(METRE.divide(100).pow(3).divide(SECOND));

    /**
     * Unit used to calculate in simulations since concentrations are stored in mol per litre
     */
    public static final Unit<OsmoticPermeability> LITRE_PER_SECOND = new ProductUnit<>(METRE.divide(10).pow(3).divide(SECOND));

    public static final String SYMBOL = "p_f";

    /**
     * Creates a new Instance of the {@link OsmoticPermeability} Feature. Internally osmotic permeability is saved as
     * {@link OsmoticPermeability#LITRE_PER_SECOND} for easier calculations.
     *
     * @param osmoticPermeabilityQuantity The osmotic permeability.
     * @param featureOrigin The origin of the feature.
     */
    public OsmoticPermeability(Quantity<OsmoticPermeability> osmoticPermeabilityQuantity, FeatureOrigin featureOrigin) {
        super(osmoticPermeabilityQuantity.to(new ProductUnit<>(Environment.getTransformedVolume().divide(SECOND))), featureOrigin);
    }

    /**
     * Creates a new Instance of the {@link OsmoticPermeability} Feature. Quantity is interpreted as
     * {@link OsmoticPermeability#CUBIC_CENTIMETER_PER_SECOND}. Internally osmotic permeability is saved as
     * {@link OsmoticPermeability#LITRE_PER_SECOND} for easier calculations.
     *
     * @param osmoticPermeabilityQuantity The osmotic permeability in {@link OsmoticPermeability#CUBIC_CENTIMETER_PER_SECOND}
     * @param featureOrigin The origin of the feature.
     */
    public OsmoticPermeability(double osmoticPermeabilityQuantity, FeatureOrigin featureOrigin) {
        this(Quantities.getQuantity(osmoticPermeabilityQuantity, CUBIC_CENTIMETER_PER_SECOND), featureOrigin);
    }

    @Override
    public Quantity<OsmoticPermeability> add(Quantity<OsmoticPermeability> augend) {
        return getFeatureContent().add(augend);
    }

    @Override
    public Quantity<OsmoticPermeability> subtract(Quantity<OsmoticPermeability> subtrahend) {
        return getFeatureContent().subtract(subtrahend);
    }

    @Override
    public Quantity<?> divide(Quantity<?> divisor) {
        return getFeatureContent().divide(divisor);
    }

    @Override
    public Quantity<OsmoticPermeability> divide(Number divisor) {
        return getFeatureContent().divide(divisor);
    }

    @Override
    public Quantity<?> multiply(Quantity<?> multiplier) {
        return getFeatureContent().multiply(multiplier);
    }

    @Override
    public Quantity<OsmoticPermeability> multiply(Number multiplier) {
        return getFeatureContent().multiply(multiplier);
    }

    @Override
    public Quantity<?> inverse() {
        return getFeatureContent().inverse();
    }

    @Override
    public Quantity<OsmoticPermeability> to(Unit<OsmoticPermeability> unit) {
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
    public Unit<OsmoticPermeability> getUnit() {
        return getFeatureContent().getUnit();
    }

    @Override
    public void scale(Quantity<Time> time, Quantity<Length> space) {
        // transform to specified unit
        Quantity<OsmoticPermeability> transformedQuantity = getFeatureContent().to(new ProductUnit<>(Environment.getTransformedVolume().divide(time.getUnit())));
        // transform to specified amount
        scaledQuantity = transformedQuantity.multiply(time.getValue().doubleValue());
        // and half
        halfScaledQuantity = transformedQuantity.multiply(time.multiply(0.5).getValue().doubleValue());
    }

    @Override
    public String getSymbol() {
        return SYMBOL;
    }
}
