package bio.singa.chemistry.features.diffusivity;

import bio.singa.features.model.AbstractFeature;
import bio.singa.features.model.AbstractScalableQuantitativeFeature;
import bio.singa.features.model.FeatureRegistry;
import bio.singa.features.units.UnitRegistry;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Length;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cl
 */
public class ConcentrationDiffusivity extends AbstractScalableQuantitativeFeature<Diffusivity> {

    private static Map<Quantity<Length>, Diffusivity> cache;

    public ConcentrationDiffusivity(Quantity<Diffusivity> quantity) {
        super(quantity);
    }

    private static Map<Quantity<Length>, Diffusivity> getCache() {
        if (cache == null) {
            synchronized (FeatureRegistry.class) {
                cache = new HashMap<>();
            }
        }
        return cache;
    }

    public static Builder of(Quantity<Diffusivity> quantity) {
        return new Builder(quantity);
    }

    public static Builder of(double value, Unit<Diffusivity> unit) {
        return new Builder(Quantities.getQuantity(value, unit));
    }

    @Override
    public void scale() {
        scaledQuantity = UnitRegistry.scale(getContent()).getValue().doubleValue();
        halfScaledQuantity = scaledQuantity * 0.5;
    }

    public static class Builder extends AbstractFeature.Builder<Quantity<Diffusivity>, ConcentrationDiffusivity, Builder> {

        public Builder(Quantity<Diffusivity> quantity) {
            super(quantity);
        }

        @Override
        protected ConcentrationDiffusivity createObject(Quantity<Diffusivity> quantity) {
            return new ConcentrationDiffusivity(quantity);
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }
    }


}
