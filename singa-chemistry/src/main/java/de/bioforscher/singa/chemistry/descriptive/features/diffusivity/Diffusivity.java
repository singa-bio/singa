package de.bioforscher.singa.chemistry.descriptive.features.diffusivity;

import de.bioforscher.singa.chemistry.descriptive.features.FeatureRegistry;
import de.bioforscher.singa.features.model.AbstractFeature;
import de.bioforscher.singa.features.model.FeatureOrigin;
import de.bioforscher.singa.features.model.ScalableFeature;
import tec.uom.se.quantity.Quantities;
import tec.uom.se.unit.ProductUnit;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import static tec.uom.se.unit.Units.METRE;
import static tec.uom.se.unit.Units.SECOND;

/**
 * Diffusivity or diffusion coefficient is a proportionality constant between the molar flux due to molecular diffusion
 * and the gradient in the concentration of the species (or the driving force for diffusion). The higher the diffusivity
 * (of one substance with respect to another), the faster they diffuse into each other.
 *
 * @author cl
 */
public class Diffusivity extends AbstractFeature<Quantity<Diffusivity>> implements Quantity<Diffusivity>, ScalableFeature<Quantity<Diffusivity>> {

    public static final Unit<Diffusivity> SQUARE_CENTIMETER_PER_SECOND = new ProductUnit<>(METRE.divide(100).pow(2).divide(SECOND));

    private Quantity<Diffusivity> scaledQuantity;
    private Quantity<Diffusivity> halfScaledQuantity;

    public Diffusivity(Quantity<Diffusivity> diffusivityQuantity, FeatureOrigin origin) {
        super(diffusivityQuantity, origin);
    }

    public Diffusivity(double diffusivityQuantity, FeatureOrigin origin) {
        super(Quantities.getQuantity(diffusivityQuantity, SQUARE_CENTIMETER_PER_SECOND), origin);
    }

    /**
     * Every FeatureProvider that is registered in this method is invoked automatically when the Feature is requested
     * for the first time.
     */
    public static void register() {
        FeatureRegistry.addProviderForFeature(Diffusivity.class, DiffusivityProvider.class);
    }

    @Override
    public void scale(Quantity<Time> targetTimeScale, Quantity<Length> targetLengthScale) {
        // transform to specified unit
        Quantity<Diffusivity> scaledQuantity = getFeatureContent()
                .to(new ProductUnit<>(targetLengthScale.getUnit().pow(2).divide(targetTimeScale.getUnit())));
        // denominator
        Quantity<Diffusivity> denominator = scaledQuantity.divide(targetLengthScale.getValue()).divide(targetLengthScale.getValue());
        // transform to specified amount
        this.scaledQuantity = denominator.multiply(targetTimeScale.getValue());
        // and half of it
        halfScaledQuantity = denominator.multiply(targetTimeScale.multiply(0.5).getValue());
    }

    public Quantity<Diffusivity> getScaledQuantity() {
        return scaledQuantity;
    }

    @Override
    public Quantity<Diffusivity> getHalfScaledQuantity() {
        return halfScaledQuantity;
    }

    @Override
    public Quantity<Diffusivity> add(Quantity<Diffusivity> augend) {
        return getFeatureContent().add(augend);
    }

    @Override
    public Quantity<Diffusivity> subtract(Quantity<Diffusivity> subtrahend) {
        return getFeatureContent().subtract(subtrahend);
    }

    @Override
    public Quantity<?> divide(Quantity<?> divisor) {
        return getFeatureContent().divide(divisor);
    }

    @Override
    public Quantity<Diffusivity> divide(Number divisor) {
        return getFeatureContent().divide(divisor);
    }

    @Override
    public Quantity<?> multiply(Quantity<?> multiplier) {
        return getFeatureContent().multiply(multiplier);
    }

    @Override
    public Quantity<Diffusivity> multiply(Number multiplier) {
        return getFeatureContent().multiply(multiplier);
    }

    @Override
    public Quantity<?> inverse() {
        return getFeatureContent().inverse();
    }

    @Override
    public Quantity<Diffusivity> to(Unit<Diffusivity> unit) {
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
    public Unit<Diffusivity> getUnit() {
        return getFeatureContent().getUnit();
    }

}
