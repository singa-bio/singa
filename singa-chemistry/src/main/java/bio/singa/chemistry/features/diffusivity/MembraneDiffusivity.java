package bio.singa.chemistry.features.diffusivity;

import bio.singa.features.model.AbstractFeature;
import bio.singa.features.model.AbstractScalableQuantitativeFeature;
import bio.singa.features.units.UnitRegistry;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;

/**
 * @author cl
 */
public class MembraneDiffusivity extends AbstractScalableQuantitativeFeature<Diffusivity> {

    public MembraneDiffusivity(Quantity<Diffusivity> quantity) {
        super(quantity);
    }

    @Override
    public void scale() {
        scaledQuantity = UnitRegistry.scale(getContent()).getValue().doubleValue();
        halfScaledQuantity = scaledQuantity * 0.5;
    }

    public static Builder of(Quantity<Diffusivity> quantity) {
        return new Builder(quantity);
    }

    public static Builder of(double value, Unit<Diffusivity> unit) {
        return new Builder(Quantities.getQuantity(value, unit));
    }

    public static class Builder extends AbstractFeature.Builder<Quantity<Diffusivity>, MembraneDiffusivity, Builder> {

        public Builder(Quantity<Diffusivity> quantity) {
            super(quantity);
        }

        @Override
        protected MembraneDiffusivity createObject(Quantity<Diffusivity> quantity) {
            return new MembraneDiffusivity(quantity);
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }
    }

}
