package bio.singa.simulation.features;

import bio.singa.features.model.AbstractFeature;
import bio.singa.features.model.AbstractScalableQuantitativeFeature;
import bio.singa.features.units.UnitRegistry;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Length;

/**
 * @author cl
 */
public class AttachmentDistance extends AbstractScalableQuantitativeFeature<Length> {

    public AttachmentDistance(Quantity<Length> quantity) {
        super(quantity);
    }

    public static Builder of(Quantity<Length> quantity) {
        return new Builder(quantity);
    }

    public static Builder of(double value, Unit<Length> unit) {
        return new Builder(Quantities.getQuantity(value, unit));
    }

    @Override
    public void scale() {
        scaledQuantity = UnitRegistry.scaleForPixel(getContent()).getValue().doubleValue();
        halfScaledQuantity = scaledQuantity * 0.5;
    }

    public static class Builder extends AbstractFeature.Builder<Quantity<Length>, AttachmentDistance, Builder> {

        public Builder(Quantity<Length> quantity) {
            super(quantity);
        }

        @Override
        protected AttachmentDistance createObject(Quantity<Length> quantity) {
            return new AttachmentDistance(quantity);
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }
    }


}
