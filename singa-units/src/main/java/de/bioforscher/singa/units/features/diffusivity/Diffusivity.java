package de.bioforscher.singa.units.features.diffusivity;

import de.bioforscher.singa.units.features.model.AbstractFeature;
import de.bioforscher.singa.units.features.model.FeatureOrigin;
import de.bioforscher.singa.units.features.model.FeatureRegistry;

import javax.measure.Quantity;
import javax.measure.Unit;

/**
 * Diffusivity or diffusion coefficient is a proportionality constant between the molar flux due to molecular diffusion
 * and the gradient in the concentration of the species (or the driving force for diffusion). The higher the diffusivity
 * (of one substance with respect to another), the faster they diffuse into each other.
 *
 * @author cl
 */
public class Diffusivity extends AbstractFeature<Quantity<Diffusivity>> implements Quantity<Diffusivity> {

    /**
     * Every FeatureProvider that is registered in this method is invoked automatically when the Feature is requested
     * for the first time.
     */
    public static void register() {
        FeatureRegistry.addProviderForFeature(Diffusivity.class, DiffusivityProvider.class);
    }

    public Diffusivity(Quantity<Diffusivity> diffusivityQuantity, FeatureOrigin origin) {
        super(diffusivityQuantity, origin);
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
