package bio.singa.chemistry.features.diffusivity;

import bio.singa.features.model.AbstractFeature;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;

/**
 * @author cl
 */
public class OriginalDiffusivity extends PixelDiffusivity {

    public OriginalDiffusivity(Quantity<Diffusivity> quantity) {
        super(quantity);
    }

    public static Builder build(Quantity<Diffusivity> quantity) {
        return new Builder(quantity);
    }

    public static Builder build(double value, Unit<Diffusivity> unit) {
        return new Builder(Quantities.getQuantity(value, unit));
    }

    public static class Builder extends AbstractFeature.Builder<Quantity<Diffusivity>, OriginalDiffusivity, Builder> {

        public Builder(Quantity<Diffusivity> quantity) {
            super(quantity);
        }

        @Override
        protected OriginalDiffusivity createObject(Quantity<Diffusivity> quantity) {
            return new OriginalDiffusivity(quantity);
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }
    }


}
