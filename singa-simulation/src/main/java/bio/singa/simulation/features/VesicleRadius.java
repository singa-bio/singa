package bio.singa.simulation.features;

import bio.singa.features.model.AbstractFeature;
import bio.singa.features.model.AbstractScalableQuantitativeFeature;
import bio.singa.features.model.Evidence;
import bio.singa.features.units.UnitRegistry;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Length;
import java.util.List;

/**
 * @author cl
 */
public class VesicleRadius extends AbstractScalableQuantitativeFeature<Length> {

    public VesicleRadius(Quantity<Length> quantity, List<Evidence> evidence) {
        super(quantity, evidence);
    }

    public VesicleRadius(Quantity<Length> quantity, Evidence evidence) {
        super(quantity, evidence);
    }

    public VesicleRadius(Quantity<Length> quantity) {
        super(quantity);
    }

    public static Builder of(double value, Unit<Length> unit) {
        return new Builder(Quantities.getQuantity(value, unit));
    }

    public static Builder of(Quantity<Length> quantity) {
        return new Builder(quantity);
    }

    @Override
    public void scale() {
        scaledQuantity = UnitRegistry.scaleForPixel(getContent()).getValue().doubleValue();
        halfScaledQuantity = scaledQuantity * 0.5;
    }

    public static class Builder extends AbstractFeature.Builder<Quantity<Length>, VesicleRadius, Builder> {

        public Builder(Quantity<Length> quantity) {
            super(quantity);
        }

        @Override
        protected VesicleRadius createObject(Quantity<Length> quantity) {
            return new VesicleRadius(quantity);
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }
    }

}
